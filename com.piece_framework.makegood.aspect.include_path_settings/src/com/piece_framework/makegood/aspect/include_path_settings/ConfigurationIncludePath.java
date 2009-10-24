package com.piece_framework.makegood.aspect.include_path_settings;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.php.internal.ui.util.PHPPluginImages;

public class ConfigurationIncludePath {
    public static String text = "[Use Configuration]";
    public static ImageDescriptor icon = PHPPluginImages.DESC_OBJS_INCLUDE;

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
