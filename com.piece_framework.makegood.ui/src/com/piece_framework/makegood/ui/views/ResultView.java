package com.piece_framework.makegood.ui.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.php.internal.debug.core.model.IPHPDebugTarget;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

public class ResultView extends ViewPart {
    private Form root;
    private IDebugEventSetListener listener;

    public ResultView() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createPartControl(Composite parent) {
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        root = toolkit.createForm(parent);
        root.getBody().setLayout(new FillLayout());

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
