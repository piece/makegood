package com.piece_framework.makegood.launch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MakeGoodLauncherRegistry {
    private static Map<TestingFramework, String> launcherScripts = new HashMap<TestingFramework, String>();

    private static File registry = new File(System.getProperty("java.io.tmpdir"),
                                               Activator.PLUGIN_ID
                                               );

    static {
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

    public MakeGoodLauncher getLauncher(TestingFramework testingFramework) throws FileNotFoundException {
        File launcherScript = new File(launcherScripts.get(testingFramework));
        if (!launcherScript.exists()) {
            throw new FileNotFoundException(testingFramework.toString() + " launcher script is not found.");
        }

        return new MakeGoodLauncher(testingFramework,
                                     launcherScript.getAbsolutePath()
                                     );
    }

    public static File getRegistry() {
        return registry;
    }

    public static void createRegistry(File launcherScriptsDirectory) throws IOException {
        if (registry.exists()) {
            registry.delete();
        }

        registry.mkdirs();

        for (TestingFramework testingFramework: TestingFramework.values()) {
            File launcherScript = new File(launcherScripts.get(testingFramework));
            File sourceLauncherScript = new File(launcherScriptsDirectory.getAbsoluteFile() + "/" + launcherScript.getName());

            if (!sourceLauncherScript.exists()) {
                continue;
            }

            launcherScript.createNewFile();

            FileOutputStream output = new FileOutputStream(launcherScript);
            FileInputStream input = new FileInputStream(sourceLauncherScript);

            int data = 0;
            while ((data = input.read()) > 0) {
                output.write(data);
            }

            output.close();
            input.close();
        }
    }
}
