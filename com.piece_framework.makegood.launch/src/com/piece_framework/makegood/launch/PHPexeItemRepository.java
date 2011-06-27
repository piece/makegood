/**
 * Copyright (c) 2010-2011 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.launch;

import org.eclipse.core.resources.IProject;
import org.eclipse.php.internal.debug.core.PHPDebugPlugin;
import org.eclipse.php.internal.debug.core.preferences.PHPexeItem;

/**
 * @since 1.2.0
 */
public class PHPexeItemRepository {
    public PHPexeItem findByProject(IProject project) {
        PHPexeItem phpexeItem = PHPDebugPlugin.getPHPexeItem(project);
        if (phpexeItem == null) {
            return PHPDebugPlugin.getWorkspaceDefaultExe();
        }
        return phpexeItem;
    }
}
