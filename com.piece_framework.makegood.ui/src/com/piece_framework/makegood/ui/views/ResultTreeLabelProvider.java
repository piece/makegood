/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.piece_framework.makegood.core.result.Result;
import com.piece_framework.makegood.ui.Activator;

public class ResultTreeLabelProvider extends LabelProvider {
    private Image passIcon;
    private Image errorIcon;
    private Image failureIcon;

    public ResultTreeLabelProvider() {
        super();

        passIcon = Activator.getImageDescriptor("icons/pass-white.gif").createImage(); //$NON-NLS-1$
        errorIcon = Activator.getImageDescriptor("icons/error-white.gif").createImage(); //$NON-NLS-1$
        failureIcon = Activator.getImageDescriptor("icons/failure-white.gif").createImage(); //$NON-NLS-1$
    }

    @Override
    public String getText(Object element) {
        if (element instanceof Result) {
            Result testResult = (Result) element;

            return testResult.getName() + " (" +  //$NON-NLS-1$
                   TimeFormatter.format(testResult.getTime(), "s", "ms") +  //$NON-NLS-1$ //$NON-NLS-2$
                   ")";  //$NON-NLS-1$
        }
        return super.getText(element);
    }

    @Override
    public Image getImage(Object element) {
        if (!(element instanceof Result)) {
            return super.getImage(element);
        }

        Result result = (Result) element;
        Image icon = null;
        if (result.hasFailures()) {
            icon = failureIcon;
        } else if (result.hasErrors()) {
            icon = errorIcon;
        } else {
            icon = passIcon;
        }
        return icon;
    }
}
