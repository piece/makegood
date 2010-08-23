/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "messages"; //$NON-NLS-1$
    public static String MakeGoodLaunchShortcut_messageTitle;
    public static String MakeGoodLaunchShortcut_notFoundTestsMessage;
    public static String MakeGoodPropertyPage_addFolderLabel;
    public static String MakeGoodPropertyPage_testFolderDialogMessage;
    public static String MakeGoodPropertyPage_testFolderDialogTitle;
    public static String MakeGoodPropertyPage_testFoldersLabel;
    public static String MakeGoodPropertyPage_testingFrameworkLabel;
    public static String MakeGoodPropertyPage_browseLabel;
    public static String MakeGoodPropertyPage_preloadScriptDialogMessage;
    public static String MakeGoodPropertyPage_preloadScriptDialogTitle;
    public static String MakeGoodPropertyPage_preloadScriptLabel;
    public static String MakeGoodPropertyPage_PHPUnit;
    public static String MakeGoodPropertyPage_removeLabel;
    public static String MakeGoodPropertyPage_SimpleTest;
    public static String MakeGoodPreferencePage_runAllTestsWhenFileIsSaved;
    public static String TestResultView_errorsLabel;
    public static String TestResultView_failuresLabel;
    public static String TestResultView_failureTraceLabel;
    public static String TestResultView_passesLabel;
    public static String TestResultView_testsLabel;
    public static String TestResultView_averageTest;
    public static String TestResultView_realTime;
    public static String TestResultView_testTime;
    public static String TestRunner_TestSessionAlreadyExists_Title;
    public static String TestRunner_TestSessionAlreadyExists_Message;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
