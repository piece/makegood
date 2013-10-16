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

package com.piece_framework.makegood.ui;

import com.piece_framework.makegood.core.TestResultsLayout;

/**
 * @since 2.5.0
 */
public interface TestResultsLayoutChangeListener {
    public void layoutChanged(TestResultsLayout testResultsLayout);
}
