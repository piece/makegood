package com.piece_framework.makegood.ui.views;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import com.piece_framework.makegood.ui.Activator;

public class TestResultView extends ViewPart {
    private ScrolledForm root;
    private IDebugEventSetListener listener;

    public TestResultView() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new GridLayout(1, false));

        Label progressBar = new Label(parent, SWT.BORDER);
        progressBar.setBackground(new Color(parent.getDisplay(), 95, 191, 95));
        progressBar.setLayoutData(createHorizontalFillGridData());

        Composite summary = new Composite(parent, SWT.NULL);
        summary.setLayoutData(createHorizontalFillGridData());
        summary.setLayout(new FillLayout(SWT.HORIZONTAL));

        new ResultLabel(summary, "Tests:", null);
        new ResultLabel(summary, "Assertions:", null);
        new ResultLabel(summary,
                        "Passes:",
                        Activator.getImageDescriptor("icons/pass.gif").createImage()
                        );
        new ResultLabel(summary,
                        "Failures:",
                        Activator.getImageDescriptor("icons/failure.gif").createImage()
                        );
        new ResultLabel(summary,
                        "Errors:",
                        Activator.getImageDescriptor("icons/error.gif").createImage()
                        );

//        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
//        root = toolkit.createScrolledForm(parent);

//        final TestResultMasterDetailsBlock block = new TestResultMasterDetailsBlock();
//        block.createContent(new ManagedForm(toolkit, root));
//
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

    private Label createLabel(Composite parent, String text, int style) {
        Label label = new Label(parent, style);
        label.setText(text);
        return label;
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
    }
}
