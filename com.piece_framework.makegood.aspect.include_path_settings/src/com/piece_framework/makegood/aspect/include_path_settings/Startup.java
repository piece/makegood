package com.piece_framework.makegood.aspect.include_path_settings;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.NewExpr;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IStartup;


public class Startup implements IStartup {
    @Override
    public void earlyStartup() {
        ClassPool pool = ClassPool.getDefault();

        String[] requiredBundles = {"org.eclipse.php.ui",
                                    "com.piece_framework.makegood.aspect.include_path_settings",
                                    "com.piece_framework.makegood.include_path",
                                    "org.eclipse.dltk.ui",
                                    "org.eclipse.core.resources",
                                    "org.eclipse.jface"
                                    };
        for (String requiredBundle: requiredBundles) {
            try {
                URL bundleURL = new URL(Platform.getBundle(requiredBundle).getLocation());
                String bundleLocation = null;
                if (bundleURL.getFile().startsWith("file:")) {
                    bundleLocation = bundleURL.getFile().substring("file:".length());
                } else {
                    bundleLocation = bundleURL.getFile();
                }

                if (new File(bundleLocation).isDirectory()) {
                    bundleLocation += "bin";
                }

                pool.appendClassPath(bundleLocation);
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (NotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        try {
            CtClass targetClass = pool.get("org.eclipse.php.internal.ui.preferences.includepath.PHPIPListLabelProvider");
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

            CtMethod targetMethod = targetClass.getDeclaredMethod("getCPListElementBaseImage");
            targetMethod.insertBefore(
"org.eclipse.core.resources.IResource target = cpentry.getResource();" +
"com.piece_framework.makegood.include_path.ConfigurationIncludePath configuration = new com.piece_framework.makegood.include_path.ConfigurationIncludePath(target.getProject());" +
"if (configuration.equalsDummyResource(target)) {" +
"    return com.piece_framework.makegood.include_path.ConfigurationIncludePath.icon;" +
"}"
                );

            targetClass.toClass(getClass().getClassLoader(), null);
        } catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CannotCompileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            CtClass targetClass = pool.get("org.eclipse.php.internal.ui.preferences.includepath.PHPIncludePathsBlock");
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
            targetClass.toClass(getClass().getClassLoader(), null);
        } catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CannotCompileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
