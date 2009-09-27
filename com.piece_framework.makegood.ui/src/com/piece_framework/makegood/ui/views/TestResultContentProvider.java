package com.piece_framework.makegood.ui.views;

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import com.piece_framework.makegood.launch.phpunit.TestResult;

public class TestResultContentProvider implements ITreeContentProvider {
    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof TestResult) {
            TestResult testResult = (TestResult) parentElement;
            return testResult.getTestResults().toArray();
        } else if (parentElement instanceof Collection) {
            Collection collection = (Collection) parentElement;
            return collection.toArray();
        }
        return null;
    }

    @Override
    public Object getParent(Object element) {
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return (element instanceof TestResult);
    }

    @Override
    public Object[] getElements(Object inputElement) {
        return getChildren(inputElement);
    }

    @Override
    public void dispose() {
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }
}
