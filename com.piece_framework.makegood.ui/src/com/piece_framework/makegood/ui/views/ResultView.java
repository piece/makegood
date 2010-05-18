/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.views;

import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import com.piece_framework.makegood.core.MakeGoodCorePlugin;
import com.piece_framework.makegood.core.preference.MakeGoodPreferenceInitializer;
import com.piece_framework.makegood.core.result.Result;
import com.piece_framework.makegood.core.result.TestCaseResult;
import com.piece_framework.makegood.core.result.TestSuiteResult;
import com.piece_framework.makegood.launch.RuntimeConfiguration;
import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.Messages;
import com.piece_framework.makegood.ui.actions.RerunTestAction;
import com.piece_framework.makegood.ui.actions.RunAllTestsAction;
import com.piece_framework.makegood.ui.actions.RunAllTestsWhenFileIsSavedAction;
import com.piece_framework.makegood.ui.actions.StopTestAction;
import com.piece_framework.makegood.ui.ide.EditorOpen;

public class ResultView extends ViewPart {
    public static final String ID = "com.piece_framework.makegood.ui.views.resultView"; //$NON-NLS-1$
    private static final String CONTEXT_ID = "com.piece_framework.makegood.ui.contexts.resultView"; //$NON-NLS-1$

    private RunProgressBar progressBar;
    private CLabel testCount;
    private ResultLabel passCount;
    private ResultLabel failureCount;
    private ResultLabel errorCount;
    private TreeViewer resultTreeViewer;
    private Label progressRate;
    private Label processTimeAverage;
    private ShowTimer showTimer;
    private IAction stopAction;
    private IAction rerunAction;
    private IAction runAllTestsAction;
    private FailureTrace failureTrace;
    private boolean isRunning;
    private boolean enableRunAllTestsAction;
    private Label elapsedTime;
    private Label processTime;

