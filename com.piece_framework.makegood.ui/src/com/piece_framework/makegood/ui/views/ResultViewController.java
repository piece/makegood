package com.piece_framework.makegood.ui.views;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.ui.progress.UIJob;
import org.xml.sax.SAXException;

import com.piece_framework.makegood.core.launch.IMakeGoodEventListener;
import com.piece_framework.makegood.core.result.JUnitXMLReader;
import com.piece_framework.makegood.core.result.JUnitXMLReaderListener;
import com.piece_framework.makegood.core.result.TestCaseResult;
import com.piece_framework.makegood.core.result.TestSuiteResult;
import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.actions.StopTestAction;
import com.piece_framework.makegood.ui.ide.ViewShow;

public class ResultViewController implements IMakeGoodEventListener, JUnitXMLReaderListener {
    private JUnitXMLReader parser;
    private RunProgress progress;
    private TestCaseResult currentTestCase;
    private Thread parserThread;
    private boolean hasErrors;

    @Override
    public void create(final ILaunch launch) {
        String log = null;
        try {
            log = launch.getLaunchConfiguration().getAttribute("LOG_JUNIT", (String) null); //$NON-NLS-1$
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
        }
        if (log == null) return;

        progress = new RunProgress();

        parser = new JUnitXMLReader(new File(log));
        parser.addListener(this);
        parserThread = new Thread() {
            @Override
            public void run() {
                hasErrors = false;
                try {
                    parser.read();
                } catch (ParserConfigurationException e) {
                    Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
                } catch (SAXException e) {
                    if (launch.getAttribute(StopTestAction.ID + ".stopsByAction") == null) {
                        hasErrors = true;
                    } else {
                        Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
                    }
                } catch (IOException e) {
                    Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
                }
            }
        };
        parserThread.start();

        Job job = new UIJob("MakeGood Reset Result View") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                ViewShow.show(OutputView.ID);
                ResultView resultView = (ResultView) ViewShow.show(ResultView.ID);
                if (resultView == null) return Status.CANCEL_STATUS;

                resultView.reset();
                resultView.start(progress);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    @Override
    public void terminate(ILaunch launch) {
        if (launch.getAttribute("processTerminateOf" + getClass().getName()) != null) { //$NON-NLS-1$
            return;
        }
        launch.setAttribute("processTerminateOf" + getClass().getName(), "done"); //$NON-NLS-1$ //$NON-NLS-2$

        for (IProcess process: launch.getProcesses()) {
            try {
                int exitValue = process.getExitValue();
                if (exitValue != 0) {
                    if (launch.getAttribute(StopTestAction.ID + ".stopsByAction") == null) {
                        hasErrors = true;
                    } else {
                        Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, "A PHP process exit with a non-zero exit status [ " + exitValue + " ]")); //$NON-NLS-1$
                    }
                    break;
                }
            } catch (DebugException e) {
                Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
                break;
            }
        }

        parser.stop();
        try {
            parserThread.join();
        } catch (InterruptedException e) {
            Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
        }

        Job job = new UIJob("MakeGood Show View") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                ResultView resultView = (ResultView) ViewShow.find(ResultView.ID);
                if (resultView == null) return Status.CANCEL_STATUS;

                resultView.stop();
                resultView.refresh(progress, currentTestCase);

                if (hasErrors == true) {
                    ViewShow.show(OutputView.ID);
                    return Status.OK_STATUS;
                }

                resultView.setFocus();
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    @Override
    public void startTestSuite(TestSuiteResult testSuite) {
        if (!progress.isInitialized()) {
            progress.initialize(testSuite);
        }
    }

    @Override
    public void endTestSuite() {}

    @Override
    public void startTestCase(TestCaseResult testCase) {
        this.currentTestCase = testCase;
    }

    @Override
    public void endTestCase() {
        if (!progress.isInitialized()) return;

        progress.endTestCase();
        currentTestCase.setTime(progress.getProcessTimeForTestCase());

        Job job = new UIJob("MakeGood Refresh Result View") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                ResultView resultView = (ResultView) ViewShow.show(ResultView.ID);
                if (resultView == null) return Status.CANCEL_STATUS;

                if (!resultView.isSetTreeInput()) resultView.setTreeInput(parser.getTestResults());
                resultView.refresh(progress, currentTestCase);
                return Status.OK_STATUS;
            }
        };
        job.schedule();

        progress.startTestCase();   // The method startTestCase() does not be invoked when the test runs.
    }

    @Override
    public void startFailure(TestCaseResult failure) {
        currentTestCase = failure;
    }

    @Override
    public void endFailure() {}
}
