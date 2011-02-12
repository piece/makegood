/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import com.piece_framework.makegood.core.result.Result;
import com.piece_framework.makegood.core.result.TestCaseResult;
import com.piece_framework.makegood.core.result.TestSuiteResult;
import com.piece_framework.makegood.core.run.Failures;
import com.piece_framework.makegood.core.run.Progress;
import com.piece_framework.makegood.launch.MakeGoodLaunch;
import com.piece_framework.makegood.launch.RuntimeConfiguration;
import com.piece_framework.makegood.launch.TestLifecycle;
import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.Messages;
import com.piece_framework.makegood.ui.actions.DebugTestAction;
import com.piece_framework.makegood.ui.actions.RerunTestAction;
import com.piece_framework.makegood.ui.actions.RunAllTestsAction;
import com.piece_framework.makegood.ui.actions.RunAllTestsWhenFileIsSavedAction;
import com.piece_framework.makegood.ui.actions.ShowFailuresOnlyAction;
import com.piece_framework.makegood.ui.actions.StopOnFailureAction;
import com.piece_framework.makegood.ui.actions.StopTestAction;
import com.piece_framework.makegood.ui.launch.TestRunner;

public class ResultView extends ViewPart {
    public static final String ID = "com.piece_framework.makegood.ui.views.resultView"; //$NON-NLS-1$
    private static final String CONTEXT_ID = "com.piece_framework.makegood.ui.contexts.resultView"; //$NON-NLS-1$
    private static final Pattern STACK_TRACE_LINE_PATTERN = Pattern.compile("^((?:/|[A-Z]:).+):(\\d+)$", Pattern.MULTILINE); //$NON-NLS-1$

    private RunProgressBar progressBar;
    private CLabel testCount;
    private ResultLabel passCount;
    private ResultLabel failureCount;
    private ResultLabel errorCount;
    private TreeViewer resultTreeViewer;
    private Label processTimeAverage;
    private ElapsedTimer elapsedTimer;
    private IAction stopTestAction;
    private IAction rerunTestAction;
    private IAction runAllTestsAction;
    private FailureTrace failureTrace;
    private Label elapsedTime;
    private Label processTime;
    private boolean actionsInitialized = false;
    private Failures failures;
    private Progress progress;
    private ResultViewPartListener partListenr = new ResultViewPartListener();
    private EditorOpener editorOpener = new EditorOpener();

