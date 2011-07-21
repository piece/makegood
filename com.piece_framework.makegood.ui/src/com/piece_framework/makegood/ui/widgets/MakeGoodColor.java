/**
 * Copyright (c) 2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui.widgets;

import org.eclipse.swt.graphics.RGB;

/**
 * @since 1.6.0
 */
public class MakeGoodColor {
    public static final RGB FAILED = new RGB(209, 19, 24);

    /**
     * @since 1.7.0
     */
    public static final RGB GRADIENT_FAILED = new RGB(240, 198, 200);

    public static final RGB PASSED = new RGB(105, 153, 61);

    /**
     * @since 1.7.0
     */
    public static final RGB GRADIENT_PASSED = new RGB(218, 229, 209);

    public static final RGB STOPPED = new RGB(120, 120, 120);

    /**
     * @since 1.7.0
     */
    public static final RGB GRADIENT_STOPPED = new RGB(222, 223, 223);

    public static final RGB LINK_INTERNAL = new RGB(0, 51, 153);
    public static final RGB LINK_EXTERNAL = new RGB(114, 159, 207);
}
