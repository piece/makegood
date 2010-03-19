package com.piece_framework.makegood.ui.launch;

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.dialogs.PropertyDialog;

import com.piece_framework.makegood.core.MakeGoodProperty;

public class TestRunner {
    private static String MODE_RUN ="run";
    private static Object lastTarget;
    private static int lastRunLevel;

    public static void runRelatedTests(IEditorPart editorPart) {
        runTestsOnEditor(editorPart, MakeGoodLaunchShortcut.RUN_RELATED_TESTS);
    }

    public static void runTestsInContext(IEditorPart editorPart) {
        runTestsOnEditor(editorPart, MakeGoodLaunchShortcut.RUN_TEST_ON_CONTEXT);
    }

    public static void runTestsInClass(IEditorPart editorPart) {
        runTestsOnEditor(editorPart, MakeGoodLaunchShortcut.RUN_TESTS_ON_CLASS);
    }

    public static void runTestsInFile(IEditorPart editorPart) {
        runTestsOnEditor(editorPart, MakeGoodLaunchShortcut.RUN_TESTS_ON_FILE);
    }

    public static void runTests(ISelection selection) {
        MakeGoodProperty property = new MakeGoodProperty(getResource(selection));
        if (!property.exists()) {
            showPropertyPage(property, selection, 0);
            return;
        }

        lastTarget = selection;

        MakeGoodLaunchShortcut shortcut = new MakeGoodLaunchShortcut();
        shortcut.launch(selection, MODE_RUN); //$NON-NLS-1$
    }

    public static boolean hasLastTest() {
        return lastTarget != null;
    }

    public static void rerunLastTest() {
        if (lastTarget instanceof ISelection) {
            runTests((ISelection) lastTarget);
        } else if (lastTarget instanceof IEditorPart) {
            runTestsOnEditor((IEditorPart) lastTarget, lastRunLevel);
        }
    }

    private static void runTestsOnEditor(IEditorPart editorPart, int runLevel) {
        MakeGoodProperty property = new MakeGoodProperty(getResource(editorPart));
        if (!property.exists()) {
            showPropertyPage(property, editorPart, runLevel);
            return;
        }

        lastTarget = editorPart;
        lastRunLevel = runLevel;

        MakeGoodLaunchShortcut shortcut = new MakeGoodLaunchShortcut();
        shortcut.setRunLevelOnEditor(runLevel);
        shortcut.launch(editorPart, MODE_RUN); //$NON-NLS-1$
    }

    private static IResource getResource(Object target) {
        if (target instanceof IStructuredSelection) {
            IStructuredSelection selection = (IStructuredSelection) target;
            if (selection.getFirstElement() instanceof IModelElement) {
                return ((IModelElement) selection.getFirstElement()).getResource();
            } else if (selection.getFirstElement() instanceof IResource) {
                return (IResource) selection.getFirstElement();
            }
        } else if (target instanceof IEditorPart) {
            ISourceModule source = EditorUtility.getEditorInputModelElement((IEditorPart) target, false);
            return source.getResource();
        }
        return null;
    }

    private static void showPropertyPage(final MakeGoodProperty property,
                                         final Object target,
                                         final int runLevel
                                         ) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                PropertyDialog dialog = PropertyDialog.createDialogOn(null,
                                                                      "com.piece_framework.makegood.ui.propertyPages.makeGood", //$NON-NLS-1$
                                                                      property.getProject()
                                                                      );
                if (dialog.open() == Window.OK) {
                    if (target instanceof ISelection) {
                        runTests((ISelection) target);
                    } else if (target instanceof IEditorPart) {
                        runTestsOnEditor((IEditorPart) target, runLevel);
                    }
                }
            }
        });
    }
}
