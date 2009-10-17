package org.eclipse.php.internal.debug.core.phpIni;

import java.io.File;

import org.eclipse.core.resources.IProject;

public class PHPINIUtilPatch {
    public static String[] getIncludePathWithPHPIni(File phpIniFile,
                                                    String[] originalIncludePaths,
                                                    IProject project
                                                    ) {
        System.out.println("Hello getIncludePathWithPHPIni");
        return originalIncludePaths;
    }
}
