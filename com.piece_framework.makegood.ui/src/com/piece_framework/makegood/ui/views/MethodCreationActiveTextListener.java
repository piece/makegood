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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;

public class MethodCreationActiveTextListener extends ActiveTextListener {
    MethodCreationActiveTextListener(Pattern pattern) {
        super(pattern);
    }

    @Override
    void generateActiveText() {
        Matcher matcher = pattern.matcher(text.getText());
        while (matcher.find()) {
            StyleRange style = new StyleRange();
            style.start = matcher.start(1);
            style.length = matcher.group(1).length();
            style.underline = true;
            style.underlineColor = new Color(text.getDisplay(), 255, 0, 128);
            style.underlineStyle = SWT.UNDERLINE_SQUIGGLE;
            this.text.addStyle(style);
        }
    }
}
