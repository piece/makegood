/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2013 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.views;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import com.piece_framework.makegood.core.result.Result;
import com.piece_framework.makegood.core.result.TestCaseResult;
import com.piece_framework.makegood.core.result.TestSuiteResult;
import com.piece_framework.makegood.core.run.FailureRepository;
import com.piece_framework.makegood.launch.MakeGoodLaunch;
import com.piece_framework.makegood.launch.RuntimeConfiguration;
import com.piece_framework.makegood.launch.TestLifecycle;
import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.MakeGoodContext;
import com.piece_framework.makegood.ui.MakeGoodStatus;
import com.piece_framework.makegood.ui.MakeGoodStatusChangeListener;
import com.piece_framework.makegood.ui.Messages;
import com.piece_framework.makegood.ui.actions.ConfigureContinuousTestingAction;
import com.piece_framework.makegood.ui.actions.MoveToNextFailureAction;
import com.piece_framework.makegood.ui.actions.MoveToPreviousFailureAction;
import com.piece_framework.makegood.ui.actions.RerunFailedTestsAction;
import com.piece_framework.makegood.ui.actions.RerunTestAction;
import com.piece_framework.makegood.ui.actions.RunAllTestsAction;
import com.piece_framework.makegood.ui.actions.StopTestRunAction;
import com.piece_framework.makegood.ui.actions.ToggleDebugTestAction;
import com.piece_framework.makegood.ui.actions.ToggleShowOnlyFailuresAction;
import com.piece_framework.makegood.ui.actions.ToggleStopOnFailureAction;
import com.piece_framework.makegood.ui.widgets.ActiveText;
import com.piece_framework.makegood.ui.widgets.ActiveTextListener;
import com.piece_framework.makegood.ui.widgets.ExternalFileWithLineRange;
import com.piece_framework.makegood.ui.widgets.FileWithLineRange;
import com.piece_framework.makegood.ui.widgets.InternalFileWithLineRange;
import com.piece_framework.makegood.ui.widgets.MakeGoodColor;
import com.piece_framework.makegood.ui.widgets.ProgressBar;

public class ResultView extends ViewPart {
    public static final String VIEW_ID = "com.piece_framework.makegood.ui.views.resultView"; //$NON-NLS-1$
    private static final String CONTEXT_ID = "com.piece_framework.makegood.ui.contexts.resultView"; //$NON-NLS-1$

    private ProgressBar progressBar;
    private CLabel testCountLabel;
    private CountLabel passCountLabel;
    private CountLabel failureCountLabel;
    private CountLabel errorCountLabel;

    private TreeViewer resultTreeViewer;
    private CLabel processTimeAverageLabel;
    private ElapsedTimer elapsedTimer;
    private IAction moveToPreviousFailureAction;
    private IAction moveToNextFailureAction;
    private IAction stopTestAction;

    /**
     * @since 2.1.0
     */
    private IAction rerunFailedTestsAction;

    private IAction rerunTestAction;
    private IAction runAllTestsAction;
    private FailureTrace failureTrace;
    private CLabel elapsedTimeLabel;
    private CLabel processTimeLabel;
    private boolean actionsInitialized = false;
    private ResultViewPartListener partListener = new ResultViewPartListener();
    private EditorOpener editorOpener = new EditorOpener();

