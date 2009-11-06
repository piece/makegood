package com.piece_framework.makegood.core.launch;

import java.util.List;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.php.internal.debug.core.model.IPHPDebugTarget;

public class MakeGoodDebugEventSetListener implements IDebugEventSetListener {
    List<IMakeGoodEventListener> eventListeners;

    public MakeGoodDebugEventSetListener(List<IMakeGoodEventListener> eventListeners) {
        this.eventListeners = eventListeners;
    }

    @Override
    public void handleDebugEvents(DebugEvent[] events) {
        for (DebugEvent event: events) {
            if (!(event.getSource() instanceof IPHPDebugTarget)) {
                continue;
            }

            ILaunch launch = ((IPHPDebugTarget) event.getSource()).getLaunch();
            ILaunchConfiguration configuration = launch.getLaunchConfiguration();
            if (!configuration.getName().startsWith("MakeGood")) {
                continue;
            }

            if (event.getKind() == DebugEvent.CREATE) {
                for (IMakeGoodEventListener eventListener: eventListeners) {
                    eventListener.create(launch);
                }
            }
            if (event.getKind() == DebugEvent.TERMINATE) {
                for (IMakeGoodEventListener eventListener: eventListeners) {
                    eventListener.terminate(launch);
                }
            }
        }
    }
}
