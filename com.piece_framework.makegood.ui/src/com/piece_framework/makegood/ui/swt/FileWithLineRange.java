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

package com.piece_framework.makegood.ui.swt;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;

import com.piece_framework.makegood.ui.ide.FileFind;

public class FileWithLineRange extends StyleRange {
    public Integer line;

    public static FileWithLineRange findFileWithLineRange(Point point, final StyledText text, final Vector<FileWithLineRange> ranges) {
        int offset;

        try {
            offset = text.getOffsetAtLocation(point);
        } catch (IllegalArgumentException e) {
            return null;
        }

        for (int i = 0; i < ranges.size(); ++i) {
            FileWithLineRange range = ranges.get(i);
            int startOffset = range.start;
            int endOffset = startOffset + range.length;
            if (offset >= startOffset && offset <= endOffset) return range;
        }

        return null;
    }

    public static Vector<FileWithLineRange> generateLinks(String text, StyledText styledText) {
        Vector<FileWithLineRange> ranges = new Vector<FileWithLineRange>();
        Matcher matcher = Pattern.compile(
                              "^(.+):(\\d+)$", //$NON-NLS-1$
                              Pattern.MULTILINE
                                  ).matcher(text);
        while (matcher.find()) {
            IFile[] files = FileFind.findFiles(matcher.group(1));
            if (files == null)
                continue;
            FileWithLineRange range;
            if (files.length > 0) {
                InternalFileWithLineRange iRange = new InternalFileWithLineRange();
                iRange.file = files[0];
                iRange.foreground = styledText.getDisplay().getSystemColor(
                        SWT.COLOR_BLUE);
                range = (FileWithLineRange) iRange;
            } else {
                ExternalFileWithLineRange eRange = new ExternalFileWithLineRange();
                IFileStore fileStore = FileFind.findFileStore(matcher.group(1));
                if (fileStore == null)
                    continue;
                eRange.fileStore = fileStore;
                eRange.foreground = new Color(styledText.getDisplay(), 114,
                        159, 207);
                range = (FileWithLineRange) eRange;
            }

            range.start = matcher.start();
            range.length = matcher.group().length();
            range.line = Integer.valueOf(matcher.group(2));
            ranges.add(range);
            styledText.setStyleRange(range);
        }

        return ranges;
    }
}
