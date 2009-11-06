package com.piece_framework.makegood.core.launch;

import java.util.List;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;

public class MakeGoodDebugEventSetListener implements IDebugEventSetListener {
    List<IMakeGoodEventListener> eventListeners;

    public MakeGoodDebugEventSetListener(List<IMakeGoodEventListener> eventListeners) {
        this.eventListeners = eventListeners;
    }

    @Override
    public void handleDebugEvents(DebugEvent[] events) {
        // TODO Auto-generated method stub

    }
}
