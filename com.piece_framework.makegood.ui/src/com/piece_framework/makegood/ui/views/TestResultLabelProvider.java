package com.piece_framework.makegood.ui.views;

import java.text.NumberFormat;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.piece_framework.makegood.launch.phpunit.TestResult;
import com.piece_framework.makegood.ui.Activator;

public class TestResultLabelProvider extends LabelProvider {
    private Image passIcon;
    private Image errorIcon;
    private Image failureIcon;
    private final NumberFormat timeFormat;

    public TestResultLabelProvider() {
        super();

        passIcon = Activator.getImageDescriptor("icons/pass-white.gif").createImage(); //$NON-NLS-1$
        errorIcon = Activator.getImageDescriptor("icons/error-white.gif").createImage(); //$NON-NLS-1$
        failureIcon = Activator.getImageDescriptor("icons/failure-white.gif").createImage(); //$NON-NLS-1$

        timeFormat = NumberFormat.getNumberInstance();
        timeFormat.setMaximumFractionDigits(3);
        timeFormat.setMinimumFractionDigits(3);
    }

    @Override
    public String getText(Object element) {
        if (element instanceof TestResult) {
            TestResult testResult = (TestResult) element;

            String formattedTime = timeFormat.format(testResult.getTime());
            return testResult.getName() + " (" + formattedTime + " s)"; //$NON-NLS-1$ //$NON-NLS-2$
        }
        return super.getText(element);
    }

    @Override
    public Image getImage(Object element) {
        if (!(element instanceof TestResult)) {
            return super.getImage(element);
        }

        TestResult result = (TestResult) element;
        Image icon = null;
        if (result.hasFailure()) {
            icon = failureIcon;
        } else if (result.hasError()) {
            icon = errorIcon;
        } else {
            icon = passIcon;
        }
        return icon;
    }
}
