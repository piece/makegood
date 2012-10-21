/**
 * Copyright (c) 2012 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 *
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.core;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.ast.Modifiers;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IModelElementVisitorExtension;
import org.eclipse.dltk.core.INamespace;
import org.eclipse.dltk.core.IOpenable;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptModel;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ITypeHierarchy;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.WorkingCopyOwner;

/**
 * @since 2.2.0
 */
public class TestClass implements IType {
    private IType origin;
    private IType baseType;
    private IModelElement[] children;
    private TestingFramework testingFramework;

    public TestClass(IType type, TestingFramework testingFramework) {
        this.origin = type;
        while (this.origin instanceof TestClass) {
            this.origin = ((TestClass) this.origin).origin;
        }

        this.testingFramework = testingFramework;
    }

    @Override
    public IPath getPath() {
        return origin.getPath();
    }

    @Override
    public ISourceRange getNameRange() throws ModelException {
        return origin.getNameRange();
    }

    @Override
    public int getFlags() throws ModelException {
        return origin.getFlags();
    }

    @Override
    public IType getDeclaringType() {
        return createTestClass(origin.getDeclaringType());
    }

    @Override
    public ISourceModule getSourceModule() {
        return origin.getSourceModule();
    }

    @Override
    public IType getType(String name, int occurrenceCount) {
        return createTestClass(origin.getType(name, occurrenceCount));
    }

    @Override
    public int getElementType() {
        return origin.getElementType();
    }

    @Override
    public String getElementName() {
        return origin.getElementName();
    }

    @Override
    public IModelElement getParent() {
        return origin.getParent();
    }

    @Override
    public boolean isReadOnly() {
        return origin.isReadOnly();
    }

    @Override
    public IResource getResource() {
        return origin.getResource();
    }

    @Override
    public boolean exists() {
        return origin.exists();
    }

    @Override
    public IModelElement getAncestor(int ancestorType) {
        return origin.getAncestor(ancestorType);
    }

    @Override
    public IOpenable getOpenable() {
        return origin.getOpenable();
    }

    @Override
    public IScriptModel getModel() {
        return origin.getModel();
    }

    @Override
    public IScriptProject getScriptProject() {
        return origin.getScriptProject();
    }

    @Override
    public IResource getUnderlyingResource() throws ModelException {
        return origin.getUnderlyingResource();
    }

    @Override
    public IResource getCorrespondingResource() throws ModelException {
        return origin.getCorrespondingResource();
    }

    @Override
    public IModelElement getPrimaryElement() {
        return origin.getPrimaryElement();
    }

    @Override
    public String getHandleIdentifier() {
        return origin.getHandleIdentifier();
    }

    @Override
    public boolean isStructureKnown() throws ModelException {
        return origin.isStructureKnown();
    }

