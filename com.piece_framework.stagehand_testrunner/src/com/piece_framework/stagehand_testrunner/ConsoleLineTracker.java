package com.piece_framework.stagehand_testrunner;

import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;

public class ConsoleLineTracker implements IConsoleLineTracker {
    private IConsole fConsole;

    @Override
    public void init(IConsole console) {
        fConsole = console;
    }

    @Override
    public void lineAppended(IRegion line) {
        System.out.println(line);
        String message = "";
        try {
            message = fConsole.getDocument().get(line.getOffset(), line.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        System.out.println(message);
    }

    @Override
    public void dispose() {
    }
}
