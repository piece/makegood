package org.eclipse.php.debug.core.patch.makegood;

import org.eclipse.ui.IStartup;

public class Startup implements IStartup {
    @Override
    public void earlyStartup() {
        // TODO Auto-generated method stub
        System.out.println("Hello Patch!");
    }
}
