/**
 * Copyright (c) 2013 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;

/**
 * @since 2.4.0
 */
public class PDTVersionSourceProvider extends AbstractSourceProvider {
    private static final String SOURCE_NAME_MAJOR = "com.piece_framework.makegood.aspect.pdtVersionMajor"; //$NON-NLS-1$
    private static final String SOURCE_NAME_MINOR = "com.piece_framework.makegood.aspect.pdtVersionMinor"; //$NON-NLS-1$
    private static final String SOURCE_NAME_MICRO = "com.piece_framework.makegood.aspect.pdtVersionMicro"; //$NON-NLS-1$
    private static final String SOURCE_NAME_QUALIFIER = "com.piece_framework.makegood.aspect.pdtVersionQualifier"; //$NON-NLS-1$

    @Override
    public void dispose() {
    }

    @Override
    public Map<String, Object> getCurrentState() {
        Map<String, Object> currentState = new HashMap<String, Object>();
        currentState.put(SOURCE_NAME_MAJOR, PDTVersion.getInstance().getVersion().getMajor());
        currentState.put(SOURCE_NAME_MINOR, PDTVersion.getInstance().getVersion().getMinor());
        currentState.put(SOURCE_NAME_MICRO, PDTVersion.getInstance().getVersion().getMicro());
        currentState.put(SOURCE_NAME_QUALIFIER, PDTVersion.getInstance().getVersion().getQualifier());

        return currentState;
    }

    @Override
    public String[] getProvidedSourceNames() {
        return new String[] {
            SOURCE_NAME_MAJOR,
            SOURCE_NAME_MINOR,
            SOURCE_NAME_MICRO,
            SOURCE_NAME_QUALIFIER,
        };
    }
}
