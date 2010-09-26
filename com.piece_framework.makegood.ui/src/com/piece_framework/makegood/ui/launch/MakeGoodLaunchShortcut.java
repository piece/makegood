/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.launch;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.php.internal.debug.ui.launching.PHPExeLaunchShortcut;
import org.eclipse.ui.IEditorPart;

public class MakeGoodLaunchShortcut extends PHPExeLaunchShortcut {
    @Override
    public void launch(final ISelection selection, final String mode) {
        super.launch(selection, mode);
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
        super.launch(editor, mode);
    }

    @Override
    protected ILaunchConfigurationType getPHPExeLaunchConfigType() {
        return DebugPlugin.getDefault()
                          .getLaunchManager()
                          .getLaunchConfigurationType("com.piece_framework.makegood.launch.makegoodLaunchConfigurationType"); //$NON-NLS-1$
    }
}
