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

package com.piece_framework.makegood.ui.ide;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.piece_framework.makegood.ui.Activator;

public class FileFind {
    public static IFile[] findFiles(String file) {
        try {
            return ResourcesPlugin.getWorkspace()
                                  .getRoot()
                                  .findFilesForLocationURI(
                                      new URI("file:///" + file.replaceAll("\\\\", "/")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                   );
        } catch (URISyntaxException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            return null;
        }
    }

    public static IFileStore findFileStore(String file) {
        try {
            return EFS.getLocalFileSystem()
                      .getStore(new URI("file:///" + file.replaceAll("\\\\", "/"))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        } catch (URISyntaxException e) {
            Activator.getDefault().getLog().log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));
            return null;
        }
    }
}
