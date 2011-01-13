/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.views;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.internal.ui.views.console.ProcessConsole;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.php.internal.debug.core.model.IPHPDebugTarget;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.progress.UIJob;

import com.piece_framework.makegood.core.result.TestCaseResult;
import com.piece_framework.makegood.core.result.TestSuiteResult;
import com.piece_framework.makegood.core.run.JUnitXMLReaderListener;
import com.piece_framework.makegood.launch.MakeGoodLaunch;
import com.piece_framework.makegood.launch.TestLifecycle;
import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.actions.StopTestAction;
import com.piece_framework.makegood.ui.launch.TestRunner;

public class ResultViewController implements IDebugEventSetListener {
    @Override
    public void handleDebugEvents(DebugEvent[] events) {
        if (events == null) return;
        int size = events.length;
        for (int i = 0; i < size; ++i) {
            final Object source = events[i].getSource();
            ILaunch launch = getLaunch(source);
            if (launch == null) continue;
            if (!(launch instanceof MakeGoodLaunch)) continue;

            if (events[i].getKind() == DebugEvent.CREATE) {
                handleCreateEvent((MakeGoodLaunch) launch);
            } else if (events[i].getKind() == DebugEvent.TERMINATE) {
                handleTerminateEvent((MakeGoodLaunch) launch);
            }
        }
    }

    private void handleCreateEvent(final MakeGoodLaunch launch) {
        try {
            TestLifecycle.getInstance().initialize(launch, new ResultJUnitXMLReaderListener());
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            return;
        }
        TestLifecycle.getInstance().start();

        preventConsoleViewFocusing();

        Job job = new UIJob("MakeGood Test Start") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                ResultView resultView = null;
                resultView = (ResultView) ViewShow.show(ResultView.ID);
                if (resultView == null) return Status.CANCEL_STATUS;

                TestRunner.restoreFocusToLastActivePart();

                resultView.reset();
                resultView.startTest(TestLifecycle.getInstance().getProgress(), TestLifecycle.getInstance().getFailures());
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    private void handleTerminateEvent(final MakeGoodLaunch launch) {
        if (!TestLifecycle.getInstance().validateLaunchIdentity(launch)) return;
        TestLifecycle.getInstance().end();

        Job job = new UIJob("MakeGood Test End") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                ResultView resultView = (ResultView) ViewShow.find(ResultView.ID);
                if (resultView == null) {
                    TestLifecycle.destroy();
                    return Status.CANCEL_STATUS;
                }

                resultView.endTest();

                if (TestLifecycle.getInstance().hasErrors()) {
                    resultView.markAsStopped();
                    if (!StopTestAction.isStoppedByAction(launch)) {
                        ViewShow.show(IConsoleConstants.ID_CONSOLE_VIEW);
                    }
                }

                TestLifecycle.destroy();

                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    private void preventConsoleViewFocusing() {
        for (IConsole console: ConsolePlugin.getDefault().getConsoleManager().getConsoles()) {
            if (!(console instanceof ProcessConsole)) continue;
            IOConsoleOutputStream stdoutStream = ((ProcessConsole) console).getStream(IDebugUIConstants.ID_STANDARD_OUTPUT_STREAM);
            if (stdoutStream == null) continue;
            stdoutStream.setActivateOnWrite(false);
            IOConsoleOutputStream stderrStream = ((ProcessConsole) console).getStream(IDebugUIConstants.ID_STANDARD_ERROR_STREAM);
            if (stderrStream == null) continue;
            stderrStream.setActivateOnWrite(false);
        }
    }

    private ILaunch getLaunch(Object eventSource) {
        if (eventSource instanceof IPHPDebugTarget) {
            return ((IPHPDebugTarget) eventSource).getLaunch();
        } else if (eventSource instanceof IProcess) {
            return ((IProcess) eventSource).getLaunch();
        } else {
            return null;
        }
    }

    public class ResultJUnitXMLReaderListener implements JUnitXMLReaderListener {
        @Override
        public void startTestSuite(TestSuiteResult testSuite) {
            TestLifecycle.getInstance().startTestSuite(testSuite);

            if (TestLifecycle.getInstance().isProgressInitialized()) {
                return;
            }

            TestLifecycle.getInstance().initializeProgress(testSuite);

            Job job = new UIJob("MakeGood Result Tree Set") { //$NON-NLS-1$
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    ResultView resultView = (ResultView) ViewShow.find(ResultView.ID);
                    if (resultView == null) return Status.CANCEL_STATUS;
                    resultView.setTreeInput(TestLifecycle.getInstance().getResult());
                    return Status.OK_STATUS;
                }
            };
            job.schedule();
        }

        @Override
        public void endTestSuite() {}

        @Override
        public void startTestCase(final TestCaseResult testCase) {
            TestLifecycle.getInstance().startTestCase(testCase);

            Job job = new UIJob("MakeGood Test Case Start") { //$NON-NLS-1$
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    ResultView resultView = (ResultView) ViewShow.find(ResultView.ID);
                    if (resultView == null) return Status.CANCEL_STATUS;
                    resultView.printCurrentlyRunningTestCase(TestLifecycle.getInstance().getCurrentTestCase());
                    resultView.updateOnStartTestCase(TestLifecycle.getInstance().getCurrentTestCase());
                    return Status.OK_STATUS;
                }
            };
            job.schedule();
            try {
                job.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void endTestCase() {
            if (!TestLifecycle.getInstance().isProgressInitialized()) return;

            TestLifecycle.getInstance().endTestCase();

            Job job = new UIJob("MakeGood Test Case End") { //$NON-NLS-1$
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    ResultView resultView = (ResultView) ViewShow.find(ResultView.ID);
                    if (resultView == null) return Status.CANCEL_STATUS;
                    if (TestLifecycle.getInstance().hasFailures()) {
                        resultView.markAsFailed();
                    }
                    resultView.updateOnEndTestCase(TestLifecycle.getInstance().getCurrentTestCase());
                    return Status.OK_STATUS;
                }
            };
            job.schedule();
        }

        @Override
        public void startFailure(TestCaseResult failure) {
            TestLifecycle.getInstance().startFailure(failure);
        }

        @Override
        public void endFailure() {}

        @Override
        public void endTest() {
            TestLifecycle.getInstance().endTest();
        }
    }
}
