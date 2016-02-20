/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2012, 2014 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.preferences;

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
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.piece_framework.makegood.core.DefaultConfiguration;
import com.piece_framework.makegood.core.TestingFramework;
import com.piece_framework.makegood.core.preference.MakeGoodProperties;
import com.piece_framework.makegood.ui.Messages;

public class MakeGoodPropertyPage extends PropertyPage {
    private static final int SELECTION_ALLOW_FILE = 1;
    private static final int SELECTION_ALLOW_FOLDER = 2;
    private Text preloadScriptText;
    private Text phpunitConfigFileText;
    private Text phpunitPharFileText;
    private Button phpunitButton;

    private TreeViewer testFolderTreeViewer;
    private Button testFolderRemoveButton;
    private TabFolder contents;

    /**
     * @since 2.0.0
     */
    private ArrayList<Button> frameworkButtons = new ArrayList<Button>();

    /**
     * @since 2.0.0
     */
    private ArrayList<TabItem> frameworkTabItems = new ArrayList<TabItem>();

    /**
     * @since 2.0.0
     */
    private Label testFilePatternLabel;

    /**
     * @since 2.0.0
     */
    private Text testFilePatternText;

    @Override
    protected Control createContents(Composite parent) {
        contents = new TabFolder(parent, SWT.NONE);
        contents.setLayout(new GridLayout());

        TabItem generalTabItem = new TabItem(contents, SWT.NONE);
        generalTabItem.setText(Messages.MakeGoodPropertyPage_generalLabel);
        Composite generalTab = new Composite(contents, SWT.NONE);
        generalTab.setLayout(new GridLayout());
        generalTabItem.setControl(generalTab);

        Group frameworkGroup = new Group(generalTab, SWT.LEFT);
        frameworkGroup.setText(Messages.MakeGoodPropertyPage_testingFrameworkLabel);
        frameworkGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        frameworkGroup.setLayout(new GridLayout());

        phpunitButton = new Button(frameworkGroup, SWT.RADIO);
        phpunitButton.setText(TestingFramework.PHPUnit.name());
        phpunitButton.addSelectionListener(new FrameworkSelectionAdapter());
        frameworkButtons.add(phpunitButton);

        Group testFolderGroup = new Group(generalTab, SWT.LEFT);
        testFolderGroup.setText(Messages.MakeGoodPropertyPage_testFolderLabel);
        testFolderGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
        testFolderGroup.setLayout(new GridLayout(2, false));
        testFolderTreeViewer = new TreeViewer(testFolderGroup, SWT.BORDER + SWT.SINGLE);
        testFolderTreeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
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
        testFolderButtons.setLayoutData(new GridData(SWT.NONE, SWT.BEGINNING, false, false));
        Button testFolderAddButton = new Button(testFolderButtons, SWT.NONE);
        testFolderAddButton.setText(Messages.MakeGoodPropertyPage_testFolderAddLabel);
        testFolderAddButton.addSelectionListener(new AddTestFolderSelectionListener());
        testFolderRemoveButton = new Button(testFolderButtons, SWT.NONE);
        testFolderRemoveButton.setText(Messages.MakeGoodPropertyPage_testFolderRemoveLabel);
        testFolderRemoveButton.setEnabled(false);
        testFolderRemoveButton.addSelectionListener(new RemoveTestFolderSelectionListener());

        Composite preloadScript = new Composite(generalTab, SWT.NONE);
        preloadScript.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        preloadScript.setLayout(new GridLayout(3, false));
        Label preloadScriptLabel = new Label(preloadScript, SWT.NONE);
        preloadScriptLabel.setText(Messages.MakeGoodPropertyPage_preloadScriptLabel);
        preloadScriptText = new Text(preloadScript, SWT.SINGLE | SWT.BORDER);
        preloadScriptText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Button preloadScriptBrowseButton = new Button(preloadScript, SWT.NONE);
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

        Composite testFilePattern = new Composite(generalTab, SWT.NONE);
        testFilePattern.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        testFilePattern.setLayout(new GridLayout(2, false));
        testFilePatternLabel = new Label(testFilePattern, SWT.NONE);
        testFilePatternText = new Text(testFilePattern, SWT.SINGLE | SWT.BORDER);
        testFilePatternText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // PHPUnit
        TabItem phpunitTabItem = new TabItem(contents, SWT.NONE);
        phpunitTabItem.setText(TestingFramework.PHPUnit.name());
        Composite phpunitTab = new Composite(contents, SWT.NONE);
        phpunitTab.setLayout(new GridLayout());
        phpunitTabItem.setControl(phpunitTab);
        Composite phpunitConfigFile = new Composite(phpunitTab, SWT.NONE);
        phpunitConfigFile.setLayout(new GridLayout(3, false));
        phpunitConfigFile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Label phpunitConfigFileLabel = new Label(phpunitConfigFile, SWT.NONE);
        phpunitConfigFileLabel.setText(Messages.MakeGoodPropertyPage_phpunitConfigFileLabel);
        phpunitConfigFileText = new Text(phpunitConfigFile, SWT.SINGLE | SWT.BORDER);
        phpunitConfigFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Button phpunitConfigFileBrowseButton = new Button(phpunitConfigFile, SWT.NONE);
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
        
        Label phpunitPharFileLabel = new Label(phpunitConfigFile, SWT.NONE);
        phpunitPharFileLabel.setText(Messages.MakeGoodPropertyPage_phpunitPharFileLabel);
        phpunitPharFileText = new Text(phpunitConfigFile, SWT.SINGLE | SWT.BORDER);
        phpunitPharFileText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Button phpunitPharFileBrowseButton = new Button(phpunitConfigFile, SWT.NONE);
        phpunitPharFileBrowseButton.setText(Messages.MakeGoodPropertyPage_phpunitPharFileBrowseLabel);
        phpunitPharFileBrowseButton.addSelectionListener(
            new FileSelectionListener(
                phpunitPharFileText,
                Messages.MakeGoodPropertyPage_phpunitPharFileDialogTitle,
                Messages.MakeGoodPropertyPage_phpunitPharFileDialogMessage,
                SELECTION_ALLOW_FILE,
                new FileViewerFilter()
            )
        );
        
        frameworkTabItems.add(phpunitTabItem);

        loadProperties(createMakeGoodProperty());
        contents.setSelection(generalTabItem);

        return contents;
    }

