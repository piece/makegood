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

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;

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
}