    private ViewerFilter failureViewFilter = new ViewerFilter() {
        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (!(element instanceof Result)) return false;
            Result result = (Result) element;
            return result.hasFailures() || result.hasErrors();
        }
    };

    @Override
    public void createPartControl(final Composite parent) {
        IContextService service = (IContextService) getSite().getService(IContextService.class);
        service.activateContext(CONTEXT_ID);

        parent.setLayout(adjustLayout(new GridLayout(1, false)));

        Composite row1 = new Composite(parent, SWT.NONE);
        row1.setLayoutData(createHorizontalFillGridData());
        row1.setLayout(adjustLayout(new GridLayout(2, true)));

        Composite progress = new Composite(row1, SWT.NONE);
        progress.setLayoutData(createHorizontalFillGridData());
        progress.setLayout(adjustLayout(new GridLayout(2, false)));
        Composite progressBarBorder = new Composite(progress, SWT.NONE);
        progressBarBorder.setLayoutData(createHorizontalFillGridData());
        GridLayout progressBarBorderLayout = new GridLayout();
        progressBarBorderLayout.marginWidth = 2;
        progressBarBorderLayout.marginHeight = 2;
        progressBarBorderLayout.horizontalSpacing = 2;
        progressBarBorderLayout.verticalSpacing = 2;
        progressBarBorder.setLayout(progressBarBorderLayout);
        progressBar = new RunProgressBar(progressBarBorder);
        progressBar.setLayoutData(createHorizontalFillGridData());
        progressBar.setLayout(adjustLayout(new GridLayout()));
        Composite clock = new Composite(row1, SWT.NONE);
        clock.setLayoutData(createHorizontalFillGridData());
        clock.setLayout(new FillLayout(SWT.HORIZONTAL));
        processTimeAverage = new Label(clock, SWT.LEFT);
        processTime = new Label(clock, SWT.LEFT);
        elapsedTime = new Label(clock, SWT.LEFT);

        Composite row2 = new Composite(parent, SWT.NONE);
        row2.setLayoutData(createHorizontalFillGridData());
        row2.setLayout(adjustLayout(new GridLayout(2, true)));

        Composite counter = new Composite(row2, SWT.NONE);
        counter.setLayoutData(createHorizontalFillGridData());
        counter.setLayout(new FillLayout(SWT.HORIZONTAL));
        testCount = new CLabel(counter, SWT.LEFT);
        passCount = new ResultLabel(
                     counter,
                     Messages.TestResultView_passesLabel,
                     Activator.getImageDescriptor("icons/pass-gray.gif").createImage() //$NON-NLS-1$
                 );
        failureCount = new ResultLabel(
                       counter,
                       Messages.TestResultView_failuresLabel,
                       Activator.getImageDescriptor("icons/failure-gray.gif").createImage() //$NON-NLS-1$
                   );
        errorCount = new ResultLabel(
                     counter,
                     Messages.TestResultView_errorsLabel,
                     Activator.getImageDescriptor("icons/error-gray.gif").createImage() //$NON-NLS-1$
                 );

        SashForm row3 = new SashForm(parent, SWT.NONE);
        row3.setLayoutData(createBothFillGridData());
        row3.setLayout(adjustLayout(new GridLayout(2, true)));

        Tree resultTree = new Tree(row3, SWT.BORDER);
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

        Composite row3Right = new Composite(row3, SWT.NONE);
        row3Right.setLayoutData(createHorizontalFillGridData());
        row3Right.setLayout(adjustLayout(new GridLayout(1, true)));
        new ResultLabel(
            row3Right,
            Messages.TestResultView_failureTraceLabel,
            Activator.getImageDescriptor("icons/failure-trace.gif").createImage() //$NON-NLS-1$
        );
        failureTrace = createFailureTrace(row3Right);

        IViewSite site = getViewSite();
        site.getPage().addPartListener(partListenr);
        initializeActions(site);

        reset();
    }

    @Override
    public void setFocus() {}

    public void reset() {
        progressBar.reset();
        processTimeAverage.setText(
            TimeFormatter.format(0)
            + "/" + //$NON-NLS-1$
            Messages.TestResultView_averageTest
        );
        elapsedTime.setText(
            Messages.TestResultView_realTime +
            ": " +  //$NON-NLS-1$
            TimeFormatter.format(0)
        );
        processTime.setText(
            Messages.TestResultView_testTime +
            ": " +  //$NON-NLS-1$
            TimeFormatter.format(0)
        );
        testCount.setText(Messages.TestResultView_testsLabel + ": 0/0"); //$NON-NLS-1$
        passCount.reset();
        failureCount.reset();
        errorCount.reset();
        resultTreeViewer.setInput(null);
    }

    public void updateStateOfRunAllTestsAction() {
        if (!actionsInitialized) return;
        if (TestLifecycle.isRunning()) return;

        runAllTestsAction.setEnabled(ActivePart.getInstance().isAllTestsRunnable());

        IProject activeProject = ActivePart.getInstance().getProject();
        if (activeProject != null) {
            setContentDescription(activeProject.getName());
        }
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

    private FailureTrace createFailureTrace(Composite parent) {
        FailureTrace failureTrace =
            new FailureTrace(parent, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        failureTrace.setLayoutData(new GridData(GridData.FILL_BOTH));
        failureTrace.setEditable(false);
        failureTrace.addListener(new EditorOpenActiveTextListener(STACK_TRACE_LINE_PATTERN));
        return failureTrace;
    }

    public void moveToNextFailure() {
        moveToPreviousOrNextFailure(Failures.FIND_NEXT);
    }

    public void moveToPreviousFailure() {
        moveToPreviousOrNextFailure(Failures.FIND_PREVIOUS);
    }

    @Override
    public void dispose() {
        IViewSite site = getViewSite();
        if (site != null) {
            site.getPage().removePartListener(partListenr);
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

    void setTreeInput(TestSuiteResult result) {
        resultTreeViewer.setInput(result);
    }

    void updateOnEndTestCase(TestCaseResult currentTestCase) {
        updateResult();
    }

    void updateOnStartTestCase(TestCaseResult currentTestCase) {
        updateTestCount();

        resultTreeViewer.refresh();
        resultTreeViewer.expandAll();
    }

    void startTest(Progress progress, Failures failures) {
        elapsedTimer = new ElapsedTimer(200);
        elapsedTimer.schedule();

        this.progress = progress;
        this.failures = failures;

        stopTestAction.setEnabled(true);
        rerunTestAction.setEnabled(false);
        runAllTestsAction.setEnabled(false);
    }

    void endTest() {
        if (progress.isCompleted() && progress.getAllTestCount() == 0) {
            setContentDescription(Messages.TestResultView_noTestsFound);
        }

        TreeItem topItem = resultTreeViewer.getTree().getTopItem();
        if (topItem != null) {
            Result topResult = (Result) topItem.getData();
            if (topResult != null) {
                resultTreeViewer.setSelection(new StructuredSelection(topResult));
            }
        }

        stopTestAction.setEnabled(false);
        rerunTestAction.setEnabled(TestRunner.hasLastTest());
        ActivePart.getInstance().setPart();
        runAllTestsAction.setEnabled(ActivePart.getInstance().isAllTestsRunnable());
    }

    /**
     * @since 1.3.0
     */
    void collapseResultTree() {
        resultTreeViewer.collapseAll();
    }

    void printCurrentlyRunningTestCase(TestCaseResult currentTestCase) {
        Assert.isNotNull(currentTestCase, "The given test case should not be null."); //$NON-NLS-1$

        String className = currentTestCase.getClassName();
        if (className != null) {
            setContentDescription(
                currentTestCase.getClassName() +
                " - " + //$NON-NLS-1$
                currentTestCase.getName()
            );
        } else {
            setContentDescription(currentTestCase.getName());
        }
    }

    void markAsStopped() {
        progressBar.markAsStopped();
    }

    void markAsFailed() {
        progressBar.markAsFailed();
    }

    private void initializeActions(IViewSite site) {
        IToolBarManager manager = site.getActionBars().getToolBarManager();

        ActionContributionItem showFailuresOnlyItem =
            (ActionContributionItem) manager.find(ShowFailuresOnlyAction.ID);
        if (showFailuresOnlyItem != null) {
            showFailuresOnlyItem.getAction().setChecked(
                RuntimeConfiguration.getInstance().showsFailuresOnly
            );
            actionsInitialized = true;
        }

        ActionContributionItem stopOnFailureItem =
            (ActionContributionItem) manager.find(StopOnFailureAction.ID);
        if (stopOnFailureItem != null) {
            stopOnFailureItem.getAction().setChecked(
                RuntimeConfiguration.getInstance().stopsOnFailure
            );
        }

        ActionContributionItem debugTestItem =
            (ActionContributionItem) manager.find(DebugTestAction.ID);
        if (debugTestItem != null) {
            debugTestItem.getAction().setChecked(
                RuntimeConfiguration.getInstance().debugsTest
            );
        }

        ActionContributionItem runAllTestsWhenFileIsSavedItem =
            (ActionContributionItem) manager.find(RunAllTestsWhenFileIsSavedAction.ID);
        if (runAllTestsWhenFileIsSavedItem != null) {
            runAllTestsWhenFileIsSavedItem.getAction().setChecked(
                RuntimeConfiguration.getInstance().runsAllTestsWhenFileIsSaved
            );
        }

        ActionContributionItem stopTestItem =
            (ActionContributionItem) manager.find(StopTestAction.ID);
        if (stopTestItem != null) {
            stopTestAction = stopTestItem.getAction();
            stopTestAction.setEnabled(MakeGoodLaunch.hasActiveLaunch());
        }

        ActionContributionItem rerunTestItem =
            (ActionContributionItem) manager.find(RerunTestAction.ID);
        if (rerunTestItem != null) {
            rerunTestAction = rerunTestItem.getAction();
            rerunTestAction.setEnabled(TestRunner.hasLastTest());
        }

        ActionContributionItem runAllTestsItem =
            (ActionContributionItem) manager.find(RunAllTestsAction.ID);
        if (runAllTestsItem != null) {
            runAllTestsAction = runAllTestsItem.getAction();
            runAllTestsAction.setEnabled(ActivePart.getInstance().isAllTestsRunnable());
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

        TestCaseResult previousOrNextResult = failures.find(selectedResult, direction);
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
        progressBar.update(progress.calculateRate());

        processTimeAverage.setText(
            TimeFormatter.format(progress.calculateProcessTimeAverage()) +
            "/" + //$NON-NLS-1$
            Messages.TestResultView_averageTest
        );
        processTimeAverage.getParent().layout();

        processTime.setText(
            Messages.TestResultView_testTime +
            ": " + //$NON-NLS-1$
            TimeFormatter.format(progress.getProcessTime())
        );

        passCount.setCount(progress.getPassCount());
        failureCount.setCount(progress.getFailureCount());
        errorCount.setCount(progress.getErrorCount());

        resultTreeViewer.refresh();
    }

    private void updateElapsedTime() {
        elapsedTime.setText(
            Messages.TestResultView_realTime +
            ": " + //$NON-NLS-1$
            TimeFormatter.format(progress.getElapsedTime())
        );
    }

    private void updateTestCount() {
        testCount.setText(
            Messages.TestResultView_testsLabel +
            ": " + //$NON-NLS-1$
            (progress.isRunning() ? progress.getTestCount() + 1 : progress.getTestCount()) +
            "/" + //$NON-NLS-1$
            progress.getAllTestCount()
        );
    }

    private void update() {
        IViewSite site = super.getViewSite();
        if (site == null) return;

        initializeActions(site);

        if (progress != null) {
            updateResult();
            updateElapsedTime();
            updateTestCount();
        }
    }

    private class ResultLabel {
        private CLabel label;
        private String text;

        public ResultLabel(Composite parent, String text, Image icon) {
            label = new CLabel(parent, SWT.LEFT);
            label.setText(text);
            if (icon != null) label.setImage(icon);
            this.text = text;
        }

        private void setCount(int count) {
            label.setText(text + ": " + count); //$NON-NLS-1$
        }

        private void reset() {
            setCount(0);
        }
    }

    private class ElapsedTimer implements Runnable {
        private int delay;

        public ElapsedTimer(int delay) {
            this.delay = delay;
        }

        private void schedule() {
            elapsedTime.getDisplay().timerExec(delay, this);
        }

        private void update() {
            updateElapsedTime();
        }

        @Override
        public void run() {
            update();
            if (progress.isRunning()) {
                schedule();
            }
        }
    }

    private class FailureTrace extends ActiveText {
        public FailureTrace(Composite parent, int style) {
            super(parent, style);
        }

        public void clearText() {
            setText(""); //$NON-NLS-1$
            hideScrollBar();
        }

        /**
         * @since 1.3.0
         */
        @Override
        public void setText(String text) {
            super.setText(text.trim());
        }
    }

    private class ResultViewPartListener implements IPartListener2 {
        @Override
        public void partActivated(IWorkbenchPartReference partRef) {
            if (!ID.equals(partRef.getId())) {
                IWorkbenchPart activePart = partRef.getPage().getActivePart();
                if (activePart != null) {
                    ActivePart.getInstance().setPart(activePart);
                    updateStateOfRunAllTestsAction();
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
            if (!ID.equals(partRef.getId())) return;
            update();
        }

        @Override
        public void partHidden(IWorkbenchPartReference partRef) {}

        @Override
        public void partVisible(IWorkbenchPartReference partRef) {
            if (!ID.equals(partRef.getId())) return;
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
            return !((TestSuiteResult) element).getChildren().isEmpty();
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

            passIcon = Activator.getImageDescriptor("icons/pass-white.gif").createImage(); //$NON-NLS-1$
            errorIcon = Activator.getImageDescriptor("icons/error-white.gif").createImage(); //$NON-NLS-1$
            failureIcon = Activator.getImageDescriptor("icons/failure-white.gif").createImage(); //$NON-NLS-1$
            inProgressIcon = Activator.getImageDescriptor("icons/inProgress.gif").createImage(); //$NON-NLS-1$
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
            if (!result.isFixed()) {
                if (result instanceof TestCaseResult) {
                    return inProgressIcon;
                } else {
                    return null;
                }
            }

            if (result.hasFailures()) {
                return failureIcon;
            } else if (result.hasErrors()) {
                return errorIcon;
            } else {
                return passIcon;
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
        public EditorOpenActiveTextListener(Pattern pattern) {
            super(pattern);
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
                    iStyle.foreground = new Color(text.getDisplay(), 0, 51, 153);
                    style = (FileWithLineRange) iStyle;
                } else {
                    ExternalFileWithLineRange eStyle = new ExternalFileWithLineRange();
                    eStyle.fileStore =
                        EFS.getLocalFileSystem().getStore(new Path(matcher.group(1)));
                    eStyle.foreground = new Color(text.getDisplay(), 114, 159, 207);
                    style = (FileWithLineRange) eStyle;
                }

                style.start = matcher.start();
                style.length = matcher.group().length();
                style.line = Integer.valueOf(matcher.group(2));

                this.text.addStyle(style);
            }
        }
    }
}
