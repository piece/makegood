/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2012 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import com.piece_framework.makegood.launch.PHPexeItemRepository;
import com.piece_framework.makegood.launch.RuntimeConfiguration;
import com.piece_framework.makegood.launch.TestLifecycle;
import com.piece_framework.makegood.launch.TestTargetRepository;
import com.piece_framework.makegood.ui.ActivePart;
import com.piece_framework.makegood.ui.Messages;
import com.piece_framework.makegood.ui.MakeGoodContext;
import com.piece_framework.makegood.ui.views.ViewOpener;

public class TestRunner {
    private MakeGoodLaunchShortcut lastShortcut;
    private Object lastTestTarget;
    private IWorkbenchPart lastActivePart;

    /**
     * @since 1.4.0
     */
    private boolean isTestRunByAutotest = false;

    /**
     * @since 1.6.0
     */
    private PHPexeItemRepository phpexeItemRepository = new PHPexeItemRepository();

    public void runRelatedTests(IEditorPart editorPart) {
        runTests(editorPart, new RelatedTestsLaunchShortcut());
    }

    public void runTestsInContext(IEditorPart editorPart) {
        runTests(editorPart, new ContextLaunchShortcut());
    }

    public void runTestsInClass(IEditorPart editorPart) {
        runTests(editorPart, new ClassLaunchShortcut());
    }

    public void runTestsInFile(IEditorPart editorPart) {
        runTests(editorPart, new FileLaunchShortcut());
    }

    public void runTestsFromExplorer(ISelection selection) {
        runTests(selection, new ResourceLaunchShortcut());
    }

    public void runAllTestsByAutotest(ISelection selection) {
        isTestRunByAutotest = true;
        runTests(selection, new AllTestsLaunchShortcut());
        isTestRunByAutotest = false;
    }

    public void runAllTests() {
        runTests(MakeGoodContext.getInstance().getActivePart().getEntity(), new AllTestsLaunchShortcut());
    }

    public boolean hasLastTest() {
        if (lastTestTarget == null) return false;
        IProject lastTestProject = TestTargetRepository.getInstance().getProject();
        if (lastTestProject == null) return false;
        if (!lastTestProject.equals(MakeGoodContext.getInstance().getActivePart().getProject())) return false;
        return true;
    }

    public void rerunLastTest() {
        runTests(lastTestTarget, lastShortcut);
    }

    /**
     * @since 1.4.0
     */
    public void rerunLastTestByAutotest() {
        isTestRunByAutotest = true;
        rerunLastTest();
        isTestRunByAutotest = false;
    }

    public void restoreFocusToLastActivePart() {
        if (lastActivePart != null) {
            ViewOpener.setFocus(lastActivePart);
        }
    }

    private void runTests(Object testTarget, MakeGoodLaunchShortcut shortcut) {
        synchronized (TestRunner.class) {
            if (TestLifecycle.isRunning()) {
                if (!isTestRunByAutotest) {
                    raiseTestSessionAlreadyExistsError();
                }

                return;
            }
            TestLifecycle.create();
        }

        if (hasPHPexeItem()) {
            if (!isTestRunByAutotest) {
                lastShortcut = shortcut;
                lastTestTarget = testTarget;
            }

            lastActivePart = ActivePart.getActivePart();
        }

        String launchMode = RuntimeConfiguration.getInstance().getLaunchMode();

        try {
            if (testTarget instanceof ISelection) {
                shortcut.launch((ISelection) testTarget, launchMode);
            } else if (testTarget instanceof IEditorPart) {
                shortcut.launch((IEditorPart) testTarget, launchMode);
            }
        } catch (NotLaunchedException e) {
            TestLifecycle.destroy();
        }

        if (!hasPHPexeItem()) {
            TestLifecycle.destroy();
        }
    }

    private void raiseTestSessionAlreadyExistsError() {
        final Display display = Display.getDefault();
        display.syncExec(new Runnable() {
            @Override
            public void run() {
                MessageDialog.openError(
                    display.getActiveShell(),
                    Messages.TestRunner_TestSessionAlreadyExists_Title,
                    Messages.TestRunner_TestSessionAlreadyExists_Message
                );
            }
        });
    }

    /**
     * @since 1.4.0
     */
    private boolean hasPHPexeItem() {
        return phpexeItemRepository.findByProject(TestTargetRepository.getInstance().getProject()) != null;
    }
}
