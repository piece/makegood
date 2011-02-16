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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;

/**
 * @since 1.3.0
 */
public abstract class Marker {
    public IMarker create(String markerId, String fileName, int line, String message) throws CoreException {
        IFile file = getFile(fileName);
        if (file == null) return null;

        IMarker marker = file.createMarker(markerId);
        marker.setAttribute(IMarker.LINE_NUMBER, line);
        marker.setAttribute(IMarker.MESSAGE, message);
        marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
        marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
        return marker;
    }

    public void clear(String markerId, IProject project) throws CoreException {
        if (project == null) return;
        if (!project.exists()) return;
        project.deleteMarkers(markerId, true, IResource.DEPTH_INFINITE);
    }

    IFile getFile(String fileName) {
        return ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(new Path(fileName));
    }
}
