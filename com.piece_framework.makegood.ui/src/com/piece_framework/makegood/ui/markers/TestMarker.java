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

package com.piece_framework.makegood.ui.markers;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import com.piece_framework.makegood.core.result.TestCaseResult;

/**
 * @since 1.3.0
 */
public class TestMarker extends Marker {
    private static final String MARKER_ID_TEST = "com.piece_framework.makegood.ui.markers.testMarker"; //$NON-NLS-1$
    private static final String MARKER_ID_TEST_FAILURE = "com.piece_framework.makegood.ui.markers.testFailureMarker"; //$NON-NLS-1$
    private static final String MARKER_ID_TEST_ERROR = "com.piece_framework.makegood.ui.markers.testErrorMarker"; //$NON-NLS-1$

    public IMarker create(TestCaseResult testCase) throws CoreException {
        String markerId;
        if (testCase.hasFailures()) {
            markerId = MARKER_ID_TEST_FAILURE;
        } else if (testCase.hasErrors()) {
            markerId = MARKER_ID_TEST_ERROR;
        } else {
            return null;
        }
        return create(markerId, testCase.getFile(), testCase.getLine(), testCase.getFailureMessage());
    }

    public void clear(TestCaseResult testCase) throws CoreException {
        clear(MARKER_ID_TEST, testCase.getFile());
    }
}
