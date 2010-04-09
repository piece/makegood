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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.piece_framework.makegood.ui.swt.FileWithLineRange;

class ActiveText implements MouseListener, MouseMoveListener {
    private StyledText text;
    private Cursor handCursor;
    private Cursor arrowCursor;
    private List<StyleRange> styles;
    private List<ActiveTextListener> listeners = new ArrayList<ActiveTextListener>();

    ActiveText(Composite parent) {
        text = new StyledText(
                   parent,
                   SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL
               );
        text.setLayoutData(new GridData(GridData.FILL_BOTH));
        text.setEditable(false);
        text.addMouseListener(this);
        text.addMouseMoveListener(this);

        handCursor = new Cursor(parent.getDisplay(), SWT.CURSOR_HAND);
        arrowCursor = new Cursor(parent.getDisplay(), SWT.CURSOR_ARROW);

        initializeStyles();
        hideScrollBar();
    }

    @Override
    public void mouseDoubleClick(MouseEvent e) {}

    @Override
    public void mouseDown(MouseEvent e) {
        StyleRange style = findStyle(new Point(e.x, e.y));
        if (style == null) return;
        if (!(style instanceof FileWithLineRange)) return;
        ((FileWithLineRange) style).openEditor();
    }

    @Override
    public void mouseUp(MouseEvent e) {}

    @Override
    public void mouseMove(MouseEvent e) {
        StyleRange style = findStyle(new Point(e.x, e.y));
        if (style == null) {
            text.setCursor(arrowCursor);
            return;
        }

        if (style instanceof FileWithLineRange) {
            text.setCursor(handCursor);
            return;
        }

        text.setCursor(arrowCursor);
    }

    void setText(String text) {
        initializeStyles();
        this.text.setText(text);

        for (ActiveTextListener listener: listeners) {
            listener.generateActiveText();
        }

        showScrollBar();
    }

    void addListener(ActiveTextListener listener) {
        listener.setActiveText(this);
        listeners.add(listener);
    }

    String getText() {
        return text.getText();
    }

    void addStyle(StyleRange style) {
        styles.add(style);
        text.setStyleRange(style);
    }

    Display getDisplay() {
        return text.getDisplay();
    }

    void showScrollBar() {
        text.getVerticalBar().setVisible(true);
        text.getHorizontalBar().setVisible(true);
    }

    void hideScrollBar() {
        text.getVerticalBar().setVisible(false);
        text.getHorizontalBar().setVisible(false);
    }

    private StyleRange findStyle(Point point) {
        int offset;

        try {
            offset = text.getOffsetAtLocation(point);
        } catch (IllegalArgumentException e) {
            return null;
        }

        for (int i = 0; i < styles.size(); ++i) {
            StyleRange style = styles.get(i);
            int startOffset = style.start;
            int endOffset = startOffset + style.length;
            if (offset >= startOffset && offset <= endOffset) {
                return style;
            }
        }

        return null;
    }

    private void initializeStyles() {
        styles = new ArrayList<StyleRange>();
    }
}
