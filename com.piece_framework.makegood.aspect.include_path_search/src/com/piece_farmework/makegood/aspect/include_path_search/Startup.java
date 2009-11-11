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
                new String[]{"org.eclipse.php.core",
                             "com.piece_framework.makegood.aspect.include_path_search",
                             "org.eclipse.core.resources"
                             });
        try {
            loader.load();
        } catch (NotFoundException e) {
            log(e);
            return;
        }

        try {
            CtClass targetClass = ClassPool.getDefault().get("org.eclipse.php.internal.core.util.PHPSearchEngine");
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
        CtMethod targetMethod = targetClass.getDeclaredMethod("find");
        targetMethod.instrument(new ExprEditor() {
            public void edit(Cast cast) throws CannotCompileException {
                try {
                    CtClass castClass = cast.getType();
                    if (castClass.getName().equals("org.eclipse.core.resources.IContainer")) {
                        cast.replace(
"$_ = null;" +
"if (includePath.getEntry() instanceof org.eclipse.core.resources.IContainer) {" +
"    $_ = ($r) includePath.getEntry();" +
"}"
                            );
                    }
                } catch (NotFoundException e) {
                }
            }

            public void edit(MethodCall methodCall) throws CannotCompileException {
                if (methodCall.getClassName().equals("org.eclipse.core.resources.IContainer")
                    && methodCall.getMethodName().equals("findMember")
                    ) {
                    methodCall.replace(
"$_ = null;" +
"if (container != null) {" +
"    $_ = $proceed($$);" +
"}"
                        );
                }
            }
        });
    }

    private void log(Exception e) {
        IStatus status = new Status(IStatus.ERROR,
                                    "com.piece_framework.makegood.aspect.include_path_search",
                                    0,
                                    e.getMessage(),
                                    e
                                    );
        Bundle bundle = Platform.getBundle("com.piece_framework.makegood.aspect.include_path_search");
        Platform.getLog(bundle).log(status);
    }
}
