/**
 * Copyright (c) 2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect.org.eclipse.php.core;

import org.eclipse.php.internal.core.compiler.ast.parser.PHPSourceParserFactory;

/**
 * An implementation of the PHPSourceParserFactory class to avoid raising internal
 * error during "Selection Job title" in an PHP Editor view.
 * This error is raised when the cursor is the line which other PHP files are read.
 * (e.g. require 'foo.php';)
 */
public class MakeGoodSourceParserFactory extends PHPSourceParserFactory {
    public MakeGoodSourceParserFactory() {
        if (MonitorTarget.endWeaving) return;
        new FragmentWeavingProcess().process();
    }
}
