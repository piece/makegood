package com.piece_framework.makegood.ui.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.launch.phpunit.ProblemType;
import com.piece_framework.makegood.launch.phpunit.TestCase;
import com.piece_framework.makegood.launch.phpunit.TestResult;

public class TestResultLabelProvider extends LabelProvider {
    private Image passIcon;
    private Image errorIcon;
    private Image failureIcon;

    public TestResultLabelProvider() {
        super();

        passIcon = Activator.getImageDescriptor("icons/pass.gif").createImage();
        errorIcon = Activator.getImageDescriptor("icons/error.gif").createImage();
        failureIcon = Activator.getImageDescriptor("icons/failure.gif").createImage();
    }

    @Override
    public String getText(Object element) {
        if (element instanceof TestResult) {
            TestResult testResult = (TestResult) element;
            return testResult.getName();
        }
        return super.getText(element);
    }

    @Override
    public Image getImage(Object element) {
        ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
        if (element instanceof TestCase) {
            TestCase testCase = (TestCase) element;
            if (testCase.getProblem().getType() == ProblemType.Pass) {
                return passIcon;
            } else if (testCase.getProblem().getType() == ProblemType.Failure) {
                return failureIcon;
            } else if (testCase.getProblem().getType() == ProblemType.Error) {
                return errorIcon;
            }
        }
        return super.getImage(element);
    }
}
