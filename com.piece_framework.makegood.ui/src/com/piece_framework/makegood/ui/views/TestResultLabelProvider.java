package com.piece_framework.makegood.ui.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import com.piece_framework.makegood.launch.phpunit.Error;
import com.piece_framework.makegood.launch.phpunit.Failure;
import com.piece_framework.makegood.launch.phpunit.TestCase;
import com.piece_framework.makegood.launch.phpunit.TestResult;

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

    @Override
    public Image getImage(Object element) {
        ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
        if (element instanceof TestCase) {
            TestCase testCase = (TestCase) element;
            if (testCase.getError() != null) {
                return images.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
            } else if (testCase.getFailure() != null){
                return images.getImage(ISharedImages.IMG_OBJS_WARN_TSK);
            } else {
                return images.getImage(ISharedImages.IMG_OBJS_INFO_TSK);
            }
        }
        return super.getImage(element);
    }
}
