/**
 * Copyright (c) 2009 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.launch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.piece_framework.makegood.core.TestingFramework;

public class MakeGoodLauncherRegistryTest {

    @Before
    public void setUp() throws Exception {
        File launcherScriptsDirectory = new File(System.getProperty("user.dir") +
                                                 String.valueOf(File.separatorChar) +
                                                 "launchers"
                                                 );
        try {
            MakeGoodLauncherRegistry.createRegistry(launcherScriptsDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @After
    public void tearDown() {
        MakeGoodLauncherRegistry.deleteRegistry();
    }

    @Test
    public void returnLauncherOfPHPUnit() {
        MakeGoodLauncherRegistry registry = new MakeGoodLauncherRegistry();
        MakeGoodLauncher launcher = null;
        try {
            launcher = registry.getLauncher(TestingFramework.PHPUnit);
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }

        assertEquals(MakeGoodLauncherRegistry.getRegistry().getAbsolutePath() + String.valueOf(File.separatorChar) + "phpunitrunner",
                     launcher.getScript()
                     );
    }

    @Test
    public void returnLauncherOfSimpleTest() {
        MakeGoodLauncherRegistry registry = new MakeGoodLauncherRegistry();
        MakeGoodLauncher launcher = null;
        try {
            launcher = registry.getLauncher(TestingFramework.SimpleTest);
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }

        assertEquals(MakeGoodLauncherRegistry.getRegistry().getAbsolutePath() + String.valueOf(File.separatorChar) + "simpletestrunner",
                     launcher.getScript()
                     );
    }
}
