package com.piece_framework.makegood.launch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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

        assertEquals(MakeGoodLauncherRegistry.getRegistry().getAbsolutePath() + String.valueOf(File.separatorChar) + "testrunner",
                     launcher.getScript()
                     );
    }
}
