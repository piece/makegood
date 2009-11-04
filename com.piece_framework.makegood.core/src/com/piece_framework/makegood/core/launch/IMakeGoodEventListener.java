package com.piece_framework.makegood.core.launch;

import org.eclipse.debug.core.ILaunch;

public interface IMakeGoodEventListener {
    public void create(ILaunch launch);

    public void terminate(ILaunch launch);
}
