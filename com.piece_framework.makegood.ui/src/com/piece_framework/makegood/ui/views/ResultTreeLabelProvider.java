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
import com.piece_framework.makegood.core.result.TestCaseResult;
import com.piece_framework.makegood.ui.Activator;

public class ResultTreeLabelProvider extends LabelProvider {
    private Image passIcon;
    private Image errorIcon;
    private Image failureIcon;
    private Image inProgressIcon;

    public ResultTreeLabelProvider() {
        super();

        passIcon = Activator.getImageDescriptor("icons/pass-white.gif").createImage(); //$NON-NLS-1$
        errorIcon = Activator.getImageDescriptor("icons/error-white.gif").createImage(); //$NON-NLS-1$
        failureIcon = Activator.getImageDescriptor("icons/failure-white.gif").createImage(); //$NON-NLS-1$
        inProgressIcon = Activator.getImageDescriptor("icons/inProgress.gif").createImage(); //$NON-NLS-1$
    }

    @Override
    public String getText(Object element) {
        Result result = (Result) element;
        return result.getName() + " (" +  //$NON-NLS-1$
               TimeFormatter.format(result.getTime()) +
               ")";  //$NON-NLS-1$
    }

    @Override
    public Image getImage(Object element) {
        Result result = (Result) element;
        if (!result.fixed()) {
            if (result instanceof TestCaseResult) {
                return inProgressIcon;
            } else {
                return null;
            }
        }

        if (result.hasFailures()) {
            return failureIcon;
        } else if (result.hasErrors()) {
            return errorIcon;
        } else {
            return passIcon;
        }
    }
}
