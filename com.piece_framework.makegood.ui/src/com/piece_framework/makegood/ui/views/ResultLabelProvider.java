package com.piece_framework.makegood.ui.views;

import java.text.NumberFormat;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.piece_framework.makegood.launch.elements.TestResult;
import com.piece_framework.makegood.ui.Activator;

public class ResultLabelProvider extends LabelProvider {
    private Image passIcon;
    private Image errorIcon;
    private Image failureIcon;

    public ResultLabelProvider() {
        super();

        passIcon = Activator.getImageDescriptor("icons/pass-white.gif").createImage(); //$NON-NLS-1$
        errorIcon = Activator.getImageDescriptor("icons/error-white.gif").createImage(); //$NON-NLS-1$
        failureIcon = Activator.getImageDescriptor("icons/failure-white.gif").createImage(); //$NON-NLS-1$
    }

    @Override
    public String getText(Object element) {
        if (element instanceof TestResult) {
            TestResult testResult = (TestResult) element;

            return testResult.getName() + " (" +  //$NON-NLS-1$
                   TimeFormatter.format(testResult.getTime(), "s", "ms") +  //$NON-NLS-1$ //$NON-NLS-2$
                   ")";  //$NON-NLS-1$
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
