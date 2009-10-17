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

public class PHPINIUtilPatch {
    public static String[] getIncludePathWithPHPIni(File phpIniFile,
                                                    String[] originalIncludePaths,
                                                    IProject project
                                                    ) {
        ProjectScope scope = new ProjectScope(project);
        String includePathBlock = scope.getNode("org.eclipse.php.core").get("include_path", "");
        if (includePathBlock == null || includePathBlock.indexOf("0;INCLUDE_PATH_ON_PHP_INI") == -1) {
            return originalIncludePaths;
        }

        List<Integer> preferenceKindList = new LinkedList<Integer>();
        List<String> preferencePathList = new LinkedList<String>();
        int startIndex = 0;
        int endIndex = 0;
        while (endIndex != -1) {
            endIndex = includePathBlock.indexOf((char) 5, startIndex);
            String block = null;
            if (endIndex != -1) {
                block = includePathBlock.substring(startIndex, endIndex);
            } else {
                block = includePathBlock.substring(startIndex);
            }

            String[] attributes = block.split(";");
            if (attributes.length != 2) {
                break;
            }
            preferenceKindList.add(new Integer(attributes[0]));
            preferencePathList.add(attributes[1]);

            startIndex = endIndex + 1;
        }

        List<String> newList = new LinkedList<String>();
        int index = 0;
        int insertIndex = -1;
        for (String includePath: originalIncludePaths) {
            Integer kind = preferenceKindList.get(index);
            String path = null;
            if (kind == 0) {
                IResource resource = project.getWorkspace().getRoot().findMember(preferencePathList.get(index));
                path = resource.getLocation().toOSString();
            } else {
                path = preferencePathList.get(index);
            }
            ++index;

            if (path.equals(includePath)) {
                newList.add(includePath);
            } else if (kind.intValue() == 0 && path.equals("INCLUDE_PATH_ON_PHP_INI") && insertIndex != -1){
                insertIndex = index;
            }
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(phpIniFile));
            String line;
            Pattern NAME_VAL_PATTERN = Pattern.compile("([\\w]+)\\s*=\\s*(.*)");
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                Matcher matcher = NAME_VAL_PATTERN.matcher(line);
                if (!matcher.matches()) {
                    continue;
                }

                String name = matcher.group(1);
                if (!name.equals("include_path")) {
                    continue;
                }

                String value = matcher.group(2).replaceAll("\"", "");
                String[] includePaths = value.split(File.pathSeparator);
                for (String includePath: includePaths) {
                    if (includePath.equals(".")) {
                        continue;
                    }

                    if (insertIndex != -1) {
                        newList.add(insertIndex, includePath);
                        ++insertIndex;
                    } else {
                        newList.add(includePath);
                    }
                }
                break;
            }
            reader.close();
        } catch (FileNotFoundException e) {
            PHPDebugPlugin.log(e);
        } catch (IOException e) {
            PHPDebugPlugin.log(e);
        }

        return newList.toArray(new String[newList.size()]);
    }
}
