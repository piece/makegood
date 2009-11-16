package com.piece_framework.makegood.aspect.include_path_settings;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$
    public static String PHPIncludePathSourcePageForConfiguration_label;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
