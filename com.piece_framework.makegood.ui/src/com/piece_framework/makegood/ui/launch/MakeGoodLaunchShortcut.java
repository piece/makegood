package com.piece_framework.makegood.ui.launch;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.ScriptModelUtil;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.php.internal.debug.ui.launching.PHPExeLaunchShortcut;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.eclipse.ui.texteditor.ITextEditor;

import com.piece_framework.makegood.core.MakeGoodProperty;
import com.piece_framework.makegood.launch.MakeGoodLaunchParameter;

public class MakeGoodLaunchShortcut extends PHPExeLaunchShortcut {
    public static int RUN_TEST_ON_CURSOR = 1;
    public static int RUN_TESTS_ON_CLASS = 2;
    public static int RUN_TESTS_ON_FILE = 3;

    private int runLevelOnEditor = RUN_TEST_ON_CURSOR;

    public void setRunLevelOnEditor(int runLevel) {
        this.runLevelOnEditor = runLevel;
    }

    @Override
    public void launch(final ISelection selection, final String mode) {
        final MakeGoodProperty property = new MakeGoodProperty(getResource(selection));
        if (!property.exists()) {
            showPropertyPage(property, selection, mode);
            return;
        }

        if (!(selection instanceof IStructuredSelection)) {
            return;
        }

        Object target = ((IStructuredSelection) selection).getFirstElement();
        MakeGoodLaunchParameter parameter = MakeGoodLaunchParameter.get();
        parameter.setTarget(target);

        ISelection element = new StructuredSelection(parameter.getScriptResource());
        super.launch(element, mode);
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
        final MakeGoodProperty property = new MakeGoodProperty(getResource(editor));
        if (!property.exists()) {
            showPropertyPage(property, editor, mode);
            return;
        }

        if (!(editor instanceof ITextEditor)) {
            return;
        }

        MakeGoodLaunchParameter parameter = MakeGoodLaunchParameter.get();
        parameter.setTarget(getElementOnRunLevel(editor));

        super.launch(editor, mode);
    }

    @Override
    protected ILaunchConfigurationType getPHPExeLaunchConfigType() {
        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        return manager.getLaunchConfigurationType("com.piece_framework.makegood.launch.launchConfigurationType");
    }

    private void showPropertyPage(final MakeGoodProperty property,
                                  final Object target,
                                  final String mode
                                  ) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                PropertyDialog dialog = PropertyDialog.createDialogOn(null,
                                                                      "com.piece_framework.makegood.ui.MakeGood",
                                                                      property.getProject()
                                                                      );
                if (dialog.open() == Window.OK) {
                    int runLevelOnEditor = MakeGoodLaunchShortcut.this.runLevelOnEditor;
                    MakeGoodLaunchShortcut shortcut = new MakeGoodLaunchShortcut();
                    shortcut.setRunLevelOnEditor(runLevelOnEditor);
                    if (target instanceof ISelection) {
                        shortcut.launch((ISelection) target, mode);
                    } else if (target instanceof IEditorPart) {
                        shortcut.launch((IEditorPart) target, mode);
                    }
                }
            }
        });
    }

    private IResource getResource(Object target) {
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

    private IModelElement getElementOnRunLevel(IEditorPart editor) {
        ISourceModule source = EditorUtility.getEditorInputModelElement(editor, false);
        if (source == null) {
            return null;
        }
        if (runLevelOnEditor == RUN_TESTS_ON_FILE) {
            return source;
        }

        ITextEditor textEditor = (ITextEditor) editor;
        ISelectionProvider provider = (ISelectionProvider) textEditor.getSelectionProvider();
        ITextSelection selection = (ITextSelection) provider.getSelection();
        int offset = selection.getOffset();

        IModelElement element = null;
        try {
            ScriptModelUtil.reconcile(source);
            element = source.getElementAt(offset);
        } catch (ModelException e) {
        }

        IModelElement elementOnRunLevel = null;
        if (runLevelOnEditor == RUN_TEST_ON_CURSOR) {
            elementOnRunLevel = element;
        } else if (runLevelOnEditor == RUN_TESTS_ON_CLASS) {
            if (element instanceof IMethod) {
                elementOnRunLevel = ((IMethod) element).getParent();
            } else {
                elementOnRunLevel = element;
            }
        }
        return elementOnRunLevel;
    }
}
