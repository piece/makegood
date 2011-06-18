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

import org.eclipse.core.resources.IProject;

import com.piece_framework.makegood.launch.TestLifecycle;
import com.piece_framework.makegood.ui.MakeGoodContext;
import com.piece_framework.makegood.ui.launch.TestRunner;

/**
 * @since 1.6.0
 */
public abstract class RunHandler extends AbstractHandler {
    @Override
    public boolean isEnabled() {
        if (!super.isEnabled()) return false;
        IProject project = getProject();
        if (project == null) return false;
        if (!MakeGoodContext.getInstance().getProjectValidation().validate(project)) return false;
        if (TestLifecycle.isRunning()) return false;
        return true;
    }

    protected IProject getProject() {
        return MakeGoodContext.getInstance().getActivePart().getProject();
    }

    protected TestRunner getTestRunner() {
        return MakeGoodContext.getInstance().getTestRunner();
    }
}
