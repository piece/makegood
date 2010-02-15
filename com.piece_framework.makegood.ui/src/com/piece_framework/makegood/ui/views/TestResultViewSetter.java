package com.piece_framework.makegood.ui.views;

import java.io.File;
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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.xml.sax.SAXException;

import com.piece_framework.makegood.core.launch.IMakeGoodEventListener;
import com.piece_framework.makegood.launch.elements.ParserListener;
import com.piece_framework.makegood.launch.elements.Problem;
import com.piece_framework.makegood.launch.elements.TestCase;
import com.piece_framework.makegood.launch.elements.TestResult;
import com.piece_framework.makegood.launch.elements.TestResultParser;
import com.piece_framework.makegood.launch.elements.TestSuite;
import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.Messages;

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
                TestResultView view = TestResultView.showView();
                if (view == null) {
                    // TODO
                    return null;
                }
                view.setFocus();
                view.reset();
                view.start(progress);

                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    @Override
    public void terminate(ILaunch launch) {
        if (launch.getAttribute("processTerminateOf" + getClass().getName()) != null) {
            return;
        }
        launch.setAttribute("processTerminateOf" + getClass().getName(), "done");

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
        for (int i = 0; i < 10 && !parser.wasEnd(); ++i) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {}
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

                progress.finalize();
                view.terminate();
                view.refresh(progress, currentTestCase);

                if (parserException != null) {
                    MessageDialog.openError(view.getViewSite().getShell(),
                                            Messages.TestResultViewSetter_messageTitle,
                                            Messages.TestResultViewSetter_failedToParseXMLMessage + parserException.getMessage()
                                            );

                    IStatus status = new Status(IStatus.ERROR,
                                                Activator.PLUGIN_ID,
                                                0,
                                                parserException.getMessage(),
                                                parserException
                                                );
                    Activator.getDefault().getLog().log(status);
                }

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
                TestResultView view = TestResultView.showView();
                if (view == null) {
                    // TODO
                    return null;
                }
                view.setFocus();
                if (!view.isSetTreeInput()) {
                    view.setTreeInput(parser.getTestResults());
                }
                view.refresh(progress, currentTestCase);

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
