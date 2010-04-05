package com.piece_framework.makegood.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.piece_framework.makegood.core.MakeGoodProperty;
import com.piece_framework.makegood.javassist.monitor.WeavingMonitor;
import com.piece_framework.makegood.ui.launch.TestRunner;

public class RunAllTests extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof IEditorPart) {
            TestRunner.runAllTests(activePart);
        } else {
            TestRunner.runAllTests(HandlerUtil.getCurrentSelection(event));
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        if (!WeavingMonitor.endAll()) return false;

        IResource resource = null;
        IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        if (activePage.getActivePart() instanceof IEditorPart) {
            resource = TestRunner.getResource(activePage.getActivePart());
        } else {
            resource = TestRunner.getResource(activePage.getSelection());
        }

        if (resource == null || !resource.getProject().exists()) return false;

        MakeGoodProperty property = new MakeGoodProperty(resource);
        if (property.getTestFolders().size() == 0) return false;

        return true;
    }
}
