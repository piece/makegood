/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
import org.eclipse.swt.events.SelectionAdapter;
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

public class MakeGoodPropertyPage extends PropertyPage {
    private static final int SELECTION_ALLOW_FILE = 1;
    private static final int SELECTION_ALLOW_FOLDER = 2;
    private Text preloadScriptText;
    private Label phpunitConfigFileLabel;
    private Text phpunitConfigFileText;
    private Button phpunitConfigFileBrowseButton;
    private Button phpunitButton;
    private Button simpletestButton;
    private Button cakephpButton;
    private Label cakephpAppPathLabel;
    private Text cakephpAppPathText;
    private Button cakephpAppPathBrowseButton;
    private Label cakephpCorePathLabel;
    private Text cakephpCorePathText;
    private Button cakephpCorePathBrowseButton;

    /**
     * @since 1.3.0
     */
    private Button ciunitButton;

    /**
     * @since 1.3.0
     */
    private Label ciunitPathLabel;

    /**
     * @since 1.3.0
     */
    private Text ciunitPathText;

    /**
     * @since 1.3.0
     */
    private Button ciunitPathBrowseButton;

    /**
     * @since 1.3.0
     */
    private Label ciunitConfigFileLabel;

    /**
     * @since 1.3.0
     */
    private Text ciunitConfigFileText;

