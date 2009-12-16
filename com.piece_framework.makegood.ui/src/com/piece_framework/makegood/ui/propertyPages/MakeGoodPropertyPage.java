package com.piece_framework.makegood.ui.propertyPages;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.piece_framework.makegood.core.MakeGoodProperty;
import com.piece_framework.makegood.core.PHPResource;
import com.piece_framework.makegood.core.TestingFramework;
import com.piece_framework.makegood.ui.Messages;

public class MakeGoodPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {
    private static String PRELOAD_SCRIP_KEY = "preload_script"; //$NON-NLS-1$
    private Text preloadScript;
    private Button phpUnit;
    private Button simpleTest;

    @Override
    protected Control createContents(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Group group = new Group(composite, SWT.LEFT | SWT.TOP);
        group.setText("&Testing Framework");
        group.setLayout(new RowLayout());
        GridData gridData = new GridData();
        gridData.horizontalSpan = 3;
        gridData.horizontalAlignment = SWT.FILL;
        group.setLayoutData(gridData);

        phpUnit = new Button(group, SWT.RADIO);
        phpUnit.setText("P&HPUnit");
        simpleTest = new Button(group, SWT.RADIO);
        simpleTest.setText("&SimpleTest");

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
                            return PHPResource.isTrue((IFile) element);
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

        MakeGoodProperty property = new MakeGoodProperty(getProject());
        if (property.usePHPUnit()) {
            phpUnit.setSelection(true);
        } else if (property.useSimpleTest()) {
            simpleTest.setSelection(true);
        } else {
            phpUnit.setSelection(true);
        }
        preloadScript.setText(property.getPreloadScript());

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
