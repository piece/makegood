package com.piece_framework.makegood.ui.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.php.internal.debug.core.model.IPHPDebugTarget;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

import com.piece_framework.makegood.launch.phpunit.TestResultConverter;
import com.piece_framework.makegood.launch.phpunit.TestSuite;

public class TestResultView extends ViewPart {
    private ScrolledForm root;
    private IDebugEventSetListener listener;

    public TestResultView() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createPartControl(Composite parent) {
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        root = toolkit.createScrolledForm(parent);

        final TestResultMasterDetailsBlock block = new TestResultMasterDetailsBlock();
        block.createContent(new ManagedForm(toolkit, root));

        listener = new IDebugEventSetListener() {
            @Override
            public void handleDebugEvents(DebugEvent[] events) {
                for (DebugEvent event: events) {
                    if (event.getKind() == DebugEvent.TERMINATE
                        && event.getSource() instanceof IPHPDebugTarget
                        ) {
                        final IPHPDebugTarget debugTarget = (IPHPDebugTarget) event.getSource();

                        Job job = new UIJob("MakeGood result parse") {
                            public IStatus runInUIThread(IProgressMonitor monitor) {
                                ILaunchConfiguration configuration = debugTarget.getLaunch().getLaunchConfiguration();

                                try {
                                    String logFile = configuration.getAttribute("LOG_JUNIT", (String) null);
                                    List<TestSuite> suites = TestResultConverter.convert(new File(logFile));
                                    block.setInput(suites);
                                } catch (CoreException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (FileNotFoundException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                String[] results = debugTarget.getOutputBuffer().toString().split("\n");
                                int lastLine = results.length - 1;
                                String result = results[lastLine];

                                root.setText(result);
                                if (result.startsWith("OK")) {
                                    root.setBackground(new Color(null, 0, 255, 0));
                                } else {
                                    root.setBackground(new Color(null, 255, 0, 0));
                                }

                                return Status.OK_STATUS;
                            }
                        };
                        job.schedule();
                    }
                }
            }
        };
        DebugPlugin.getDefault().addDebugEventListener(listener);
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
}