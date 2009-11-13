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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.piece_framework.makegood.launch.phpunit.ProblemType;
import com.piece_framework.makegood.launch.phpunit.TestCase;
import com.piece_framework.makegood.launch.phpunit.TestResult;
import com.piece_framework.makegood.launch.phpunit.TestSuite;
import com.piece_framework.makegood.ui.Activator;

public class TestResultView extends ViewPart {
    private static final RGB GREEN = new RGB(95, 191, 95);
    private static final RGB RED = new RGB(159, 63, 63);
    private static final RGB NONE = new RGB(255, 255, 255);
    private static final String VIEW_ID = "com.piece_framework.makegood.ui.views.resultView";

    private Label progressBar;
    private ResultLabel tests;
    private ResultLabel assertions;
    private ResultLabel passes;
    private ResultLabel failures;
    private ResultLabel errors;
    private TreeViewer resultTreeViewer;
    private List resultList;

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
        parent.setLayout(new GridLayout(1, false));

        progressBar = new Label(parent, SWT.BORDER);
        progressBar.setBackground(new Color(parent.getDisplay(), NONE));
        progressBar.setLayoutData(createHorizontalFillGridData());

        Composite summary = new Composite(parent, SWT.NULL);
        summary.setLayoutData(createHorizontalFillGridData());
        summary.setLayout(new FillLayout(SWT.HORIZONTAL));

        tests = new ResultLabel(summary, "Tests:", null);
        assertions = new ResultLabel(summary, "Assertions:", null);
        passes = new ResultLabel(summary,
                                 "Passes:",
                                 Activator.getImageDescriptor("icons/pass.gif").createImage()
                                 );
        failures = new ResultLabel(summary,
                                   "Failures:",
                                   Activator.getImageDescriptor("icons/failure.gif").createImage()
                                   );
        errors = new ResultLabel(summary,
                                 "Errors:",
                                 Activator.getImageDescriptor("icons/error.gif").createImage()
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

        new CLabel(result, SWT.LEFT);   // The dummy label.

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

                String[] contents = testCase.getProblem().getContent().split("\n");
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
                        "Failure Trace",
                        Activator.getImageDescriptor("icons/pass.gif").createImage()
                        );

        resultList = new List(traceParent, SWT.BORDER + SWT.V_SCROLL + SWT.H_SCROLL);
        resultList.setLayoutData(createBothFillGridData());
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
        
    }

    public void reset() {
        tests.reset();
        assertions.reset();
        passes.reset();
        failures.reset();
        errors.reset();

        progressBar.setBackground(new Color(progressBar.getDisplay(), NONE));

        resultTreeViewer.getTree().removeAll();
        resultList.removeAll();
    }

    public void showTestResult(java.util.List<TestSuite> suites) {
        TestSuite suite = suites.get(0);
        tests.setCount(suite.getTestCount());
        assertions.setCount(suite.getAssertionCount());
        passes.setCount(suite.getTestCount()
                        - suite.getFailureCount()
                        - suite.getErrorCount()
                        );
        failures.setCount(suite.getFailureCount());
        errors.setCount(suite.getErrorCount());

        if (!suite.hasError() && !suite.hasFailure()) {
            progressBar.setBackground(new Color(progressBar.getDisplay(), GREEN));
        } else {
            progressBar.setBackground(new Color(progressBar.getDisplay(), RED));
        }

        resultTreeViewer.setInput(suites);
        if (suite.hasError() || suite.hasFailure()) {
            resultTreeViewer.expandToLevel(2);
        }
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
            label.setText(text + " " + count);
        }

        private void reset() {
            label.setText(text);
        }
    }
}
