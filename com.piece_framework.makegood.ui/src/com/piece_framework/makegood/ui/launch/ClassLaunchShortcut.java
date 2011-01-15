/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
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

public class ClassLaunchShortcut extends NoSearchLaunchShortcut {
    @Override
    protected IModelElement getTarget(IEditorPart editor) {
        EditorParser parser = new EditorParser(editor);
        IModelElement element = parser.getModelElementOnSelection();
        if (element == null) {
            return parser.getSourceModule();
        }

        if (element.getElementType() == IModelElement.FIELD
            || element.getElementType() == IModelElement.METHOD
            ) {
            return element.getParent();
        }

        return element;
    }
}
