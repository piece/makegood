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

package com.piece_framework.makegood.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IModelElementVisitorExtension;
import org.eclipse.dltk.core.INamespace;
import org.eclipse.dltk.core.IOpenable;
import org.eclipse.dltk.core.IParameter;
import org.eclipse.dltk.core.IScriptModel;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;

/**
 * @since 1.x.0
 */
public class TestMethod implements IMethod {
    private IMethod method;
    private IType baseType;

    public TestMethod(IMethod method) {
        this.method = method;
    }

    @Override
    public ISourceRange getNameRange() throws ModelException {
        return method.getNameRange();
    }

    @Override
    public int getFlags() throws ModelException {
        return method.getFlags();
    }

    @Override
    public IType getDeclaringType() {
        if (baseType != null) return baseType;
        return method.getDeclaringType();
    }

    @Override
    public ISourceModule getSourceModule() {
        return method.getSourceModule();
    }

    @Override
    public IType getType(String name, int occurrenceCount) {
        return method.getType(name, occurrenceCount);
    }

    @Override
    public int getElementType() {
        return method.getElementType();
    }

    @Override
    public String getElementName() {
        return method.getElementName();
    }

    @Override
    public IModelElement getParent() {
        return method.getParent();
    }

    @Override
    public boolean isReadOnly() {
        return method.isReadOnly();
    }

    @Override
    public IResource getResource() {
        if (baseType != null) return baseType.getResource();
        return method.getResource();
    }

    @Override
    public IPath getPath() {
        return method.getPath();
    }

    @Override
    public boolean exists() {
        return method.exists();
    }

    @Override
    public IModelElement getAncestor(int ancestorType) {
        return method.getAncestor(ancestorType);
    }

    @Override
    public IOpenable getOpenable() {
        return method.getOpenable();
    }

    @Override
    public IScriptModel getModel() {
        return method.getModel();
    }

    @Override
    public IScriptProject getScriptProject() {
        return method.getScriptProject();
    }

    @Override
    public IResource getUnderlyingResource() throws ModelException {
        return method.getUnderlyingResource();
    }

    @Override
    public IResource getCorrespondingResource() throws ModelException {
        return method.getCorrespondingResource();
    }

    @Override
    public IModelElement getPrimaryElement() {
        return method.getPrimaryElement();
    }

    @Override
    public String getHandleIdentifier() {
        return method.getHandleIdentifier();
    }

    @Override
    public boolean isStructureKnown() throws ModelException {
        return method.isStructureKnown();
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
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        return method.getAdapter(adapter);
    }

    @Override
    public ISourceRange getSourceRange() throws ModelException {
        return method.getSourceRange();
    }

    @Override
    public String getSource() throws ModelException {
        return method.getSource();
    }

    @Override
    public IModelElement[] getChildren() throws ModelException {
        return method.getChildren();
    }

    @Override
    public boolean hasChildren() throws ModelException {
        return method.hasChildren();
    }

    @Override
    public IParameter[] getParameters() throws ModelException {
        return method.getParameters();
    }

    @Override
    public String[] getParameterNames() throws ModelException {
        return method.getParameterNames();
    }

    @Override
    public boolean isConstructor() throws ModelException {
        return method.isConstructor();
    }

    @Override
    public String getFullyQualifiedName(String enclosingTypeSeparator) {
        return method.getFullyQualifiedName(enclosingTypeSeparator);
    }

    @Override
    public String getFullyQualifiedName() {
        return method.getFullyQualifiedName();
    }

    @Override
    public String getTypeQualifiedName(String enclosingTypeSeparator,
                                       boolean showParameters) throws ModelException {
        return method.getTypeQualifiedName(enclosingTypeSeparator, showParameters);
    }

    @Override
    public String getType() throws ModelException {
        return method.getType();
    }

    @Override
    public INamespace getNamespace() throws ModelException {
        return method.getNamespace();
    }

    public void setBaseType(IType baseType) {
        this.baseType = baseType;
    }
}
