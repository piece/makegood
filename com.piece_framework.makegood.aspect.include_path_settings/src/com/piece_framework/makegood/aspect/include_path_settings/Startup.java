package com.piece_framework.makegood.aspect.include_path_settings;
import org.eclipse.ui.IStartup;


public class Startup implements IStartup {
    @Override
    public void earlyStartup() {
        System.out.println("include_path_settings startup");
    }
}
