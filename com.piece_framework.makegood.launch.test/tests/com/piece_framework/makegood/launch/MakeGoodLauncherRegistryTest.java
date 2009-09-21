package com.piece_framework.makegood.launch;

import java.io.File;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MakeGoodLauncherRegistryTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void returnLauncherOfPHPUnit() {
        MakeGoodLauncherRegistry registry = new MakeGoodLauncherRegistry();
        MakeGoodLauncher launcher = registry.getLauncher(TestingFramework.PHPUnit);

        assertEquals(MakeGoodLauncherRegistry.getRegistry().getAbsolutePath() + "/testrunner", launcher.getScript());
    }
}
