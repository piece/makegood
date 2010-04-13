/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.launch;

import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.piece_framework.makegood.launch.MakeGoodLaunchParameter;
import com.piece_framework.makegood.ui.Activator;

public abstract class NoSearchLaunchShortcut extends MakeGoodLaunchShortcut {
    @Override
    public void launch(IEditorPart editor, String mode) {
        if (!(editor instanceof ITextEditor)) {
            return;
        }

        IModelElement target = getTarget(editor);
        if (target.exists() == false) {
            Activator.getDefault().getLog().log(
                new Status(
                    Status.WARNING,
                    Activator.PLUGIN_ID,
                    "The given test target is not found" //$NON-NLS-1$
                )
            );
            return;
        }

        MakeGoodLaunchParameter parameter = MakeGoodLaunchParameter.getInstance();
        parameter.clearTargets();
        parameter.addTarget(target);

        super.launch(editor, mode);
    }

    protected abstract IModelElement getTarget(IEditorPart editor);
}
