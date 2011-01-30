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

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.ui.IEditorPart;

import com.piece_framework.makegood.ui.views.EditorParser;

public class ContextLaunchShortcut extends NoSearchLaunchShortcut {
    @Override
    protected IModelElement getTestingTarget(IEditorPart editor) {
        EditorParser parser = new EditorParser(editor);
        IModelElement target = parser.getModelElementOnSelection();
        if (target == null) {
            return parser.getSourceModule();
        }

        if (target.getElementType() == IModelElement.FIELD) {
            return target.getParent();
        }

        return target;
    }
}
