package com.piece_framework.makegood.ui.views;

import org.eclipse.debug.core.ILaunch;

import com.piece_framework.makegood.core.launch.IMakeGoodEventListener;

public class TestResultViewSetter implements IMakeGoodEventListener {
    @Override
    public void create(ILaunch launch) {
        System.out.println("create:" + launch);
    }

    @Override
    public void terminate(ILaunch launch) {
        System.out.println("terminate:" + launch);
    }
}
