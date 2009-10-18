package com.piece_framework.makegood.aspect.include_path_protection;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IStartup;

public class Startup implements IStartup {
    @Override
    public void earlyStartup() {
        ClassPool pool = ClassPool.getDefault();

        String[] requiredBundles = {"org.eclipse.php.debug.core",
                                    "com.piece_framework.makegood.aspect.include_path_protection",
                                    "org.eclipse.core.resources"
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
            CtClass targetClass = pool.get("org.eclipse.php.internal.debug.core.phpIni.PHPINIUtil");
            CtMethod targetMethod = targetClass.getDeclaredMethod("createPhpIniByProject");
            targetMethod.instrument(new ExprEditor() {
                public void edit(MethodCall methodCall) throws CannotCompileException {
                    if (methodCall.getMethodName().equals("modifyIncludePath")) {
                        methodCall.replace(
"com.piece_framework.makegood.aspect.include_path_protection.PHPConfiguration configuration =" +
"    new com.piece_framework.makegood.aspect.include_path_protection.PHPConfiguration();" +
"String[] includePathsFromConfiguration = configuration.getIncludePathWithConfiguration($1," +
"                                                                                       includePath,"+
"                                                                                       project" +
"                                                                                       );" +
"$_ = $proceed($1, includePathsFromConfiguration);");
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
