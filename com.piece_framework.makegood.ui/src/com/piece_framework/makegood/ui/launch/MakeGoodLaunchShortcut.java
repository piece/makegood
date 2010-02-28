package com.piece_framework.makegood.ui.launch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.dltk.compiler.env.ISourceField;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ITypeHierarchy;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.framework.debug.Debug;
import org.eclipse.php.internal.debug.ui.launching.PHPExeLaunchShortcut;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.eclipse.ui.texteditor.ITextEditor;

import com.piece_framework.makegood.core.MakeGoodProperty;
import com.piece_framework.makegood.core.PHPResource;
import com.piece_framework.makegood.launch.MakeGoodLaunchParameter;
import com.piece_framework.makegood.ui.Messages;

public class MakeGoodLaunchShortcut extends PHPExeLaunchShortcut {
    public static int RUN_TEST_ON_CONTEXT = 1;
    public static int RUN_TESTS_ON_CLASS = 2;
    public static int RUN_TESTS_ON_FILE = 3;
    public static int RUN_RELATED_TESTS = 4;

    private int runLevelOnEditor = RUN_TEST_ON_CONTEXT;

    private Object lastTarget;
    private String lastMode;

    private static MakeGoodLaunchShortcut shortcut;

    private MakeGoodLaunchShortcut() {
    }

    public static MakeGoodLaunchShortcut get() {
        if (shortcut == null) {
            shortcut = new MakeGoodLaunchShortcut();
        }
        return shortcut;
    }

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

        lastTarget = selection;
        lastMode = mode;

        Object target = ((IStructuredSelection) selection).getFirstElement();
        MakeGoodLaunchParameter parameter = MakeGoodLaunchParameter.get();
        parameter.clearTargets();
        parameter.addTarget(target);

        ISelection element = new StructuredSelection(parameter.getMainScriptResource());
        super.launch(element, mode);
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
        MakeGoodProperty property = new MakeGoodProperty(getResource(editor));
        if (!property.exists()) {
            showPropertyPage(property, editor, mode);
            return;
        }

        if (!(editor instanceof ITextEditor)) {
            return;
        }

        MakeGoodLaunchParameter parameter = MakeGoodLaunchParameter.get();
        parameter.clearTargets();

        lastTarget = editor;
        lastMode = mode;

        if (runLevelOnEditor != RUN_RELATED_TESTS) {
            parameter.addTarget(getElementOnRunLevel(editor));
        } else {
            launchTestsForProductCode(editor, mode);
            return;     // Run tests by launchTestsForProductCode().
        }

        super.launch(editor, mode);
    }

    public boolean hasLastTest() {
        return lastTarget != null;
    }

    public void rerunLastTest() {
        if (lastTarget instanceof ISelection) {
            launch((ISelection) lastTarget, lastMode);
        } else if (lastTarget instanceof IEditorPart) {
            launch((IEditorPart) lastTarget, lastMode);
        }
    }

    @Override
    protected ILaunchConfigurationType getPHPExeLaunchConfigType() {
        ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
        return manager.getLaunchConfigurationType("com.piece_framework.makegood.launch.launchConfigurationTypes.makeGood"); //$NON-NLS-1$
    }

    private void showPropertyPage(final MakeGoodProperty property,
                                  final Object target,
                                  final String mode
                                  ) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                PropertyDialog dialog = PropertyDialog.createDialogOn(null,
                                                                      "com.piece_framework.makegood.ui.propertyPages.makeGood", //$NON-NLS-1$
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
        EditorParser parser = new EditorParser(editor);
        ISourceModule source = parser.getSourceModule();
        if (source == null) {
            return null;
        }
        if (runLevelOnEditor == RUN_TESTS_ON_FILE) {
            return source;
        }

        IModelElement element = parser.getModelElementOnSelection();
        if (element == null) {
            return source;
        }
        if (element.getElementType() == IModelElement.FIELD) {
            element = element.getParent();
        }

        IModelElement elementOnRunLevel = null;
        if (runLevelOnEditor == RUN_TEST_ON_CONTEXT) {
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

    private void launchTestsForProductCode(final IEditorPart editor,
                                           final String mode
                                           ) {
        SearchRequestor requestor = new SearchRequestor() {
            Set<IResource> tests = new HashSet<IResource>();

            @Override
            public void acceptSearchMatch(SearchMatch match) throws CoreException {
                IModelElement element = DLTKCore.create(match.getResource());
                if (!(element instanceof ISourceModule)) {
                    return;
                }
                if (!PHPResource.includeTestClass((ISourceModule) element)) {
                    return;
                }

                tests.add(match.getResource());
            }

            @Override
            public void endReporting() {
                EditorParser parser = new EditorParser(editor);
                ISourceModule source = parser.getSourceModule();
                if (source != null && PHPResource.includeTestClass(source)) {
                    tests.add(source.getResource());
                }

                if (tests.size() == 0) {
                    MessageDialog.openInformation(editor.getEditorSite().getShell(),
                                                  Messages.MakeGoodLaunchShortcut_messageTitle,
                                                  Messages.MakeGoodLaunchShortcut_notFoundTestsMessage
                                                  );
                    return;
                }

                MakeGoodLaunchParameter parameter = MakeGoodLaunchParameter.get();
                parameter.clearTargets();
                for (IResource test: tests) {
                    Debug.println(test);
                    parameter.addTarget(test);
                }
                MakeGoodLaunchShortcut.super.launch(editor, mode);
            }
        };

        List<IType> types = getTypes(editor);
        if (types == null || types.size() == 0) {
            return;
        }

        IDLTKLanguageToolkit toolkit = DLTKLanguageManager.getLanguageToolkit(types.get(0));
        if (toolkit == null) {
            return;
        }

        StringBuilder patternString = new StringBuilder();
        for (IType type: types) {
            Debug.println(type.getElementName());
            patternString.append(patternString.length() > 0 ? "|" : ""); //$NON-NLS-1$ //$NON-NLS-2$
            patternString.append(type.getElementName());
        }
        SearchPattern pattern = SearchPattern.createPattern(patternString.toString(),
                                                            IDLTKSearchConstants.TYPE,
                                                            IDLTKSearchConstants.REFERENCES,
                                                            SearchPattern.R_REGEXP_MATCH,
                                                            toolkit
                                                            );
        IDLTKSearchScope scope = SearchEngine.createSearchScope(types.get(0).getScriptProject());
        SearchEngine engine = new SearchEngine();
        try {
            engine.search(pattern,
                          new SearchParticipant[] {SearchEngine.getDefaultSearchParticipant()},
                          scope,
                          requestor,
                          null
                          );
        } catch (CoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private List<IType> getTypes(IEditorPart editor) {
        EditorParser parser = new EditorParser(editor);
        ISourceModule source = parser.getSourceModule();
        if (source == null) {
            return null;
        }

        List<IType> types = new ArrayList<IType>();
        try {
            for (IType type: source.getAllTypes()) {
                types.add(type);

                ITypeHierarchy hierarchy = type.newTypeHierarchy(new NullProgressMonitor());
                if (hierarchy != null) {
                    for (IType subClass : hierarchy.getAllSubtypes(type)) {
                        types.add(subClass);
                    }
                }
            }
        } catch (ModelException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return types;
    }
}
