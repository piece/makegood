package com.piece_framework.makegood.ui.views;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.ui.progress.UIJob;

import com.piece_framework.makegood.core.launch.IMakeGoodEventListener;
import com.piece_framework.makegood.launch.phpunit.TestResultConverter;

public class TestResultViewSetter implements IMakeGoodEventListener {
    @Override
    public void create(ILaunch launch) {
        Job job = new UIJob("MakeGood reset") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                TestResultView view = TestResultView.showView();
                if (view == null) {
                    // TODO
                    return null;
                }
                view.setFocus();

                view.reset();

                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    @Override
    public void terminate(ILaunch launch) {
        String log = null;
        try {
            log = launch.getLaunchConfiguration().getAttribute("LOG_JUNIT", (String) null);
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (log == null) {
            // TODO
            return;
        }
        final File logFile = new File(log);

        Job job = new UIJob("MakeGood result parse") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                TestResultView view = TestResultView.showView();
                if (view == null) {
                    // TODO
                    return null;
                }
                view.setFocus();

                try {
                    view.showTestResult(TestResultConverter.convert(logFile));
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
