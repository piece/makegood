/**
 * Copyright (c) 2012 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.launch;

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.IType;

/**
 * @since 2.1.0
 */
public class ClassTestTarget {
    private IType type;

    public ClassTestTarget(IType type) {
        this.type = type;
    }

    public IResource getResource() {
        return type.getResource();
    }

    public String getClassName() {
        return type.getElementName();
    }
}
