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

package com.piece_framework.makegood.aspect.com.piece_framework.makegood.core.aspect;

/**
 * @since 1.2.0
 */
public class GalileoPHPFlags {
    public static boolean isClass(int flags) {
        return org.eclipse.php.internal.core.compiler.PHPFlags.isClass(flags);
    }

    public static boolean isNamespace(int flags) {
        return org.eclipse.php.internal.core.compiler.PHPFlags.isNamespace(flags);
    }
}
