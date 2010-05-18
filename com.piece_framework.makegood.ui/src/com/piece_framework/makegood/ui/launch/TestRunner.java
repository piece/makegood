/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.launch;

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.internal.dialogs.PropertyDialog;

import com.piece_framework.makegood.core.MakeGoodProperty;
import com.piece_framework.makegood.launch.MakeGoodLaunchConfigurationDelegate;
import com.piece_framework.makegood.launch.RuntimeConfiguration;
import com.piece_framework.makegood.ui.Messages;

public class TestRunner {
    private static MakeGoodLaunchShortcut lastShortcut;
    private static Object lastTarget;

    public static void runRelatedTests(IEditorPart editorPart) {
        MakeGoodLaunchShortcut shortcut = new RelatedTestsLaunchShortcut();
        runTests(editorPart, shortcut);
    }

    public static void runTestsInContext(IEditorPart editorPart) {
        MakeGoodLaunchShortcut shortcut = new ContextLaunchShortcut();
        runTests(editorPart, shortcut);
    }

    public static void runTestsInClass(IEditorPart editorPart) {
        MakeGoodLaunchShortcut shortcut = new ClassLaunchShortcut();
        runTests(editorPart, shortcut);
    }

    public static void runTestsInFile(IEditorPart editorPart) {
        MakeGoodLaunchShortcut shortcut = new FileLaunchShortcut();
        runTests(editorPart, shortcut);
    }

    public static void runTests(ISelection selection) {
        MakeGoodLaunchShortcut shortcut = new ResourceLaunchShortcut();
        runTests(selection, shortcut);
    }

    public static void runAllTests(Object target) {
        MakeGoodLaunchShortcut shortcut = new AllTestsLaunchShortcut();
        runTests(target, shortcut);
    }

    public static boolean hasLastTest() {
        return lastTarget != null;
    }

    public static void rerunLastTest() {
        runTests(lastTarget, lastShortcut);
    }

    public static IResource getResource(Object target) {
        if (target instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) target;
            if (selection.getFirstElement() instanceof IModelElement) {
                return ((IModelElement) selection.getFirstElement()).getResource();
            } else if (selection.getFirstElement() instanceof IResource) {
                return (IResource) selection.getFirstElement();
            }
        } else if (target instanceof IEditorPart) {
            ISourceModule source = EditorUtility.getEditorInputModelElement((IEditorPart) target, false);
            if (source != null) {
                return source.getResource();
            }

            IEditorPart editor = (IEditorPart) target;
            if (editor.getEditorInput() instanceof IFileEditorInput) {
                return ((IFileEditorInput) editor.getEditorInput()).getFile();
            }
        }

        return null;
    }

    public static boolean isRunnableAllTests(Object target) {
        if (target == null) return false;

        IResource resource = getResource(target);
        if (resource == null) return false;
        if (!resource.getProject().exists()) return false;
        if (new MakeGoodProperty(resource).getTestFolders().size() == 0) return false;

        return true;
    }

    private static void runTests(Object target, MakeGoodLaunchShortcut shortcut) {
        MakeGoodProperty property = new MakeGoodProperty(getResource(target));
        if (!property.exists()) {
            showPropertyPage(property, target, shortcut);
            return;
        }

        if (MakeGoodLaunchConfigurationDelegate.hasActiveMakeGoodLaunches()) {
            raiseTestSessionAlreadyExistsError();
            return;
        }

        lastShortcut = shortcut;
        lastTarget = target;
        String launchMode = RuntimeConfiguration.getInstance().getLaunchMode();

        if (target instanceof ISelection) {
            shortcut.launch((ISelection) target, launchMode);
        } else if (target instanceof IEditorPart) {
            shortcut.launch((IEditorPart) target, launchMode);
        }
    }

    private static void showPropertyPage(
        final MakeGoodProperty property,
        final Object target,
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
                    runTests(target, shortcut);
                }
            }
        });
    }

    private static void raiseTestSessionAlreadyExistsError() {
        final Display display = Display.getDefault();
        display.syncExec(new Runnable() {
            @Override
            public void run() {
                if (RuntimeConfiguration.getInstance().background) return;

                MessageDialog.openError(
                    display.getActiveShell(),
                    Messages.TestRunner_TestSessionAlreadyExists_Title,
                    Messages.TestRunner_TestSessionAlreadyExists_Message
                );
            }
        });
    }
}
