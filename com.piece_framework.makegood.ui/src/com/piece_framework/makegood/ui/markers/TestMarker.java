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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

import com.piece_framework.makegood.core.result.TestCaseResult;

/**
 * @since 1.3.0
 */
public class TestMarker {
    private static final String MARKER_TEST = "com.piece_framework.makegood.ui.markers.testMarker"; //$NON-NLS-1$
    private static final String MARKER_TEST_FAILURE = "com.piece_framework.makegood.ui.markers.testFailureMarker"; //$NON-NLS-1$
    private static final String MARKER_TEST_ERROR = "com.piece_framework.makegood.ui.markers.testErrorMarker"; //$NON-NLS-1$

    public static void createMarker(TestCaseResult testCase) throws CoreException {
        IFile file = getFile(testCase);
        if (file == null) return;

        IMarker marker;
        if (testCase.hasFailures()) {
            marker = file.createMarker(MARKER_TEST_FAILURE);
        } else if(testCase.hasErrors()) {
            marker = file.createMarker(MARKER_TEST_ERROR);
        } else {
            return;
        }

        marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
        marker.setAttribute(IMarker.LINE_NUMBER, testCase.getLine());
        marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
        marker.setAttribute(IMarker.MESSAGE, testCase.getFailureMessage());
    }

    public static void clearMarkers(TestCaseResult testCase) throws CoreException {
        IFile file = getFile(testCase);
        if (file == null) return;

        file.deleteMarkers(MARKER_TEST, true, 1);
    }

    private static IFile getFile(TestCaseResult testCase) {
        String fileName = testCase.getFile();
        if (fileName == null) return null;

        return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(fileName));
    }
}
