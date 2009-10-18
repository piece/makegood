package org.eclipse.php.internal.debug.core.phpIni;

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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.php.internal.debug.core.PHPDebugPlugin;

public class PHPConfiguration {
    private static final String PHPCORE_PLUGIN_ID = "org.eclipse.php.core";
    private static final String INCLUDE_PATH_KEY = "include_path";
    private static final String FROM_CONFIGURATION_FILE = "INCLUDE_PATH_FROM_LOADED_CONFIGURATION_FILE";
    private static final int SOURCE_KIND = 0;
    private static final char BLOCK_SEPARATOR = (char) 5;
    private static final String INCLUDE_PATH_SEPARATOR = ";";

    private class ProjectIncludePath {
        int kind;
        String path;

        private ProjectIncludePath(int kind,
                                   String path,
                                   IProject project
                                   ) {
            this.kind = kind;

            boolean isProjectResource = this.kind == SOURCE_KIND
                                        && !path.equals(FROM_CONFIGURATION_FILE);
            if (isProjectResource) {
                IResource resource = project.getWorkspace().getRoot().findMember(path);
                this.path = resource.getLocation().toOSString();
            } else {
                this.path = path;
            }
        }
    }

    public String[] getIncludePathWithConfiguration(File configurationFile,
                                                    List<String> originalIncludePaths,
                                                    IProject project
                                                    ) {
        String includePathBlock = new ProjectScope(project).getNode(PHPCORE_PLUGIN_ID).get(INCLUDE_PATH_KEY, "");
        boolean unnecessaryFromConfiguration = includePathBlock.indexOf(SOURCE_KIND + INCLUDE_PATH_SEPARATOR + FROM_CONFIGURATION_FILE) == -1;
        if (unnecessaryFromConfiguration) {
            return originalIncludePaths.toArray(new String[originalIncludePaths.size()]);
        }

        List<ProjectIncludePath> projectIncludePaths = parseIncludePathBlock(includePathBlock,
                                                                             project
                                                                             );

        List<String> newIncludePaths = new LinkedList<String>(originalIncludePaths);

        int insertIndex = calcInsertIndex(projectIncludePaths,
                                          newIncludePaths
                                          );

        List<String> configurationIncludePaths = getIncludePathsFromConfiguration(configurationFile);

        insertConfigurationIncludePaths(configurationIncludePaths,
                                           newIncludePaths,
                                           insertIndex
                                           );

        return newIncludePaths.toArray(new String[newIncludePaths.size()]);
    }

    private List<ProjectIncludePath> parseIncludePathBlock(String includePathBlock,
                                                           IProject project
                                                           ) {
        List<ProjectIncludePath> projectIncludePaths = new LinkedList<ProjectIncludePath>();
        int startIndex = 0;
        int endIndex = 0;
        while (endIndex != -1) {
            endIndex = includePathBlock.indexOf(BLOCK_SEPARATOR, startIndex);
            String block = null;
            if (endIndex != -1) {
                block = includePathBlock.substring(startIndex, endIndex);
            } else {
                block = includePathBlock.substring(startIndex);
            }

            String[] attributes = block.split(INCLUDE_PATH_SEPARATOR);
            if (attributes.length != 2) {
                break;
            }

            ProjectIncludePath projectIncludePath = new ProjectIncludePath(Integer.parseInt(attributes[0]),
                                                                           attributes[1],
                                                                           project
                                                                           );
            projectIncludePaths.add(projectIncludePath);

            startIndex = endIndex + 1;
        }
        return projectIncludePaths;
    }

    private int calcInsertIndex(List<ProjectIncludePath> projectIncludePaths,
                                List<String> newIncludePaths
                                ) {
        int index = 0;
        int insertIndex = -1;
        for (String includePath: newIncludePaths) {
            ProjectIncludePath projectIncludePath = projectIncludePaths.get(index);
            ++index;

            if (!projectIncludePath.path.equals(includePath)
                && projectIncludePath.kind == SOURCE_KIND
                && projectIncludePath.path.equals(FROM_CONFIGURATION_FILE)
                && insertIndex != -1
                ){
                insertIndex = index;
            }
        }
        return insertIndex;
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

    private void insertConfigurationIncludePaths(List<String> configurationIncludePaths,
                                                 List<String> newIncludePaths,
                                                 int insertIndex
                                                 ) {
        for (String includePath: configurationIncludePaths) {
            if (insertIndex != -1) {
                newIncludePaths.add(insertIndex, includePath);
                ++insertIndex;
            } else {
                newIncludePaths.add(includePath);
            }
        }
    }
}
