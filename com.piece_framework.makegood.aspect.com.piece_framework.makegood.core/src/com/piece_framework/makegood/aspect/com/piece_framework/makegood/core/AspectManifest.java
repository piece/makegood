/**
 * Copyright (c) 2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect.com.piece_framework.makegood.core;

import com.piece_framework.makegood.aspect.Aspect;
import com.piece_framework.makegood.aspect.com.piece_framework.makegood.core.aspect.PHPFlagsAspect;

/**
 * @since 1.2.0
 */
public class AspectManifest implements com.piece_framework.makegood.aspect.AspectManifest {
    private static final Aspect[] ASPECTS = {
        new PHPFlagsAspect()
    };
    private static final String[] DEPENDENCIES = {
        "com.piece_framework.makegood.core", //$NON-NLS-1$
        "org.eclipse.php.core" //$NON-NLS-1$
    };

    @Override
    public String pluginId() {
        return Fragment.PLUGIN_ID;
    }

    @Override
    public Aspect[] aspects() {
        return ASPECTS;
    }

    @Override
    public String[] dependencies() {
        return DEPENDENCIES;
    }
}