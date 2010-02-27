package com.piece_framework.makegood.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$
    public static String MakeGoodLaunchShortcut_messageTitle;
    public static String MakeGoodLaunchShortcut_notFoundTestsMessage;
    public static String MakeGoodPropertyPage_testingFrameworkLabel;
    public static String MakeGoodPropertyPage_browseLabel;
    public static String MakeGoodPropertyPage_preloadScriptDialogMessage;
    public static String MakeGoodPropertyPage_preloadScriptDialogTitle;
    public static String MakeGoodPropertyPage_preloadScriptLabel;
    public static String MakeGoodPropertyPage_PHPUnit;
    public static String MakeGoodPropertyPage_SimpleTest;
    public static String TestResultView_assertionsLabel;
    public static String TestResultView_errorsLabel;
    public static String TestResultView_failuresLabel;
    public static String TestResultView_failureTraceLabel;
    public static String TestResultView_passesLabel;
    public static String TestResultView_testsLabel;
    public static String TestResultView_resultsLabel;
    public static String TestResultView_averageTest;
    public static String TestResultView_percent;
    public static String TestResultView_realTime;
    public static String TestResultView_second;
    public static String TestResultView_millisecond;
    public static String TestResultView_testTime;
    public static String TestResultViewSetter_messageTitle;
    public static String TestResultViewSetter_failedToParseXMLMessage;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
