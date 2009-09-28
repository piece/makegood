package com.piece_framework.makegood.ui.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.piece_framework.makegood.launch.phpunit.ProblemType;
import com.piece_framework.makegood.launch.phpunit.TestCase;

public class TestResultDetailsPage implements IDetailsPage {
    private IManagedForm managedForm;
    private List contentList;

    @Override
    public void initialize(IManagedForm form) {
        this.managedForm = form;
    }

    @Override
    public void createContents(Composite parent) {
        parent.setLayout(new FillLayout());

        FormToolkit toolkit = managedForm.getToolkit();
        Section section = toolkit.createSection(parent, Section.NO_TITLE);

        Composite client = toolkit.createComposite(section);
        client.setLayout(new FillLayout());

        contentList = new List(client, SWT.BORDER + SWT.SINGLE + SWT.V_SCROLL + SWT.H_SCROLL);

        section.setClient(client);
    }

    @Override
    public void commit(boolean onSave) {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub

    }


    @Override
    public boolean isDirty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isStale() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void refresh() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean setFormInput(Object input) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void selectionChanged(IFormPart part, ISelection selection) {
        IStructuredSelection structuredSelection = (IStructuredSelection) selection;
        if (!(structuredSelection.getFirstElement() instanceof TestCase)) {
            return;
        }

        contentList.removeAll();

        TestCase testCase = (TestCase) structuredSelection.getFirstElement();
        if (testCase.getProblem().getType() == ProblemType.NONE) {
            return;
        }

        String[] contents = testCase.getProblem().getContent().split("\n");
        for (String content: contents) {
            contentList.add(content);
        }
    }
}
