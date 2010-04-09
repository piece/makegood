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

package com.piece_framework.makegood.ui.views;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import com.piece_framework.makegood.ui.ide.FileFind;
import com.piece_framework.makegood.ui.swt.ExternalFileWithLineRange;
import com.piece_framework.makegood.ui.swt.FileWithLineRange;
import com.piece_framework.makegood.ui.swt.InternalFileWithLineRange;

class EditorOpenActiveTextListener extends ActiveTextListener {
    EditorOpenActiveTextListener(Pattern pattern) {
        super(pattern);
    }

    @Override
    void generateActiveText() {
        Matcher matcher = pattern.matcher(text.getText());

        while (matcher.find()) {
            IFile[] files = FileFind.findFiles(matcher.group(1));
            if (files == null) continue;

            FileWithLineRange style;
            if (files.length > 0) {
                InternalFileWithLineRange iStyle = new InternalFileWithLineRange();
                iStyle.file = files[0];
                iStyle.foreground = text.getDisplay().getSystemColor(SWT.COLOR_BLUE);
                style = (FileWithLineRange) iStyle;
            } else {
                ExternalFileWithLineRange eStyle = new ExternalFileWithLineRange();
                IFileStore fileStore = FileFind.findFileStore(matcher.group(1));
                if (fileStore == null) continue;
                eStyle.fileStore = fileStore;
                eStyle.foreground = new Color(text.getDisplay(), 114, 159, 207);
                style = (FileWithLineRange) eStyle;
            }

            style.start = matcher.start();
            style.length = matcher.group().length();
            style.line = Integer.valueOf(matcher.group(2));

            this.text.addStyle(style);
        }
    }
}
