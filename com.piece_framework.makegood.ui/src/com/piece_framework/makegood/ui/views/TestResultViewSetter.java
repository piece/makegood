package com.piece_framework.makegood.ui.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.xml.sax.SAXException;

import com.piece_framework.makegood.core.launch.IMakeGoodEventListener;
import com.piece_framework.makegood.launch.elements.TestResult;
import com.piece_framework.makegood.launch.elements.TestResultParser;
import com.piece_framework.makegood.launch.elements.TestSuite;

public class TestResultViewSetter implements IMakeGoodEventListener {
    private TestResultParser parser;

    @Override
    public void create(ILaunch launch) {
        String log = null;
        try {
            log = launch.getLaunchConfiguration().getAttribute("LOG_JUNIT", (String) null); //$NON-NLS-1$
        } catch (CoreException e) {
        }
        if (log == null) {
            return;
        }

        parser = new TestResultParser(new File(log));
        Job job = new UIJob("MakeGood reset") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                TestResultView view = TestResultView.showView();
                if (view == null) {
                    // TODO
                    return null;
                }
                view.setFocus();

                view.reset();

                try {
                    parser.start();
                } catch (ParserConfigurationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (SAXException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

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

        parser.terminate();
        while (!parser.wasEnd() || parser.getTestResults().size() == 0) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
        }
        final List<TestSuite> suites = new ArrayList<TestSuite>();
        for (TestResult result : parser.getTestResults()) {
            if (result instanceof TestSuite) {
                suites.add((TestSuite) result);
            }
        }

        Job job = new UIJob("MakeGood result parse") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                TestResultView view = TestResultView.showView();
                if (view == null) {
                    // TODO
                    return null;
                }
                view.setFocus();
                view.showTestResult(suites);

                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    private void showDebugOutput() {
        Job job = new UIJob("Show Debug Output") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                try {
                    page.showView("org.eclipse.debug.ui.PHPDebugOutput"); //$NON-NLS-1$
                } catch (PartInitException e) {
                }

                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    private TestResultParser getParser() {
        return parser;
    }
}
