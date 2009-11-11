package com.piece_framework.makegood.ui.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import com.piece_framework.makegood.ui.launch.MakeGoodLaunchShortcut;

public class RunTestFromExplorer extends AbstractHandler {
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        ISelection selection = HandlerUtil.getActiveMenuSelection(event);
        if (selection == null) {
            selection = getSelectionFromActivePage();
        }
        MakeGoodLaunchShortcut shortcut = new MakeGoodLaunchShortcut();
        shortcut.launch(selection, "run");
        return shortcut;
    }

    @Override
    public boolean isEnabled() {
        ISelection selection = getSelectionFromActivePage();
        if (!(selection instanceof IStructuredSelection)) {
            return super.isEnabled();
        }

        Object element = ((IStructuredSelection) selection).getFirstElement();
        if (!(element instanceof IFile)) {
            return super.isEnabled();
        }

        try {
            IContentType contentType = ((IFile) element).getContentDescription().getContentType();
            if (contentType.getId().equals("org.eclipse.php.core.phpsource")) {
                return true;
            }
        } catch (CoreException e) {
        }
        return false;
    }

    private ISelection getSelectionFromActivePage() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        return page.getSelection();
    }
}
