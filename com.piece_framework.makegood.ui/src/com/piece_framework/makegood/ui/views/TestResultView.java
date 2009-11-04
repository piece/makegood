package com.piece_framework.makegood.ui.views;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.php.internal.debug.core.model.IPHPDebugTarget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import com.piece_framework.makegood.launch.phpunit.ProblemType;
import com.piece_framework.makegood.launch.phpunit.TestCase;
import com.piece_framework.makegood.launch.phpunit.TestResultConverter;
import com.piece_framework.makegood.launch.phpunit.TestSuite;
import com.piece_framework.makegood.ui.Activator;

public class TestResultView extends ViewPart {
    private static final RGB GREEN = new RGB(95, 191, 95);
    private static final RGB RED = new RGB(159, 63, 63);
    private static final RGB NONE = new RGB(255, 255, 255);

    private IDebugEventSetListener listener;
    private Label progressBar;
    private ResultLabel tests;
    private ResultLabel assertions;
    private ResultLabel passes;
    private ResultLabel failures;
    private ResultLabel errors;
    private TreeViewer resultTreeViewer;
    private List resultList;

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

        Tree resultTree = new Tree(form, SWT.BORDER);
        resultTree.setLayoutData(createBothFillGridData());
        resultTreeViewer = new TreeViewer(resultTree);
        resultTreeViewer.setContentProvider(new TestResultContentProvider());
        resultTreeViewer.setLabelProvider(new TestResultLabelProvider());
        resultTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (!(event.getSelection() instanceof IStructuredSelection)) {
                    return;
                }
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                if (!(selection.getFirstElement() instanceof TestCase)) {
                    return;
                }

                resultList.removeAll();

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

        resultList = new List(form, SWT.BORDER + SWT.V_SCROLL + SWT.H_SCROLL);
        resultList.setLayoutData(createBothFillGridData());

        listener = new IDebugEventSetListener() {
            @Override
            public void handleDebugEvents(DebugEvent[] events) {
                for (DebugEvent event: events) {
                    if (event.getKind() != DebugEvent.TERMINATE
                        || !(event.getSource() instanceof IPHPDebugTarget)
                        ) {
                        continue;
                    }

                    final IPHPDebugTarget debugTarget = (IPHPDebugTarget) event.getSource();
                    Job job = new UIJob("MakeGood result parse") {
                        @Override
                        public IStatus runInUIThread(IProgressMonitor monitor) {
                            ILaunchConfiguration configuration = debugTarget.getLaunch().getLaunchConfiguration();
                            try {
                                String logFile = configuration.getAttribute("LOG_JUNIT", (String) null);
                                if (logFile == null) {
                                    // TODO
                                    return null;
                                }
                                java.util.List<TestSuite> suites = TestResultConverter.convert(new File(logFile));
                                TestSuite suite = suites.get(0);
                                tests.setCount(suite.getTestCount());
                                assertions.setCount(suite.getAssertionCount());
                                passes.setCount(suite.getTestCount() -
                                                suite.getFailureCount() -
                                                suite.getErrorCount()
                                                );
                                failures.setCount(suite.getFailureCount());
                                errors.setCount(suite.getErrorCount());

                                if (!suite.hasErrorChild()) {
                                    progressBar.setBackground(new Color(progressBar.getDisplay(), GREEN));
                                } else {
                                    progressBar.setBackground(new Color(progressBar.getDisplay(), RED));
                                }

                                resultTreeViewer.setInput(suites);
                                if (suite.hasErrorChild()) {
                                    resultTreeViewer.expandToLevel(2);
                                }
                            } catch (CoreException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            return Status.OK_STATUS;
                        }
                    };
                    job.schedule();
                }
            }
        };
        DebugPlugin.getDefault().addDebugEventListener(listener);

//        listener = new IDebugEventSetListener() {
//            @Override
//            public void handleDebugEvents(DebugEvent[] events) {
//                for (DebugEvent event: events) {
//                    if (event.getKind() == DebugEvent.TERMINATE
//                        && event.getSource() instanceof IPHPDebugTarget
//                        ) {
//                        final IPHPDebugTarget debugTarget = (IPHPDebugTarget) event.getSource();
//
//                        Job job = new UIJob("MakeGood result parse") {
//                            public IStatus runInUIThread(IProgressMonitor monitor) {
//                                ILaunchConfiguration configuration = debugTarget.getLaunch().getLaunchConfiguration();
//
//                                try {
//                                    String logFile = configuration.getAttribute("LOG_JUNIT", (String) null);
//                                    List<TestSuite> suites = TestResultConverter.convert(new File(logFile));
//                                    block.setInput(suites);
//                                } catch (CoreException e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                } catch (FileNotFoundException e) {
//                                    // TODO Auto-generated catch block
//                                    e.printStackTrace();
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                                String[] results = debugTarget.getOutputBuffer().toString().split("\n");
//                                int lastLine = results.length - 1;
//                                String result = results[lastLine];
//
//                                root.setText(result);
//                                if (result.startsWith("OK")) {
//                                    root.setBackground(new Color(null, 0, 255, 0));
//                                } else {
//                                    root.setBackground(new Color(null, 255, 0, 0));
//                                }
//
//                                return Status.OK_STATUS;
//                            }
//                        };
//                        job.schedule();
//                    }
//                }
//            }
//        };
//        DebugPlugin.getDefault().addDebugEventListener(listener);
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        super.dispose();
        DebugPlugin.getDefault().removeDebugEventListener(listener);
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
    }
}