    @Override
    public void accept(IModelElementVisitor visitor) throws ModelException {
        if (visitor.visit(this)) {
            IModelElement[] elements = getChildren();
            for (int i = 0; i < elements.length; ++i) {
                elements[i].accept(visitor);
            }
            if (visitor instanceof IModelElementVisitorExtension) {
                ((IModelElementVisitorExtension) visitor).endVisit(this);
            }
        }
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Object getAdapter(Class adapter) {
        return origin.getAdapter(adapter);
    }

    @Override
    public ISourceRange getSourceRange() throws ModelException {
        return origin.getSourceRange();
    }

    @Override
    public String getSource() throws ModelException {
        return origin.getSource();
    }

    @Override
    public IModelElement[] getChildren() throws ModelException {
        if (this.children != null) return this.children;

        List<IModelElement> children = new ArrayList<IModelElement>();
        if ((getFlags() & Modifiers.AccNameSpace) == 0) {
            children.addAll(Arrays.asList(getMethods()));

            ITypeHierarchy hierarchy = newSupertypeHierarchy(new NullProgressMonitor());
            for (IType supertype: hierarchy.getSupertypes(origin)) {
                if (!TestingFramework.isTestClassSuperType(supertype)) {
                    children.add(createTestClass(supertype));
                }
            }
        } else {
            for (IType type: getTypes()) {
                if (isTestClass(type, testingFramework)) {
                    children.add(createTestClass(type));
                }
            }
        }
        this.children = children.toArray(new IModelElement[0]);

        return this.children;
    }

    @Override
    public boolean hasChildren() throws ModelException {
        return getChildren().length > 0;
    }

    @Override
    public String[] getSuperClasses() throws ModelException {
        return origin.getSuperClasses();
    }

    @Override
    public IField getField(String name) {
        return origin.getField(name);
    }

    @Override
    public IField[] getFields() throws ModelException {
        return origin.getFields();
    }

    @Override
    public IType getType(String name) {
        return createTestClass(origin.getType(name));
    }

    @Override
    public IType[] getTypes() throws ModelException {
        List<IType> types= new ArrayList<IType>();
        for (IType type: this.origin.getTypes()) {
            types.add(createTestClass(type));
        }
        return types.toArray(new IType[0]);
    }

    @Override
    public IMethod getMethod(String name) {
        return createTestMethod(origin.getMethod(name));
    }

    @Override
    public IMethod[] getMethods() throws ModelException {
        List<IMethod> methods = new ArrayList<IMethod>();
        if (origin.getResource() == null) return methods.toArray(new IMethod[0]);
        for (IMethod method: origin.getMethods()) {
            if (testingFramework.isTestMethod(method)) methods.add(createTestMethod(method));
        }
        return methods.toArray(new IMethod[0]);
    }

    @Override
    public String getFullyQualifiedName(String enclosingTypeSeparator) {
        return origin.getFullyQualifiedName(enclosingTypeSeparator);
    }

    @Override
    public String getFullyQualifiedName() {
        return origin.getFullyQualifiedName();
    }

    @Override
    public void codeComplete(char[] snippet,
                             int insertion,
                             int position,
                             char[][] localVariableTypeNames,
                             char[][] localVariableNames,
                             int[] localVariableModifiers,
                             boolean isStatic,
                             CompletionRequestor requestor) throws ModelException {
        origin.codeComplete(
            snippet,
            insertion,
            position,
            localVariableTypeNames,
            localVariableNames,
            localVariableModifiers,
            isStatic,
            requestor);
    }

    @Override
    public void codeComplete(char[] snippet,
                             int insertion,
                             int position,
                             char[][] localVariableTypeNames,
                             char[][] localVariableNames,
                             int[] localVariableModifiers,
                             boolean isStatic,
                             CompletionRequestor requestor,
                             WorkingCopyOwner owner) throws ModelException {
        origin.codeComplete(
            snippet,
            insertion,
            position,
            localVariableTypeNames,
            localVariableNames,
            localVariableModifiers,
            isStatic,
            requestor,
            owner);
    }

    @Override
    public IScriptFolder getScriptFolder() {
        return origin.getScriptFolder();
    }

    @Override
    public String getTypeQualifiedName() {
        return origin.getTypeQualifiedName();
    }

    @Override
    public String getTypeQualifiedName(String enclosingTypeSeparator) {
        return origin.getTypeQualifiedName(enclosingTypeSeparator);
    }

    @Override
    public IMethod[] findMethods(IMethod method) {
        return origin.findMethods(method);
    }

    @Override
    public ITypeHierarchy loadTypeHierachy(InputStream input,
                                           IProgressMonitor monitor) throws ModelException {
        return origin.loadTypeHierachy(input, monitor);
    }

    @Override
    public ITypeHierarchy newSupertypeHierarchy(IProgressMonitor monitor) throws ModelException {
        return origin.newSupertypeHierarchy(monitor);
    }

    @Override
    public ITypeHierarchy newSupertypeHierarchy(ISourceModule[] workingCopies,
                                                IProgressMonitor monitor) throws ModelException {
        return origin.newSupertypeHierarchy(workingCopies, monitor);
    }

    @Override
    public ITypeHierarchy newSupertypeHierarchy(WorkingCopyOwner owner,
                                                IProgressMonitor monitor) throws ModelException {
        return origin.newSupertypeHierarchy(owner, monitor);
    }

    @Override
    public ITypeHierarchy newTypeHierarchy(IScriptProject project,
                                           IProgressMonitor monitor) throws ModelException {
        return origin.newTypeHierarchy(project, monitor);
    }

    @Override
    public ITypeHierarchy newTypeHierarchy(IScriptProject project,
                                           WorkingCopyOwner owner,
                                           IProgressMonitor monitor) throws ModelException {
        return origin.newTypeHierarchy(project, owner, monitor);
    }

    @Override
    public ITypeHierarchy newTypeHierarchy(IProgressMonitor monitor) throws ModelException {
        return origin.newTypeHierarchy(monitor);
    }

    @Override
    public ITypeHierarchy newTypeHierarchy(ISourceModule[] workingCopies,
                                           IProgressMonitor monitor) throws ModelException {
        return origin.newTypeHierarchy(workingCopies, monitor);
    }

    @Override
    public ITypeHierarchy newTypeHierarchy(WorkingCopyOwner owner,
                                           IProgressMonitor monitor) throws ModelException {
        return origin.newSupertypeHierarchy(owner, monitor);
    }

    @Override
    public INamespace getNamespace() throws ModelException {
        return origin.getNamespace();
    }

    public static boolean isTestClass(IType type, TestingFramework testingFramework) {
        if (type == null) return false;
        try {
            PHPType phpType = new PHPType(type, testingFramework);
            if (!phpType.isNamespace()) {
                return testingFramework.isTestClass(
                    (type instanceof TestClass) ? ((TestClass) type).origin : type);
            } else {
                for (IType child: type.getTypes()) {
                    if (isTestClass(child, testingFramework)) return true;
                }
            }
        } catch (CoreException e) {}
        return false;
    }

    public void setBaseType(IType baseType) {
        this.baseType = baseType;
    }

    public boolean isSubtype(IType targetSuperType) throws ModelException {
        ITypeHierarchy hierarchy = newSupertypeHierarchy(new NullProgressMonitor());
        for (IType superType: hierarchy.getAllSuperclasses(origin)) {
            if (superType.getElementName().equals(targetSuperType.getElementName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isNamespace() {
        try {
            PHPType phpType = new PHPType(this, testingFramework);
            return phpType.isNamespace();
        } catch (CoreException e) {
            return false;
        }
    }

    private TestClass createTestClass(IType type) {
        if (type == null) return null;
        TestClass testClass = new TestClass(type, testingFramework);
        if (baseType != null) testClass.setBaseType(baseType);
        return testClass;
    }

    private TestMethod createTestMethod(IMethod method) {
        TestMethod testMethod = new TestMethod(method);
        if (baseType != null) testMethod.setBaseType(baseType);
        return testMethod;
    }
}
