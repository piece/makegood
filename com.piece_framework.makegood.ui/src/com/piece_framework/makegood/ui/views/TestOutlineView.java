/**
 * Copyright (c) 2012-2013 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2013 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.views;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.ScriptElementImageProvider;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.dltk.ui.viewsupport.DecoratingModelLabelProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.php.core.compiler.IPHPModifiers;
import org.eclipse.php.internal.ui.preferences.PHPAppearancePreferencePage;
import org.eclipse.php.internal.ui.util.PHPPluginImages;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.ITextEditor;

import com.piece_framework.makegood.core.TestClass;
import com.piece_framework.makegood.core.TestMethod;
import com.piece_framework.makegood.core.TestingFramework;
import com.piece_framework.makegood.core.preference.MakeGoodProperty;
import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.ActiveEditor;
import com.piece_framework.makegood.ui.EditorParser;
import com.piece_framework.makegood.ui.MakeGoodContext;
import com.piece_framework.makegood.ui.Messages;

/**
 * @since 2.3.0
 */
public class TestOutlineView extends ViewPart {
    public static final String ID = "com.piece_framework.makegood.ui.views.testOutlineView"; //$NON-NLS-1$

    private static boolean isOutlineSelecting = false;

    private TreeViewer viewer;
    private List<IType> baseTestClasses;

