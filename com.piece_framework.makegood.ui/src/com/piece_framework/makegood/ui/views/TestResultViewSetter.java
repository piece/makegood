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
import com.piece_framework.makegood.launch.elements.ParserListener;
import com.piece_framework.makegood.launch.elements.Problem;
import com.piece_framework.makegood.launch.elements.TestCase;
import com.piece_framework.makegood.launch.elements.TestResultParser;
import com.piece_framework.makegood.launch.elements.TestSuite;
import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.ide.ViewShow;

public class TestResultViewSetter implements IMakeGoodEventListener, ParserListener {
    private TestResultParser parser;
    private TestProgress progress;
    private TestCase currentTestCase;
    private Exception parserException;

    @Override
    public void create(ILaunch launch) {
        String log = null;
        try {
            log = launch.getLaunchConfiguration().getAttribute("LOG_JUNIT", (String) null); //$NON-NLS-1$
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(
                new Status(
                    Status.WARNING,
                    Activator.PLUGIN_ID,
                    e.getMessage(),
                    e
                )
            );
        }
        if (log == null) {
            return;
        }

        progress = new TestProgress();

        parser = new TestResultParser(new File(log));
        parser.addParserListener(this);
        Thread parserThread = new Thread() {
            @Override
            public void run() {
                parserException = null;
                try {
                    parser.start();
                } catch (ParserConfigurationException e) {
                    parserException = e;
                } catch (SAXException e) {
                    parserException = e;
                } catch (IOException e) {
                    parserException = e;
                }
            }
        };
        parserThread.start();

        Job job = new UIJob("MakeGood reset") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                ViewShow.show(OutputView.ID);
                ResultView resultView = (ResultView)ViewShow.show(ResultView.ID);
                if (resultView == null) {
                    return Status.CANCEL_STATUS;
                }

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
                if (process.getExitValue() != 0) {
                    showOutput();
                    return;
                }
            } catch (DebugException e) {
                Activator.getDefault().getLog().log(
                    new Status(
                        Status.WARNING,
                        Activator.PLUGIN_ID,
                        e.getMessage(),
                        e
                    )
                );
            }
        }

        parser.stop();
        for (int i = 0; i < 10 && !parser.wasEnd(); ++i) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {}
        }

        Job job = new UIJob("MakeGood result parse") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                ResultView resultView = (ResultView)ViewShow.show(ResultView.ID);
                if (resultView == null) {
                    return Status.CANCEL_STATUS;
                }

                progress.finalize();
                resultView.stop();
                resultView.refresh(progress, currentTestCase);

                if (parserException != null) {
                    Activator.getDefault().getLog().log(
                        new Status(
                            IStatus.ERROR,
                            Activator.PLUGIN_ID,
                            parserException.getMessage(),
                            parserException
                        )
                    );
                }

                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    private void showOutput() {
        Job job = new UIJob("MakeGood Show Output") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                ViewShow.show(OutputView.ID);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    @Override
    public void startTestSuite(TestSuite testSuite) {
        if (!progress.isInitialized()) {
            progress.initialize(testSuite.getTestCount());
        }
    }

    @Override
    public void endTestSuite() {
    }

    @Override
    public void startTestCase(TestCase testCase) {
        this.currentTestCase = testCase;
    }

    @Override
    public void endTestCase() {
        if (!progress.isInitialized()) {
            return;
        }

        progress.incrementEndTestCount();
        progress.incrementResultCount(currentTestCase.getProblem().getType());
        progress.endTestCase();

        currentTestCase.setTime(progress.getTestCaseTime());

        Job job = new UIJob("MakeGood refresh") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                ResultView resultView = (ResultView)ViewShow.show(ResultView.ID);
                if (resultView == null) {
                    return Status.CANCEL_STATUS;
                }

                if (!resultView.isSetTreeInput()) {
                    resultView.setTreeInput(parser.getTestResults());
                }

                resultView.refresh(progress, currentTestCase);

                return Status.OK_STATUS;
            }
        };
        job.schedule();

        progress.startTestCase();   // The method startTestCase() does not be invoked when the test runs.
    }

    @Override
    public void startProblem(Problem problem) {
    }

    @Override
    public void endProblem() {
    }
}
