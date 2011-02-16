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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public class ProgressBar extends Composite implements PaintListener, ControlListener {
    private Color passColor;
    private Color failureColor;
    private Color stoppedColor;
    private Color gradientColor;
    private CLabel bar;
    private int progressRate;

    public ProgressBar(Composite parent) {
        super(parent, SWT.BORDER);

        passColor = new Color(getDisplay(), 105, 153, 61);
        failureColor = new Color(getDisplay(), 209, 19, 24);
        stoppedColor = new Color(getDisplay(), 120, 120, 120);
        gradientColor = new Color(getDisplay(), 255, 255, 255);
        addPaintListener(this);

        bar = new CLabel(this, SWT.NONE);
        bar.addControlListener(this);
        bar.addPaintListener(this);

        clear();
    }

    @Override
    public void paintControl(PaintEvent e) {
        String text = progressRate + "%"; //$NON-NLS-1$
        Point size = getSize();
        FontMetrics fontMetrics = e.gc.getFontMetrics();
        int width = fontMetrics.getAverageCharWidth() * text.length();
        int height = fontMetrics.getHeight();
        e.gc.drawText(text, (size.x - width) / 2 , (size.y - height) / 2, true);
    }

    @Override
    public void controlMoved(ControlEvent e) {
    }

    @Override
    public void controlResized(ControlEvent e) {
        update(progressRate);
    }

    public void update(int progressRate) {
        int maxWidth = getSize().x;

        int width = bar.getSize().x;
        if (progressRate < 100) {
            width = (int) (maxWidth * ((double) progressRate / 100d));
        } else if (progressRate >= 100) {
            width = maxWidth;
        }
        final int barWidth = width;

        getDisplay().asyncExec(
            new Runnable() {
                @Override
                public void run() {
                    Point size = bar.getSize();
                    size.x = barWidth;
                    bar.setSize(size);
                    redraw();
                    bar.redraw();
                }
            }
        );

        this.progressRate = progressRate;
    }

    public void markAsFailed() {
        bar.setBackground(
            new Color[] { gradientColor, failureColor },
            new int[] { 100 },
            true
        );
    }

    public void markAsStopped() {
        bar.setBackground(
            new Color[] { gradientColor, stoppedColor },
            new int[] { 100 },
            true
        );
    }

    public void clear() {
        bar.setBackground(
            new Color[] { gradientColor, passColor },
            new int[] { 100 },
            true
        );
        update(0);
    }
}
