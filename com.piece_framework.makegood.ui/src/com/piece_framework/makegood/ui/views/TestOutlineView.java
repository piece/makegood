/**
 * Copyright (c) 2012 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
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
import org.eclipse.core.runtime.Assert;
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
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.dltk.ui.viewsupport.DecoratingModelLabelProvider;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
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
 * @since 1.x.0
 */
public class TestOutlineView extends ViewPart {
    public static final String ID = "com.piece_framework.makegood.ui.views.testOutlineView"; //$NON-NLS-1$

    private TreeViewer viewer;
    private boolean runningTest;
    private List<IType> baseTestClasses;

    public TestOutlineView() {}

    @Override
    public void createPartControl(Composite parent) {
        parent.setLayout(new FillLayout());

        viewer = new TreeViewer(parent);
        viewer.setContentProvider(new HierarchyContentProvider());
        viewer.setLabelProvider(
            new DecoratingModelLabelProvider(
                new AppearanceAwareLabelProvider(
                    AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS
                    | ScriptElementLabels.F_APP_TYPE_SIGNATURE
                    | ScriptElementLabels.ALL_CATEGORY,
                    AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS,
                    DLTKUIPlugin.getDefault().getPreferenceStore())));
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

        initializeTestOutline();
    }

    private void initializeTestOutline() {
        for (Job job: Job.getJobManager().find(null)) {
            if (job.getName().startsWith("DLTK indexing")) {
                job.addJobChangeListener(new JobChangeAdapter() {
                    @Override
                    public void done(IJobChangeEvent event) {
                        updateTestOutline();
                    }
                });
                return;
            }
        }

        updateTestOutline();
    }

    @Override
    public void setFocus() {}

    public boolean runningTest() {
        return runningTest;
    }

    public void setRunningTest(boolean runningTest) {
        this.runningTest = runningTest;
    }

    public void updateTestOutline() {
        if (viewer == null) return;
        if (viewer.getContentProvider() == null) return;

        Control control = viewer.getControl();
        if (control == null || control.isDisposed()) return;

        Display display = control.getDisplay();
        if (display == null) return;

        display.asyncExec(new Runnable() {
            public void run() {
                if (viewer.getControl().isDisposed()) return;

                viewer.setInput(null);

                ActiveEditor activeEditor = MakeGoodContext.getInstance().getActiveEditor();
                if (!activeEditor.isPHP()) return;

                ISourceModule module = EditorParser.createActiveEditorParser().getSourceModule();
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
                viewer.setInput(testClasses);
                viewer.expandAll();

                collectBaseTestClasses(testClasses);
            }
        });
    }

    public void setSelection(IModelElement element) {
        Tree tree = (Tree) viewer.getControl();
        tree.deselectAll();
        TreeItem foundItem = findItem(tree.getItems(), element);
        if (foundItem == null) return;
        tree.select(foundItem);
    }

    private TreeItem findItem(TreeItem[] items, IModelElement element) {
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
        manager.add(new CollapseTreeAction());
        manager.add(new SortAction());
        manager.add(new FlatLookAction(false));
        manager.add(new HierarchyLookAction(true));
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

    private class TreeEventListener implements ISelectionChangedListener, IDoubleClickListener {
        @Override
        public void doubleClick(DoubleClickEvent event) {
            showEditor(event.getSelection(), true);
        }

        @Override
        public void selectionChanged(SelectionChangedEvent event) {
            showEditor(event.getSelection(), false);

            StructuredSelection structuredSelection = (StructuredSelection) event.getSelection();
            if (structuredSelection.getFirstElement() instanceof TestMethod) {
                setBaseTestClassToTestMethod(
                    (TestMethod) structuredSelection.getFirstElement());
            }
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
            boolean targetIsActivate =
                EditorParser.createActiveEditorParser().getSourceModule().equals(source);
            if (targetIsActivate) {
                ActiveEditor activeEditor = MakeGoodContext.getInstance().getActiveEditor();
                ((ITextEditor) activeEditor.get()).selectAndReveal(
                        nameRange.getOffset(),
                        nameRange.getLength());
            } else {
                if (showWhenDeactivate) {
                    EditorOpener.open(
                            (IFile) source.getResource(),
                            nameRange.getOffset(),
                            nameRange.getLength());
                }
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

    private class HierarchyContentProvider implements ITreeContentProvider {
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

    private class FlatContentProvider extends HierarchyContentProvider {
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

    private class CollapseTreeAction extends Action {
        public CollapseTreeAction() {
            super(Messages.TestOutlineView_CollapseAll, AS_PUSH_BUTTON);
            setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
            setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL_DISABLED));
            setToolTipText(getText());
        }

        public void run() {
            viewer.collapseAll();
        }
    }

    private class SortAction extends Action {
        private ViewerSorter sorter = new ViewerSorter();
        private boolean checked = false;

        public SortAction() {
            super(Messages.TestOutlineView_Sort, AS_CHECK_BOX);
            setImageDescriptor(Activator.getImageDescriptor("icons/sort.gif")); //$NON-NLS-1$
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

    private abstract class LookAction extends Action {
        private boolean checked;

        public LookAction(String text, boolean checked) {
            super(text, AS_RADIO_BUTTON);
            setToolTipText(getText());

            this.checked = checked;
            setChecked(this.checked);
        }

        @Override
        public void run() {
            checked = !checked;
            setChecked(checked);
            viewer.setContentProvider(getContentProvider());
            viewer.expandAll();
        }

        abstract IContentProvider getContentProvider();
    }

    private class FlatLookAction extends LookAction {
        private IContentProvider flatContentProvider = new FlatContentProvider();

        public FlatLookAction(boolean checked) {
            super(Messages.TestOutlineView_FlatLook, checked);
            setImageDescriptor(Activator.getImageDescriptor("icons/flat-look.gif")); //$NON-NLS-1$
        }

        @Override
        IContentProvider getContentProvider() {
            return flatContentProvider;
        }
    }

    private class HierarchyLookAction extends LookAction {
        private IContentProvider hierarchyContentProvider = new HierarchyContentProvider();

        public HierarchyLookAction(boolean checked) {
            super(Messages.TestOutlineView_HierarchyLook, checked);
            setImageDescriptor(Activator.getImageDescriptor("icons/hierarchy-look.gif")); //$NON-NLS-1$
        }

        @Override
        IContentProvider getContentProvider() {
            return hierarchyContentProvider;
        }
    }
}
