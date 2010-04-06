package com.piece_framework.makegood.ui.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
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
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import java.util.List;
import java.util.regex.Pattern;

import com.piece_framework.makegood.core.runner.ErrorTestCaseResult;
import com.piece_framework.makegood.core.runner.FailureTestCaseResult;
import com.piece_framework.makegood.core.runner.RunProgress;
import com.piece_framework.makegood.core.runner.TestCaseResult;
import com.piece_framework.makegood.core.runner.Result;
import com.piece_framework.makegood.core.runner.TestSuiteResult;
import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.Messages;
import com.piece_framework.makegood.ui.ide.EditorOpen;
import com.piece_framework.makegood.ui.ide.FileFind;
import com.piece_framework.makegood.ui.swt.LinkedText;

public class ResultView extends ViewPart {
    public static final String ID = "com.piece_framework.makegood.ui.views.resultView"; //$NON-NLS-1$
    private static final String STOP_ACTION_ID = Activator.PLUGIN_ID + ".viewActions.resultView.stopTest"; //$NON-NLS-1$
    private static final String RERUN_ACTION_ID = Activator.PLUGIN_ID + ".viewActions.resultView.rerunTest"; //$NON-NLS-1$
    private static final String CONTEXT_ID = Activator.PLUGIN_ID + ".contexts.resultView"; //$NON-NLS-1$

    private RunProgressBar progressBar;
    private Label tests;
    private ResultLabel passes;
    private ResultLabel failures;
    private ResultLabel errors;
    private TreeViewer resultTreeViewer;
    private Label rate;
    private Label average;
    private ShowTimer showTimer;
    private IAction stopAction;
    private IAction rerunAction;
    private FailureTrace failureTrace;

    private ViewerFilter failureFilter = new ViewerFilter() {
        @Override
        public boolean select(Viewer viewer, Object parentElement, Object element) {
            if (!(element instanceof Result)) return false;
            Result result = (Result) element;
            return result.hasFailure() || result.hasError();
        }
    };

    public ResultView() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public IViewSite getViewSite() {
        IViewSite site = super.getViewSite();

        // There is no hook point for disabling the actions...
        if (stopAction == null) {
            IToolBarManager manager = site.getActionBars().getToolBarManager();
            ActionContributionItem stopItem = (ActionContributionItem) manager.find(STOP_ACTION_ID);
            if (stopItem != null) {
                stopAction = stopItem.getAction();
                stopAction.setEnabled(false);
            }

            ActionContributionItem rerunItem = (ActionContributionItem) manager.find(RERUN_ACTION_ID);
            if (rerunItem != null) {
                rerunAction = rerunItem.getAction();
                rerunAction.setEnabled(false);
            }
        }

        return site;
    }

