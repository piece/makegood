package com.piece_framework.makegood.include_path;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.jface.resource.ImageDescriptor;

public class ConfigurationIncludePath {
    public static String text = "[The System Include Path]";
    public static ImageDescriptor icon = null;

    private IResource dummy;

    public ConfigurationIncludePath(IProject project) {
        dummy = project.getFile(".project");
    }

    public boolean use() {
        IEclipsePreferences preferences = new ProjectScope(dummy.getProject()).getNode("org.eclipse.php.core");
        String includePathBlock = preferences.get("include_path", "");
        return includePathBlock.indexOf(IBuildpathEntry.BPE_SOURCE + ";" + dummy.getFullPath().toString()) != -1;
    }

    public IResource getDummyResource() {
        return dummy;
    }

    public boolean equalsDummyResource(IResource target) {
        return dummy.equals(target);
    }
}
