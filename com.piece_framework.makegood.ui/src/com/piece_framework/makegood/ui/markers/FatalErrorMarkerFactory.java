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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * @since 1.3.0
 */
public class FatalErrorMarkerFactory extends MarkerFactory {
    private static final String MARKER_ID = "com.piece_framework.makegood.ui.markers.fatalErrorMarker"; //$NON-NLS-1$
    private static final Pattern FATAL_ERROR_MESSAGE_PATTERN =
        Pattern.compile("^((?:Parse|Fatal) error: .+) in (.+?)(?:\\((\\d+)\\) : eval\\(\\)'d code(?:\\(\\d+\\) : eval\\(\\)'d code)*)? on line (\\d+)$", Pattern.MULTILINE); //$NON-NLS-1$
    private String file;
    private int line;

    public IMarker create(String fatalErrorMessage) throws CoreException, UnknownFatalErrorMessageException {
        Matcher matcher = FATAL_ERROR_MESSAGE_PATTERN.matcher(fatalErrorMessage);
        while (matcher.find()) {
            file = matcher.group(2);
            if (matcher.group(3) == null) {
                line = Integer.valueOf(matcher.group(4));
            } else {
                line = Integer.valueOf(matcher.group(3));
            }
            return create(MARKER_ID, file, line, matcher.group(1));
        }

        throw new UnknownFatalErrorMessageException(fatalErrorMessage);
    }

    public void clear(IProject project) throws CoreException {
        clear(MARKER_ID, project);
    }

    public String getFile() {
        return file;
    }

    public int getLine() {
        return line;
    }
}
