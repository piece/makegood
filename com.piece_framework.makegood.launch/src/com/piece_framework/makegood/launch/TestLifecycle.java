/**
 * Copyright (c) 2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.launch;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.php.internal.debug.core.IPHPDebugConstants;
import org.eclipse.php.internal.debug.core.model.IPHPDebugTarget;
import org.eclipse.php.internal.debug.core.zend.communication.DebuggerCommunicationDaemon;
import org.xml.sax.SAXException;

import com.piece_framework.makegood.core.result.TestCaseResult;
import com.piece_framework.makegood.core.result.TestSuiteResult;
import com.piece_framework.makegood.core.run.Failures;
import com.piece_framework.makegood.core.run.JUnitXMLReader;
import com.piece_framework.makegood.core.run.JUnitXMLReaderListener;
import com.piece_framework.makegood.core.run.Progress;

/**
 * @since 1.2.0
 */
public class TestLifecycle {
    private Progress progress = new Progress();
    private boolean hasErrors = false;
    private Failures failures = new Failures();
    private ILaunch launch;

    /**
     * @since 1.3.0
     */
    private StreamListener outputStreamListener = new StreamListener();

    private JUnitXMLReader junitXMLReader;
    private Thread junitXMLReaderThread;

    /**
     * @since 1.4.0
     */
    private boolean isDestroyed = false;

    private static TestLifecycle currentTestLifecycle;

    private TestLifecycle() {
        super();
    }

    public void start(JUnitXMLReaderListener junitXMLReaderListener) throws CoreException {
        junitXMLReader = new JUnitXMLReader(new File(MakeGoodLaunchConfigurationDelegate.getJUnitXMLFile(launch)));
        junitXMLReader.addListener(junitXMLReaderListener);

        junitXMLReaderThread = new Thread() {
            @Override
            public void run() {
                try {
                    junitXMLReader.read();
                } catch (ParserConfigurationException e) {
                    Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
                } catch (SAXException e) {
                    hasErrors = true;
                } catch (IOException e) {
                    Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
                }
            }
        };

        progress.start();
        junitXMLReaderThread.start();

        for (IProcess process: launch.getProcesses()) {
            if (process.isTerminated()) continue;
            IStreamsProxy streamsProxy = process.getStreamsProxy();
            if (streamsProxy == null) continue;
            IStreamMonitor outputStreamMonitor = streamsProxy.getOutputStreamMonitor();
            if (outputStreamMonitor == null) continue;
            outputStreamMonitor.addListener(outputStreamListener);
        }
    }

    public void end() {
        for (IProcess process: launch.getProcesses()) {
            if (process.isTerminated()) continue;
            IStreamsProxy streamsProxy = process.getStreamsProxy();
            if (streamsProxy == null) continue;
            IStreamMonitor outputStreamMonitor = streamsProxy.getOutputStreamMonitor();
            if (outputStreamMonitor == null) continue;
            outputStreamMonitor.removeListener(outputStreamListener);
        }

        for (IProcess process: launch.getProcesses()) {
            int exitValue = 0;
            try {
                if (!process.isTerminated()) continue;
                exitValue = process.getExitValue();
            } catch (DebugException e) {
                Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            }

            if (exitValue != 0) {
                hasErrors = true;
            }

            break;
        }

        junitXMLReader.stop();

        // TODO Since PDT 2.1 always returns 0 from IProcess.getExitValue(), We decided to use SAXException to check whether or not a PHP process exited with a fatal error.
        try {
            junitXMLReaderThread.join();
        } catch (InterruptedException e) {
            Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
        }

        progress.end();
    }

    public Progress getProgress() {
        return progress;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public TestSuiteResult getResult() {
        if (junitXMLReader == null) return null;
        return junitXMLReader.getResult();
    }

    public Failures getFailures() {
        return failures;
    }

    public void endTest() {
        progress.markAsCompleted();
    }

    public void startFailure(TestCaseResult failure) {
        failures.markCurrentResultAsFailure();
    }

    public void endTestCase(TestCaseResult testCase) {
        progress.endTestCase();
        testCase.setTime(progress.getProcessTimeForTestCase());
    }

    public void startTestCase(TestCaseResult testCase) {
        failures.addResult(testCase);
        progress.startTestCase();
    }

    public void startTestSuite(TestSuiteResult testSuite) {
        failures.addResult(testSuite);
    }

    public boolean validateLaunchIdentity(MakeGoodLaunch launch) {
        return this.launch.equals(launch);
    }

    public static void create() {
        currentTestLifecycle = new TestLifecycle();
    }

    public static void destroy() {
        currentTestLifecycle.isDestroyed = true;
    }

    public static boolean isRunning() {
        return currentTestLifecycle != null && (!currentTestLifecycle.isDestroyed && !currentTestLifecycle.isAborted());
    }

    public static TestLifecycle getInstance() {
        return currentTestLifecycle;
    }

    /**
     * @since 1.3.0
     */
    public String getOutputContents() {
        IDebugTarget debugTarget = launch.getDebugTarget();
        if (debugTarget != null && debugTarget instanceof IPHPDebugTarget) {
            return ((IPHPDebugTarget) debugTarget).getOutputBuffer().toString();
        }
        return outputStreamListener.getContents();
    }

    /**
     * @since 1.4.0
     */
    public void setLaunch(ILaunch launch) {
        this.launch = launch;
    }

    /**
     * @since 1.3.0
     */
    private class StreamListener implements IStreamListener {
        private StringBuilder contents = new StringBuilder();

        @Override
        public void streamAppended(String text, IStreamMonitor monitor) {
            contents.append(text);
        }

        public String getContents() {
            return contents.toString();
        }
    }

    /**
     * @since 1.4.0
     */
    private boolean isAborted() {
        try {
            return launch != null
                && launch.getDebugTarget() == null
                && (launch.getLaunchConfiguration() != null && launch.getLaunchConfiguration().getAttribute(IPHPDebugConstants.RUN_WITH_DEBUG_INFO, true))
                && DebuggerCommunicationDaemon.ZEND_DEBUGGER_ID.equals(new PHPexeItemRepository().findByProject(TestingTargets.getInstance().getProject()).getDebuggerID());
        } catch (CoreException e) {
            return true;
        }
    }
}
