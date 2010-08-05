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

public abstract class ActiveTextListener {
    protected Pattern pattern;
    protected ActiveText text;

    public ActiveTextListener(Pattern pattern) {
        this.pattern = pattern;
    }

    void setActiveText(ActiveText text) {
        this.text = text;
    }

    public abstract void generateActiveText();
}
