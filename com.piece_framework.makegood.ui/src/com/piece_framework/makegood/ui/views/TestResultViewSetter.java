package com.piece_framework.makegood.ui.views;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
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
        for (IProcess process: launch.getProcesses()) {
            try {
                if (process.getExitValue() != 0) {
                    showDebugOutput();
                    return;
                }
            } catch (DebugException e) {
            }
        }

        String log = null;
        try {
            log = launch.getLaunchConfiguration().getAttribute("LOG_JUNIT", (String) null);
        } catch (CoreException e) {
        }
        if (log == null) {
            showDebugOutput();
            return;
        }
        final File logFile = new File(log);
        if (!logFile.exists() || logFile.length() == 0) {
            showDebugOutput();
            return;
        }

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

    private void showDebugOutput() {
        Job job = new UIJob("Show Debug Output") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try {
                    page.showView("org.eclipse.debug.ui.PHPDebugOutput");
                } catch (PartInitException e) {
                }

                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }
}
