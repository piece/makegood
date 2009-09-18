package com.piece_framework.stagehand_testrunner.views;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;

public class ResultView extends ViewPart {
    private Label resultLabel;

    public ResultView() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void createPartControl(Composite parent) {
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        Form root = toolkit.createForm(parent);
        root.getBody().setLayout(new FillLayout());

        resultLabel = toolkit.createLabel(parent, "");
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

}
