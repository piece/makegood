/**
 * Copyright (c) 2013-2014 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.launch;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.php.internal.core.typeinference.PHPClassType;

import com.piece_framework.makegood.core.PHPType;
import com.piece_framework.makegood.core.TestingFramework;
import com.piece_framework.makegood.core.preference.MakeGoodProperties;

/**
 * @since 2.5.0
 */
@SuppressWarnings("restriction")
public class CommandLineBuilder {
    public static boolean stopOnFailure = false;

    private String junitXMLFile;
    // Flag indicating whether these command arguments should be compatible with a phpunit phar launch
    private boolean isPharLaunch;

    public CommandLineBuilder(String junitXMLFile, boolean isPharLaunch) {
        this.junitXMLFile = junitXMLFile;
        this.isPharLaunch = isPharLaunch;
    }

    public String build() throws CoreException, MethodNotFoundException, ResourceNotFoundException {
        MakeGoodProperties property = new MakeGoodProperties(TestLifecycle.getInstance().getTestTargets().getFirstResource());
        StringBuilder buffer = new StringBuilder();

        if(!isPharLaunch) {
        	buffer.append(" --no-ansi"); //$NON-NLS-1$
        	buffer.append(" " + property.getTestingFramework().name().toLowerCase()); //$NON-NLS-1$
        

	        String preloadScript = property.getPreloadScript();
	        if (!preloadScript.equals("")) { //$NON-NLS-1$
	            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
	            IResource preloadResource = root.findMember(preloadScript);
	            if (preloadResource == null) {
	                throw new ResourceNotFoundException("The resource [ " + preloadScript + " ] is not found."); //$NON-NLS-1$ //$NON-NLS-2$
	            }
	
	            if(isPharLaunch) {
	            	buffer.append(" --bootstrap \"" + preloadResource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
	            } else {
	            	buffer.append(" -p \"" + preloadResource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
	            }
	        }
        }
	
	    buffer.append(" --log-junit=\"" + junitXMLFile + "\""); //$NON-NLS-1$ //$NON-NLS-2$
	    if(isPharLaunch) {
	    	if (stopOnFailure) {
	    		buffer.append(" --stop-on-failure"); //$NON-NLS-1$
	    	}
	    } else {
	    	buffer.append(" --log-junit-realtime"); //$NON-NLS-1$
	        
	        if (stopOnFailure) {
	            buffer.append(" -s"); //$NON-NLS-1$
	        }
	    }

        if (property.getTestingFramework() == TestingFramework.PHPUnit) {
            String phpunitConfigFile = property.getPHPUnitConfigFile();
            if (!"".equals(phpunitConfigFile)) { //$NON-NLS-1$
                IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(phpunitConfigFile);
                if (resource == null) {
                    throw new ResourceNotFoundException("The resource [ " + phpunitConfigFile + " ] is not found."); //$NON-NLS-1$ //$NON-NLS-2$
                }

                if(isPharLaunch) {
                	buffer.append(" -c \""+ resource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
                } else {
                	buffer.append(" --phpunit-config=\"" + resource.getLocation().toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }

        Set<String> testFiles = new HashSet<String>();
        Set<String> testClasses = new HashSet<String>();
        Set<String> testMethods = new HashSet<String>();
        for (Object testTarget: TestLifecycle.getInstance().getTestTargets().getAll()) {
            IResource resource = TestLifecycle.getInstance().getTestTargets().getResource(testTarget);
            if (resource == null || resource.exists() == false) {
                throw new ResourceNotFoundException("The resource for the test target [ " + testTarget + " ] is not found."); //$NON-NLS-1$ //$NON-NLS-2$
            }

            testFiles.add(resource.getLocation().toString());
            if (testTarget instanceof IType) {
                PHPType phpType = new PHPType((IType) testTarget, property.getTestingFramework());
                if (phpType.isNamespace()) {
                    for (IType type: ((IType) testTarget).getTypes()) {
                        testClasses.add(urlencode(PHPClassType.fromIType(type).getTypeName()));
                    }
                } else if (phpType.isClass()) {
                    testClasses.add(urlencode(PHPClassType.fromIType((IType) testTarget).getTypeName()));
                }
            } else if (testTarget instanceof IMethod) {
                IMethod method = findMethod((IMethod) testTarget);
                if (method == null) {
                    throw new MethodNotFoundException("An unknown method context [ " + testTarget + " ] has been found."); //$NON-NLS-1$ //$NON-NLS-2$
                }
                testMethods.add(
                    urlencode(
                        PHPClassType.fromIType(method.getDeclaringType()).getTypeName() +
                        "::" + //$NON-NLS-1$
                        method.getElementName()
                    )
                );
            } else if (testTarget instanceof ClassTestTarget) {
                testClasses.add(urlencode(((ClassTestTarget) testTarget).getClassName()));
            }
        }

        if(!isPharLaunch) {
	        if (testClasses.size() > 0) {
	            for (String testClass: testClasses) {
	                buffer.append(" --test-class=\"" + testClass.toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
	            }
	        }
	
	        if (testMethods.size() > 0) {
	            for (String testMethod: testMethods) {
	                buffer.append(" --test-method=\"" + testMethod.toString() + "\""); //$NON-NLS-1$ //$NON-NLS-2$
	            }
	        }

	        buffer.append(" -R"); //$NON-NLS-1$
	        buffer.append(
	            " --test-file-pattern=\"" + //$NON-NLS-1$
	            (property.getTestFilePattern().equals("") ? property.getTestingFramework().getTestFilePattern() : property.getTestFilePattern()) + //$NON-NLS-1$
	            "\"" //$NON-NLS-1$
	        );
	        for (String testFile: testFiles) {
	            buffer.append(" \"" + testFile + "\""); //$NON-NLS-1$ //$NON-NLS-2$
	        }
        }

        return buffer.toString();
    }

    private IMethod findMethod(IMethod method) {
        IModelElement parent = method.getParent();
        if (parent == null) return null;
        if (parent instanceof IType) {
            return method;
        }

        while (true) {
            if (parent instanceof IMethod) {
                return findMethod((IMethod) parent);
            }

            parent = parent.getParent();
            if (parent == null) return null;
        }
    }

    private String urlencode(String subject) throws CoreException
    {
        try {
            return URLEncoder.encode(subject, TestLifecycle.getInstance().getTestTargets().getProject().getDefaultCharset());
        } catch (UnsupportedEncodingException e) {
            Activator.getDefault().getLog().log(new Status(Status.WARNING, Activator.PLUGIN_ID, e.getMessage(), e));

            return subject;
        }
    }
}
