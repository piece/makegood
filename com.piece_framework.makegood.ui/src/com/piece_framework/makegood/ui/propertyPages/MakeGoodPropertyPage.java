/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.propertyPages;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.piece_framework.makegood.core.MakeGoodProperty;
import com.piece_framework.makegood.core.PHPResource;
import com.piece_framework.makegood.core.TestingFramework;
import com.piece_framework.makegood.ui.Messages;

public class MakeGoodPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {
    private Text preloadScriptText;
    private Text phpunitConfigFileText;
    private Button phpunitButton;
    private Button simpletestButton;
    private TreeViewer testFolderTreeViewer;
    private Button removeTestFolderButton;
    private Composite contents;

    @Override
    protected Control createContents(Composite parent) {
        contents = new Composite(parent, SWT.NONE);
        {
            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            contents.setLayout(layout);
        }
        contents.setLayoutData(new GridData(GridData.FILL_BOTH));

        Group frameworkGroup = new Group(contents, SWT.LEFT | SWT.TOP);
        frameworkGroup.setText(Messages.MakeGoodPropertyPage_testingFrameworkLabel);
        frameworkGroup.setLayout(new GridLayout(1, false));
        {
            GridData gridData = new GridData();
            gridData.horizontalSpan = 3;
            gridData.horizontalAlignment = SWT.FILL;
            frameworkGroup.setLayoutData(gridData);
        }

        phpunitButton = new Button(frameworkGroup, SWT.RADIO);
        phpunitButton.setText(Messages.MakeGoodPropertyPage_PHPUnit);
        Composite phpunitSettings = new Composite(frameworkGroup, SWT.LEFT);
        {
            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            phpunitSettings.setLayout(layout);
        }
        phpunitSettings.setLayoutData(new GridData(GridData.FILL_BOTH));
        Label phpunitConfigFileLabel = new Label(phpunitSettings, SWT.NONE);
        phpunitConfigFileLabel.setText(Messages.MakeGoodPropertyPage_phpunitConfigFileLabel);
        phpunitConfigFileText = new Text(phpunitSettings, SWT.SINGLE | SWT.BORDER);
        phpunitConfigFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Button browsePHPUnitConfigFileButton = new Button(phpunitSettings, SWT.NONE);
        browsePHPUnitConfigFileButton.setText(Messages.MakeGoodPropertyPage_phpunitConfigFileBrowseLabel);
        browsePHPUnitConfigFileButton.addSelectionListener(
            new PreloadScriptSelectionListener(
                phpunitConfigFileText,
                Messages.MakeGoodPropertyPage_phpunitConfigFileDialogTitle,
                Messages.MakeGoodPropertyPage_phpunitConfigFileDialogMessage,
                new ViewerFilter() {
                    @Override
                    public boolean select(Viewer viewer, Object parentElement, Object element) {
                        if (element instanceof IFile) {
                            return true;
                        } else if (element instanceof IFolder) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            )
        );

        simpletestButton = new Button(frameworkGroup, SWT.RADIO);
        simpletestButton.setText(Messages.MakeGoodPropertyPage_SimpleTest);

        Label preloadScriptLabel = new Label(contents, SWT.NONE);
        preloadScriptLabel.setText(Messages.MakeGoodPropertyPage_preloadScriptLabel);
        preloadScriptText = new Text(contents, SWT.SINGLE | SWT.BORDER);
        preloadScriptText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Button browsePreloadScriptButton = new Button(contents, SWT.NONE);
        browsePreloadScriptButton.setText(Messages.MakeGoodPropertyPage_browseLabel);
        browsePreloadScriptButton.addSelectionListener(
            new PreloadScriptSelectionListener(
                preloadScriptText,
                Messages.MakeGoodPropertyPage_preloadScriptDialogTitle,
                Messages.MakeGoodPropertyPage_preloadScriptDialogMessage,
                new ViewerFilter() {
                    @Override
                    public boolean select(Viewer viewer, Object parentElement, Object element) {
                        if (element instanceof IFile) {
                            return PHPResource.isPHPSource((IFile) element);
                        } else if (element instanceof IFolder) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            )
        );

        Group testFolderGroup = new Group(contents, SWT.LEFT | SWT.TOP);
        testFolderGroup.setText(Messages.MakeGoodPropertyPage_testFoldersLabel);
        testFolderGroup.setLayout(new GridLayout(2, false));
        {
            GridData gridData = new GridData();
            gridData.horizontalSpan = 3;
            gridData.horizontalAlignment = SWT.FILL;
            gridData.verticalAlignment = SWT.FILL;
            gridData.grabExcessHorizontalSpace = true;
            gridData.grabExcessVerticalSpace = true;
            testFolderGroup.setLayoutData(gridData);
        }

        testFolderTreeViewer = new TreeViewer(testFolderGroup, SWT.BORDER + SWT.SINGLE);
        {
            GridData gridData = new GridData();
            gridData.horizontalAlignment = SWT.FILL;
            gridData.verticalAlignment = SWT.FILL;
            gridData.grabExcessHorizontalSpace = true;
            gridData.grabExcessVerticalSpace = true;
            testFolderTreeViewer.getTree().setLayoutData(gridData);
        }
        testFolderTreeViewer.setContentProvider(new TestFolderTreeContentProvider());
        testFolderTreeViewer.setLabelProvider(new TestFolderTreeLabelProvider());
        testFolderTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                removeTestFolderButton.setEnabled(event.getSelection() != null);
            }
        });

        Composite testFolderButtons = new Composite(testFolderGroup, SWT.NONE);
        testFolderButtons.setLayout(new FillLayout(SWT.VERTICAL));
        {
            GridData gridData = new GridData();
            gridData.verticalAlignment = SWT.TOP;
            testFolderButtons.setLayoutData(gridData);
        }

        Button addTestFolderButton = new Button(testFolderButtons, SWT.NONE);
        addTestFolderButton.setText(Messages.MakeGoodPropertyPage_addFolderLabel);
        addTestFolderButton.addSelectionListener(new AddTestFolderSelectionListener());

        removeTestFolderButton = new Button(testFolderButtons, SWT.NONE);
        removeTestFolderButton.setText(Messages.MakeGoodPropertyPage_removeLabel);
        removeTestFolderButton.setEnabled(false);
        removeTestFolderButton.addSelectionListener(new RemoveTestFolderSelectionListener());

        MakeGoodProperty property = new MakeGoodProperty(getProject());
        if (property.usingPHPUnit()) {
            phpunitButton.setSelection(true);
        } else if (property.usingSimpleTest()) {
            simpletestButton.setSelection(true);
        } else {
            phpunitButton.setSelection(true);
        }
        phpunitConfigFileText.setText(property.getPHPUnitConfigFile());
        preloadScriptText.setText(property.getPreloadScript());
        testFolderTreeViewer.setInput(property.getTestFolders());

        return contents;
    }

    @Override
    public boolean performOk() {
        MakeGoodProperty property = new MakeGoodProperty(getProject());
        TestingFramework testingFramework = null;
        if (phpunitButton.getSelection()) {
            testingFramework = TestingFramework.PHPUnit;
        } else if (simpletestButton.getSelection()) {
            testingFramework = TestingFramework.SimpleTest;
        }
        property.setTestingFramework(testingFramework);
        property.setPHPUnitConfigFile(phpunitConfigFileText.getText());
        property.setPreloadScript(preloadScriptText.getText());
        property.setTestFolders((List<IFolder>) testFolderTreeViewer.getInput());
        property.flush();

        return true;
    }

    private IProject getProject() {
        IProject project = null;
        if (getElement() instanceof IProject) {
            project = (IProject) getElement();
        } else if (getElement() instanceof IScriptProject) {
            project = ((IScriptProject) getElement()).getProject();
        }
        return project;
    }

    private class PreloadScriptSelectionListener implements SelectionListener {
        private Text subject;
        private String dialogTitle;
        private String dialogMessage;
        private ViewerFilter viewerFilter;

        PreloadScriptSelectionListener(
            Text subject,
            String dialogTitle,
            String dialogMessage,
            ViewerFilter viewerFilter) {
            this.subject = subject;
            this.dialogTitle = dialogTitle;
            this.dialogMessage = dialogMessage;
            this.viewerFilter = viewerFilter;
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            ElementTreeSelectionDialog dialog =
                new ElementTreeSelectionDialog(
                    contents.getShell(),
                    new WorkbenchLabelProvider(),
                    new WorkbenchContentProvider()
                );

            dialog.setTitle(dialogTitle);
            dialog.setMessage(dialogMessage);
            dialog.setAllowMultiple(false);

            dialog.setComparator(
                new ViewerComparator() {
                    @Override
                    public int compare(Viewer viewer, Object e1, Object e2) {
                        if (e1 instanceof IFile && e2 instanceof IFolder) {
                            return 1;
                        } else if (e1 instanceof IFolder && e2 instanceof IFile) {
                            return -1;
                        }
                        return super.compare(viewer, e1, e2);
                    }
                }
            );

            dialog.addFilter(viewerFilter);
            dialog.setInput(getProject());

            if (dialog.open() == Window.OK && dialog.getFirstResult() != null) {
                String text = ""; //$NON-NLS-1$
                IFile result = (IFile) dialog.getFirstResult();
                if (result != null) {
                    text = result.getFullPath().toString();
                }
                subject.setText(text);
            }
        }
    }

    private class TestFolderTreeContentProvider implements ITreeContentProvider {
        @Override
        public Object[] getChildren(Object parentElement) {
            return null;
        }

        @Override
        public Object getParent(Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            return false;
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return ((List<IFolder>) inputElement).toArray();
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    private class TestFolderTreeLabelProvider extends LabelProvider {
        @Override
        public Image getImage(Object element) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
        }

        @Override
        public String getText(Object element) {
            if (element instanceof IFolder) {
                return ((IFolder) element).getFullPath().toString();
            }
            return super.getText(element);
        }
    }

    private class AddTestFolderSelectionListener implements SelectionListener {
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            CheckedTreeSelectionDialog dialog =
                new CheckedTreeSelectionDialog(
                    contents.getShell(),
                    new WorkbenchLabelProvider(),
                    new WorkbenchContentProvider()
                );

            dialog.setTitle(Messages.MakeGoodPropertyPage_testFolderDialogTitle);
            dialog.setMessage(Messages.MakeGoodPropertyPage_testFolderDialogMessage);

            dialog.addFilter(
                new ViewerFilter() {
                    @Override
                    public boolean select(Viewer viewer, Object parentElement, Object element) {
                        return element instanceof IFolder;
                    }
                }
            );

            dialog.setInput(getProject());

            if (dialog.open() == Window.OK && dialog.getResult().length > 0) {
                List<IFolder> folders = new ArrayList<IFolder>();
                folders.addAll((List<IFolder>) testFolderTreeViewer.getInput());
                for (Object selected: dialog.getResult()) {
                    boolean sameFolder = false;
                    for (IFolder current: folders) {
                        if (current.equals(selected)) {
                            sameFolder = true;
                            continue;
                        }
                    }
                    if (!sameFolder) {
                        folders.add((IFolder) selected);
                    }
                }
                testFolderTreeViewer.setInput(folders);
            }
        }
    }

    private class RemoveTestFolderSelectionListener implements SelectionListener {
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            IStructuredSelection selection = (IStructuredSelection) testFolderTreeViewer.getSelection();
            if (selection == null) return;

            IFolder removedFolder = (IFolder) selection.getFirstElement();
            List<IFolder> folders = new ArrayList<IFolder>();
            for (IFolder folder: (List<IFolder>) testFolderTreeViewer.getInput()) {
                if (!removedFolder.equals(folder)) folders.add(folder);
            }
            testFolderTreeViewer.setInput(folders);
        }
    }
}
