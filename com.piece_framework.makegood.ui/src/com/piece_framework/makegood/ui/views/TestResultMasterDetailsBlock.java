package com.piece_framework.makegood.ui.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

public class TestResultMasterDetailsBlock extends MasterDetailsBlock {
    private TreeViewer viewer;

    @Override
    protected void createMasterPart(IManagedForm managedForm, Composite parent) {
        FormToolkit toolkit = managedForm.getToolkit();
        Section section = toolkit.createSection(parent, Section.NO_TITLE);

        Composite client = toolkit.createComposite(section);
        client.setLayout(new FillLayout());
        Tree tree = toolkit.createTree(client, SWT.BORDER);
        viewer = new TreeViewer(tree);
        viewer.setContentProvider(new TestResultContentProvider());
        viewer.setLabelProvider(new TestResultLabelProvider());

        section.setClient(client);
    }

    @Override
    protected void createToolBarActions(IManagedForm managedForm) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void registerPages(DetailsPart detailsPart) {
        // TODO Auto-generated method stub

    }

    public void setInput(Object input) {
        viewer.setInput(input);
    }
}
