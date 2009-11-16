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

    private static File registry = new File(System.getProperty("java.io.tmpdir"), //$NON-NLS-1$
                                               Activator.PLUGIN_ID
                                               );

    static {
        launcherScripts.put(TestingFramework.PHPUnit,
                            registry.getAbsolutePath() + String.valueOf(File.separatorChar) + "phpunitrunner" //$NON-NLS-1$
                            );
        launcherScripts.put(TestingFramework.SimpleTest,
                            registry.getAbsolutePath() + String.valueOf(File.separatorChar) + "simpletestrunner" //$NON-NLS-1$
                            );
        launcherScripts.put(TestingFramework.PHPSpec,
                            registry.getAbsolutePath() + String.valueOf(File.separatorChar) + "phpspecrunner" //$NON-NLS-1$
                            );
    }

    public MakeGoodLauncher getLauncher(TestingFramework testingFramework) throws FileNotFoundException {
        File launcherScript = new File(launcherScripts.get(testingFramework));
        if (!launcherScript.exists()) {
            throw new FileNotFoundException(testingFramework.toString() + Messages.MakeGoodLauncherRegistry_notFoundMessage);
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
            File sourceLauncherScript = new File(launcherScriptsDirectory.getAbsoluteFile() +
                                                  String.valueOf(File.separatorChar) +
                                                  launcherScript.getName()
                                                  );

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

    public static void deleteRegistry() {
        for (File file: registry.listFiles()) {
            file.delete();
        }
        registry.delete();
    }
}
