/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2011-2012, 2014 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.core.preference;

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

import com.piece_framework.makegood.core.Activator;
import com.piece_framework.makegood.core.DefaultConfiguration;
import com.piece_framework.makegood.core.TestingFramework;

public class MakeGoodProperties {
    private static String PRELOAD_SCRIPT_KEY = "preload_script"; //$NON-NLS-1$
    private static String TESTING_FRAMEWORK_KEY = "testing_framework"; //$NON-NLS-1$
    private static String TEST_FOLDERS = "test_folders"; //$NON-NLS-1$
    private static String PHPUNIT_CONFIG_FILE = "phpunit_config_file"; //$NON-NLS-1$
    private static String PHPUNIT_PHAR_FILE = "phpunit_phar_file"; //$NON-NLS-1$

    /**
     * @since 2.0.0
     */
    private static String TEST_FILE_PATTERN_KEY = "test_file_pattern"; //$NON-NLS-1$

    private IEclipsePreferences preferences;
    private IProject project;

    /**
     * @since 2.0.0
     */
    private DefaultConfiguration defaultConfiguration = new DefaultConfiguration();

    public MakeGoodProperties(IResource resource) {
        Assert.isNotNull(resource, "The given resource should not be null."); //$NON-NLS-1$
        project = resource.getProject();
        preferences = createPreferences(project);
    }

    public MakeGoodProperties(String path) {
        this(ResourcesPlugin.getWorkspace().getRoot());
    }

    /**
     * @since 1.6.0
     */
    public MakeGoodProperties(IProject project) {
        this.project = project;
        preferences = createPreferences(project);
    }

    public String getPreloadScript() {
        return preferences.get(PRELOAD_SCRIPT_KEY, defaultConfiguration.getPreloadScript());
    }

    public void setPreloadScript(String preloadScript) {
        preferences.put(PRELOAD_SCRIPT_KEY, preloadScript);
    }

    public boolean exists() {
        return preferences.get(PRELOAD_SCRIPT_KEY, null) != null;
    }

    public void setTestingFramework(TestingFramework testingFramework) {
        preferences.put(TESTING_FRAMEWORK_KEY, testingFramework.name());
    }

    public TestingFramework getTestingFramework() {
        String testingFramework = preferences.get(TESTING_FRAMEWORK_KEY, defaultConfiguration.getTestingFramework().name());
        if (testingFramework.equals(TestingFramework.PHPUnit.name())) {
            return TestingFramework.PHPUnit;
        } else {
            return defaultConfiguration.getTestingFramework();
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
            Activator.getDefault().getLog().log(new Status(
                Status.ERROR, Activator.PLUGIN_ID, e.getMessage(), e
            ));
        }
    }

    public String getPHPUnitConfigFile() {
        return preferences.get(PHPUNIT_CONFIG_FILE, defaultConfiguration.getPHPUnitConfigFile());
    }

    public void setPHPUnitConfigFile(String phpunitConfigFile) {
        preferences.put(PHPUNIT_CONFIG_FILE, phpunitConfigFile);
    }
    
    public String getPHPUnitPharFile() {
    	return preferences.get(PHPUNIT_PHAR_FILE, defaultConfiguration.getPHPUnitConfigFile());
    }
    
    public void setPHPUnitPharFile(String phpunitPharFile) {
    	preferences.put(PHPUNIT_PHAR_FILE, phpunitPharFile);
    }

    /**
     * @since 2.0.0
     */
    public void setTestFilePattern(String testFilePattern) {
        preferences.put(TEST_FILE_PATTERN_KEY, testFilePattern);
    }

    /**
     * @since 2.0.0
     */
    public String getTestFilePattern() {
        return preferences.get(TEST_FILE_PATTERN_KEY, defaultConfiguration.getTestFilePattern());
    }

    /**
     * @since 1.6.0
     */
    private IEclipsePreferences createPreferences(IProject project) {
        return new ProjectScope(project).getNode(Activator.PLUGIN_ID);
    }
}
