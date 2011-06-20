/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
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
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.internal.dialogs.PropertyDialog;

import com.piece_framework.makegood.core.MakeGoodProperty;
import com.piece_framework.makegood.launch.PHPexeItemRepository;
import com.piece_framework.makegood.launch.RuntimeConfiguration;
import com.piece_framework.makegood.launch.TestLifecycle;
import com.piece_framework.makegood.launch.TestingTargets;
import com.piece_framework.makegood.ui.Messages;
import com.piece_framework.makegood.ui.views.ActivePart;
import com.piece_framework.makegood.ui.views.ViewOpener;

public class TestRunner {
    /**
     * @since 1.6.0
     */
    private static TestRunner soleInstance;

    private MakeGoodLaunchShortcut lastShortcut;
    private Object lastTestingTarget;
    private IWorkbenchPart lastActivePart;

    /**
     * @since 1.4.0
     */
    private boolean isTestRunByAutotest = false;

    /**
     * @since 1.6.0
     */
    private PHPexeItemRepository phpexeItemRepository = new PHPexeItemRepository();

    /**
     * @since 1.6.0
     */
    private TestRunner() {
    }

    /**
     * @since 1.6.0
     */
    public static TestRunner getInstance() {
        if (soleInstance == null) {
            soleInstance = new TestRunner();
        }

        return soleInstance;
    }

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
        runTests(ActivePart.getInstance().getEntity(), new AllTestsLaunchShortcut());
    }

    public boolean hasLastTest() {
        if (lastTestingTarget == null) return false;
        IProject lastTestingProject = TestingTargets.getInstance().getProject();
        if (lastTestingProject == null) return false;
        if (!lastTestingProject.equals(ActivePart.getInstance().getProject())) return false;
        return true;
    }

    public void rerunLastTest() {
        runTests(lastTestingTarget, lastShortcut);
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

    private void runTests(Object testingTarget, MakeGoodLaunchShortcut shortcut) {
        MakeGoodProperty property = new MakeGoodProperty(ActivePart.getResource(testingTarget));
        if (!property.exists()) {
            showPropertyPage(property, testingTarget, shortcut);
            return;
        }

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
                lastTestingTarget = testingTarget;
            }

            lastActivePart = ViewOpener.getActivePart();
        }

        String launchMode = RuntimeConfiguration.getInstance().getLaunchMode();

        try {
            if (testingTarget instanceof ISelection) {
                shortcut.launch((ISelection) testingTarget, launchMode);
            } else if (testingTarget instanceof IEditorPart) {
                shortcut.launch((IEditorPart) testingTarget, launchMode);
            }
        } catch (NotLaunchedException e) {
            TestLifecycle.destroy();
        }

        if (!hasPHPexeItem()) {
            TestLifecycle.destroy();
        }
    }

    private void showPropertyPage(
        final MakeGoodProperty property,
        final Object testingTarget,
        final MakeGoodLaunchShortcut shortcut) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                PropertyDialog dialog =
                    PropertyDialog.createDialogOn(
                        null,
                        "com.piece_framework.makegood.ui.propertyPages.makeGood", //$NON-NLS-1$
                        property.getProject()
                    );
                if (dialog.open() == Window.OK) {
                    runTests(testingTarget, shortcut);
                }
            }
        });
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
        return phpexeItemRepository.findByProject(TestingTargets.getInstance().getProject()) != null;
    }
}
