package com.piece_framework.makegood.ui.views;

import org.eclipse.jface.viewers.LabelProvider;
import com.piece_framework.makegood.launch.phpunit.TestResult;
import com.piece_framework.makegood.launch.phpunit.Error;
import com.piece_framework.makegood.launch.phpunit.Failure;

public class TestResultLabelProvider extends LabelProvider {
    @Override
    public String getText(Object element) {
        if (element instanceof TestResult) {
            TestResult testResult = (TestResult) element;
            return testResult.getName();
        } else if (element instanceof Error) {
            Error error = (Error) element;
            return error.getType();
        } else if (element instanceof Failure) {
            Failure failure = (Failure) element;
            return failure.getType();
        }
        return super.getText(element);
    }
}
