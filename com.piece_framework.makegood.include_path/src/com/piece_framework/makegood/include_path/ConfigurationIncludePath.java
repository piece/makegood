/**
 * Copyright (c) 2009 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.include_path;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.jface.resource.ImageDescriptor;

public class ConfigurationIncludePath {
    public static String text = Messages.ConfigurationIncludePath_text;
    public static ImageDescriptor icon = null;

    private IResource dummy;

    public ConfigurationIncludePath(IProject project) {
        dummy = project.getFile(".project"); //$NON-NLS-1$
    }

    public boolean use() {
        IEclipsePreferences preferences = new ProjectScope(dummy.getProject()).getNode("org.eclipse.php.core"); //$NON-NLS-1$
        String includePathBlock = preferences.get("include_path", ""); //$NON-NLS-1$ //$NON-NLS-2$
        return includePathBlock.indexOf(IBuildpathEntry.BPE_SOURCE + ";" + dummy.getFullPath().toString()) != -1; //$NON-NLS-1$
    }

    public IResource getDummyResource() {
        return dummy;
    }

    public boolean equalsDummyResource(IResource target) {
        return dummy.equals(target);
    }

    /**
     * @since 1.6.0
     */
    public static boolean equalsDummyResource(IProject project, Object target) {
        if (!(target instanceof IResource)) return false;
        return new ConfigurationIncludePath(project).equalsDummyResource((IResource) target);
    }
}
