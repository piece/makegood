package com.piece_framework.makegood.ui.propertyPages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

public class MakeGoodPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {
    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        Label label = new Label(composite, SWT.NONE);
        label.setText("&Preload Script:");

        Text preloadScript = new Text(composite, SWT.SINGLE | SWT.BORDER);
        preloadScript.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Button browse = new Button(composite, SWT.NONE);
        browse.setText("&Browse...");

        return composite;
    }
}
