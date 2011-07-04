/**
 * Copyright (c) 2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.handlers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.piece_framework.makegood.launch.TestLifecycle;
import com.piece_framework.makegood.ui.Activator;
import com.piece_framework.makegood.ui.MakeGoodContext;
import com.piece_framework.makegood.ui.launch.TestRunner;

/**
 * @since 1.6.0
 */
public abstract class RunHandler extends AbstractHandler {
    @Override
    public boolean isEnabled() {
        if (!super.isEnabled()) return false;
        if (!doIsEnabled()) return false;
        try {
            if (!MakeGoodContext.getInstance().getProjectValidation().validate(MakeGoodContext.getInstance().getActivePart().getProject())) return false;
        } catch (CoreException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, e.getMessage(), e));
            return false;
        }
        if (TestLifecycle.isRunning()) return false;
        return true;
    }

    protected TestRunner getTestRunner() {
        return MakeGoodContext.getInstance().getTestRunner();
    }

    protected boolean doIsEnabled() {
        return true;
    }
}
