/**
 * Copyright (c) 2009-2010 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *               2010-2012 KUBO Atsuhiro <kubo@iteman.jp>,
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
    public static String MakeGoodPropertyPage_testFolderAddLabel;
    public static String MakeGoodPropertyPage_testFolderDialogMessage;
    public static String MakeGoodPropertyPage_testFolderDialogTitle;
    public static String MakeGoodPropertyPage_testFolderLabel;
    public static String MakeGoodPropertyPage_testingFrameworkLabel;
    public static String MakeGoodPropertyPage_preloadScriptBrowseLabel;
    public static String MakeGoodPropertyPage_preloadScriptDialogMessage;
    public static String MakeGoodPropertyPage_preloadScriptDialogTitle;
    public static String MakeGoodPropertyPage_preloadScriptLabel;
    public static String MakeGoodPropertyPage_phpunitConfigFileDialogTitle;
    public static String MakeGoodPropertyPage_phpunitConfigFileDialogMessage;
    public static String MakeGoodPropertyPage_phpunitConfigFileLabel;
    public static String MakeGoodPropertyPage_phpunitConfigFileBrowseLabel;
    public static String MakeGoodPropertyPage_testFolderRemoveLabel;
    public static String MakeGoodPropertyPage_cakephpAppPathDialogTitle;
    public static String MakeGoodPropertyPage_cakephpAppPathDialogMessage;
    public static String MakeGoodPropertyPage_cakephpAppPathLabel;
    public static String MakeGoodPropertyPage_cakephpAppPathBrowseLabel;
    public static String MakeGoodPropertyPage_cakephpCorePathDialogTitle;
    public static String MakeGoodPropertyPage_cakephpCorePathDialogMessage;
    public static String MakeGoodPropertyPage_cakephpCorePathLabel;
    public static String MakeGoodPropertyPage_cakephpCorePathBrowseLabel;

    /**
     * @since 1.3.0
     */
    public static String MakeGoodPropertyPage_ciunitPathDialogTitle;

    /**
     * @since 1.3.0
     */
    public static String MakeGoodPropertyPage_ciunitPathDialogMessage;

    /**
     * @since 1.3.0
     */
    public static String MakeGoodPropertyPage_ciunitPathLabel;

    /**
     * @since 1.3.0
     */
    public static String MakeGoodPropertyPage_ciunitPathBrowseLabel;

    /**
     * @since 1.3.0
     */
    public static String MakeGoodPropertyPage_ciunitConfigFileDialogTitle;

    /**
     * @since 1.3.0
     */
    public static String MakeGoodPropertyPage_ciunitConfigFileDialogMessage;

    /**
     * @since 1.3.0
     */
    public static String MakeGoodPropertyPage_ciunitConfigFileLabel;

    /**
     * @since 1.3.0
     */
    public static String MakeGoodPropertyPage_ciunitConfigFileBrowseLabel;

    /**
     * @since 2.0.0
     */
    public static String MakeGoodPropertyPage_generalLabel;

    /**
     * @since 2.0.0
     */
    public static String MakeGoodPropertyPage_testFilePatternLabel;

    /**
     * @since 2.0.0
     */
    public static String MakeGoodPropertyPage_defaultTestFilePatternLabel;

    /**
     * @since 2.3.0
     */
    public static String MakeGoodPreferencePage_continuousTestingGroupLabel;

    /**
     * @since 1.4.0
     */
    public static String MakeGoodPreferencePage_continuousTestingEnabledLabel;

    /**
     * @since 1.4.0
     */
    public static String MakeGoodPreferencePage_continuousTestingScopeAllTestsLabel;

    /**
     * @since 1.4.0
     */
    public static String MakeGoodPreferencePage_continuousTestingScopeLastTestLabel;

    /**
     * @since 2.1.0
     */
    public static String MakeGoodPreferencePage_continuousTestingScopeFailedTestsLabel;

    /**
     * @since 1.4.0
     */
    public static String MakeGoodPreferencePage_autotestScopeNoneLabel;

    public static String MakeGoodView_errorsLabel;
    public static String MakeGoodView_failuresLabel;
    public static String MakeGoodView_failureTraceLabel;
    public static String MakeGoodView_passesLabel;
    public static String MakeGoodView_testsLabel;
    public static String MakeGoodView_averageTest;
    public static String MakeGoodView_realTime;
    public static String MakeGoodView_testTime;
    public static String MakeGoodView_Status_TestsNotFound;

    /**
     * @since 1.6.0
     */
    public static String MakeGoodView_Status_NoProjectSelected;

    /**
     * @since 1.6.0
     */
    public static String MakeGoodView_Status_ProjectNotFound;

    /**
     * @since 2.2.0
     */
    public static String MakeGoodView_Status_ProjectNotOpen;

    /**
     * @since 1.6.0
     */
    public static String MakeGoodView_Status_NoTestableProjectSelected;

    /**
     * @since 1.6.0
     */
    public static String MakeGoodView_Status_NoPHPExecutablesDefined;

    /**
     * @since 1.6.0
     */
    public static String MakeGoodView_Status_MakeGoodNotConfigured;

    /**
     * @since 1.6.0
     */
    public static String MakeGoodView_Status_SAPINotCLI;

    /**
     * @since 1.6.0
     */
    public static String MakeGoodView_Status_RunningTest;

    /**
     * @since 1.6.0
     */
    public static String MakeGoodView_Status_WaitingForTestRun;

    /**
     * @since 1.6.0
     */
    public static String MakeGoodView_Status_TestingFrameworkNotAvailable;

    public static String MakeGoodView_Status_RelatedTestsNotFound;

    /**
     * @since 1.8.0
     */
    public static String MakeGoodView_Status_TypesNotFound;

    /**
     * @since 2.1.0
     */
    public static String MakeGoodView_Status_TestTargetNotFound;

    /**
     * @since 1.9.0
     */
    public static String MakeGoodView_endTime;

    /**
     * @since 2.0.0
     */
    public static String MakeGoodView_testResultsLabel;

    public static String TestRunner_TestSessionAlreadyExists_Title;
    public static String TestRunner_TestSessionAlreadyExists_Message;

    /**
     * @since 2.1.0
     */
    public static String ResultSquare_WaitingForTestRun;

    /**
     * @since 2.1.0
     */
    public static String ResultSquare_TestPassed;

    /**
     * @since 2.1.0
     */
    public static String ResultSquare_TestFailed;

    /**
     * @since 2.1.0
     */
    public static String ResultSquare_TestStopped;

    /**
     * @since 2.1.0
     */
    public static String ResultSquare_NoTests;

    /**
     * @since 2.3.0
     */
    public static String MakeGoodView_ConfigureContinuousTestingAction_DisableContinuousTestingAction;

    /**
     * @since 2.3.0
     */
    public static String MakeGoodView_ConfigureContinuousTestingAction_EnableContinuousTestingAction;

    /**
     * @since 2.3.0
     */
    public static String MakeGoodView_ConfigureContinuousTestingAction_SelectAllTestsAsContinuousTestingScopeAction;

    /**
     * @since 2.3.0
     */
    public static String MakeGoodView_ConfigureContinuousTestingAction_SelectFailedTestsAsContinuousTestingScopeAction;

    /**
     * @since 2.3.0
     */
    public static String MakeGoodView_ConfigureContinuousTestingAction_SelectLastTestAsContinuousTestingScopeAction;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
