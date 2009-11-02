package com.piece_farmework.makegood.aspect.include_path_search;

import org.eclipse.ui.IStartup;

public class Startup implements IStartup {
    @Override
    public void earlyStartup() {
        System.out.println("com.piece_framework.makegood.aspect.include_path_search");
    }
}
