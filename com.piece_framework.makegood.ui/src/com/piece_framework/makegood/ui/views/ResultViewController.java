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

import org.eclipse.core.resources.IMarker;
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
import com.piece_framework.makegood.ui.markers.FatalErrorMarker;
import com.piece_framework.makegood.ui.markers.TestMarker;

public class ResultViewController implements IDebugEventSetListener {
    private static final String MAKEGOOD_RESULTVIEWCONTROLLER_MARKER_CREATE = "MAKEGOOD_RESULTVIEWCONTROLLER_MARKER_CREATE"; //$NON-NLS-1$
    private static final String MAKEGOOD_RESULTVIEWCONTROLLER_MARKER_TERMINATE = "MAKEGOOD_RESULTVIEWCONTROLLER_MARKER_TERMINATE"; //$NON-NLS-1$
    private TestLifecycle testLifecycle;

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
        // TODO This marker is to avoid calling create() twice by PDT.
        if (createEventFired(launch)) {
            return;
        }
        markAsCreateEventFired(launch);

        if (terminateEventFired(launch)) {
            return;
        }

        synchronized (TestLifecycle.class) {
            if (!TestLifecycle.isRunning()) {
                TestLifecycle.create();
            }
        }

        testLifecycle = TestLifecycle.getInstance();

        try {
            testLifecycle.start(launch, new ResultJUnitXMLReaderListener());
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            return;
        }

        preventConsoleViewFocusing();

        Job job = new UIJob("MakeGood Test Start") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                ResultView resultView = null;
                resultView = (ResultView) ViewOpener.show(ResultView.ID);
                if (resultView == null) return Status.CANCEL_STATUS;

                TestRunner.restoreFocusToLastActivePart();

                try {
                    new FatalErrorMarker().clear(testLifecycle.getProject());
                } catch (CoreException e) {
                    Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
                }

                resultView.reset();
                resultView.startTest(testLifecycle.getProgress(), testLifecycle.getFailures());
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    private void handleTerminateEvent(final MakeGoodLaunch launch) {
        // TODO This code is to avoid calling terminate() twice by PDT.
        if (terminateEventFired(launch)) {
            return;
        }
        markAsTerminateEventFired(launch);

        if (!createEventFired(launch)) {
            return;
        }

        if (!testLifecycle.validateLaunchIdentity(launch)) {
            return;
        }
        testLifecycle.end();

        Job job = new UIJob("MakeGood Test End") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                ResultView resultView = (ResultView) ViewOpener.find(ResultView.ID);
                if (resultView == null) {
                    TestLifecycle.destroy();
                    return Status.CANCEL_STATUS;
                }

                resultView.endTest();

                if (testLifecycle.hasErrors()) {
                    resultView.markAsStopped();
                    if (!StopTestAction.isStoppedByAction(launch)) {
                        IMarker marker = null;
                        try {
                            marker = new FatalErrorMarker().create(testLifecycle.getOutputContents());
                        } catch (CoreException e) {
                            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
                        }
                        if (marker != null) {
                            EditorOpener.open(marker);
                        }
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

    private void markAsCreateEventFired(ILaunch launch) {
        launch.setAttribute(MAKEGOOD_RESULTVIEWCONTROLLER_MARKER_CREATE, Boolean.TRUE.toString());
    }

    private boolean createEventFired(ILaunch launch) {
        String isCreated = launch.getAttribute(MAKEGOOD_RESULTVIEWCONTROLLER_MARKER_CREATE);
        if (isCreated == null) return false;
        return Boolean.TRUE.toString().equals(isCreated);
    }

    private void markAsTerminateEventFired(ILaunch launch) {
        launch.setAttribute(MAKEGOOD_RESULTVIEWCONTROLLER_MARKER_TERMINATE, Boolean.TRUE.toString());
    }

    private boolean terminateEventFired(ILaunch launch) {
        String isTerminated = launch.getAttribute(MAKEGOOD_RESULTVIEWCONTROLLER_MARKER_TERMINATE);
        if (isTerminated == null) return false;
        return Boolean.TRUE.toString().equals(isTerminated);
    }

    public class ResultJUnitXMLReaderListener implements JUnitXMLReaderListener {
        @Override
        public void startTestSuite(TestSuiteResult testSuite) {
            testLifecycle.startTestSuite(testSuite);

            if (!testLifecycle.isProgressInitialized()) {
                testLifecycle.initializeProgress(testSuite);

                Job job = new UIJob("MakeGood Result Tree Set") { //$NON-NLS-1$
                    @Override
                    public IStatus runInUIThread(IProgressMonitor monitor) {
                        ResultView resultView = (ResultView) ViewOpener.find(ResultView.ID);
                        if (resultView == null) return Status.CANCEL_STATUS;
                        resultView.setTreeInput(testLifecycle.getResult());
                        return Status.OK_STATUS;
                    }
                };
                job.schedule();
                return;
            }
        }

        @Override
        public void endTestSuite(TestSuiteResult testSuite) {
        }

        @Override
        public void startTestCase(final TestCaseResult testCase) {
            testLifecycle.startTestCase(testCase);

            Job job = new UIJob("MakeGood Test Case Start") { //$NON-NLS-1$
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    ResultView resultView = (ResultView) ViewOpener.find(ResultView.ID);
                    if (resultView == null) return Status.CANCEL_STATUS;
                    resultView.printCurrentlyRunningTestCase(testCase);
                    resultView.updateOnStartTestCase(testCase);
                    if (testLifecycle.isFileFirstAccessed(testCase)) {
                        try {
                            new TestMarker().clear(testCase);
                        } catch (CoreException e) {
                            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
                        }
                    }
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
        public void endTestCase(final TestCaseResult testCase) {
            if (!testLifecycle.isProgressInitialized()) return;

            testLifecycle.endTestCase(testCase);

            Job job = new UIJob("MakeGood Test Case End") { //$NON-NLS-1$
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    ResultView resultView = (ResultView) ViewOpener.find(ResultView.ID);
                    if (resultView == null) return Status.CANCEL_STATUS;
                    if (testLifecycle.hasFailures()) {
                        resultView.markAsFailed();
                    }
                    resultView.updateOnEndTestCase(testCase);
                    return Status.OK_STATUS;
                }
            };
            job.schedule();
        }

        @Override
        public void startFailure(final TestCaseResult failure) {
            testLifecycle.startFailure(failure);
            Job job = new UIJob("MakeGood Marker Create") { //$NON-NLS-1$
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    try {
                        new TestMarker().create(failure);
                    } catch (CoreException e) {
                        Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
                    }
                    return Status.OK_STATUS;
                }
            };
            job.schedule();
        }

        @Override
        public void endFailure(TestCaseResult failure) {
        }

        @Override
        public void endTest() {
            testLifecycle.endTest();
        }
    }
}
