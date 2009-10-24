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
import javassist.expr.MethodCall;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IStartup;


public class Startup implements IStartup {
    @Override
    public void earlyStartup() {
        ClassPool pool = ClassPool.getDefault();

        String[] requiredBundles = {"org.eclipse.php.ui",
                                    "com.piece_framework.makegood.aspect.include_path_settings",
                                    "org.eclipse.dltk.ui",
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
            CtClass targetClass = pool.get("org.eclipse.php.internal.ui.preferences.includepath.PHPIPListLabelProvider");
            CtMethod newMethod = CtNewMethod.make(
"public String getCPListElementText(org.eclipse.dltk.internal.ui.wizards.buildpath.BPListElement cpentry) {" +
"    org.eclipse.core.resources.IResource target = cpentry.getResource();" +
"    com.piece_framework.makegood.aspect.include_path_settings.ConfigurationIncludePath configuration = new com.piece_framework.makegood.aspect.include_path_settings.ConfigurationIncludePath(target.getProject());" +
"    if (configuration.equalsDummyResource(target)) {" +
"        return com.piece_framework.makegood.aspect.include_path_settings.ConfigurationIncludePath.text;" +
"    }" +
"" +
"    return super.getCPListElementText(cpentry);"+
"}"
                    ,targetClass);
            targetClass.addMethod(newMethod);
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
