package com.piece_framework.makegood.ui.propertyPages;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.osgi.service.prefs.BackingStoreException;

import com.piece_framework.makegood.ui.Activator;

public class MakeGoodPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {
    private static String PRELOAD_SCRIP_KEY = "preload_script";
    private Text preloadScript;

    @Override
    protected Control createContents(Composite parent) {
        final Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label label = new Label(composite, SWT.NONE);
        label.setText("&Preload Script:");

        preloadScript = new Text(composite, SWT.SINGLE | SWT.BORDER);
        preloadScript.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Button browse = new Button(composite, SWT.NONE);
        browse.setText("&Browse...");
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
                dialog.setTitle("MakeGood Preload Script");
                dialog.setMessage("Select a preload script:");
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
                    private IContentType contentType = Platform.getContentTypeManager().getContentType("org.eclipse.php.core.phpsource");

                    @Override
                    public boolean select(Viewer viewer,
                                          Object parentElement,
                                          Object element
                                          ) {
                        if (element instanceof IFile) {
                            if (contentType.isAssociatedWith(((IFile) element).getName())) {
                                return true;
                            }
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

        IScopeContext context = new ProjectScope(getProject());
        preloadScript.setText(context.getNode(Activator.PLUGIN_ID).get(PRELOAD_SCRIP_KEY, ""));

        return composite;
    }

    @Override
    public boolean performOk() {
        IEclipsePreferences projectNode = new ProjectScope(getProject()).getNode(Activator.PLUGIN_ID);
        projectNode.put(PRELOAD_SCRIP_KEY, preloadScript.getText());
        try {
            projectNode.flush();
        } catch (BackingStoreException e) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR,
                                                           Activator.PLUGIN_ID,
                                                           e.getMessage(),
                                                           e
                                                           ));
        }

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
