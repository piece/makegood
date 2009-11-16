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
                new String[]{"org.eclipse.php.debug.core", //$NON-NLS-1$
                             "com.piece_framework.makegood.aspect.include_path_protection", //$NON-NLS-1$
                             "org.eclipse.core.resources", //$NON-NLS-1$
                             "org.eclipse.equinox.common", //$NON-NLS-1$
                             "org.eclipse.php.core" //$NON-NLS-1$
                             });
        try {
            loader.load();
        } catch (NotFoundException e) {
            log(e);
            return;
        }

        try {
            CtClass targetClass = ClassPool.getDefault().get("org.eclipse.php.internal.debug.core.phpIni.PHPINIUtil"); //$NON-NLS-1$
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
        CtMethod targetMethod = targetClass.getDeclaredMethod("createPhpIniByProject"); //$NON-NLS-1$
        targetMethod.instrument(new ExprEditor() {
            public void edit(Cast cast) throws CannotCompileException {
                try {
                    CtClass castClass = cast.getType();
                    if (castClass.getName().equals("org.eclipse.core.resources.IContainer")) { //$NON-NLS-1$
                        cast.replace(
"$_ = null;" + //$NON-NLS-1$
"if (pathObject.getEntry() instanceof org.eclipse.core.resources.IContainer) {" + //$NON-NLS-1$
"    $_ = ($r) pathObject.getEntry();" + //$NON-NLS-1$
"} else {" + //$NON-NLS-1$
"    org.eclipse.core.resources.IResource resource = (org.eclipse.core.resources.IResource) pathObject.getEntry();" + //$NON-NLS-1$
"    includePath.add(resource.getFullPath().toOSString());" + //$NON-NLS-1$
"}" //$NON-NLS-1$
                            );
                    }
                } catch (NotFoundException e) {
                }
            }

            public void edit(MethodCall methodCall) throws CannotCompileException {
                if (methodCall.getClassName().equals("org.eclipse.core.resources.IContainer") //$NON-NLS-1$
                    && methodCall.getMethodName().equals("getLocation") //$NON-NLS-1$
                    ) {
                    methodCall.replace(
"$_ = null;" + //$NON-NLS-1$
"if (container != null) {" + //$NON-NLS-1$
"    $_ = $proceed($$);" + //$NON-NLS-1$
"}" //$NON-NLS-1$
                        );
                } else if (methodCall.getMethodName().equals("modifyIncludePath")) { //$NON-NLS-1$
                    methodCall.replace(
"com.piece_framework.makegood.aspect.include_path_protection.PHPConfiguration phpConfiguration =" + //$NON-NLS-1$
"    new com.piece_framework.makegood.aspect.include_path_protection.PHPConfiguration();" + //$NON-NLS-1$
"String[] transformedIncludePaths = phpConfiguration.transformIncludePaths($1," + //$NON-NLS-1$
"                                                                          includePath,"+ //$NON-NLS-1$
"                                                                          project" + //$NON-NLS-1$
"                                                                          );" + //$NON-NLS-1$
"$_ = $proceed($1, transformedIncludePaths);" //$NON-NLS-1$
                        );
                }
            }
        });
    }

    private void log(Exception e) {
        IStatus status = new Status(IStatus.ERROR,
                                    "com.piece_framework.makegood.aspect.include_path_protection", //$NON-NLS-1$
                                    0,
                                    e.getMessage(),
                                    e
                                    );
        Bundle bundle = Platform.getBundle("com.piece_framework.makegood.aspect.include_path_protection"); //$NON-NLS-1$
        Platform.getLog(bundle).log(status);
    }
}
