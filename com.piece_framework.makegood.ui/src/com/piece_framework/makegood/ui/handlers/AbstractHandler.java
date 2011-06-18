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

package com.piece_framework.makegood.ui.handlers;

import com.piece_framework.makegood.aspect.AspectWeaver;
import com.piece_framework.makegood.ui.MakeGoodContext;

/**
 * @since 1.6.0
 */
public abstract class AbstractHandler extends org.eclipse.core.commands.AbstractHandler {
    @Override
    public boolean isEnabled() {
        if (MakeGoodContext.getInstance().isShuttingDown()) return false;
        if (!AspectWeaver.isFinished()) return false;
        return true;
    }
}
