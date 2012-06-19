/**
 * Copyright (c) 2011-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.php.internal.debug.core.IPHPDebugConstants;
import org.eclipse.php.internal.debug.core.zend.communication.DebuggerCommunicationDaemon;
import org.xml.sax.SAXException;

import com.piece_framework.makegood.core.run.Failures;
import com.piece_framework.makegood.core.run.Progress;
import com.piece_framework.makegood.core.run.ResultReader;
import com.piece_framework.makegood.core.run.ResultReaderListener;

/**
 * @since 1.2.0
 */
public class TestLifecycle {
    private Progress progress = new Progress();
    private Failures failures = new Failures();
    private MakeGoodLaunch launch;

    private ResultReader resultReader;
    private Thread resultReaderThread;

    /**
     * @since 1.4.0
     */
    private boolean isDestroyed = false;

    /**
     * @since 1.9.0
     */
    private Date endTime;

    private static TestLifecycle currentTestLifecycle;

    private TestLifecycle() {
        super();
    }

    /**
     * Creates a ResultReader object and a thread for reading the result.
     *
     * @param resultReaderListener
     * @throws CoreException
     * @since 1.9.0
     */
    public void initialize(ResultReaderListener resultReaderListener) throws CoreException {
        resultReader = createResultReader(resultReaderListener);
        resultReaderThread = createResultReaderThread();
    }

    public void start() {
        progress.start();
        resultReaderThread.start();
    }

    public void end() {
        resultReader.stop();

        try {
            resultReaderThread.join();
        } catch (InterruptedException e) {
            Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
        }

        progress.end();
        endTime = new Date();
    }

    public Progress getProgress() {
        return progress;
    }

    public boolean hasErrors() {
        for (IProcess process: launch.getProcesses()) {
            int exitValue = 0;
            try {
                if (!process.isTerminated()) continue;
                exitValue = process.getExitValue();
            } catch (DebugException e) {
                Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            }

            if (exitValue != 0) {
                return true;
            }

            break;
        }

        return false;
    }

    public Failures getFailures() {
        return failures;
    }

    public boolean validateLaunchIdentity(MakeGoodLaunch launch) {
        return this.launch.equals(launch);
    }

    public static void create() {
        currentTestLifecycle = new TestLifecycle();
    }

    public static void destroy() {
        if (currentTestLifecycle.launch != null) {
            currentTestLifecycle.launch.removeStreamListener();
        }
        currentTestLifecycle.isDestroyed = true;
    }

    public static boolean isRunning() {
        return currentTestLifecycle != null && (!currentTestLifecycle.isDestroyed && !currentTestLifecycle.isAborted());
    }

    public static TestLifecycle getInstance() {
        return currentTestLifecycle;
    }

    /**
     * @since 1.4.0
     */
    public void setLaunch(MakeGoodLaunch launch) {
        this.launch = launch;
    }

    /**
     * @since 1.9.0
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @since 1.4.0
     */
    private boolean isAborted() {
        try {
            return launch != null
                && launch.getDebugTarget() == null
                && (launch.getLaunchConfiguration() != null && launch.getLaunchConfiguration().getAttribute(IPHPDebugConstants.RUN_WITH_DEBUG_INFO, true))
                && DebuggerCommunicationDaemon.ZEND_DEBUGGER_ID.equals(new PHPexeItemRepository().findByProject(TestTargetRepository.getInstance().getProject()).getDebuggerID());
        } catch (CoreException e) {
            return true;
        }
    }

    /**
     * @since 1.7.0
     */
    private ResultReader createResultReader(ResultReaderListener resultReaderListener) throws CoreException {
        ResultReader resultReader = new ResultReader(new File(MakeGoodLaunchConfigurationDelegate.getJUnitXMLFile(launch)));
        resultReader.addListener(progress);
        resultReader.addListener(failures);
        resultReader.addListener(resultReaderListener);
        return resultReader;
    }

    /**
     * @since 1.7.0
     */
    private Thread createResultReaderThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    resultReader.read();
                } catch (ParserConfigurationException e) {
                    Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
                } catch (SAXException e) {
                } catch (IOException e) {
                    Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
                }
            }
        };
        return thread;
    }
}
