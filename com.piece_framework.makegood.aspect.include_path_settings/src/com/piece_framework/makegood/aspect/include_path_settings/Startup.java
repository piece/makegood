package com.piece_framework.makegood.aspect.include_path_settings;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

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
                new String[]{"org.eclipse.php.ui",
                             "com.piece_framework.makegood.aspect.include_path_settings",
                             "com.piece_framework.makegood.include_path",
                             "org.eclipse.dltk.ui",
                             "org.eclipse.core.resources",
                             "org.eclipse.jface"
                             });
        try {
            loader.load();
        } catch (NotFoundException e) {
            log(e);
            return;
        }

        try {
            CtClass targetClass = ClassPool.getDefault().get("org.eclipse.php.internal.ui.preferences.includepath.PHPIPListLabelProvider");
            addGetCPListElementTextMethod(targetClass);
            modifyGetCPListElementBaseImage(targetClass);
            targetClass.toClass(getClass().getClassLoader(), null);
        } catch (NotFoundException e) {
            log(e);
        } catch (CannotCompileException e) {
            log(e);
        }

        try {
            CtClass targetClass = ClassPool.getDefault().get("org.eclipse.php.internal.ui.preferences.includepath.PHPIncludePathsBlock");
            modifyCreateControlMethod(targetClass);
            targetClass.toClass(getClass().getClassLoader(), null);
        } catch (NotFoundException e) {
            log(e);
        } catch (CannotCompileException e) {
            log(e);
        }

        MonitorTarget.endWeaving = true;
    }

    private void addGetCPListElementTextMethod(CtClass targetClass) throws CannotCompileException {
        CtMethod newMethod = CtNewMethod.make(
"public String getCPListElementText(org.eclipse.dltk.internal.ui.wizards.buildpath.BPListElement cpentry) {" +
"    org.eclipse.core.resources.IResource target = cpentry.getResource();" +
"    com.piece_framework.makegood.include_path.ConfigurationIncludePath configuration = new com.piece_framework.makegood.include_path.ConfigurationIncludePath(target.getProject());" +
"    if (configuration.equalsDummyResource(target)) {" +
"        return com.piece_framework.makegood.include_path.ConfigurationIncludePath.text;" +
"    }" +
"" +
"    return super.getCPListElementText(cpentry);"+
"}"
            ,targetClass);
        targetClass.addMethod(newMethod);
    }

    private void modifyGetCPListElementBaseImage(CtClass targetClass) throws NotFoundException, CannotCompileException {
        CtMethod targetMethod = targetClass.getDeclaredMethod("getCPListElementBaseImage");
        targetMethod.insertBefore(
"org.eclipse.core.resources.IResource target = cpentry.getResource();" +
"com.piece_framework.makegood.include_path.ConfigurationIncludePath configuration = new com.piece_framework.makegood.include_path.ConfigurationIncludePath(target.getProject());" +
"if (configuration.equalsDummyResource(target)) {" +
"    return com.piece_framework.makegood.include_path.ConfigurationIncludePath.icon;" +
"}"
            );
    }

    private void modifyCreateControlMethod(CtClass targetClass) throws NotFoundException, CannotCompileException {
        CtMethod targetMethod = targetClass.getDeclaredMethod("createControl");
        targetMethod.instrument(new ExprEditor() {
            public void edit(NewExpr expression) throws CannotCompileException {
                if (expression.getClassName().equals("org.eclipse.php.internal.ui.preferences.includepath.PHPIncludePathSourcePage")) {
                    expression.replace(
"$_ = new com.piece_framework.makegood.aspect.include_path_settings.PHPIncludePathSourcePageForConfiguration($1);"
                        );
                }
            }
        });
    }

    private void log(Exception e) {
        IStatus status = new Status(IStatus.ERROR,
                                    "com.piece_framework.makegood.aspect.include_path_settings",
                                    0,
                                    e.getMessage(),
                                    e
                                    );
        Bundle bundle = Platform.getBundle("com.piece_framework.makegood.aspect.include_path_settings");
        Platform.getLog(bundle).log(status);
    }
}
