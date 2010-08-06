/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

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
import org.eclipse.core.runtime.Assert;
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
        Assert.isNotNull(resource, "The given resource should be not null."); //$NON-NLS-1$

        project = resource.getProject();
        preferences = new ProjectScope(project).getNode(MakeGoodCorePlugin.PLUGIN_ID);
    }

    public MakeGoodProperty(String path) {
        this(ResourcesPlugin.getWorkspace().getRoot());
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

    public boolean usingPHPUnit() {
        return preferences.get(TESTING_FRAMEWORK_KEY, "").equals(TestingFramework.PHPUnit.name()); //$NON-NLS-1$
    }

    public boolean usingSimpleTest() {
        return preferences.get(TESTING_FRAMEWORK_KEY, "").equals(TestingFramework.SimpleTest.name()); //$NON-NLS-1$
    }

    public void setTestingFramework(TestingFramework testingFramework) {
        preferences.put(TESTING_FRAMEWORK_KEY, testingFramework.name());
    }

    public TestingFramework getTestingFramework() {
        String testingFramework = preferences.get(TESTING_FRAMEWORK_KEY, ""); //$NON-NLS-1$
        if (testingFramework.equals(TestingFramework.PHPUnit.name())) {
            return TestingFramework.PHPUnit;
        } else if (testingFramework.equals(TestingFramework.SimpleTest.name())) {
            return TestingFramework.SimpleTest;
        } else {
            return TestingFramework.PHPUnit;
        }
    }

    public List<IFolder> getTestFolders() {
        String[] testFolders = preferences.get(TEST_FOLDERS, "").split("\u0005"); //$NON-NLS-1$ //$NON-NLS-2$
        List<IFolder> testFoldersList = new ArrayList<IFolder>();
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        for (String testFolder: testFolders) {
            if (!testFolder.equals("")) testFoldersList.add(root.getFolder(new Path(testFolder))); //$NON-NLS-1$
        }
        return Collections.unmodifiableList(testFoldersList);
    }

    public void setTestFolders(List<IFolder> testFolders) {
        StringBuilder builder = new StringBuilder();
        for (IFolder testFolder: testFolders) {
            if (builder.length() > 0) builder.append("\u0005"); //$NON-NLS-1$
            builder.append(testFolder.getFullPath().toString());
        }
        preferences.put(TEST_FOLDERS, builder.toString());
    }

    public void flush() {
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
            MakeGoodCorePlugin.getDefault().getLog().log(
                new Status(
                    Status.ERROR,
                    MakeGoodCorePlugin.PLUGIN_ID,
                    e.getMessage(),
                    e
                )
            );
        }
    }
}
