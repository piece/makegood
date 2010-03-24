package com.piece_framework.makegood.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

public class MakeGoodProperty {
    private static String PRELOAD_SCRIPT_KEY = "preload_script"; //$NON-NLS-1$
    private static String TESTING_FRAMEWORK_KEY = "testing_framework"; //$NON-NLS-1$
    private static String TEST_FOLDERS = "test_folders"; //$NON-NLS-1$
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
    }

    public boolean exists() {
        return preferences.get(PRELOAD_SCRIPT_KEY, null) != null;
    }

    public IProject getProject() {
        return project;
    }

    public boolean usePHPUnit() {
        return preferences.get(TESTING_FRAMEWORK_KEY, "").equals(TestingFramework.PHPUnit.name());
    }

    public boolean useSimpleTest() {
        return preferences.get(TESTING_FRAMEWORK_KEY, "").equals(TestingFramework.SimpleTest.name());
    }

    public void setTestingFramework(TestingFramework testingFramework) {
        preferences.put(TESTING_FRAMEWORK_KEY, testingFramework.name());
    }

    public TestingFramework getTestingFramework() {
        String testingFramework = preferences.get(TESTING_FRAMEWORK_KEY, "");
        if (testingFramework.equals(TestingFramework.PHPUnit.name())) {
            return TestingFramework.PHPUnit;
        } else if (testingFramework.equals(TestingFramework.SimpleTest.name())) {
            return TestingFramework.SimpleTest;
        } else {
            return TestingFramework.PHPUnit;
        }
    }

    public List<IFolder> getTestFolders() {
        String[] testFolders = preferences.get(TEST_FOLDERS, "").split("\u0005");
        List<IFolder> testFoldersList = new ArrayList<IFolder>();
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        for (String testFolder: testFolders) {
            testFoldersList.add(root.getFolder(new Path(testFolder)));
        }
        return Collections.unmodifiableList(testFoldersList);
    }

    public void flush() {
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            Activator.getDefault().getLog().log(
                new Status(
                    Status.ERROR,
                    Activator.PLUGIN_ID,
                    e.getMessage(),
                    e
                )
            );
        }
    }
}
