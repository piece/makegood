/**
 * Copyright (c) 2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect.com.piece_framework.makegood.launch.aspect;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.php.internal.debug.core.PHPDebugPlugin;
import org.eclipse.php.internal.debug.core.preferences.PHPDebugCorePreferenceNames;
import org.eclipse.php.internal.debug.core.preferences.PHPProjectPreferences;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;
import org.eclipse.php.internal.debug.core.preferences.PHPexes;

/**
 * @since 1.2.0
 */
public class GalileoPHPexeItemFactory {

	/**
     * @see org.eclipse.php.internal.debug.ui.launching.PHPExeLaunchShortcut#getDefaultPHPExe(IProject project)
     */
    public static PHPexeItem create(IProject project) {
        // Take the default workspace item for the debugger's id.
        String phpDebuggerId = PHPDebugPlugin.getCurrentDebuggerId();
        PHPexeItem defaultItem = PHPexes.getInstance().getDefaultItem(phpDebuggerId);
        if (defaultItem == null) {
            // We have no executable defined for this debugger. 
            return null;
        }
        String phpExe = defaultItem.getName();
        if (project != null) {
            // In case that the project is not null, check that we have project-specific settings for it.
            // Otherwise, map it to the workspace default server.
            IScopeContext[] preferenceScopes = createPreferenceScopes(project);
            if (preferenceScopes[0] instanceof ProjectScope) {
                IEclipsePreferences node = preferenceScopes[0].getNode(PHPProjectPreferences.getPreferenceNodeQualifier());
                if (node != null) {
                    // Replace the workspace defaults with the project-specific settings.
                    phpDebuggerId = node.get(PHPDebugCorePreferenceNames.PHP_DEBUGGER_ID, phpDebuggerId);
                    phpExe = node.get(PHPDebugCorePreferenceNames.DEFAULT_PHP, phpExe);
                }
            }
        }
        return PHPexes.getInstance().getItem(phpDebuggerId, phpExe);
    }

    /**
     * @see org.eclipse.php.internal.debug.ui.launching.PHPExeLaunchShortcut#createPreferenceScopes(IProject project)
     */
    private static IScopeContext[] createPreferenceScopes(IProject project) {
        if (project != null) {
            return new IScopeContext[] { new ProjectScope(project), new InstanceScope(), new DefaultScope() };
        }
        return new IScopeContext[] { new InstanceScope(), new DefaultScope() };
    }
}