    @Override
    protected void performDefaults() {
        loadProperties(new DefaultConfiguration());
        super.performDefaults();
    }

    @Override
    public boolean performOk() {
        MakeGoodProperties property = createMakeGoodProperty();
        TestingFramework testingFramework = null;
        if (phpunitButton.getSelection()) {
            testingFramework = TestingFramework.PHPUnit;
        }
        property.setTestingFramework(testingFramework);
        property.setTestFolders((List<IFolder>) testFolderTreeViewer.getInput());
        property.setPreloadScript(preloadScriptText.getText());
        property.setTestFilePattern(testFilePatternText.getText());
        property.setPHPUnitConfigFile(phpunitConfigFileText.getText());
        property.setPHPUnitPharFile(phpunitPharFileText.getText());
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

    /**
     * @since 2.0.0
     */
    private void updateFrameworkSettings(TestingFramework testingFramework)
    {
        for (Button frameworkButton: frameworkButtons) {
            frameworkButton.setSelection(testingFramework.name().equals(frameworkButton.getText()));
        }

        for (TabItem frameworkTabItem: frameworkTabItems) {
            Composite frameworkTab = (Composite) frameworkTabItem.getControl();
            for (Control frameworkControl: frameworkTab.getChildren()) {
                changeControlState(frameworkControl, testingFramework.name().equals(frameworkTabItem.getText()));
            }
        }

        testFilePatternLabel.setText(
            Messages.MakeGoodPropertyPage_testFilePatternLabel +
            " (" + //$NON-NLS-1$
            Messages.MakeGoodPropertyPage_defaultTestFilePatternLabel +
            ": " + //$NON-NLS-1$
            testingFramework.getTestFilePattern() +
            "):" //$NON-NLS-1$
        );
    }

    /**
     * @since 2.0.0
     */
    private void changeControlState(Control control, boolean enabled) {
        if (control instanceof Composite) {
            for (Control child: ((Composite) control).getChildren()) {
                changeControlState(child, enabled);
            }
        } else {
            control.setEnabled(enabled);
        }
    }

    /**
     * @since 2.0.0
     */
    private void loadProperties(MakeGoodProperties property) {
        updateFrameworkSettings(property.getTestingFramework());
        testFolderTreeViewer.setInput(property.getTestFolders());
        preloadScriptText.setText(property.getPreloadScript());
        testFilePatternText.setText(property.getTestFilePattern());
        phpunitConfigFileText.setText(property.getPHPUnitConfigFile());
        phpunitPharFileText.setText(property.getPHPUnitPharFile());
    }

    /**
     * @since 2.0.0
     */
    private void loadProperties(DefaultConfiguration defaultConfiguration) {
        updateFrameworkSettings(defaultConfiguration.getTestingFramework());
        testFolderTreeViewer.setInput(defaultConfiguration.getTestFolders());
        preloadScriptText.setText(defaultConfiguration.getPreloadScript());
        testFilePatternText.setText(defaultConfiguration.getTestFilePattern());
        phpunitConfigFileText.setText(defaultConfiguration.getPHPUnitConfigFile());
        phpunitPharFileText.setText(defaultConfiguration.getPHPUnitPharFile());
    }

    /**
     * @since 2.0.0
     */
    private MakeGoodProperties createMakeGoodProperty() {
        return new MakeGoodProperties(getProject());
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

    /**
     * @since 2.0.0
     */
    private class FrameworkSelectionAdapter extends SelectionAdapter {
        public void widgetSelected(SelectionEvent e) {
            if (e.getSource() instanceof Button) {
                updateFrameworkSettings(TestingFramework.valueOf(((Button) e.getSource()).getText()));
            }
        }
    }
}