    private ViewerFilter failureViewFilter = new ViewerFilter() {
        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (!(element instanceof Result)) return false;
            Result result = (Result) element;
            return result.hasFailures() || result.hasErrors();
        }
    };

    /**
     * @since 1.3.0
     */
    private TestLifecycle testLifecycle;

    /**
     * @since 1.6.0
     */
    private StatusArea statusArea;

    /**
     * @since 1.8.0
     */
    private AdditionalInformation additionalInformation = new AdditionalInformation();

    /**
     * @since 1.9.0
     */
    private CLabel endTimeLabel;

    /**
     * @since 2.0.0
     */
    private CTabFolder testResultsTabFolder;

    /**
     * @since 2.0.0
     */
    private CTabItem resultTreeTabItem;

    /**
     * @since 2.0.0
     */
    private CTabItem failureTraceTabItem;

    @Override
    public void createPartControl(final Composite parent) {
        IContextService service = (IContextService) getSite().getService(IContextService.class);
        service.activateContext(CONTEXT_ID);

        parent.setLayout(adjustLayout(new GridLayout()));

        // Row1: The Status Area
        statusArea = createStatusArea(parent, SWT.NONE);
        statusArea.setLayoutData(createHorizontalFillGridData());
        statusArea.setLayout(adjustLayout(new GridLayout()));

        // Row2: The Progress Bar and Watches
        Composite progressBarAndWatches = new Composite(parent, SWT.NONE);
        progressBarAndWatches.setLayoutData(createHorizontalFillGridData());
        progressBarAndWatches.setLayout(adjustLayout(new GridLayout(5, true)));

        Composite progress = new Composite(progressBarAndWatches, SWT.NONE);
        GridData progressGridData = createHorizontalFillGridData();
        progressGridData.horizontalSpan = 2;
        progress.setLayoutData(progressGridData);
        progress.setLayout(adjustLayout(new GridLayout()));
        Composite progressBarBorder = new Composite(progress, SWT.NONE);
        progressBarBorder.setLayoutData(createHorizontalFillGridData());
        GridLayout progressBarBorderLayout = new GridLayout();
        progressBarBorderLayout.marginWidth = 2;
        progressBarBorderLayout.marginHeight = 2;
        progressBarBorderLayout.horizontalSpacing = 2;
        progressBarBorderLayout.verticalSpacing = 2;
        progressBarBorder.setLayout(progressBarBorderLayout);
        progressBar = new ProgressBar(progressBarBorder);
        progressBar.setLayoutData(createHorizontalFillGridData());
        progressBar.setLayout(adjustLayout(new GridLayout()));

        processTimeAverageLabel = new CLabel(progressBarAndWatches, SWT.LEFT);
        processTimeAverageLabel.setLayoutData(createHorizontalFillGridData());
        processTimeLabel = new CLabel(progressBarAndWatches, SWT.LEFT);
        processTimeLabel.setLayoutData(createHorizontalFillGridData());
        elapsedTimeLabel = new CLabel(progressBarAndWatches, SWT.LEFT);
        elapsedTimeLabel.setLayoutData(createHorizontalFillGridData());

        // Row3: The Counters
        Composite counter = new Composite(parent, SWT.NONE);
        counter.setLayoutData(createHorizontalFillGridData());
        counter.setLayout(adjustLayout(new GridLayout(5, true)));
        testCountLabel = new CLabel(counter, SWT.LEFT);
        testCountLabel.setLayoutData(createHorizontalFillGridData());
        passCountLabel = new CountLabel(
            counter,
            SWT.LEFT,
            Messages.MakeGoodView_passesLabel,
            Activator.getImageDescriptor("icons/pass_gray.gif").createImage() //$NON-NLS-1$
        );
        failureCountLabel = new CountLabel(
            counter,
            SWT.LEFT,
            Messages.MakeGoodView_failuresLabel,
            Activator.getImageDescriptor("icons/failure_gray.gif").createImage() //$NON-NLS-1$
        );
        errorCountLabel = new CountLabel(
            counter,
            SWT.LEFT,
            Messages.MakeGoodView_errorsLabel,
            Activator.getImageDescriptor("icons/error_gray.gif").createImage() //$NON-NLS-1$
        );
        endTimeLabel = new CLabel(counter, SWT.LEFT);
        endTimeLabel.setLayoutData(createHorizontalFillGridData());

        // Row4: The Test Results Tabs
        testResultsTabFolder = new CTabFolder(parent, SWT.NONE);
        testResultsTabFolder.setSimple(false);
        testResultsTabFolder.setLayoutData(createBothFillGridData());
        testResultsTabFolder.setLayout(adjustLayout(new GridLayout()));

        resultTreeTabItem = new CTabItem(testResultsTabFolder, SWT.NONE);
        resultTreeTabItem.setText(Messages.MakeGoodView_testResultsLabel);

        Tree resultTree = new Tree(testResultsTabFolder, SWT.BORDER);
        resultTree.setLayoutData(createBothFillGridData());
        resultTreeViewer = new TreeViewer(resultTree);
        resultTreeViewer.setContentProvider(new ResultTreeContentProvider());
        resultTreeViewer.setLabelProvider(new ResultTreeLabelProvider());
        resultTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                failureTrace.clearText();
                if (!(event.getSelection() instanceof IStructuredSelection)) return;
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                Object element = selection.getFirstElement();
                if (!(element instanceof TestCaseResult)) return;
                TestCaseResult testCase = (TestCaseResult) element;
                if (!testCase.isFixed()) return;
                if (!testCase.hasFailures() && !testCase.hasErrors()) return;
                failureTrace.setText(testCase.getFailureTrace());
            }
        });
        resultTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                Object element = ((IStructuredSelection) event.getSelection()).getFirstElement();
                if (element == null) return;
                if (element instanceof Result) {
                    try {
                        editorOpener.open((Result) element);
                    } catch (PartInitException e) {
                        Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
                    }
                }
            }
        });
        resultTreeTabItem.setControl(resultTree);

        failureTraceTabItem = new CTabItem(testResultsTabFolder, SWT.NONE);
        failureTraceTabItem.setText(Messages.MakeGoodView_failureTraceLabel);
        failureTraceTabItem.setImage(Activator.getImageDescriptor("icons/failure_trace.gif").createImage()); //$NON-NLS-1$
        failureTrace = createFailureTrace(
            testResultsTabFolder,
            SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
        );
        failureTraceTabItem.setControl(failureTrace);

        testResultsTabFolder.setSelection(resultTreeTabItem);

        IViewSite site = getViewSite();
        site.getPage().addPartListener(partListener);

        MakeGoodContext.getInstance().addStatusChangeListener(statusArea);

        clear();

        testLifecycle = TestLifecycle.getInstance();
        if (testLifecycle != null) {
            if (testLifecycle.getProgress().isStopped()) {
                markAsStopped();
            } else if (testLifecycle.getProgress().hasFailures()) {
                markAsFailed();
            }

            setTreeInput(testLifecycle.getProgress().getResult());
        }

        elapsedTimer = new ElapsedTimer(200);
    }

    @Override
    public void setFocus() {
    }

    private void clear() {
        progressBar.clear();
        processTimeAverageLabel.setText(
            TimeFormatter.format(0)
            + "/" + //$NON-NLS-1$
            Messages.MakeGoodView_averageTest
        );
        elapsedTimeLabel.setText(
            Messages.MakeGoodView_realTime +
            ": " +  //$NON-NLS-1$
            TimeFormatter.format(0)
        );
        processTimeLabel.setText(
            Messages.MakeGoodView_testTime +
            ": " +  //$NON-NLS-1$
            TimeFormatter.format(0)
        );
        endTimeLabel.setText(Messages.MakeGoodView_endTime + ":");  //$NON-NLS-1$
        testCountLabel.setText(Messages.MakeGoodView_testsLabel + ": 0/0"); //$NON-NLS-1$
        passCountLabel.clear();
        failureCountLabel.clear();
        errorCountLabel.clear();
        resultTreeViewer.setInput(null);
        additionalInformation.clearMessage();
        setContentDescription(additionalInformation.toString());
    }

    private GridData createHorizontalFillGridData() {
        GridData horizontalFillGrid = new GridData();
        horizontalFillGrid.horizontalAlignment = GridData.FILL;
        horizontalFillGrid.grabExcessHorizontalSpace = true;
        return horizontalFillGrid;
    }

    private GridData createBothFillGridData() {
        GridData bothFillGrid = new GridData();
        bothFillGrid.horizontalAlignment = GridData.FILL;
        bothFillGrid.verticalAlignment = GridData.FILL;
        bothFillGrid.grabExcessHorizontalSpace = true;
        bothFillGrid.grabExcessVerticalSpace = true;
        return bothFillGrid;
    }

    private FailureTrace createFailureTrace(Composite parent, int style) {
        FailureTrace failureTrace = new FailureTrace(parent, style);
        failureTrace.setLayoutData(new GridData(GridData.FILL_BOTH));
        failureTrace.setEditable(false);
        failureTrace.addListener(new EditorOpenActiveTextListener());
        return failureTrace;
    }

    /**
     * @since 1.6.0
     */
    private StatusArea createStatusArea(Composite parent, int style) {
        StatusArea statusArea = new StatusArea(parent, style);
        statusArea.setBackground(parent.getBackground());
        statusArea.addListener(new PreferencesOpenActiveTextListener());
        return statusArea;
    }

    public void moveToNextFailure() {
        moveToPreviousOrNextFailure(FailureRepository.FIND_NEXT);
    }

    public void moveToPreviousFailure() {
        moveToPreviousOrNextFailure(FailureRepository.FIND_PREVIOUS);
    }

    @Override
    public void dispose() {
        MakeGoodContext.getInstance().removeStatusChangeListener(statusArea);

        IViewSite site = getViewSite();
        if (site != null) {
            site.getPage().removePartListener(partListener);
        }

        super.dispose();
    }

    public void filterResults(boolean showsFailuresOnly) {
        if (showsFailuresOnly) {
            resultTreeViewer.addFilter(failureViewFilter);
        } else {
            resultTreeViewer.removeFilter(failureViewFilter);
        }
    }

    /**
     * @since 1.3.0
     */
    public boolean hasFailures() {
        if (testLifecycle == null) return false;
        return testLifecycle.getProgress().hasFailures();
    }

    /**
     * @since 2.0.0
     */
    public void switchToUnselectedTestResultsTab() {
        if (testResultsTabFolder.getSelection() == resultTreeTabItem) {
            testResultsTabFolder.setSelection(failureTraceTabItem);
        } else if (testResultsTabFolder.getSelection() == failureTraceTabItem) {
            testResultsTabFolder.setSelection(resultTreeTabItem);
        }
    }

    void setTreeInput(TestSuiteResult result) {
        resultTreeViewer.setInput(result);
    }

    void updateOnEndTestCase() {
        if (testLifecycle.getProgress().hasFailures()) {
            markAsFailed();
        }
        updateResult();
    }

    void updateOnStartTestCase(TestCaseResult testCase) {
        updateTestCount();

        resultTreeViewer.refresh();
    }

    void startTest(TestLifecycle testLifecycle) {
        clear();

        this.testLifecycle = testLifecycle;

        elapsedTimer.schedule();
    }

    void endTest() {
        updateResult();
        updateEndTime();

        TreeItem topItem = resultTreeViewer.getTree().getTopItem();
        if (topItem != null) {
            Result topResult = (Result) topItem.getData();
            if (topResult != null) {
                resultTreeViewer.setSelection(new StructuredSelection(topResult));
            }
        }
    }

    void markAsStopped() {
        progressBar.markAsStopped();
    }

    void expandResultTreeToResult(Result result) {
        resultTreeViewer.expandToLevel(result, TreeViewer.ALL_LEVELS);
    }

    private void markAsFailed() {
        progressBar.markAsFailed();

        if (moveToPreviousFailureAction != null) {
            moveToPreviousFailureAction.setEnabled(true);
        }
        if (moveToNextFailureAction != null) {
            moveToNextFailureAction.setEnabled(true);
        }
    }

    private void initializeActions(IViewSite site) {
        IToolBarManager manager = site.getActionBars().getToolBarManager();

        ActionContributionItem showOnlyFailuresItem =
            (ActionContributionItem) manager.find(ToggleShowOnlyFailuresAction.ACTION_ID);
        if (showOnlyFailuresItem != null) {
            showOnlyFailuresItem.getAction().setChecked(
                RuntimeConfiguration.getInstance().showsOnlyFailures
            );
            actionsInitialized = true;
        }

        ActionContributionItem previousFailureItem =
            (ActionContributionItem) manager.find(MoveToPreviousFailureAction.ACTION_ID);
        if (previousFailureItem != null) {
            moveToPreviousFailureAction = previousFailureItem.getAction();
            moveToPreviousFailureAction.setEnabled(hasFailures());
        }

        ActionContributionItem nextFailureItem =
            (ActionContributionItem) manager.find(MoveToNextFailureAction.ACTION_ID);
        if (nextFailureItem != null) {
            moveToNextFailureAction = nextFailureItem.getAction();
            moveToNextFailureAction.setEnabled(hasFailures());
        }

        ActionContributionItem stopOnFailureItem =
            (ActionContributionItem) manager.find(ToggleStopOnFailureAction.ACTION_ID);
        if (stopOnFailureItem != null) {
            stopOnFailureItem.getAction().setChecked(
                RuntimeConfiguration.getInstance().stopsOnFailure
            );
        }

        ActionContributionItem debugTestItem =
            (ActionContributionItem) manager.find(ToggleDebugTestAction.ACTION_ID);
        if (debugTestItem != null) {
            debugTestItem.getAction().setChecked(
                RuntimeConfiguration.getInstance().debugsTest
            );
        }

        ActionContributionItem stopTestItem =
            (ActionContributionItem) manager.find(StopTestRunAction.ACTION_ID);
        if (stopTestItem != null) {
            stopTestAction = stopTestItem.getAction();
            stopTestAction.setEnabled(MakeGoodLaunch.hasActiveLaunch());
        }

        ActionContributionItem rerunTestItem =
            (ActionContributionItem) manager.find(RerunTestAction.ACTION_ID);
        if (rerunTestItem != null) {
            rerunTestAction = rerunTestItem.getAction();
            rerunTestAction.setEnabled(MakeGoodContext.getInstance().getTestRunner().hasLastTest());
        }

        ActionContributionItem rerunFailedTestsItem =
            (ActionContributionItem) manager.find(RerunFailedTestsAction.ACTION_ID);
        if (rerunFailedTestsItem != null) {
            rerunFailedTestsAction = rerunFailedTestsItem.getAction();
            rerunFailedTestsAction.setEnabled(MakeGoodContext.getInstance().getTestRunner().hasLastTest());
        }

        ActionContributionItem runAllTestsItem =
            (ActionContributionItem) manager.find(RunAllTestsAction.ACTION_ID);
        if (runAllTestsItem != null) {
            runAllTestsAction = runAllTestsItem.getAction();
            runAllTestsAction.setEnabled(MakeGoodContext.getInstance().getActivePart().isAllTestsRunnable());
        }

        ActionContributionItem configureContinuousTestingItem =
            (ActionContributionItem) manager.find(ConfigureContinuousTestingAction.ACTION_ID);
        if (configureContinuousTestingItem != null) {
            IAction configureContinuousTestingAction = configureContinuousTestingItem.getAction();
            configureContinuousTestingAction.setImageDescriptor(
                RuntimeConfiguration.getInstance().getContinuousTesting().isEnabled() ?
                    ConfigureContinuousTestingAction.IMAGE_DESCRIPTOR_ENABLED :
                    ConfigureContinuousTestingAction.IMAGE_DESCRIPTOR_DISABLED
            );
        }
    }

    private GridLayout adjustLayout(GridLayout layout) {
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.horizontalSpacing = 0;
        layout.verticalSpacing = 0;
        return layout;
    }

    private void moveToPreviousOrNextFailure(int direction) {
        Result selectedResult = (Result) ((IStructuredSelection) resultTreeViewer.getSelection()).getFirstElement();
        if (selectedResult == null) {
            selectedResult = (Result) resultTreeViewer.getTree().getTopItem().getData();
        }

        TestCaseResult previousOrNextResult = testLifecycle.getFailures().find(selectedResult, direction);
        if (previousOrNextResult == null) return;

        resultTreeViewer.setSelection(new StructuredSelection(previousOrNextResult), true);
        resultTreeViewer.expandToLevel(previousOrNextResult, TreeViewer.ALL_LEVELS);

        try {
            editorOpener.open(previousOrNextResult);
        } catch (PartInitException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
        }
    }

    private void updateResult() {
        progressBar.update(testLifecycle.getProgress().calculateRate());

        processTimeAverageLabel.setText(
            TimeFormatter.format(testLifecycle.getProgress().calculateProcessTimeAverage()) +
            "/" + //$NON-NLS-1$
            Messages.MakeGoodView_averageTest
        );
        processTimeAverageLabel.getParent().layout();

        processTimeLabel.setText(
            Messages.MakeGoodView_testTime +
            ": " + //$NON-NLS-1$
            TimeFormatter.format(testLifecycle.getProgress().getProcessTime())
        );

        passCountLabel.setCount(testLifecycle.getProgress().getPassCount());
        failureCountLabel.setCount(testLifecycle.getProgress().getFailureCount());
        errorCountLabel.setCount(testLifecycle.getProgress().getErrorCount());

        resultTreeViewer.refresh();
    }

    private void updateElapsedTime() {
        if (testLifecycle == null) return;
        elapsedTimeLabel.setText(
            Messages.MakeGoodView_realTime +
            ": " + //$NON-NLS-1$
            TimeFormatter.format(testLifecycle.getProgress().getElapsedTime())
        );
    }

    private void updateTestCount() {
        if (testLifecycle == null) return;
        testCountLabel.setText(
            Messages.MakeGoodView_testsLabel +
            ": " + //$NON-NLS-1$
            testLifecycle.getProgress().getCurrentTestCount() +
            "/" + //$NON-NLS-1$
            testLifecycle.getProgress().getAllTestCount()
        );
    }

    private void update() {
        IViewSite site = super.getViewSite();
        if (site == null) return;

        initializeActions(site);

        if (testLifecycle != null) {
            updateResult();
            updateElapsedTime();
            updateEndTime();
            updateTestCount();
        }
    }

    /**
     * @since 1.9.0
     */
    private void updateEndTime() {
        if (testLifecycle != null && testLifecycle.getProgress().isRunning() == false && testLifecycle.getEndTime() != null) {
            endTimeLabel.setText(
                Messages.MakeGoodView_endTime +
                ": " + //$NON-NLS-1$
                new SimpleDateFormat("HH:mm:ss z").format(testLifecycle.getEndTime()) //$NON-NLS-1$
            );
        }
    }

    private class CountLabel {
        private CLabel label;
        private String text;

        public CountLabel(Composite parent, int style, String text, Image icon) {
            label = new CLabel(parent, style);
            label.setLayoutData(createHorizontalFillGridData());
            label.setText(text);
            label.setImage(icon);
            this.text = text;
        }

        public void setCount(int count) {
            label.setText(text + ": " + count); //$NON-NLS-1$
        }

        public void clear() {
            setCount(0);
        }
    }

    private class ElapsedTimer implements Runnable {
        private int delay;

        public ElapsedTimer(int delay) {
            this.delay = delay;
        }

        public void schedule() {
            elapsedTimeLabel.getDisplay().timerExec(delay, this);
        }

        @Override
        public void run() {
            if (!elapsedTimeLabel.isDisposed()) {
                updateElapsedTime();
                schedule();
            }
        }
    }

    private class FailureTrace extends ActiveText {
        public FailureTrace(Composite parent, int style) {
            super(parent, style);
        }

        @Override
        public void clearText() {
            super.clearText();
            hideScrollBar();
        }

        /**
         * @since 1.3.0
         */
        @Override
        public void setText(String text) {
            super.setText(text);
            showScrollBar();
        }

        /**
         * @since 1.6.0
         */
        private void showScrollBar() {
            getVerticalBar().setVisible(true);
            getHorizontalBar().setVisible(true);
        }

        /**
         * @since 1.6.0
         */
        private void hideScrollBar() {
            getVerticalBar().setVisible(false);
            getHorizontalBar().setVisible(false);
        }

        /**
         * @since 1.6.0
         */
        @Override
        public void mouseDoubleClick(MouseEvent e) {
        }

        /**
         * @since 1.6.0
         */
        @Override
        public void mouseDown(MouseEvent e) {
            StyleRange style = findStyle(new Point(e.x, e.y));
            if (style == null) return;
            if (!(style instanceof FileWithLineRange)) return;
            ((FileWithLineRange) style).openEditor();
        }

        /**
         * @since 1.6.0
         */
        @Override
        public void mouseUp(MouseEvent e) {
        }

        /**
         * @since 1.6.0
         */
        @Override
        public void mouseMove(MouseEvent e) {
            StyleRange style = findStyle(new Point(e.x, e.y));
            if (style == null) {
                setCursor(arrowCursor);
                return;
            }

            if (style instanceof FileWithLineRange) {
                setCursor(handCursor);
                return;
            }

            setCursor(arrowCursor);
        }
    }

    /**
     * @since 1.6.0
     */
    private class StatusArea extends ActiveText implements MakeGoodStatusChangeListener {
        private static final String UIJOB_NAME = "MakeGood Status Update"; //$NON-NLS-1$
        private MakeGoodStatus status;

        public StatusArea(Composite parent, int style) {
            super(parent, style);
        }

        /**
         * @since 1.8.0
         */
        public void updateAdditionalInformation() {
            new UIJob(UIJOB_NAME) {
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    if (isDisposed()) return Status.OK_STATUS;;
                    setContentDescription(additionalInformation.toString());
                    return Status.OK_STATUS;
                }
            }.schedule();
        }

        public void isFailure(final String message) {
            new UIJob(UIJOB_NAME) {
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    if (isDisposed()) return Status.OK_STATUS;;
                    if (actionsInitialized) {
                        runAllTestsAction.setEnabled(false);
                        rerunTestAction.setEnabled(false);
                        rerunFailedTestsAction.setEnabled(false);
                    }
                    setForeground(new Color(statusArea.getDisplay(), MakeGoodColor.FAILED));
                    setText(message);
                    return Status.OK_STATUS;
                }
            }.schedule();
        }

        public void runningTest() {
            new UIJob(UIJOB_NAME) {
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    if (isDisposed()) return Status.OK_STATUS;;
                    if (actionsInitialized) {
                        runAllTestsAction.setEnabled(false);
                        rerunTestAction.setEnabled(false);
                        rerunFailedTestsAction.setEnabled(false);
                        stopTestAction.setEnabled(true);
                        moveToPreviousFailureAction.setEnabled(false);
                        moveToNextFailureAction.setEnabled(false);
                    }
                    setForeground(statusArea.getParent().getForeground());
                    setText(Messages.MakeGoodView_Status_RunningTest);
                    return Status.OK_STATUS;
                }
            }.schedule();
        }

        public void waitingForTestRun() {
            new UIJob(UIJOB_NAME) {
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    if (isDisposed()) return Status.OK_STATUS;;
                    if (actionsInitialized) {
                        runAllTestsAction.setEnabled(MakeGoodContext.getInstance().getActivePart().isAllTestsRunnable());
                        rerunTestAction.setEnabled(MakeGoodContext.getInstance().getTestRunner().hasLastTest());
                        rerunFailedTestsAction.setEnabled(MakeGoodContext.getInstance().getTestRunner().hasLastTest());
                        stopTestAction.setEnabled(false);
                    }
                    setForeground(statusArea.getParent().getForeground());
                    setText(Messages.MakeGoodView_Status_WaitingForTestRun);
                    return Status.OK_STATUS;
                }
            }.schedule();
        }

        @Override
        public void statusChanged(MakeGoodStatus status) {
            this.status = status;
            if (this.status.getProject() != null) {
                additionalInformation.setProject(this.status.getProject());
            }
            if (status == MakeGoodStatus.TestsNotFound) {
                additionalInformation.setMessage(Messages.MakeGoodView_Status_TestsNotFound);
            } else if (status == MakeGoodStatus.RelatedTestsNotFound) {
                additionalInformation.setMessage(Messages.MakeGoodView_Status_RelatedTestsNotFound);
            } else if (status == MakeGoodStatus.TypesNotFound) {
                additionalInformation.setMessage(Messages.MakeGoodView_Status_TypesNotFound);
            } else if (status == MakeGoodStatus.TestTargetNotFound) {
                additionalInformation.setMessage(Messages.MakeGoodView_Status_TestTargetNotFound);
            }
            updateAdditionalInformation();

            switch (status) {
            case NoProjectSelected:
                isFailure(Messages.MakeGoodView_Status_NoProjectSelected);
                break;
            case ProjectNotFound:
                isFailure(Messages.MakeGoodView_Status_ProjectNotFound);
                break;
            case ProjectNotOpen:
                isFailure(Messages.MakeGoodView_Status_ProjectNotOpen);
                break;
            case NoTestableProjectSelected:
                isFailure(Messages.MakeGoodView_Status_NoTestableProjectSelected);
                break;
            case NoPHPExecutablesDefined:
                isFailure(Messages.MakeGoodView_Status_NoPHPExecutablesDefined);
                break;
            case SAPINotCLI:
                isFailure(Messages.MakeGoodView_Status_SAPINotCLI);
                break;
            case MakeGoodNotConfigured:
                isFailure(Messages.MakeGoodView_Status_MakeGoodNotConfigured);
                break;
            case TestingFrameworkNotAvailable:
                isFailure(status.getReason() + " " + Messages.MakeGoodView_Status_TestingFrameworkNotAvailable); //$NON-NLS-1$
                break;
            case RunningTest:
                runningTest();
                break;
            case WaitingForTestRun:
                waitingForTestRun();
                break;
            }
        }

        @Override
        public void mouseDoubleClick(MouseEvent e) {
        }

        @Override
        public void mouseDown(MouseEvent e) {
            if (status == null) return;
            IProject project = status.getProject();
            if (project == null) return;
            if (!project.exists()) return;
            StyleRange style = findStyle(new Point(e.x, e.y));
            if (style == null) return;
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (window == null) return;
            IEclipsePreferences node = new ProjectScope(project).getNode("org.eclipse.php.debug.core.Debug_Process_Preferences"); //$NON-NLS-1$
            if (node == null) return;

            switch (status) {
            case NoPHPExecutablesDefined:
            case SAPINotCLI:
                if (!node.getBoolean("org.eclipse.php.debug.core.use-project-settings", false)) { //$NON-NLS-1$
                    PreferencesUtil.createPreferenceDialogOn(
                        window.getShell(),
                        "org.eclipse.php.debug.ui.preferences.PhpDebugPreferencePage", //$NON-NLS-1$
                        null,
                        null
                    ).open();
                } else {
                    PreferencesUtil.createPropertyDialogOn(
                        window.getShell(),
                        project,
                        "org.eclipse.php.debug.ui.property.PhpDebugPreferencePage", //$NON-NLS-1$
                        null,
                        null
                    ).open();
                }
                break;
            case MakeGoodNotConfigured:
            case TestingFrameworkNotAvailable:
                PreferencesUtil.createPropertyDialogOn(
                    window.getShell(),
                    project,
                    "com.piece_framework.makegood.ui.propertyPages.makeGood", //$NON-NLS-1$
                    null,
                    null
                ).open();
                break;
            }
        }

        @Override
        public void mouseUp(MouseEvent e) {
        }

        @Override
        public void mouseMove(MouseEvent e) {
            StyleRange style = findStyle(new Point(e.x, e.y));
            if (style == null) {
                setCursor(arrowCursor);
            } else {
                setCursor(handCursor);
            }
        }
    }

    private class ResultViewPartListener implements IPartListener2 {
        @Override
        public void partActivated(IWorkbenchPartReference partRef) {
            if (!VIEW_ID.equals(partRef.getId())) {
                IWorkbenchPart activePart = partRef.getPage().getActivePart();
                if (activePart != null) {
                    MakeGoodContext.getInstance().getActivePart().update(activePart);
                }

                return;
            }

            update();
        }

        @Override
        public void partBroughtToTop(IWorkbenchPartReference partRef) {}

        @Override
        public void partClosed(IWorkbenchPartReference partRef) {}

        @Override
        public void partDeactivated(IWorkbenchPartReference partRef) {}

        @Override
        public void partOpened(IWorkbenchPartReference partRef) {
            if (!VIEW_ID.equals(partRef.getId())) return;
            update();
        }

        @Override
        public void partHidden(IWorkbenchPartReference partRef) {}

        @Override
        public void partVisible(IWorkbenchPartReference partRef) {
            if (!VIEW_ID.equals(partRef.getId())) return;
            update();
        }

        @Override
        public void partInputChanged(IWorkbenchPartReference partRef) {}
    }

    private class ResultTreeContentProvider implements ITreeContentProvider {
        @Override
        public Object[] getChildren(Object parentElement) {
            List<Result> children = new ArrayList<Result>(((Result) parentElement).getChildren());
            Collections.reverse(children);
            return children.toArray(new Result[ children.size() ]);
        }

        @Override
        public Object getParent(Object element) {
            return ((Result) element).getParent();
        }

        @Override
        public boolean hasChildren(Object element) {
            if (!(element instanceof TestSuiteResult)) return false;
            return ((TestSuiteResult) element).hasChildren();
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    private class ResultTreeLabelProvider extends LabelProvider {
        private Image passIcon;
        private Image errorIcon;
        private Image failureIcon;
        private Image inProgressIcon;

        public ResultTreeLabelProvider() {
            super();

            passIcon = Activator.getImageDescriptor("icons/pass_white.gif").createImage(); //$NON-NLS-1$
            errorIcon = Activator.getImageDescriptor("icons/error_white.gif").createImage(); //$NON-NLS-1$
            failureIcon = Activator.getImageDescriptor("icons/failure_white.gif").createImage(); //$NON-NLS-1$
            inProgressIcon = Activator.getImageDescriptor("icons/in_progress.gif").createImage(); //$NON-NLS-1$
        }

        @Override
        public String getText(Object element) {
            Result result = (Result) element;
            return result.getName() + " (" +  //$NON-NLS-1$
            TimeFormatter.format(result.getTime()) +
            ")";  //$NON-NLS-1$
        }

        @Override
        public Image getImage(Object element) {
            Result result = (Result) element;
            if (result.isFixed()) {
                if (result.hasFailures()) {
                    return failureIcon;
                } else if (result.hasErrors()) {
                    return errorIcon;
                } else {
                    return passIcon;
                }
            } else {
                return inProgressIcon;
            }
        }
    }

    private static class TimeFormatter {
        private static String format(long nanoTime) {
            double timeForFormat = 0.0d;
            String unit = null;
            if (nanoTime >= 1000000000) {
                timeForFormat = nanoTime / 1000000000d;
                unit = "s"; //$NON-NLS-1$
            } else if (nanoTime < 1000000000 && nanoTime >= 1000){
                timeForFormat = nanoTime / 1000000d;
                unit = "ms";  //$NON-NLS-1$
            } else if (nanoTime > 0){
                return "< 0.001ms"; //$NON-NLS-1$
            } else {
                return "0.000ms"; //$NON-NLS-1$
            }
            return String.format("%.3f%s", timeForFormat, unit); //$NON-NLS-1$
        }
    }

    private class EditorOpenActiveTextListener extends ActiveTextListener {
        public EditorOpenActiveTextListener() {
            super(Pattern.compile("^((?:/|[A-Z]:).+):(\\d+)$", Pattern.MULTILINE));
        }

        @Override
        public void generateActiveText() {
            Matcher matcher = pattern.matcher(text.getText());

            while (matcher.find()) {
                FileWithLineRange style;
                IFile file =
                    ResourcesPlugin.getWorkspace()
                    .getRoot()
                    .getFileForLocation(new Path(matcher.group(1)));
                if (file != null) {
                    InternalFileWithLineRange iStyle = new InternalFileWithLineRange();
                    iStyle.file = file;
                    iStyle.foreground = new Color(text.getDisplay(), MakeGoodColor.LINK_INTERNAL);
                    style = (FileWithLineRange) iStyle;
                } else {
                    ExternalFileWithLineRange eStyle = new ExternalFileWithLineRange();
                    eStyle.fileStore =
                        EFS.getLocalFileSystem().getStore(new Path(matcher.group(1)));
                    eStyle.foreground = new Color(text.getDisplay(), MakeGoodColor.LINK_EXTERNAL);
                    style = (FileWithLineRange) eStyle;
                }

                style.start = matcher.start();
                style.length = matcher.group().length();
                style.line = Integer.valueOf(matcher.group(2));

                this.text.addStyle(style);
            }
        }
    }

    /**
     * @since 1.6.0
     */
    private class PreferencesOpenActiveTextListener extends ActiveTextListener {
        public PreferencesOpenActiveTextListener() {
            super(Pattern.compile("(?:<a>)(.+?)(?:</a>)")); //$NON-NLS-1$
        }

        @Override
        public void generateActiveText() {
            Matcher matcher = pattern.matcher(text.getText());

            while (matcher.find()) {
                StyleRange style = new StyleRange();
                style.underline = true;
                style.start = matcher.start();
                style.length = matcher.group(1).length();
                text.replaceText(matcher.replaceFirst(matcher.group(1)));
                text.addStyle(style);
            }
        }
    }

    /**
     * @since 1.8.0
     */
    private class AdditionalInformation {
        private IProject project;
        private String message;

        public void setProject(IProject project) {
            if (this.project != null && this.project != project) {
                clearMessage();
            }
            this.project = project;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void clearMessage() {
            setMessage(null);
        }

        @Override
        public String toString() {
            StringBuilder information = new StringBuilder();

            if (project != null) {
                information.append("[" + project.getName() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }

            if (message != null && message.length() > 0) {
                information.append(" "); //$NON-NLS-1$
                information.append(message);
            }

            return information.toString();
        }
    }
}
