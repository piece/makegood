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
import org.eclipse.swt.widgets.Composite;


public class ActiveText extends StyledText implements MouseListener, MouseMoveListener {
    private Cursor handCursor;
    private Cursor arrowCursor;
    private List<StyleRange> styles;
    private List<ActiveTextListener> listeners = new ArrayList<ActiveTextListener>();

    public ActiveText(Composite parent, int style) {
        super(parent, style);
        addMouseListener(this);
        addMouseMoveListener(this);

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
            setCursor(arrowCursor);
            return;
        }

        if (style instanceof FileWithLineRange) {
            setCursor(handCursor);
            return;
        }

        setCursor(arrowCursor);
    }

    @Override
    public void setText(String text) {
        initializeStyles();
        super.setText(text);

        for (ActiveTextListener listener: listeners) {
            listener.generateActiveText();
        }

        showScrollBar();
    }

    public void addListener(ActiveTextListener listener) {
        listener.setActiveText(this);
        listeners.add(listener);
    }

    public void addStyle(StyleRange style) {
        styles.add(style);
        setStyleRange(style);
    }

    void showScrollBar() {
        getVerticalBar().setVisible(true);
        getHorizontalBar().setVisible(true);
    }

    protected void hideScrollBar() {
        getVerticalBar().setVisible(false);
        getHorizontalBar().setVisible(false);
    }

    private StyleRange findStyle(Point point) {
        int offset;

        try {
            offset = getOffsetAtLocation(point);
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
