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

package com.piece_framework.makegood.ui;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.xml.sax.SAXException;

import com.piece_framework.makegood.core.result.JUnitXMLReader;
import com.piece_framework.makegood.core.result.JUnitXMLReaderListener;
import com.piece_framework.makegood.core.result.RunProgress;
import com.piece_framework.makegood.core.result.TestCaseResult;
import com.piece_framework.makegood.core.result.TestSuiteResult;
import com.piece_framework.makegood.launch.MakeGoodLaunchConfigurationDelegate;
import com.piece_framework.makegood.ui.actions.StopTestAction;
import com.piece_framework.makegood.ui.views.Failures;

/**
 * @since 1.2.0
 */
public class TestRun {
    private RunProgress runProgress = new RunProgress();
    private boolean hasErrors = false;
    private Failures failures = new Failures();
    private ILaunch launch;
    private JUnitXMLReader junitXMLReader;
    private Thread parserThread;
    private TestCaseResult currentTestCase;

    public TestRun(ILaunch launch, JUnitXMLReaderListener junitXMLReaderListener) throws CoreException {
        this.launch = launch;

        junitXMLReader = new JUnitXMLReader(new File(MakeGoodLaunchConfigurationDelegate.getJUnitXMLFile(launch)));
        junitXMLReader.addListener(junitXMLReaderListener);

        parserThread = new Thread() {
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
    }

    public void start() {
        runProgress.start();
        parserThread.start();
    }

    public void end() {
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
            parserThread.join();
        } catch (InterruptedException e) {
            Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
        }

        runProgress.end();
    }

    public RunProgress getRunProgress() {
        return runProgress;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    public boolean isStoppedByAction() {
        return StopTestAction.isStoppedByAction(launch);
    }

    public TestSuiteResult getResult() {
        if (junitXMLReader == null) return null;
        return junitXMLReader.getResult();
    }

    public Failures getFailures() {
        return failures;
    }

    public TestCaseResult getCurrentTestCase() {
        return currentTestCase;
    }

    public void setCurrentTestCase(TestCaseResult testCase) {
        currentTestCase = testCase;
    }

    public Object getLaunch() {
        return launch;
    }
}
