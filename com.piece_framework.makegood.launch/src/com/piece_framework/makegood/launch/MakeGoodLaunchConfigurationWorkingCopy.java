/**
 * Copyright (c) 2009 MATSUFUJI Hideharu <matsufuji2008@gmail.com>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.launch;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.internal.core.LaunchConfiguration;

public class MakeGoodLaunchConfigurationWorkingCopy implements ILaunchConfigurationWorkingCopy {
    private ILaunchConfigurationWorkingCopy original;

    public MakeGoodLaunchConfigurationWorkingCopy(ILaunchConfigurationWorkingCopy source) {
        this.original = source;
    }

    @Override
    public void addModes(Set modes) {
        original.addModes(modes);
    }

    @Override
    public ILaunchConfiguration doSave() throws CoreException {
        // MakeGood does not save a launch configuration, because not re-run on [Run As...].
        return null;
    }

    @Override
    public ILaunchConfiguration getOriginal() {
        return original.getOriginal();
    }

    @Override
    public ILaunchConfigurationWorkingCopy getParent() {
        return original.getParent();
    }

    @Override
    public boolean isDirty() {
        return original.isDirty();
    }

    @Override
    public Object removeAttribute(String attributeName) {
        return original.removeAttribute(attributeName);
    }

    @Override
    public void removeModes(Set modes) {
        original.removeModes(modes);
    }

    @Override
    public void rename(String name) {
        original.rename(name);
    }

    @Override
    public void setAttribute(String attributeName, int value) {
        original.setAttribute(attributeName, value);
    }

    @Override
    public void setAttribute(String attributeName, String value) {
        original.setAttribute(attributeName, value);
    }

    @Override
    public void setAttribute(String attributeName, List value) {
        original.setAttribute(attributeName, value);
    }

    @Override
    public void setAttribute(String attributeName, Map value) {
        original.setAttribute(attributeName, value);
    }

    @Override
    public void setAttribute(String attributeName, boolean value) {
        original.setAttribute(attributeName, value);
    }

    @Override
    public void setAttributes(Map attributes) {
        original.setAttributes(attributes);
    }

    @Override
    public void setContainer(IContainer container) {
        original.setContainer(container);
    }

    @Override
    public void setMappedResources(IResource[] resources) {
        original.setMappedResources(resources);
    }

    @Override
    public void setModes(Set modes) {
        original.setModes(modes);
    }

    @Override
    public void setPreferredLaunchDelegate(Set modes, String delegateId) {
        original.setPreferredLaunchDelegate(modes, delegateId);
    }

    @Override
    public boolean contentsEqual(ILaunchConfiguration configuration) {
        return original.contentsEqual(configuration);
    }

    @Override
    public ILaunchConfigurationWorkingCopy copy(String name)
            throws CoreException {
        return original.copy(name);
    }

    @Override
    public void delete() throws CoreException {
        original.delete();
    }

    @Override
    public boolean exists() {
        return original.exists();
    }

    @Override
    public boolean getAttribute(String attributeName, boolean defaultValue)
            throws CoreException {
        return original.getAttribute(attributeName, defaultValue);
    }

    @Override
    public int getAttribute(String attributeName, int defaultValue)
            throws CoreException {
        return original.getAttribute(attributeName, defaultValue);
    }

    @Override
    public List getAttribute(String attributeName, List defaultValue)
            throws CoreException {
        return original.getAttribute(attributeName, defaultValue);
    }

    @Override
    public Set getAttribute(String attributeName, Set defaultValue)
            throws CoreException {
        return original.getAttribute(attributeName, defaultValue);
    }

    @Override
    public Map getAttribute(String attributeName, Map defaultValue)
            throws CoreException {
        return original.getAttribute(attributeName, defaultValue);
    }

    @Override
    public String getAttribute(String attributeName, String defaultValue)
            throws CoreException {
        return original.getAttribute(attributeName, defaultValue);
    }

    @Override
    public Map getAttributes() throws CoreException {
        return original.getAttributes();
    }

    @Override
    public String getCategory() throws CoreException {
        return original.getCategory();
    }

    @Override
    public IFile getFile() {
        return original.getFile();
    }

    @Override
    public IPath getLocation() {
        return original.getLocation();
    }

    @Override
    public IResource[] getMappedResources() throws CoreException {
        return original.getMappedResources();
    }

    @Override
    public String getMemento() throws CoreException {
        return original.getMemento();
    }

    @Override
    public Set getModes() throws CoreException {
        return original.getModes();
    }

    @Override
    public String getName() {
        return original.getName();
    }

    @Override
    public ILaunchDelegate getPreferredDelegate(Set modes) throws CoreException {
        return original.getPreferredDelegate(modes);
    }

    @Override
    public ILaunchConfigurationType getType() throws CoreException {
        return original.getType();
    }

    @Override
    public ILaunchConfigurationWorkingCopy getWorkingCopy()
            throws CoreException {
        return original.getWorkingCopy();
    }

    @Override
    public boolean hasAttribute(String attributeName) throws CoreException {
        return original.hasAttribute(attributeName);
    }

    @Override
    public boolean isLocal() {
        return original.isLocal();
    }

    @Override
    public boolean isMigrationCandidate() throws CoreException {
        return original.isMigrationCandidate();
    }

    @Override
    public boolean isReadOnly() {
        return original.isReadOnly();
    }

    @Override
    public boolean isWorkingCopy() {
        return original.isWorkingCopy();
    }

    @Override
    public ILaunch launch(String mode, IProgressMonitor monitor)
            throws CoreException {
        return original.launch(mode, monitor);
    }

    @Override
    public ILaunch launch(String mode, IProgressMonitor monitor, boolean build)
            throws CoreException {
        return original.launch(mode, monitor, build);
    }

    @Override
    public ILaunch launch(String mode,
                          IProgressMonitor monitor,
                          boolean build,
                          boolean register
                          ) throws CoreException {
        return original.launch(mode, monitor, build, register);
    }

    @Override
    public void migrate() throws CoreException {
        original.migrate();
    }

    @Override
    public boolean supportsMode(String mode) throws CoreException {
        return original.supportsMode(mode);
    }

    @Override
    public Object getAdapter(Class adapter) {
        return original.getAdapter(adapter);
    }
}
