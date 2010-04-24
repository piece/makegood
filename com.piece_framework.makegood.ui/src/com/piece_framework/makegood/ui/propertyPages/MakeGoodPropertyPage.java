/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
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
import org.eclipse.swt.layout.RowLayout;
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
    private Text preloadScript;
    private Button phpUnit;
    private Button simpleTest;
    private TreeViewer testFolders;
    private Button remove;

    @Override
    protected Control createContents(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        {
            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            composite.setLayout(layout);
        }
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Group group = new Group(composite, SWT.LEFT | SWT.TOP);
        group.setText(Messages.MakeGoodPropertyPage_testingFrameworkLabel);
        group.setLayout(new RowLayout());
        {
            GridData gridData = new GridData();
            gridData.horizontalSpan = 3;
            gridData.horizontalAlignment = SWT.FILL;
            group.setLayoutData(gridData);
        }

        phpUnit = new Button(group, SWT.RADIO);
        phpUnit.setText(Messages.MakeGoodPropertyPage_PHPUnit);
        simpleTest = new Button(group, SWT.RADIO);
        simpleTest.setText(Messages.MakeGoodPropertyPage_SimpleTest);

        Label label = new Label(composite, SWT.NONE);
        label.setText(Messages.MakeGoodPropertyPage_preloadScriptLabel);

        preloadScript = new Text(composite, SWT.SINGLE | SWT.BORDER);
        preloadScript.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Button browse = new Button(composite, SWT.NONE);
        browse.setText(Messages.MakeGoodPropertyPage_browseLabel);
        browse.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(composite.getShell(),
                                                                                   new WorkbenchLabelProvider(),
                                                                                   new WorkbenchContentProvider()
                                                                                   );
                dialog.setTitle(Messages.MakeGoodPropertyPage_preloadScriptDialogTitle);
                dialog.setMessage(Messages.MakeGoodPropertyPage_preloadScriptDialogMessage);
                dialog.setAllowMultiple(false);
                dialog.setComparator(new ViewerComparator() {
                    @Override
                    public int compare(Viewer viewer, Object e1, Object e2) {
                        if (e1 instanceof IFile
                            && e2 instanceof IFolder
                            ) {
                            return 1;
                        } else if (e1 instanceof IFolder
                                   && e2 instanceof IFile
                                   ) {
                            return -1;
                        }
                        return super.compare(viewer, e1, e2);
                    }
                });
                dialog.addFilter(new ViewerFilter() {
                    @Override
                    public boolean select(Viewer viewer,
                                          Object parentElement,
                                          Object element
                                          ) {
                        if (element instanceof IFile) {
                            return PHPResource.isPHPSource((IFile) element);
                        } else if (element instanceof IFolder) {
                            return true;
                        }
                        return false;
                    }
                });
                dialog.setInput(getProject());
                if (dialog.open() == Window.OK
                    && dialog.getFirstResult() != null
                    ) {
                    IFile script = (IFile) dialog.getFirstResult();
                    preloadScript.setText(script.getFullPath().toString());
                }
            }
        });

        Group testFoldersGroup = new Group(composite, SWT.LEFT | SWT.TOP);
        testFoldersGroup.setText(Messages.MakeGoodPropertyPage_testFoldersLabel);
        testFoldersGroup.setLayout(new GridLayout(2, false));
        {
            GridData gridData = new GridData();
            gridData.horizontalSpan = 3;
            gridData.horizontalAlignment = SWT.FILL;
            gridData.verticalAlignment = SWT.FILL;
            gridData.grabExcessHorizontalSpace = true;
            gridData.grabExcessVerticalSpace = true;
            testFoldersGroup.setLayoutData(gridData);
        }

        testFolders = new TreeViewer(testFoldersGroup, SWT.BORDER + SWT.SINGLE);
        {
            GridData gridData = new GridData();
            gridData.horizontalAlignment = SWT.FILL;
            gridData.verticalAlignment = SWT.FILL;
            gridData.grabExcessHorizontalSpace = true;
            gridData.grabExcessVerticalSpace = true;
            testFolders.getTree().setLayoutData(gridData);
        }
        testFolders.setContentProvider(new ITreeContentProvider() {
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
                List<IFolder> folders = (List<IFolder>) inputElement;
                return folders.toArray();
            }

            @Override
            public void dispose() {}

            @Override
            public void inputChanged(Viewer viewer,
                                     Object oldInput,
                                     Object newInput) {}
        });
        testFolders.setLabelProvider(new LabelProvider() {
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
        });
        testFolders.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                remove.setEnabled(event.getSelection() != null);
            }
        });

        Composite buttons = new Composite(testFoldersGroup, SWT.NONE);
        buttons.setLayout(new FillLayout(SWT.VERTICAL));
        {
            GridData gridData = new GridData();
            gridData.verticalAlignment = SWT.TOP;
            buttons.setLayoutData(gridData);
        }

        Button addFolder = new Button(buttons, SWT.NONE);
        addFolder.setText(Messages.MakeGoodPropertyPage_addFolderLabel);
        addFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                CheckedTreeSelectionDialog dialog = new CheckedTreeSelectionDialog(composite.getShell(),
                                                                                   new WorkbenchLabelProvider(),
                                                                                   new WorkbenchContentProvider()
                                                                                   );
                dialog.setTitle(Messages.MakeGoodPropertyPage_testFolderDialogTitle);
                dialog.setMessage(Messages.MakeGoodPropertyPage_testFolderDialogMessage);
                dialog.addFilter(new ViewerFilter() {
                    @Override
                    public boolean select(Viewer viewer,
                                          Object parentElement,
                                          Object element
                                          ) {
                        return element instanceof IFolder;
                    }
                });
                dialog.setInput(getProject());
                if (dialog.open() == Window.OK
                    && dialog.getResult().length > 0
                    ) {
                    List<IFolder> folders = new ArrayList<IFolder>();
                    folders.addAll((List<IFolder>) testFolders.getInput());
                    for (Object selected: dialog.getResult()) {
                        boolean sameFolder = false;
                        for (IFolder current: folders) {
                            if (current.equals(selected)) {
                                sameFolder = true;
                                continue;
                            }
                        }
                        if (!sameFolder) folders.add((IFolder) selected);
                    }
                    testFolders.setInput(folders);
                }
            }
        });

        remove = new Button(buttons, SWT.NONE);
        remove.setText(Messages.MakeGoodPropertyPage_removeLabel);
        remove.setEnabled(false);
        remove.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                IStructuredSelection selection = (IStructuredSelection) testFolders.getSelection();
                if (selection == null) return;

                IFolder deleteFolder = (IFolder) selection.getFirstElement();
                List<IFolder> folders = new ArrayList<IFolder>();
                for (IFolder folder: (List<IFolder>) testFolders.getInput()) {
                    if (!deleteFolder.equals(folder)) folders.add(folder);
                }
                testFolders.setInput(folders);
            }
        });

        MakeGoodProperty property = new MakeGoodProperty(getProject());
        if (property.usingPHPUnit()) {
            phpUnit.setSelection(true);
        } else if (property.useSimpleTest()) {
            simpleTest.setSelection(true);
        } else {
            phpUnit.setSelection(true);
        }
        preloadScript.setText(property.getPreloadScript());
        testFolders.setInput(property.getTestFolders());

        return composite;
    }

    @Override
    public boolean performOk() {
        MakeGoodProperty property = new MakeGoodProperty(getProject());
        TestingFramework testingFramework = null;
        if (phpUnit.getSelection()) {
            testingFramework = TestingFramework.PHPUnit;
        } else if (simpleTest.getSelection()) {
            testingFramework = TestingFramework.SimpleTest;
        }
        property.setTestingFramework(testingFramework);
        property.setPreloadScript(preloadScript.getText());
        property.setTestFolders((List<IFolder>) testFolders.getInput());
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
}
