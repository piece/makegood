package com.piece_farmework.makegood.aspect.include_path_search;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.Cast;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.IStartup;
import org.osgi.framework.Bundle;

import com.piece_framework.makegood.javassist.BundleLoader;

public class Startup implements IStartup {
    @Override
    public void earlyStartup() {
        BundleLoader loader = new BundleLoader(
                new String[]{"org.eclipse.php.core", //$NON-NLS-1$
                             "com.piece_framework.makegood.aspect.include_path_search", //$NON-NLS-1$
                             "org.eclipse.core.resources" //$NON-NLS-1$
                             });
        try {
            loader.load();
        } catch (NotFoundException e) {
            log(e);
            return;
        }

        try {
            CtClass targetClass = ClassPool.getDefault().get("org.eclipse.php.internal.core.util.PHPSearchEngine"); //$NON-NLS-1$
            modifyFind(targetClass);
            targetClass.toClass(getClass().getClassLoader(), null);
        } catch (NotFoundException e) {
            log(e);
        } catch (CannotCompileException e) {
            log(e);
        }

        MonitorTarget.endWeaving = true;
    }

    private void modifyFind(CtClass targetClass) throws NotFoundException, CannotCompileException {
        CtMethod targetMethod = targetClass.getDeclaredMethod("find"); //$NON-NLS-1$
        targetMethod.instrument(new ExprEditor() {
            public void edit(Cast cast) throws CannotCompileException {
                CtClass castClass = null;
                try {
                    castClass = cast.getType();
                } catch (NotFoundException e) {}

                if (castClass != null && castClass.getName().equals("org.eclipse.core.resources.IContainer")) { //$NON-NLS-1$
                    cast.replace(
"$_ = null;" + //$NON-NLS-1$
"if (includePath.getEntry() instanceof org.eclipse.core.resources.IContainer) {" + //$NON-NLS-1$
"    $_ = ($r) includePath.getEntry();" + //$NON-NLS-1$
"}" //$NON-NLS-1$
                    );
                }
            }

            public void edit(MethodCall methodCall) throws CannotCompileException {
                if (methodCall.getClassName().equals("org.eclipse.core.resources.IContainer") //$NON-NLS-1$
                    && methodCall.getMethodName().equals("findMember") //$NON-NLS-1$
                    ) {
                    methodCall.replace(
"$_ = null;" + //$NON-NLS-1$
"if (container != null) {" + //$NON-NLS-1$
"    $_ = $proceed($$);" + //$NON-NLS-1$
"}" //$NON-NLS-1$
                        );
                }
            }
        });
    }

    private void log(Exception e) {
        IStatus status = new Status(IStatus.ERROR,
                                    "com.piece_framework.makegood.aspect.include_path_search", //$NON-NLS-1$
                                    0,
                                    e.getMessage(),
                                    e
                                    );
        Bundle bundle = Platform.getBundle("com.piece_framework.makegood.aspect.include_path_search"); //$NON-NLS-1$
        Platform.getLog(bundle).log(status);
    }
}
