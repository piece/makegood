package com.piece_framework.makegood.ui.launch;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;

public class TestRunner {
    private static String MODE_RUN ="run";
    private static MakeGoodLaunchShortcut shortcut;

    static {
        shortcut = MakeGoodLaunchShortcut.get();
    }

    public static void runRelatedTests(IEditorPart editorPart) {
        runTestsOnEditor(editorPart, MakeGoodLaunchShortcut.RUN_RELATED_TESTS);
    }

    public static void runTestsInContext(IEditorPart editorPart) {
        runTestsOnEditor(editorPart, MakeGoodLaunchShortcut.RUN_TEST_ON_CONTEXT);
    }

    public static void runTestsInClass(IEditorPart editorPart) {
        runTestsOnEditor(editorPart, MakeGoodLaunchShortcut.RUN_TESTS_ON_CLASS);
    }

    public static void runTestsInFile(IEditorPart editorPart) {
        runTestsOnEditor(editorPart, MakeGoodLaunchShortcut.RUN_TESTS_ON_FILE);
    }

    public static void runTests(ISelection selection) {
        shortcut.launch(selection, MODE_RUN); //$NON-NLS-1$
    }

    public static boolean hasLastTest() {
        return shortcut.hasLastTest();
    }

    public static void rerunLastTest() {
        shortcut.rerunLastTest();
    }

    private static void runTestsOnEditor(IEditorPart editorPart, int runLevel) {
        shortcut.setRunLevelOnEditor(runLevel);
        shortcut.launch(editorPart, MODE_RUN); //$NON-NLS-1$
    }
}
