package com.piece_framework.makegood.ui.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.piece_framework.makegood.launch.phpunit.ProblemType;
import com.piece_framework.makegood.launch.phpunit.TestCase;
import com.piece_framework.makegood.launch.phpunit.TestResult;

public class TestResultLabelProvider extends LabelProvider {
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
            if (testCase.getProblem().getType() == ProblemType.NONE) {
                return images.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
            } else if (testCase.getProblem().getType() == ProblemType.Failure) {
                return images.getImage(ISharedImages.IMG_OBJS_WARN_TSK);
            } else if (testCase.getProblem().getType() == ProblemType.Error) {
                return images.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
            }
        }
        return super.getImage(element);
    }
}
