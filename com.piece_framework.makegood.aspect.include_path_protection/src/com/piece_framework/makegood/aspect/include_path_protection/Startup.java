package com.piece_framework.makegood.aspect.include_path_protection;

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
                new String[]{"org.eclipse.php.debug.core",
                             "com.piece_framework.makegood.aspect.include_path_protection",
                             "org.eclipse.core.resources",
                             "org.eclipse.equinox.common",
                             "org.eclipse.php.core"
                             });
        try {
            loader.load();
        } catch (NotFoundException e) {
            log(e);
            return;
        }

        try {
            CtClass targetClass = ClassPool.getDefault().get("org.eclipse.php.internal.debug.core.phpIni.PHPINIUtil");
            modifyCreatePhpIniByProject(targetClass);
            targetClass.toClass(getClass().getClassLoader(), null);
        } catch (NotFoundException e) {
            log(e);
        } catch (CannotCompileException e) {
            log(e);
        }

        MonitorTarget.endWeaving = true;
    }

    private void modifyCreatePhpIniByProject(CtClass targetClass) throws NotFoundException, CannotCompileException {
        CtMethod targetMethod = targetClass.getDeclaredMethod("createPhpIniByProject");
        targetMethod.instrument(new ExprEditor() {
            public void edit(Cast cast) throws CannotCompileException {
                try {
                    CtClass castClass = cast.getType();
                    if (castClass.getName().equals("org.eclipse.core.resources.IContainer")) {
                        cast.replace(
"$_ = null;" +
"if (pathObject.getEntry() instanceof org.eclipse.core.resources.IContainer) {" +
"    $_ = ($r) pathObject.getEntry();" +
"} else {" +
"    org.eclipse.core.resources.IResource resource = (org.eclipse.core.resources.IResource) pathObject.getEntry();" +
"    includePath.add(resource.getFullPath().toOSString());" +
"}"
                            );
                    }
                } catch (NotFoundException e) {
                }
            }

            public void edit(MethodCall methodCall) throws CannotCompileException {
                if (methodCall.getClassName().equals("org.eclipse.core.resources.IContainer")
                    && methodCall.getMethodName().equals("getLocation")
                    ) {
                    methodCall.replace(
"$_ = null;" +
"if (container != null) {" +
"    $_ = $proceed($$);" +
"}"
                        );
                } else if (methodCall.getMethodName().equals("modifyIncludePath")) {
                    methodCall.replace(
"com.piece_framework.makegood.aspect.include_path_protection.PHPConfiguration phpConfiguration =" +
"    new com.piece_framework.makegood.aspect.include_path_protection.PHPConfiguration();" +
"String[] transformedIncludePaths = phpConfiguration.transformIncludePaths($1," +
"                                                                          includePath,"+
"                                                                          project" +
"                                                                          );" +
"$_ = $proceed($1, transformedIncludePaths);"
                        );
                }
            }
        });
    }

    private void log(Exception e) {
        IStatus status = new Status(IStatus.ERROR,
                                    "com.piece_framework.makegood.aspect.include_path_protection",
                                    0,
                                    e.getMessage(),
                                    e
                                    );
        Bundle bundle = Platform.getBundle("com.piece_framework.makegood.aspect.include_path_protection");
        Platform.getLog(bundle).log(status);
    }
}
