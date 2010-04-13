/**
 * Copyright (c) 2009 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.core.launch;

import org.eclipse.debug.core.ILaunch;

public interface IMakeGoodEventListener {
    public void create(ILaunch launch);

    public void terminate(ILaunch launch);
}
