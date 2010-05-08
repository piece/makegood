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

import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class OutputView extends ViewPart {
    public static final String ID = "com.piece_framework.makegood.ui.views.outputView"; //$NON-NLS-1$
    private ActiveText output;

    @Override
    public void createPartControl(Composite parent) {
        output = new ActiveText(parent);
        output.addListener(
            new EditorOpenActiveTextListener(
                Pattern.compile("in (.+) on line (\\d+)", Pattern.MULTILINE) //$NON-NLS-1$
            )
        );
        output.addListener(
            new MethodCreationActiveTextListener(
                Pattern.compile("Fatal error: Call to undefined method .+::(.+)\\(\\)", Pattern.MULTILINE) //$NON-NLS-1$
            )
        );
    }

    @Override
    public void setFocus() {}

    public void setText(String text) {
        output.setText(text);
    }
}