    public TestOutlineView() {}

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new FillLayout());

        viewer = new TreeViewer(parent);
        viewer.setContentProvider(new HierarchicalLayoutContentProvider());

        viewer.setLabelProvider(
            new DecoratingModelLabelProvider(
                new TestOutlineAppearanceAwareLabelProvider()));
        TreeEventListener eventListener = new TreeEventListener();
        viewer.addSelectionChangedListener(eventListener);
        viewer.addDoubleClickListener(eventListener);

        MenuManager contextMenuManager = new MenuManager();
        contextMenuManager.setRemoveAllWhenShown(true);
        Menu contextMenu = contextMenuManager.createContextMenu(viewer.getTree());
        viewer.getTree().setMenu(contextMenu);

        getSite().registerContextMenu(contextMenuManager, viewer);
        getSite().setSelectionProvider(viewer);

        registerActions();

        updateTestOutline();

        // Add the caret listener to the active PHP editor.
        // (TestOutlineViewController#partActivate() is not invoked into start up Eclipse.)
        ActiveEditor activeEditor = MakeGoodContext.getInstance().getActiveEditor();
        if (activeEditor.isPHP()) {
            StyledText text = (StyledText) activeEditor.get().getAdapter(Control.class);
            text.addCaretListener(new TestOutlineViewController());
        }
    }

    @Override
    public void setFocus() {}

    public void updateTestOutline() {
        if (viewer == null) return;
        if (viewer.getContentProvider() == null) return;

        if (viewer.getControl().isDisposed()) return;

        viewer.setInput(null);

        ActiveEditor activeEditor = MakeGoodContext.getInstance().getActiveEditor();
        if (!activeEditor.isPHP()) return;

        ISourceModule module = EditorParser.createActiveEditorParser().getSourceModule();
        IResource activeResource = module.getResource();
        if (activeResource == null) return;
        if (!new MakeGoodProperty(activeResource.getProject()).exists()) return;

        List<TestClass> testClasses = new ArrayList<TestClass>();
        try {
            for (IType type: module.getTypes()) {
                TestingFramework testingFramework =
                        new MakeGoodProperty(type.getResource().getProject()).getTestingFramework();
                if (!TestClass.isTestClass(type, testingFramework)) continue;
                TestClass testClass = new TestClass(type, testingFramework);
                testClasses.add(testClass);
            }
        } catch (ModelException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
        }

        // If the name includes "test" but test class is not found,
        // add the retry listener to the DLTK job.
        boolean mayBeTest = module.getElementName().toLowerCase().indexOf("test") > 0;
        if (testClasses.size() == 0 && mayBeTest) {
            for (Job job: UIJob.getJobManager().find(null)) {
                if (job.getName().indexOf("DLTK") != -1) {
                    job.addJobChangeListener(new JobChangeAdapter() {
                        @Override
                        public void done(IJobChangeEvent event) {
                            Job job = new UIJob("MakeGood Test Outline View Updated") {
                                @Override
                                public IStatus runInUIThread(IProgressMonitor monitor) {
                                    TestOutlineView.this.updateTestOutline();
                                    return Status.OK_STATUS;
                                }
                            };
                            job.schedule();
                        }
                    });
                    return;
                }
            }
        }

        viewer.setInput(testClasses);
        viewer.expandAll();

        collectBaseTestClasses(testClasses);

        selectCurrentElement();
    }

    public void refresh() {
        viewer.refresh();
    }

    public void selectCurrentElement() {
        if (isOutlineSelecting) return;
        EditorParser parser = EditorParser.createActiveEditorParser();
        if (parser == null) return;

        Tree tree = (Tree) viewer.getControl();
        tree.deselectAll();

        TreeItem foundItem = findItem(
            tree.getItems(),
            parser.getModelElementOnSelection());
        if (foundItem == null) return;
        tree.select(foundItem);
    }

    private TreeItem findItem(TreeItem[] items, IModelElement element) {
        if (element == null) return null;
        for (TreeItem item: items) {
            if (!(item.getData() instanceof IModelElement)) continue;
            IModelElement target = (IModelElement) item.getData();

            if (target.getElementName().equals(element.getElementName())) {
                return item;
            }

            if (item.getItems().length > 0) {
                TreeItem foundItem = findItem(item.getItems(), element);
                if (foundItem != null) return foundItem;
            }
        }
        return null;
    }

    private void registerActions() {
        IToolBarManager manager = getViewSite().getActionBars().getToolBarManager();
        manager.add(new CollapseAllAction());
        manager.add(new ToggleSortAction());
        manager.add(new ToggleShowHierarchyAction(Messages.TestOutlineView_ToggleShowHierarchyAction, ToggleShowHierarchyAction.LAYOUT_HIERARCHICAL));
    }

    private void collectBaseTestClasses(List<TestClass> testClasses) {
        baseTestClasses = new ArrayList<IType>();
        try {
            for (TestClass testClass: testClasses) {
                if (testClass.isNamespace()) {
                    for (IType type: testClass.getTypes()) {
                        Assert.isTrue(type instanceof TestClass);
                        baseTestClasses.add(type);
                    }
                } else {
                    baseTestClasses.add(testClass);
                }
            }
        } catch (ModelException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
        }
    }

    private class TestOutlineAppearanceAwareLabelProvider extends AppearanceAwareLabelProvider {
        public TestOutlineAppearanceAwareLabelProvider() {
            super(
                AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS
                | ScriptElementLabels.F_APP_TYPE_SIGNATURE
                | ScriptElementLabels.ALL_CATEGORY,
                AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS,
                DLTKUIPlugin.getDefault().getPreferenceStore());

            fImageLabelProvider = new ScriptElementImageProvider() {
                @Override
                public ImageDescriptor getBaseImageDescriptor(
                        IModelElement element,
                        int renderFlags) {

                    if (element instanceof TestClass) {
                        TestClass type = (TestClass) element;
                        try {
                            if ((type.getFlags() & IPHPModifiers.AccTrait) != 0) {
                                return PHPPluginImages.DESC_OBJS_TRAIT;
                            }
                        } catch (ModelException e) {}
                    }

                    return super.getBaseImageDescriptor(element, renderFlags);
                }
            };
        }
    }

    private class TreeEventListener implements ISelectionChangedListener, IDoubleClickListener {
        @Override
        public void doubleClick(DoubleClickEvent event) {
            isOutlineSelecting = true;
            showEditor(event.getSelection(), true);
            isOutlineSelecting = false;
        }

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            isOutlineSelecting = true;
            showEditor(event.getSelection(), false);

            StructuredSelection structuredSelection = (StructuredSelection) event.getSelection();
            if (structuredSelection.getFirstElement() instanceof TestMethod) {
                setBaseTestClassToTestMethod(
                    (TestMethod) structuredSelection.getFirstElement());
            }
            isOutlineSelecting = false;
        }

        private void showEditor(ISelection selection, Boolean showWhenDeactivate) {
            if (selection.isEmpty()) return;
            Assert.isTrue(selection instanceof StructuredSelection);
            StructuredSelection structuredSelection = (StructuredSelection) selection;
            Assert.isTrue(structuredSelection.getFirstElement() instanceof IMember);
            IMember member = (IMember) structuredSelection.getFirstElement();
            if (member == null) return;
            if (member.getSourceModule() == null) return;

            ISourceRange nameRange = null;
            try {
                nameRange = member.getNameRange();
            } catch (ModelException e) {
                Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            }
            if (nameRange == null) return;

            ISourceModule source = member.getSourceModule();
            if (member instanceof IMethod) {
                source = ((IMember) member.getParent()).getSourceModule();
            }

            EditorParser activeEditorParser = EditorParser.createActiveEditorParser();
            if (activeEditorParser != null) {
                ISourceModule activeSourceModule = activeEditorParser.getSourceModule();
                if (activeSourceModule != null) {
                    if (activeSourceModule.equals(source)) {
                        ActiveEditor activeEditor = MakeGoodContext.getInstance().getActiveEditor();
                        ((ITextEditor) activeEditor.get()).selectAndReveal(nameRange.getOffset(), nameRange.getLength());
                        return;
                    }
                }
            }

            if (showWhenDeactivate) {
                EditorOpener.open((IFile) source.getResource(), nameRange.getOffset(), nameRange.getLength());
            }
        }

        private void setBaseTestClassToTestMethod(TestMethod method) {
            IType type = (IType) method.getParent();
            try {
                for (IType baseTestClass: baseTestClasses) {
                    Assert.isTrue(baseTestClass instanceof TestClass);
                    if (((TestClass) baseTestClass).isSubtype(type)) {
                        method.setBaseType(baseTestClass);
                    }
                }
            } catch (ModelException e) {
                Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            }
        }
    }

    private class HierarchicalLayoutContentProvider implements ITreeContentProvider {
        @Override
        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }

        @Override
        @SuppressWarnings("rawtypes")
        public Object[] getChildren(Object parentElement) {
            List children = null;
            if (parentElement instanceof List) {
                children = (List) parentElement;
            } else if (parentElement instanceof TestClass) {
                try {
                    children = Arrays.asList(((TestClass) parentElement).getChildren());
                } catch (ModelException e) {
                    Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
                }
            } else if (parentElement instanceof IMethod) {
                children = new ArrayList();
            }
            Assert.isNotNull(children);
            return children.toArray();
        }

        @Override
        public Object getParent(Object element) {
            if (element instanceof IMember) return ((IMember) element).getParent();
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof IMethod) return false;
            try {
                if (element instanceof IMember) return ((IMember) element).hasChildren();
            } catch (ModelException e) {
                Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            }
            return false;
        }

        @Override
        public void dispose() {}

        @Override
        public void inputChanged(Viewer viewer,
                                 Object oldInput,
                                 Object newInput) {}
    }

    private class FlatLayoutContentProvider extends HierarchicalLayoutContentProvider {
        @Override
        public Object[] getChildren(Object parentElement) {
            boolean isNotTestClass =
                !(parentElement instanceof TestClass)
                || ((TestClass) parentElement).isNamespace();
            if (isNotTestClass) return super.getChildren(parentElement);

            return collectMethods((TestClass) parentElement);
        }

        private Object[] collectMethods(TestClass target) {
            final List<TestMethod> methods = new ArrayList<TestMethod>();
            try {
                target.accept(new IModelElementVisitor() {
                    @Override
                    public boolean visit(IModelElement element) {
                        if (element instanceof TestMethod) {
                            methods.add((TestMethod) element);
                            return false;
                        }
                        return true;
                    }
                });
            } catch (ModelException e) {
                Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            }
            return methods.toArray();
        }
    }

    private class CollapseAllAction extends Action {
        public CollapseAllAction() {
            super(Messages.TestOutlineView_CollapseAll, AS_PUSH_BUTTON);
            setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
            setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL_DISABLED));
            setToolTipText(getText());
        }

        public void run() {
            viewer.collapseAll();
        }
    }

    private class ToggleSortAction extends Action {
        private ViewerSorter sorter = new ViewerSorter();
        private boolean checked = false;

        public ToggleSortAction() {
            super(Messages.TestOutlineView_ToggleSort, AS_CHECK_BOX);
            setImageDescriptor(Activator.getImageDescriptor("icons/toggle_sort.gif")); //$NON-NLS-1$
            setToolTipText(getText());
        }

        @Override
        public void run() {
            checked = !checked;
            viewer.setSorter(checked ? sorter : null);
            viewer.expandAll();
            setChecked(checked);
        }
    }

    private class ToggleShowHierarchyAction extends Action {
        private static final int LAYOUT_FLAT = 1;
        private static final int LAYOUT_HIERARCHICAL = 2;

        private IContentProvider hierarchicalLayoutContentProvider = new HierarchicalLayoutContentProvider();
        private IContentProvider flatLayoutContentProvider = new FlatLayoutContentProvider();
        private int layout;

        public ToggleShowHierarchyAction(String text, int layout) {
            super(text, AS_RADIO_BUTTON);
            this.layout = layout;
            setToolTipText(getText());
            setImageDescriptor(Activator.getImageDescriptor("icons/toggle_show_hierarchy.gif")); //$NON-NLS-1$

            setChecked(layout == LAYOUT_HIERARCHICAL);
        }

        @Override
        public void run() {
            if (layout == LAYOUT_HIERARCHICAL) layout = LAYOUT_FLAT;
            else if (layout == LAYOUT_FLAT) layout = LAYOUT_HIERARCHICAL;

            setChecked(layout == LAYOUT_HIERARCHICAL);

            if (layout == LAYOUT_HIERARCHICAL)
                viewer.setContentProvider(hierarchicalLayoutContentProvider);
            else if (layout == LAYOUT_FLAT)
                viewer.setContentProvider(flatLayoutContentProvider);

            viewer.expandAll();
        }
    }
}