    /**
     * @since 1.3.0
     */
    private Button ciunitConfigFileBrowseButton;
    private TreeViewer testFolderTreeViewer;
    private Button testFolderRemoveButton;
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
        phpunitButton.setText(Messages.MakeGoodPropertyPage_phpunit);
        phpunitButton.addSelectionListener(
            new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    enablePHPUnitSettings();
                }
            }
        );
        Composite phpunitSettings = new Composite(frameworkGroup, SWT.NONE);
        {
            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            phpunitSettings.setLayout(layout);
        }
        phpunitSettings.setLayoutData(new GridData(GridData.FILL_BOTH));
        phpunitConfigFileLabel = new Label(phpunitSettings, SWT.NONE);
        phpunitConfigFileLabel.setText(Messages.MakeGoodPropertyPage_phpunitConfigFileLabel);
        phpunitConfigFileText = new Text(phpunitSettings, SWT.SINGLE | SWT.BORDER);
        phpunitConfigFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        phpunitConfigFileBrowseButton = new Button(phpunitSettings, SWT.NONE);
        phpunitConfigFileBrowseButton.setText(Messages.MakeGoodPropertyPage_phpunitConfigFileBrowseLabel);
        phpunitConfigFileBrowseButton.addSelectionListener(
            new FileSelectionListener(
                phpunitConfigFileText,
                Messages.MakeGoodPropertyPage_phpunitConfigFileDialogTitle,
                Messages.MakeGoodPropertyPage_phpunitConfigFileDialogMessage,
                SELECTION_ALLOW_FILE,
                new FileViewerFilter()
            )
        );

        simpletestButton = new Button(frameworkGroup, SWT.RADIO);
        simpletestButton.setText(Messages.MakeGoodPropertyPage_simpletest);
        simpletestButton.addSelectionListener(
            new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    enableSimpleTestSettings();
                }
            }
        );

        cakephpButton = new Button(frameworkGroup, SWT.RADIO);
        cakephpButton.setText(Messages.MakeGoodPropertyPage_cakephp);
        cakephpButton.addSelectionListener(
            new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    enableCakePHPSettings();
                }
            }
        );
        Composite cakephpSettings = new Composite(frameworkGroup, SWT.NONE);
        {
            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            cakephpSettings.setLayout(layout);
        }
        cakephpSettings.setLayoutData(new GridData(GridData.FILL_BOTH));
        cakephpAppPathLabel = new Label(cakephpSettings, SWT.NONE);
        cakephpAppPathLabel.setText(Messages.MakeGoodPropertyPage_cakephpAppPathLabel);
        cakephpAppPathText = new Text(cakephpSettings, SWT.SINGLE | SWT.BORDER);
        cakephpAppPathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cakephpAppPathBrowseButton = new Button(cakephpSettings, SWT.NONE);
        cakephpAppPathBrowseButton.setText(Messages.MakeGoodPropertyPage_cakephpAppPathBrowseLabel);
        cakephpAppPathBrowseButton.addSelectionListener(
            new FileSelectionListener(
                cakephpAppPathText,
                Messages.MakeGoodPropertyPage_cakephpAppPathDialogTitle,
                Messages.MakeGoodPropertyPage_cakephpAppPathDialogMessage,
                SELECTION_ALLOW_FOLDER,
                new FileViewerFilter()
            )
        );
        cakephpCorePathLabel = new Label(cakephpSettings, SWT.NONE);
        cakephpCorePathLabel.setText(Messages.MakeGoodPropertyPage_cakephpCorePathLabel);
        cakephpCorePathText = new Text(cakephpSettings, SWT.SINGLE | SWT.BORDER);
        cakephpCorePathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cakephpCorePathBrowseButton = new Button(cakephpSettings, SWT.NONE);
        cakephpCorePathBrowseButton.setText(Messages.MakeGoodPropertyPage_cakephpCorePathBrowseLabel);
        cakephpCorePathBrowseButton.addSelectionListener(
            new FileSelectionListener(
                cakephpCorePathText,
                Messages.MakeGoodPropertyPage_cakephpCorePathDialogTitle,
                Messages.MakeGoodPropertyPage_cakephpCorePathDialogMessage,
                SELECTION_ALLOW_FOLDER,
                new FileViewerFilter()
            )
        );

        ciunitButton = new Button(frameworkGroup, SWT.RADIO);
        ciunitButton.setText(Messages.MakeGoodPropertyPage_ciunit);
        ciunitButton.addSelectionListener(
            new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    enableCIUnitSettings();
                }
            }
        );
        Composite ciunitSettings = new Composite(frameworkGroup, SWT.NONE);
        {
            GridLayout layout = new GridLayout();
            layout.numColumns = 3;
            ciunitSettings.setLayout(layout);
        }
        ciunitSettings.setLayoutData(new GridData(GridData.FILL_BOTH));
        ciunitPathLabel = new Label(ciunitSettings, SWT.NONE);
        ciunitPathLabel.setText(Messages.MakeGoodPropertyPage_ciunitPathLabel);
        ciunitPathText = new Text(ciunitSettings, SWT.SINGLE | SWT.BORDER);
        ciunitPathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ciunitPathBrowseButton = new Button(ciunitSettings, SWT.NONE);
        ciunitPathBrowseButton.setText(Messages.MakeGoodPropertyPage_ciunitPathBrowseLabel);
        ciunitPathBrowseButton.addSelectionListener(
            new FileSelectionListener(
                ciunitPathText,
                Messages.MakeGoodPropertyPage_ciunitPathDialogTitle,
                Messages.MakeGoodPropertyPage_ciunitPathDialogMessage,
                SELECTION_ALLOW_FOLDER,
                new FileViewerFilter()
            )
        );
        ciunitConfigFileLabel = new Label(ciunitSettings, SWT.NONE);
        ciunitConfigFileLabel.setText(Messages.MakeGoodPropertyPage_ciunitConfigFileLabel);
        ciunitConfigFileText = new Text(ciunitSettings, SWT.SINGLE | SWT.BORDER);
        ciunitConfigFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        ciunitConfigFileBrowseButton = new Button(ciunitSettings, SWT.NONE);
        ciunitConfigFileBrowseButton.setText(Messages.MakeGoodPropertyPage_ciunitConfigFileBrowseLabel);
        ciunitConfigFileBrowseButton.addSelectionListener(
            new FileSelectionListener(
                ciunitConfigFileText,
                Messages.MakeGoodPropertyPage_ciunitConfigFileDialogTitle,
                Messages.MakeGoodPropertyPage_ciunitConfigFileDialogMessage,
                SELECTION_ALLOW_FILE,
                new FileViewerFilter()
            )
        );

        Label preloadScriptLabel = new Label(contents, SWT.NONE);
        preloadScriptLabel.setText(Messages.MakeGoodPropertyPage_preloadScriptLabel);
        preloadScriptText = new Text(contents, SWT.SINGLE | SWT.BORDER);
        preloadScriptText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Button preloadScriptBrowseButton = new Button(contents, SWT.NONE);
        preloadScriptBrowseButton.setText(Messages.MakeGoodPropertyPage_preloadScriptBrowseLabel);
        preloadScriptBrowseButton.addSelectionListener(
            new FileSelectionListener(
                preloadScriptText,
                Messages.MakeGoodPropertyPage_preloadScriptDialogTitle,
                Messages.MakeGoodPropertyPage_preloadScriptDialogMessage,
                SELECTION_ALLOW_FILE,
                new FileViewerFilter()
            )
        );

        Group testFolderGroup = new Group(contents, SWT.LEFT | SWT.TOP);
        testFolderGroup.setText(Messages.MakeGoodPropertyPage_testFolderLabel);
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
                testFolderRemoveButton.setEnabled(event.getSelection() != null);
            }
        });

        Composite testFolderButtons = new Composite(testFolderGroup, SWT.NONE);
        testFolderButtons.setLayout(new FillLayout(SWT.VERTICAL));
        {
            GridData gridData = new GridData();
            gridData.verticalAlignment = SWT.TOP;
            testFolderButtons.setLayoutData(gridData);
        }

        Button testFolderAddButton = new Button(testFolderButtons, SWT.NONE);
        testFolderAddButton.setText(Messages.MakeGoodPropertyPage_testFolderAddLabel);
        testFolderAddButton.addSelectionListener(new AddTestFolderSelectionListener());

        testFolderRemoveButton = new Button(testFolderButtons, SWT.NONE);
        testFolderRemoveButton.setText(Messages.MakeGoodPropertyPage_testFolderRemoveLabel);
        testFolderRemoveButton.setEnabled(false);
        testFolderRemoveButton.addSelectionListener(new RemoveTestFolderSelectionListener());

        MakeGoodProperty property = new MakeGoodProperty(getProject());
        switch (property.getTestingFramework()) {
        case PHPUnit:
            phpunitButton.setSelection(true);
            enablePHPUnitSettings();
            break;
        case SimpleTest:
            simpletestButton.setSelection(true);
            enableSimpleTestSettings();
            break;
        case CakePHP:
            cakephpButton.setSelection(true);
            enableCakePHPSettings();
            break;
        case CIUnit:
            ciunitButton.setSelection(true);
            enableCIUnitSettings();
            break;
        }
        phpunitConfigFileText.setText(property.getPHPUnitConfigFile());
        cakephpAppPathText.setText(property.getCakePHPAppPath());
        cakephpCorePathText.setText(property.getCakePHPCorePath());
        ciunitPathText.setText(property.getCIUnitPath());
        ciunitConfigFileText.setText(property.getCIUnitConfigFile());
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
        } else if (cakephpButton.getSelection()) {
            testingFramework = TestingFramework.CakePHP;
        } else if (ciunitButton.getSelection()) {
            testingFramework = TestingFramework.CIUnit;
        }
        property.setTestingFramework(testingFramework);
        property.setPHPUnitConfigFile(phpunitConfigFileText.getText());
        property.setCakePHPAppPath(cakephpAppPathText.getText());
        property.setCakePHPCorePath(cakephpCorePathText.getText());
        property.setCIUnitPath(ciunitPathText.getText());
        property.setCIUnitConfigFile(ciunitConfigFileText.getText());
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

    private void enablePHPUnitSettings() {
        disableSimpleTestSettings();
        disableCakePHPSettings();
        disableCIUnitSettings();
        setPHPUnitSettingsEnabled(true);
    }

    private void disablePHPUnitSettings() {
        setPHPUnitSettingsEnabled(false);
    }

    private void setPHPUnitSettingsEnabled(boolean enabled) {
        phpunitConfigFileLabel.setEnabled(enabled);
        phpunitConfigFileText.setEnabled(enabled);
        phpunitConfigFileBrowseButton.setEnabled(enabled);
    }

    private void enableSimpleTestSettings() {
        disablePHPUnitSettings();
        disableCakePHPSettings();
        disableCIUnitSettings();
        setSimpleTestSettingsEnabled(true);
    }

    private void disableSimpleTestSettings() {
        setSimpleTestSettingsEnabled(false);
    }

    private void setSimpleTestSettingsEnabled(boolean enabled) {
    }

    private void enableCakePHPSettings() {
        disablePHPUnitSettings();
        disableSimpleTestSettings();
        disableCIUnitSettings();
        setCakePHPSettingsEnabled(true);
    }

    private void disableCakePHPSettings() {
        setCakePHPSettingsEnabled(false);
    }

    private void setCakePHPSettingsEnabled(boolean enabled) {
        cakephpAppPathLabel.setEnabled(enabled);
        cakephpAppPathText.setEnabled(enabled);
        cakephpAppPathBrowseButton.setEnabled(enabled);
        cakephpCorePathLabel.setEnabled(enabled);
        cakephpCorePathText.setEnabled(enabled);
        cakephpCorePathBrowseButton.setEnabled(enabled);
    }

    /**
     * @since 1.3.0
     */
    private void enableCIUnitSettings() {
        disablePHPUnitSettings();
        disableSimpleTestSettings();
        disableCakePHPSettings();
        setCIUnitSettingsEnabled(true);
    }

    /**
     * @since 1.3.0
     */
    private void disableCIUnitSettings() {
        setCIUnitSettingsEnabled(false);
    }

    /**
     * @since 1.3.0
     */
    private void setCIUnitSettingsEnabled(boolean enabled) {
        ciunitPathLabel.setEnabled(enabled);
        ciunitPathText.setEnabled(enabled);
        ciunitPathBrowseButton.setEnabled(enabled);
        ciunitConfigFileLabel.setEnabled(enabled);
        ciunitConfigFileText.setEnabled(enabled);
        ciunitConfigFileBrowseButton.setEnabled(enabled);
    }

    private class FileSelectionListener implements SelectionListener {
        private Text subject;
        private String dialogTitle;
        private String dialogMessage;
        private ViewerFilter viewerFilter;
        private int allowedResource;

        private FileSelectionListener(
            Text subject,
            String dialogTitle,
            String dialogMessage,
            int allowedResource,
            ViewerFilter viewerFilter
        ) {
            this.subject = subject;
            this.dialogTitle = dialogTitle;
            this.dialogMessage = dialogMessage;
            this.allowedResource = allowedResource;
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
                Object selectedResource = dialog.getFirstResult();
                if (selectedResource != null) {
                    if ((selectedResource instanceof IFile) && (allowedResource & SELECTION_ALLOW_FILE) == SELECTION_ALLOW_FILE) {
                        text = ((IFile) selectedResource).getFullPath().toString();
                    } else if ((selectedResource instanceof IFolder) && (allowedResource & SELECTION_ALLOW_FOLDER) == SELECTION_ALLOW_FOLDER) {
                        text = ((IFolder) selectedResource).getFullPath().toString();
                    }
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

    private class FileViewerFilter extends ViewerFilter {
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

    private class PHPResourceViewerFilter extends ViewerFilter {
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
}
