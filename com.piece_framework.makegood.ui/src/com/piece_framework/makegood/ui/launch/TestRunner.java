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
        MakeGoodLaunchShortcut shortcut = new MakeGoodLaunchShortcut();
        runTests(selection, shortcut);
    }

    public static boolean hasLastTest() {
        return lastTarget != null;
    }

    public static void rerunLastTest() {
        runTests(lastTarget, lastShortcut);
    }

    private static void runTests(Object target, MakeGoodLaunchShortcut shortcut) {
        MakeGoodProperty property = new MakeGoodProperty(getResource(target));
        if (!property.exists()) {
            showPropertyPage(property, target, shortcut);
            return;
        }

        lastShortcut = shortcut;
        lastTarget = target;

        if (target instanceof ISelection) {
            shortcut.launch((ISelection) target, MODE_RUN); //$NON-NLS-1$
        } else if (target instanceof IEditorPart) {
            shortcut.launch((IEditorPart) target, MODE_RUN); //$NON-NLS-1$
        }
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
                                         final MakeGoodLaunchShortcut shortcut
                                         ) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                PropertyDialog dialog = PropertyDialog.createDialogOn(null,
                                                                      "com.piece_framework.makegood.ui.propertyPages.makeGood", //$NON-NLS-1$
                                                                      property.getProject()
                                                                      );
                if (dialog.open() == Window.OK) {
                    runTests(target, shortcut);
                }
            }
        });
    }
}
