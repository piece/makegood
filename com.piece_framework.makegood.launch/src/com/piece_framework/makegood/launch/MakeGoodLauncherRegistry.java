package com.piece_framework.makegood.launch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MakeGoodLauncherRegistry {
    private static Map<TestingFramework, String> launcherScripts = new HashMap<TestingFramework, String>();

    private static File registry = new File(System.getProperty("java.io.tmpdir"),
                                               Activator.PLUGIN_ID
                                               );

    {
        launcherScripts.put(TestingFramework.PHPUnit,
                            registry.getAbsolutePath() + "/testrunner"
                            );
        launcherScripts.put(TestingFramework.SimpleTest,
                            registry.getAbsolutePath() + "/testrunner-st"
                            );
        launcherScripts.put(TestingFramework.PHPSpec,
                            registry.getAbsolutePath() + "/specrunner"
                            );
    }

    public MakeGoodLauncher getLauncher(TestingFramework testingFramework) {
        return new MakeGoodLauncher(testingFramework,
                             launcherScripts.get(testingFramework)
                             );
    }

    public static File getRegistry() {
        return registry;
    }
}
