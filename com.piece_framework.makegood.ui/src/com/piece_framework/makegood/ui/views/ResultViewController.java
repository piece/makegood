/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2013 KUBO Atsuhiro <kubo@iteman.jp>,
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
import org.eclipse.debug.core.DebugException;
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
import com.piece_framework.makegood.core.run.ResultReaderListener;
import com.piece_framework.makegood.launch.MakeGoodLaunch;
import com.piece_framework.makegood.launch.TestLifecycle;
import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.MakeGoodContext;
import com.piece_framework.makegood.ui.MakeGoodStatus;
import com.piece_framework.makegood.ui.actions.StopTestRunAction;
import com.piece_framework.makegood.ui.markers.FatalErrorMarkerFactory;
import com.piece_framework.makegood.ui.markers.TestMarkerFactory;
import com.piece_framework.makegood.ui.markers.UnknownFatalErrorMessageException;
import com.piece_framework.makegood.ui.widgets.ResultSquare;

@SuppressWarnings("restriction")
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
            ILaunch launch = extractLaunchFromDebugTarget(source);
            if (launch == null && source instanceof IProcess) {
                launch = ((IProcess) source).getLaunch();
            }
            if (launch == null) continue;
            if (!(launch instanceof MakeGoodLaunch)) continue;

            if (events[i].getKind() == DebugEvent.CREATE) {
                handleCreateEvent((MakeGoodLaunch) launch);
            } else if (events[i].getKind() == DebugEvent.TERMINATE) {
                handleTerminateEvent((MakeGoodLaunch) launch);
            }
        }
    }

    /**
     * Starts a test lifecycle for the given launch.
     *
     * The TestLifecycle.start() method should be called in the UI thread
     * since sometimes the ResultProjector.startTestCase() method is called
     * earlier than the UIJob object that is created in this method.
     *
     * @param launch
     */
    private void handleCreateEvent(final MakeGoodLaunch launch) {
        // TODO This marker is to avoid calling create() twice by PDT.
        if (createEventFired(launch)) {
            return;
        }

        if (!TestLifecycle.getInstance().validateLaunchIdentity(launch)) {
            return;
        }

        markAsCreateEventFired(launch);

        testLifecycle = TestLifecycle.getInstance();
        try {
            testLifecycle.initialize(new ResultProjector());
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            return;
        }

        preventConsoleViewFocusing();

        Job job = new UIJob("MakeGood Test Start") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                testLifecycle.start();
                MakeGoodContext.getInstance().updateStatus(MakeGoodStatus.RunningTest);

                ResultView resultView = (ResultView) ViewOpener.find(ResultView.VIEW_ID);
                if (resultView == null) {
                    resultView = (ResultView) ViewOpener.open(ResultView.VIEW_ID);
                }
                MakeGoodContext.getInstance().getTestRunner().restoreFocusToLastActivePart();

                if (resultView != null) {
                    resultView.startTest(testLifecycle);
                }

                ResultSquare.getInstance().startTest();

                try {
                    new TestMarkerFactory().clear(TestLifecycle.getInstance().getTestTargets().getProject());
                    new FatalErrorMarkerFactory().clear(TestLifecycle.getInstance().getTestTargets().getProject());
                } catch (CoreException e) {
                    Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
                }

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

        if (!createEventFired(launch)) {
            handleCreateEvent(launch);
        }

        if (!createEventFired(launch)) {
            return;
        }

        markAsTerminateEventFired(launch);

        Job job = new UIJob("MakeGood Test End") { //$NON-NLS-1$
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {
                testLifecycle.end();
                if (testLifecycle.getProgress().noTestsFound()) {
                    MakeGoodContext.getInstance().updateStatus(MakeGoodStatus.TestsNotFound);
                } else {
                    MakeGoodContext.getInstance().updateStatus(
                        MakeGoodStatus.WaitingForTestRun,
                        MakeGoodContext.getInstance().getActivePart().getProject()
                    );
                }

                ResultSquare.getInstance().endTest();
                if (testLifecycle.getProgress().hasFailures()) {
                    ResultSquare.getInstance().markAsFailed();
                } else {
                    if (testLifecycle.getProgress().noTestsFound()) {
                        ResultSquare.getInstance().markAsNoTests();
                    } else {
                        ResultSquare.getInstance().markAsPassed();
                    }
                }

                ResultView resultView = (ResultView) ViewOpener.find(ResultView.VIEW_ID);
                if (resultView != null) {
                    resultView.endTest();
                }

                try {
                    if (testLifecycle.hasErrors()) {
                        ResultSquare.getInstance().markAsStopped();
                        if (resultView != null) {
                            testLifecycle.getProgress().markAsStopped();
                            resultView.markAsStopped();
                            resultView.expandResultTreeToResult(testLifecycle.getProgress().getResult().getLast());
                        }
                        if (!StopTestRunAction.isStoppedByAction(launch)) {
                            FatalErrorMarkerFactory markerFactory = new FatalErrorMarkerFactory();
                            try {
                                IMarker marker = markerFactory.create(launch.getStreamOutput());
                                if (marker != null) {
                                    EditorOpener.open(marker);
                                } else {
                                    ViewOpener.open(IConsoleConstants.ID_CONSOLE_VIEW);
                                    EditorOpener.open(markerFactory.getFile(), markerFactory.getLine());
                                }
                            } catch (CoreException e) {
                                Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
                            } catch (UnknownFatalErrorMessageException e) {
                                Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
                                ViewOpener.open(IConsoleConstants.ID_CONSOLE_VIEW);
                            }
                        }
                    }
                } catch (DebugException e) {
                    Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
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

    /**
     * @since 2.2.0
     */
    private ILaunch extractLaunchFromDebugTarget(Object eventSource) {
        if (eventSource instanceof IPHPDebugTarget) {
            return ((IPHPDebugTarget) eventSource).getLaunch();
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

    public class ResultProjector implements ResultReaderListener {
        @Override
        public void startTestSuite(TestSuiteResult testSuite) {
        }

        @Override
        public void endTestSuite(TestSuiteResult testSuite) {
        }

        @Override
        public void startTestCase(final TestCaseResult testCase) {
            Job job = new UIJob("MakeGood Test Case Start") { //$NON-NLS-1$
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    ResultView resultView = (ResultView) ViewOpener.find(ResultView.VIEW_ID);
                    if (resultView != null) {
                        resultView.updateOnStartTestCase(testCase);
                    }
                    return Status.OK_STATUS;
                }
            };
            job.schedule();
        }

        @Override
        public void endTestCase(final TestCaseResult testCase) {
            if (Job.getJobManager().find(testLifecycle).length == 0) {
                EndTestCaseUIJob job = new EndTestCaseUIJob("MakeGood Test Case End", testLifecycle); //$NON-NLS-1$
                job.schedule();
            }
        }

        @Override
        public void startFailure(final TestCaseResult failure) {
            Job job = new UIJob("MakeGood Marker Create") { //$NON-NLS-1$
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    try {
                        new TestMarkerFactory().create(failure);
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

        /**
         * @since 1.7.0
         */
        @Override
        public void startError(final TestCaseResult error) {
            startFailure(error);
        }

        /**
         * @since 1.7.0
         */
        @Override
        public void endError(TestCaseResult error) {
        }

        /**
         * @since 1.7.0
         */
        @Override
        public void startTest() {
        }

        @Override
        public void endTest() {
        }

        /**
         * @since 1.7.0
         */
        @Override
        public void onFirstTestSuite(final TestSuiteResult testSuite) {
            Job job = new UIJob("MakeGood Result Tree Set") { //$NON-NLS-1$
                @Override
                public IStatus runInUIThread(IProgressMonitor monitor) {
                    ResultView resultView = (ResultView) ViewOpener.find(ResultView.VIEW_ID);
                    if (resultView != null) {
                        resultView.setTreeInput(testSuite);
                    }
                    return Status.OK_STATUS;
                }
            };
            job.schedule();
        }
    }

    /**
     * @since 1.9.0
     */
    private class EndTestCaseUIJob extends UIJob {
        private TestLifecycle testLifecycle;

        public EndTestCaseUIJob(String name, TestLifecycle testLifecycle) {
            super(name);
            this.testLifecycle = testLifecycle;
        }

        @Override
        public IStatus runInUIThread(IProgressMonitor monitor) {
            ResultView resultView = (ResultView) ViewOpener.find(ResultView.VIEW_ID);
            if (resultView != null) {
                resultView.updateOnEndTestCase();
            }
            return Status.OK_STATUS;
        }

        @Override
        public boolean belongsTo(Object family) {
            return testLifecycle.equals(family);
        }
    }
}
