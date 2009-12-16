package com.piece_framework.makegood.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

public class MakeGoodProperty {
    private static String PRELOAD_SCRIPT_KEY = "preload_script"; //$NON-NLS-1$
    private static String TESTING_FRAMEWORK_KEY = "testing_framework"; //$NON-NLS-1$
    private static String PHP_UNIT = "PHPUnit";
    private static String SIMPLE_TEST = "SimpleTest";
    private IEclipsePreferences preferences;
    private IProject project;

    public MakeGoodProperty(IResource resource) {
        project = resource.getProject();
        preferences = new ProjectScope(project).getNode(Activator.PLUGIN_ID);
    }

    public MakeGoodProperty(String path) {
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = workspaceRoot.findMember(path);
        if (resource != null) {
            project = resource.getProject();
            preferences = new ProjectScope(project).getNode(Activator.PLUGIN_ID);
        }
    }

    public String getPreloadScript() {
        return preferences.get(PRELOAD_SCRIPT_KEY, ""); //$NON-NLS-1$
    }

    public void setPreloadScript(String preloadScript) {
        preferences.put(PRELOAD_SCRIPT_KEY, preloadScript);
        flush();
    }

    public boolean exists() {
        return preferences.get(PRELOAD_SCRIPT_KEY, null) != null;
    }

    public IProject getProject() {
        return project;
    }

    public boolean usePHPUnit() {
        return preferences.get(TESTING_FRAMEWORK_KEY, "").equals(PHP_UNIT);
    }

    public boolean useSimpleTest() {
        return preferences.get(TESTING_FRAMEWORK_KEY, "").equals(SIMPLE_TEST);
    }

    private void flush() {
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            Activator.getDefault().getLog().log(new Status(Status.ERROR,
                                                           Activator.PLUGIN_ID,
                                                           e.getMessage(),
                                                           e
                                                           ));
        }
    }
}
