package com.piece_framework.makegood.aspect.include_path_protection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.php.internal.debug.core.PHPDebugPlugin;

import com.piece_framework.makegood.include_path.ConfigurationIncludePath;

public class PHPConfiguration {
    private static final String INCLUDE_PATH_KEY = "include_path";

    public String[] transformIncludePaths(File configurationFile,
                                          List<String> includePaths,
                                          IProject project
                                          ) {
        ConfigurationIncludePath configuration = new ConfigurationIncludePath(project);
        String configuraionPath = configuration.getDummyResource().getFullPath().toOSString();
        List<String> newIncludePaths = new LinkedList<String>();
        for (String includePath: includePaths) {
            if (includePath.equals(configuraionPath)) {
                List<String> configurationIncludePaths = getIncludePathsFromConfiguration(configurationFile);
                for (String configurationIncludePath: configurationIncludePaths) {
                    newIncludePaths.add(configurationIncludePath);
                }
            } else {
                newIncludePaths.add(includePath);
            }
        }
        return newIncludePaths.toArray(new String[newIncludePaths.size()]);
    }

    private List<String> getIncludePathsFromConfiguration(File configurationFile) {
        List<String> configurationIncludePaths = new LinkedList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(configurationFile));
            String line;
            Pattern keyValuePattern = Pattern.compile("([\\w]+)\\s*=\\s*(.*)");
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                Matcher matcher = keyValuePattern.matcher(line);
                if (!matcher.matches()) {
                    continue;
                }

                String key = matcher.group(1);
                if (!key.equals(INCLUDE_PATH_KEY)) {
                    continue;
                }

                String value = matcher.group(2).replaceAll("\"", "");
                String[] includePaths = value.split(File.pathSeparator);
                for (String includePath: includePaths) {
                    if (includePath.equals(".")) {
                        continue;
                    }

                    configurationIncludePaths.add(includePath);
                }
                break;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            PHPDebugPlugin.log(e);
        } catch (IOException e) {
            PHPDebugPlugin.log(e);
        }
        return configurationIncludePaths;
    }
}