    private ViewerFilter failureFilter = new ViewerFilter() {
        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (!(element instanceof Result)) return false;
            Result result = (Result) element;
            return result.hasFailures() || result.hasErrors();
        }
    };

    @Override
    public IViewSite getViewSite() {
        IViewSite site = super.getViewSite();

        // There is no hook point for disabling the actions...
        if (stopAction == null) {
            initializeActions(site);
        }

        return site;
    }

    @Override
    public void createPartControl(final Composite parent) {
        IContextService service = (IContextService) getSite().getService(IContextService.class);
        service.activateContext(CONTEXT_ID); //$NON-NLS-1$

        parent.setLayout(new GridLayout(1, false));

        Composite row1 = new Composite(parent, SWT.NONE);
        row1.setLayoutData(createHorizontalFillGridData());
        row1.setLayout(new GridLayout(2, true));

        Composite progress = new Composite(row1, SWT.NONE);
        progress.setLayoutData(createHorizontalFillGridData());
        progress.setLayout(new GridLayout(2, false));
        progressRate = new Label(progress, SWT.LEFT);
        progressBar = new RunProgressBar(progress);
        progressBar.setLayoutData(createHorizontalFillGridData());
        Composite clock = new Composite(row1, SWT.NONE);
        clock.setLayoutData(createHorizontalFillGridData());
        clock.setLayout(new FillLayout(SWT.HORIZONTAL));
        processTimeAverage = new Label(clock, SWT.LEFT);
        elapsedTime = new Label(clock, SWT.LEFT);
        processTime = new Label(clock, SWT.LEFT);

        Composite row2 = new Composite(parent, SWT.NONE);
        row2.setLayoutData(createHorizontalFillGridData());
        row2.setLayout(new GridLayout(2, true));

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

        new ResultLabel(
            row2,
            Messages.TestResultView_failureTraceLabel,
            Activator.getImageDescriptor("icons/failure-trace.gif").createImage() //$NON-NLS-1$
        );

        Composite row3 = new Composite(parent, SWT.NONE);
        row3.setLayoutData(createBothFillGridData());
        row3.setLayout(new GridLayout(2, true));

        Tree resultTree = new Tree(row3, SWT.BORDER);
        resultTree.setLayoutData(createBothFillGridData());
        resultTreeViewer = new TreeViewer(resultTree);
        resultTreeViewer.setContentProvider(new ResultContentProvider());
        resultTreeViewer.setLabelProvider(new ResultLabelProvider());
        resultTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                failureTrace.clearText();
                if (!(event.getSelection() instanceof IStructuredSelection)) return;
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                Object element = selection.getFirstElement();
                if (!(element instanceof TestCaseResult)) return;
                TestCaseResult testCase = (TestCaseResult) element;
                if (!testCase.hasFailures() && !testCase.hasErrors()) return;
                failureTrace.setText(testCase.getFailureTrace());
            }
        });
        resultTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                Object element = selection.getFirstElement();
                if (element instanceof TestCaseResult) {
                    TestCaseResult testCase = (TestCaseResult) element;
                    String fileName = testCase.getFile();
                    if (fileName == null) return;
                    IFile file =
                        ResourcesPlugin.getWorkspace()
                                       .getRoot()
                                       .getFileForLocation(new Path(fileName));
                    if (file != null) {
                        EditorOpen.open(file, testCase.getLine());
                    } else {
                        EditorOpen.open(
                            EFS.getLocalFileSystem().getStore(new Path(fileName)),
                            testCase.getLine()
                        );
                    }
                } else if (element instanceof TestSuiteResult) {
                    TestSuiteResult suite= (TestSuiteResult) element;
                    String fileName = suite.getFile();
                    if (fileName == null) return;
                    IFile file =
                        ResourcesPlugin.getWorkspace()
                                       .getRoot()
                                       .getFileForLocation(new Path(fileName));
                    if (file != null) {
                        EditorOpen.open(file);
                    } else {
                        EditorOpen.open(
                            EFS.getLocalFileSystem().getStore(new Path(fileName))
                        );
                    }
                }
            }
        });

        failureTrace = createFailureTrace(row3);

        reset();
    }

    @Override
    public void setFocus() {}

    public void reset() {
        progressRate.setText("  0" + Messages.TestResultView_percent + "  "); //$NON-NLS-1$ //$NON-NLS-2$
        progressBar.reset();
        processTimeAverage.setText(
            TimeFormatter.format(
                0,
                Messages.TestResultView_second,
                Messages.TestResultView_millisecond
            ) +
            "/" + //$NON-NLS-1$
            Messages.TestResultView_averageTest
        );
        elapsedTime.setText(
            Messages.TestResultView_realTime +
            ": " +  //$NON-NLS-1$
            TimeFormatter.format(
                0,
                Messages.TestResultView_second,
                Messages.TestResultView_millisecond
            )
        );
        processTime.setText(
            Messages.TestResultView_testTime +
            ": " +  //$NON-NLS-1$
            TimeFormatter.format(
                0,
                Messages.TestResultView_second,
                Messages.TestResultView_millisecond
            )
        );
        testCount.setText(Messages.TestResultView_testsLabel + ": 0/0"); //$NON-NLS-1$
        passCount.reset();
        failureCount.reset();
        errorCount.reset();
        resultTreeViewer.setInput(null);
    }

    public void setEnabledRunAllTestsAction(boolean enabled) {
        if (runAllTestsAction != null && !isRunning) {
            runAllTestsAction.setEnabled(enabled);
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
        FailureTrace failureTrace = new FailureTrace(parent);
        failureTrace.addListener(
            new EditorOpenActiveTextListener(
                Pattern.compile("^(.+):(\\d+)$", Pattern.MULTILINE) //$NON-NLS-1$
            )
        );

        return failureTrace;
    }

    public void nextResult() {
        IStructuredSelection selection = (IStructuredSelection) resultTreeViewer.getSelection();
        Result selected = (Result) selection.getFirstElement();

        java.util.List<Result> results = (java.util.List<Result>) resultTreeViewer.getInput();
        if (results == null || results.size() == 0) return;
        if (selected == null) selected = results.get(0);

        FailureFilter search = new FailureFilter(results, selected);
        Result next = search.getNextFailure();
        if (next != null) {
            resultTreeViewer.expandAll();
            resultTreeViewer.setSelection(new StructuredSelection(next), true);
        }
    }

    public void previousResult() {
        IStructuredSelection selection = (IStructuredSelection) resultTreeViewer.getSelection();
        Result selected = (Result) selection.getFirstElement();

        java.util.List<Result> results = (java.util.List<Result>) resultTreeViewer.getInput();
        if (results == null || results.size() == 0) return;
        if (selected == null) selected = results.get(0);

        FailureFilter search = new FailureFilter(results, selected);
        Result previous = search.getPreviousFailure();
        if (previous != null) {
            resultTreeViewer.expandAll();
            resultTreeViewer.setSelection(new StructuredSelection(previous), true);
        }
    }

    public void filterResult(boolean filterOn) {
        if (filterOn) {
            resultTreeViewer.addFilter(failureFilter);
        } else {
            resultTreeViewer.removeFilter(failureFilter);
        }
    }

    public void setTreeInput(java.util.List<Result> suites) {
        resultTreeViewer.setInput(suites);
    }

    public boolean isSetTreeInput() {
        return resultTreeViewer.getInput() != null;
    }

    public void refresh(RunProgress progress, TestCaseResult currentTestCase) {
        progressRate.setText(
            String.format("%3d", progress.calculateRate()) + //$NON-NLS-1$
            Messages.TestResultView_percent +
            "  " //$NON-NLS-1$
        );

        if (progress.hasFailures()) progressBar.red();
        progressBar.update(progress.calculateRate());

        processTimeAverage.setText(
            TimeFormatter.format(
                progress.calculateProcessTimeAverage(),
                Messages.TestResultView_second,
                Messages.TestResultView_millisecond
            ) +
            "/" + //$NON-NLS-1$
            Messages.TestResultView_averageTest
        );
        processTimeAverage.getParent().layout();

        showTimer.show();

        testCount.setText(
            Messages.TestResultView_testsLabel +
            ": " + //$NON-NLS-1$
            progress.getTestCount() +
            "/" + //$NON-NLS-1$
            progress.getAllTestCount()
        );
        passCount.setCount(progress.getPassCount());
        failureCount.setCount(progress.getFailureCount());
        errorCount.setCount(progress.getErrorCount());

        if (currentTestCase != null) {
            resultTreeViewer.expandAll();
            resultTreeViewer.setSelection(new StructuredSelection(currentTestCase));
        }
        resultTreeViewer.refresh();
    }

    public void start(RunProgress progress) {
        showTimer = new ShowTimer(elapsedTime, processTime, progress, 200);
        showTimer.start();

        stopAction.setEnabled(true);
        rerunAction.setEnabled(false);
        enableRunAllTestsAction = runAllTestsAction.isEnabled();
        runAllTestsAction.setEnabled(false);

        isRunning = true;
    }

    public void stop() {
        showTimer.stop();

        stopAction.setEnabled(false);
        rerunAction.setEnabled(true);
        runAllTestsAction.setEnabled(enableRunAllTestsAction);

        isRunning = false;
    }

    private void initializeActions(IViewSite site) {
        IToolBarManager manager = site.getActionBars().getToolBarManager();

        ActionContributionItem stopItem =
            (ActionContributionItem) manager.find(StopTestAction.ID);
        if (stopItem != null) {
            stopAction = stopItem.getAction();
            stopAction.setEnabled(false);
        }

        ActionContributionItem rerunItem =
            (ActionContributionItem) manager.find(RerunTestAction.ID);
        if (rerunItem != null) {
            rerunAction = rerunItem.getAction();
            rerunAction.setEnabled(false);
        }

        ActionContributionItem runAllTestsItem =
            (ActionContributionItem) manager.find(RunAllTestsAction.ID);
        if (runAllTestsItem != null) {
            runAllTestsAction = runAllTestsItem.getAction();
            runAllTestsAction.setEnabled(false);
        }

        ActionContributionItem runAllTestsWhenFileIsSavedItem =
            (ActionContributionItem) manager.find(RunAllTestsWhenFileIsSavedAction.ID);
        if (runAllTestsWhenFileIsSavedItem != null) {
            IAction runAllTestsWhenFileIsSavedAction = runAllTestsWhenFileIsSavedItem.getAction();
            runAllTestsWhenFileIsSavedAction.setChecked(
                RuntimeConfiguration.getInstance().runsAllTestsWhenFileIsSaved
            );
        }
    }

    private class ResultLabel {
        private CLabel label;
        private String text;

        private ResultLabel(Composite parent, String text, Image icon) {
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

    private class RunProgressBar extends Composite {
        private final RGB GREEN = new RGB(95, 191, 95);
        private final RGB RED = new RGB(159, 63, 63);
        private final RGB NONE = new RGB(255, 255, 255);

        private Label bar;
        private int rate;

        private RunProgressBar(Composite parent) {
            super(parent, SWT.BORDER);
            GridLayout layout = new GridLayout();
            layout.marginTop = 0;
            layout.marginBottom = 0;
            layout.marginLeft = 0;
            layout.marginRight = 0;
            layout.marginHeight = 0;
            layout.marginWidth = 0;
            layout.verticalSpacing = 0;
            layout.horizontalSpacing = 0;
            setLayout(layout);

            setBackground(new Color(parent.getDisplay(), NONE));

            bar = new Label(this, SWT.NONE);
            bar.setLayoutData(new GridData());

            ControlAdapter listener = new ControlAdapter() {
                @Override
                public void controlResized(ControlEvent e) {
                    update(rate);
                }
            };
            bar.addControlListener(listener);
            addControlListener(listener);

            reset();
        }

        private void update(int rate) {
            int maxWidth = getSize().x;

            int width = bar.getSize().x;
            if (rate < 100) {
                width = (int) (maxWidth * ((double) rate / 100d));
            } else if (rate >= 100) {
                width = maxWidth;
            }
            final int barWidth = width;

            getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    Point size = bar.getSize();
                    size.x = barWidth;
                    bar.setSize(size);
                }
            });

            this.rate = rate;
        }

        private void red() {
            bar.setBackground(new Color(getDisplay(), RED));
        }

        private void reset() {
            update(0);

            bar.setBackground(new Color(getDisplay(), GREEN));
        }
    }

    private class ShowTimer implements Runnable {
        private Label elapsedTime;
        private Label processTime;
        private RunProgress progress;
        private int delay;
        private long startTime;
        private boolean stop;

        private ShowTimer(
            Label elapsedTime,
            Label processTime,
            RunProgress progress,
            int delay) {
            this.elapsedTime = elapsedTime;
            this.processTime = processTime;
            this.progress = progress;
            this.delay = delay;
        }

        private void start() {
            startTime = System.nanoTime();
            schedule();
        }

        private void stop() {
            stop = true;
        }

        private void schedule() {
            elapsedTime.getDisplay().timerExec(delay, this);
        }

        private void show() {
            elapsedTime.setText(
                Messages.TestResultView_realTime +
                ": " + //$NON-NLS-1$
                TimeFormatter.format(
                    System.nanoTime() - startTime,
                    Messages.TestResultView_second,
                    Messages.TestResultView_millisecond
                )
            );
            processTime.setText(
                Messages.TestResultView_testTime +
                ": " + //$NON-NLS-1$
                TimeFormatter.format(
                    progress.getProcessTime(),
                    Messages.TestResultView_second,
                    Messages.TestResultView_millisecond
                )
            );
        }

        @Override
        public void run() {
            show();
            if (!stop) schedule();
        }
    }

    private class FailureTrace extends ActiveText {
        public FailureTrace(Composite parent) {
            super(parent);
        }

        public void clearText() {
            setText(""); //$NON-NLS-1$
            hideScrollBar();
        }
    }

    private class FailureFilter {
        private List<Result> results;
        private Result selected;
        private Result findSelected;
        private TestCaseResult lastFailure;

        public FailureFilter(List<Result> results, Result selected) {
            this.results = results;
            this.selected = selected;
        }

        public TestCaseResult getNextFailure() {
            findSelected = null;
            return getNextFailure(results);
        }

        public TestCaseResult getPreviousFailure() {
            findSelected = null;
            lastFailure = null;
            return getPreviousFailure(results);
        }

        private TestCaseResult getNextFailure(List<Result> targets) {
            for (Result result: targets) {
                if (findSelected == null) {
                    if (result == selected) {
                        findSelected = result;
                    }
                } else {
                    if (result instanceof TestCaseResult
                        && (result.hasErrors() || result.hasFailures())
                        ) {
                        return (TestCaseResult) result;
                    }
                }

                if (result instanceof TestSuiteResult) {
                    TestCaseResult testCase = getNextFailure(result.getChildren());
                    if (testCase != null) {
                        return testCase;
                    }
                }
            }
            return null;
        }

        private TestCaseResult getPreviousFailure(List<Result> targets) {
            for (Result result: targets) {
                if (findSelected == null) {
                    if (result == selected) {
                        return lastFailure;
                    } else {
                        if (result instanceof TestCaseResult
                            && (result.hasErrors() || result.hasFailures())
                            ) {
                            lastFailure = (TestCaseResult) result;
                        }
                    }
                }

                if (result instanceof TestSuiteResult) {
                    TestCaseResult testCase = getPreviousFailure(result.getChildren());
                    if (testCase != null) {
                        return testCase;
                    }
                }
            }
            return null;
        }
    }
}
