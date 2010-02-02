package com.piece_framework.makegood.ui.views;

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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import com.piece_framework.makegood.launch.elements.ProblemType;
import com.piece_framework.makegood.launch.elements.TestCase;
import com.piece_framework.makegood.launch.elements.TestResult;
import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.Messages;

public class TestResultView extends ViewPart {
    private static final String VIEW_ID = "com.piece_framework.makegood.ui.views.resultView"; //$NON-NLS-1$

    private MakeGoodProgressBar progressBar;
    private Label tests;
    private ResultLabel passes;
    private ResultLabel failures;
    private ResultLabel errors;
    private TreeViewer resultTreeViewer;
    private List resultList;
    private Label rate;
    private Label average;

    private ViewerFilter failureFilter = new ViewerFilter() {
        @Override
        public boolean select(Viewer viewer,
                              Object parentElement,
                              Object element
                              ) {
            if (!(element instanceof TestResult)) {
                return false;
            }

            TestResult result = (TestResult) element;
            return result.hasFailure() || result.hasError();
        }
    };

    public TestResultView() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createPartControl(Composite parent) {
        IContextService service = (IContextService) getSite().getService(IContextService.class);
        service.activateContext("com.piece_framework.makegood.ui.contexts.resultView"); //$NON-NLS-1$

        parent.setLayout(new GridLayout(1, false));

        Composite progress = new Composite(parent, SWT.NULL);
        progress.setLayoutData(createHorizontalFillGridData());
        progress.setLayout(new GridLayout(3, false));

        rate = new Label(progress, SWT.LEFT);
        progressBar = new MakeGoodProgressBar(progress);
        progressBar.setLayoutData(createHorizontalFillGridData());
        average = new Label(progress, SWT.LEFT);

        Composite summary = new Composite(parent, SWT.NULL);
        summary.setLayoutData(createHorizontalFillGridData());
        summary.setLayout(new FillLayout(SWT.HORIZONTAL));

        tests = new Label(summary, SWT.LEFT);
        passes = new ResultLabel(summary,
                                 Messages.TestResultView_passesLabel,
                                 Activator.getImageDescriptor("icons/pass-gray.gif").createImage() //$NON-NLS-1$
                                 );
        failures = new ResultLabel(summary,
                                   Messages.TestResultView_failuresLabel,
                                   Activator.getImageDescriptor("icons/failure-gray.gif").createImage() //$NON-NLS-1$
                                   );
        errors = new ResultLabel(summary,
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
        resultTreeViewer.setContentProvider(new TestResultContentProvider());
        resultTreeViewer.setLabelProvider(new TestResultLabelProvider());
        resultTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                resultList.removeAll();

                if (!(event.getSelection() instanceof IStructuredSelection)) {
                    return;
                }
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                if (!(selection.getFirstElement() instanceof TestCase)) {
                    return;
                }

                TestCase testCase = (TestCase) selection.getFirstElement();
                if (testCase.getProblem().getType() == ProblemType.Pass) {
                    return;
                }

                String[] contents = testCase.getProblem().getContent().split("\n"); //$NON-NLS-1$
                for (String content: contents) {
                    resultList.add(content);
                }
            }
        });

        Composite traceParent = new Composite(form, SWT.NULL);
        traceParent.setLayoutData(createHorizontalFillGridData());
        traceParent.setLayout(new GridLayout(1, false));

        Composite trace = new Composite(traceParent, SWT.NULL);
        trace.setLayoutData(createHorizontalFillGridData());
        trace.setLayout(new RowLayout());

        new ResultLabel(trace,
                        Messages.TestResultView_failureTraceLabel,
                        Activator.getImageDescriptor("icons/failure-trace.gif").createImage() //$NON-NLS-1$
                        );

        resultList = new List(traceParent, SWT.BORDER + SWT.V_SCROLL + SWT.H_SCROLL);
        resultList.setLayoutData(createBothFillGridData());

        reset();
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
        
    }

    public void reset() {
        rate.setText("    %  ");
        average.setText("      s / test  ");
        tests.setText(Messages.TestResultView_testsLabel);
        passes.reset();
        failures.reset();
        errors.reset();

        progressBar.reset();

        resultTreeViewer.setInput(null);
    }

    public static TestResultView getView() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IViewPart view = page.findView(VIEW_ID);
        if (!(view instanceof TestResultView)) {
            return null;
        }
        return (TestResultView) view;
    }

    public static TestResultView showView() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IViewPart view = null;
        try {
            view = page.showView(VIEW_ID);
        } catch (PartInitException e) {
        }
        if (!(view instanceof TestResultView)) {
            return null;
        }
        return (TestResultView) view;
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

    public void nextResult() {
        IStructuredSelection selection = (IStructuredSelection) resultTreeViewer.getSelection();
        TestResult selected = (TestResult) selection.getFirstElement();

        java.util.List<TestResult> results = (java.util.List<TestResult>) resultTreeViewer.getInput();
        if (results == null || results.size() == 0) {
            return;
        }

        if (selected == null) {
            selected = results.get(0);
        }

        TestResultSearch search = new TestResultSearch(results, selected);
        TestResult next = search.getNextFailure();
        if (next != null) {
            resultTreeViewer.expandAll();
            resultTreeViewer.setSelection(new StructuredSelection(next), true);
        }
    }

    public void previousResult() {
        IStructuredSelection selection = (IStructuredSelection) resultTreeViewer.getSelection();
        TestResult selected = (TestResult) selection.getFirstElement();

        java.util.List<TestResult> results = (java.util.List<TestResult>) resultTreeViewer.getInput();
        if (results == null || results.size() == 0) {
            return;
        }

        if (selected == null) {
            selected = results.get(0);
        }

        TestResultSearch search = new TestResultSearch(results, selected);
        TestResult previous = search.getPreviousFailure();
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

    public void setTreeInput(java.util.List<TestResult> suites) {
        resultTreeViewer.setInput(suites);
    }

    public boolean isSetTreeInput() {
        return resultTreeViewer.getInput() != null;
    }

    public void refresh(TestProgress progress, TestResult result) {
        rate.setText(String.format("%3d", progress.getRate()) + " %  ");
        average.setText(String.format("%.3f", progress.getAverage()) + " s / test  ");

        tests.setText(Messages.TestResultView_testsLabel + " " +
                      progress.getEndTestCount() + "/" +
                      progress.getAllTestCount() + " " +
                      "(" + String.format("%.3f", progress.getTotalTime()) + " s)"
                      );
        passes.setCount(progress.getPassCount());
        failures.setCount(progress.getFailureCount());
        errors.setCount(progress.getErrorCount());

        boolean raiseErrorOrFailure = progress.getErrorCount() > 0
                                      || progress.getFailureCount() > 0;
        if (raiseErrorOrFailure) {
            progressBar.raisedError();
        }
        progressBar.worked(progress.getRate());

        if (result != null) {
            resultTreeViewer.expandAll();
            resultTreeViewer.setSelection(new StructuredSelection(result));
        }
        resultTreeViewer.refresh();
    }

    private class ResultLabel {
        private CLabel label;
        private String text;

        private ResultLabel(Composite parent, String text, Image icon) {
            label = new CLabel(parent, SWT.LEFT);
            label.setText(text);
            if (icon != null) {
                label.setImage(icon);
            }

            this.text = text;
        }

        private void setCount(int count) {
            label.setText(text + " " + count); //$NON-NLS-1$
        }

        private void reset() {
            label.setText(text);
        }
    }

    private class MakeGoodProgressBar extends Composite {
        private final RGB GREEN = new RGB(95, 191, 95);
        private final RGB RED = new RGB(159, 63, 63);
        private final RGB NONE = new RGB(255, 255, 255);

        private Label bar;
        private int rate;

        private MakeGoodProgressBar(Composite parent) {
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
            bar.addControlListener(new ControlAdapter() {
                @Override
                public void controlResized(ControlEvent e) {
                    worked(rate);
                }
            });

            reset();
        }

        private void worked(int rate) {
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

        private void raisedError() {
            bar.setBackground(new Color(getDisplay(), RED));
        }

        private void reset() {
            worked(0);

            bar.setBackground(new Color(getDisplay(), GREEN));
        }
    }
}