    @Override
    public void createPartControl(final Composite parent) {
        IContextService service = (IContextService) getSite().getService(IContextService.class);
        service.activateContext(CONTEXT_ID); //$NON-NLS-1$

        parent.setLayout(new GridLayout(1, false));

        Composite progress = new Composite(parent, SWT.NULL);
        progress.setLayoutData(createHorizontalFillGridData());
        progress.setLayout(new GridLayout(3, false));

        rate = new Label(progress, SWT.LEFT);
        progressBar = new RunProgressBar(progress);
        progressBar.setLayoutData(createHorizontalFillGridData());
        average = new Label(progress, SWT.LEFT);

        Composite summary = new Composite(parent, SWT.NULL);
        summary.setLayoutData(createHorizontalFillGridData());
        summary.setLayout(new GridLayout(2, true));

        tests = new Label(summary, SWT.LEFT | SWT.WRAP);
        tests.setLayoutData(createHorizontalFillGridData());

        Composite labels = new Composite(summary, SWT.NULL);
        labels.setLayoutData(createHorizontalFillGridData());
        labels.setLayout(new FillLayout(SWT.HORIZONTAL));
        passes = new ResultLabel(labels,
                                 Messages.TestResultView_passesLabel,
                                 Activator.getImageDescriptor("icons/pass-gray.gif").createImage() //$NON-NLS-1$
                                 );
        failures = new ResultLabel(labels,
                                   Messages.TestResultView_failuresLabel,
                                   Activator.getImageDescriptor("icons/failure-gray.gif").createImage() //$NON-NLS-1$
                                   );
        errors = new ResultLabel(labels,
                                 Messages.TestResultView_errorsLabel,
                                 Activator.getImageDescriptor("icons/error-gray.gif").createImage() //$NON-NLS-1$
                                 );

        SashForm form = new SashForm(parent, SWT.HORIZONTAL);
        form.setLayoutData(createBothFillGridData());
        form.setLayout(new GridLayout(2, false));

        Composite treeParent = new Composite(form, SWT.NULL);
        treeParent.setLayoutData(createHorizontalFillGridData());
        treeParent.setLayout(new GridLayout(1, false));

        Composite result = new Composite(treeParent, SWT.NULL);
        result.setLayoutData(createHorizontalFillGridData());
        result.setLayout(new RowLayout());

        new ResultLabel(result,
                        Messages.TestResultView_resultsLabel,
                        null
                        );

        Tree resultTree = new Tree(treeParent, SWT.BORDER);
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
                TestCaseResult testCase = (TestCaseResult)element;
                if (!testCase.hasFailure() && !testCase.hasError()) return;
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
                    IFile[] files = FileFind.findFiles(fileName);
                    if (files == null) return;
                    if (files.length > 0) {
                        EditorOpen.open(files[0], testCase.getLine());
                    } else {
                        EditorOpen.open(FileFind.findFileStore(fileName), testCase.getLine());
                    }
                } else if (element instanceof TestSuiteResult) {
                    TestSuiteResult suite= (TestSuiteResult) element;
                    String fileName = suite.getFile();
                    if (fileName == null) return;
                    IFile[] files = FileFind.findFiles(fileName);
                    if (files == null) return;
                    if (files.length > 0) {
                        EditorOpen.open(files[0]);
                    } else {
                        EditorOpen.open(FileFind.findFileStore(fileName));
                    }
                }
            }
        });

        failureTrace = createFailureTrace(form);

        reset();
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
    }

    public void reset() {
        rate.setText("  0 " +   //$NON-NLS-1$ 
                     Messages.TestResultView_percent +
                     "  "       //$NON-NLS-1$
                     );
        average.setText(TimeFormatter.format(0,
                                             Messages.TestResultView_second,
                                             Messages.TestResultView_millisecond
                                             ) +
                        " / " +         //$NON-NLS-1$
                        Messages.TestResultView_averageTest +
                        "  "            //$NON-NLS-1$
                        );
        tests.setText(Messages.TestResultView_testsLabel + " " + //$NON-NLS-1$
                      " 0/0 " + //$NON-NLS-1$
                      "(" +         //$NON-NLS-1$
                          Messages.TestResultView_realTime +
                          " " +  //$NON-NLS-1$
                          TimeFormatter.format(0,
                                             Messages.TestResultView_second,
                                             Messages.TestResultView_millisecond
                                             ) +
                          "," +     //$NON-NLS-1$
                      " " +         //$NON-NLS-1$
                          Messages.TestResultView_testTime +
                          " " +  //$NON-NLS-1$
                          TimeFormatter.format(0,
                                             Messages.TestResultView_second,
                                             Messages.TestResultView_millisecond
                                             ) +
                      ")" //$NON-NLS-1$
                      );
        passes.reset();
        failures.reset();
        errors.reset();

        progressBar.reset();

        resultTreeViewer.setInput(null);
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
        Composite traceParent = new Composite(parent, SWT.NULL);
        traceParent.setLayoutData(createHorizontalFillGridData());
        traceParent.setLayout(new GridLayout(1, false));
        Composite trace = new Composite(traceParent, SWT.NULL);
        trace.setLayoutData(createHorizontalFillGridData());
        trace.setLayout(new RowLayout());
        new ResultLabel(
            trace,
            Messages.TestResultView_failureTraceLabel,
            Activator.getImageDescriptor("icons/failure-trace.gif").createImage() //$NON-NLS-1$
        );

        return new FailureTrace(traceParent);
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

    public void refresh(RunProgress progress, Result result) {
        rate.setText(String.format("%3d", progress.getRate()) +     //$NON-NLS-1$
                     " " +      //$NON-NLS-1$
                     Messages.TestResultView_percent +
                     "  "       //$NON-NLS-1$
                     );
        average.setText(TimeFormatter.format(progress.getAverage(),
                                             Messages.TestResultView_second,
                                             Messages.TestResultView_millisecond
                                             ) +
                        " / " +     //$NON-NLS-1$
                        Messages.TestResultView_averageTest +
                        "  "        //$NON-NLS-1$
                        );
        average.getParent().layout();

        showTimer.show();
        passes.setCount(progress.getPassCount());
        failures.setCount(progress.getFailureCount());
        errors.setCount(progress.getErrorCount());

        if (progress.hasFailures()) progressBar.red();
        progressBar.update(progress.getRate());

        if (result != null) {
            resultTreeViewer.expandAll();
            resultTreeViewer.setSelection(new StructuredSelection(result));
        }
        resultTreeViewer.refresh();
    }

    public void start(RunProgress progress) {
        showTimer = new ShowTimer(tests, progress, 200);
        showTimer.start();

        stopAction.setEnabled(true);
        rerunAction.setEnabled(false);
    }

    public void stop() {
        showTimer.stop();

        stopAction.setEnabled(false);
        rerunAction.setEnabled(true);
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
            label.setText(text + " " + count); //$NON-NLS-1$
        }

        private void reset() {
            label.setText(text);
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
        private Label tests;
        private RunProgress progress;
        private int delay;
        private long startTime;
        private boolean stop;

        private ShowTimer(Label tests,
                          RunProgress progress,
                          int delay
                          ) {
            this.tests = tests;
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
            tests.getDisplay().timerExec(delay, this);
        }

        private void show() {
            tests.setText(Messages.TestResultView_testsLabel + " " + //$NON-NLS-1$
                          progress.getTestCount() + "/" + //$NON-NLS-1$
                          progress.getAllTestCount() + " " + //$NON-NLS-1$
                          "(" +         //$NON-NLS-1$
                              Messages.TestResultView_realTime +
                              " " +     //$NON-NLS-1$
                              TimeFormatter.format(System.nanoTime() - startTime,
                                                   Messages.TestResultView_second,
                                                   Messages.TestResultView_millisecond
                                                   ) +
                              "," +     //$NON-NLS-1$
                          " " +         //$NON-NLS-1$
                              Messages.TestResultView_testTime +
                              " " +     //$NON-NLS-1$
                              TimeFormatter.format(progress.getProcessTime(),
                                                   Messages.TestResultView_second,
                                                   Messages.TestResultView_millisecond
                                                   ) +
                          ")" //$NON-NLS-1$
                          );
        }

        @Override
        public void run() {
            show();
            if (!stop) schedule();
        }
    }

    private class FailureTrace extends LinkedText {
        public FailureTrace(Composite parent) {
            super(parent, Pattern.compile("^(.+):(\\d+)$", Pattern.MULTILINE)); //$NON-NLS-1$
            hideScrollBar();
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
                    if (result.getName().equals(selected.getName())) {
                        findSelected = result;
                    }
                } else {
                    if (result instanceof TestCaseResult
                        && (result.hasError() || result.hasFailure())
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
                    if (result.getName().equals(selected.getName())) {
                        return lastFailure;
                    } else {
                        if (result instanceof TestCaseResult
                            && (result.hasError() || result.hasFailure())
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
