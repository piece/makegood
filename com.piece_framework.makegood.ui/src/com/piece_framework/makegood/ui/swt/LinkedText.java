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
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.piece_framework.makegood.ui.ide.EditorOpen;

public class LinkedText implements MouseListener, MouseMoveListener {
    private StyledText linkedText;
    private Cursor handCursor;
    private Cursor arrowCursor;
    private Vector<FileWithLineRange> linkedRanges;
    private Pattern linkPattern;

    public LinkedText(Composite parent, Pattern linkPattern) {
        this.linkPattern = linkPattern;

        linkedText = new StyledText(
                         parent,
                         SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
                     );
        linkedText.setLayoutData(new GridData(GridData.FILL_BOTH));
        linkedText.setEditable(false);
        linkedText.addMouseListener(this);
        linkedText.addMouseMoveListener(this);

        handCursor = new Cursor(parent.getDisplay(), SWT.CURSOR_HAND);
        arrowCursor = new Cursor(parent.getDisplay(), SWT.CURSOR_ARROW);

        hideScrollBar();
    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {}

    @Override
    public void mouseDown(MouseEvent e) {
        FileWithLineRange range =
            FileWithLineRange.findFileWithLineRange(
                new Point(e.x, e.y), linkedText, linkedRanges
            );
        if (range == null) return;
        EditorOpen.open(range, range.line);
    }

    @Override
    public void mouseUp(MouseEvent e) {}

    @Override
    public void mouseMove(MouseEvent e) {
        FileWithLineRange range =
            FileWithLineRange.findFileWithLineRange(
                new Point(e.x, e.y), linkedText, linkedRanges
            );
        if (range != null) {
            linkedText.setCursor(handCursor);
        } else {
            linkedText.setCursor(arrowCursor); 
        }
    }

    public void setText(String text) {
        this.linkedText.setText(text);
        linkedRanges =
            FileWithLineRange.generateLinks(
                text, this.linkedText, linkPattern.matcher(text)
            );
        showScrollBar();
    }

    protected void showScrollBar() {
        linkedText.getVerticalBar().setVisible(true);
        linkedText.getHorizontalBar().setVisible(true);
    }

    protected void hideScrollBar() {
        linkedText.getVerticalBar().setVisible(false);
        linkedText.getHorizontalBar().setVisible(false);
    }
}
