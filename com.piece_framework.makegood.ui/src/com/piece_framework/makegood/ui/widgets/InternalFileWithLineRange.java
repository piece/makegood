/**
 * Copyright (c) 2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.widgets;

import org.eclipse.core.resources.IFile;

import com.piece_framework.makegood.ui.views.EditorOpener;


public class InternalFileWithLineRange extends FileWithLineRange {
    public IFile file;

    @Override
    public void openEditor() {
        EditorOpener.open(file, line);
    }
}
