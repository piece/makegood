/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.launch;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

import com.piece_framework.makegood.launch.LaunchTarget;

public class ResourceLaunchShortcut extends MakeGoodLaunchShortcut {
    @Override
    public void launch(ISelection selection, String mode) {
        if (!(selection instanceof IStructuredSelection)) throw new NotLaunchedException();

        Object target = ((IStructuredSelection) selection).getFirstElement();
        LaunchTarget launchTarget = LaunchTarget.getInstance();
        launchTarget.clearTargets();
        launchTarget.addTarget(target);

        IResource mainScriptResource = launchTarget.getMainScriptResource();
        if (mainScriptResource == null) throw new NotLaunchedException();

        super.launch(new StructuredSelection(mainScriptResource), mode);
    }
}
