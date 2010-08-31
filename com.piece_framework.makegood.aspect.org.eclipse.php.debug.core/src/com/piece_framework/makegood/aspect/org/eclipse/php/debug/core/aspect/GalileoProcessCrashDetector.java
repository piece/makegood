/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/

package com.piece_framework.makegood.aspect.org.eclipse.php.debug.core.aspect;

/**
 * A process crash detector is a {@link Runnable} that hooks a PHP process error stream and blocks until the process terminates.
 * Then, the detector determines if the process terminated as a result of an abnormal crash, or as a result
 * of a normal termination or a PHP fatal termination.
 * The PHP termination codes are between 0 - 255, so any other exit value is considered as a program crash.
 * The crash detector displays a message to the user in case of a crash.
 * 
 * @author shalom
 * @since PDT 1.0.1
 */
public class GalileoProcessCrashDetector implements Runnable {
	private Process process;

	/**
	 * Constructs a process detector on a given {@link Process}.
	 * 
	 * @param p A {@link Process}.
	 */
	public GalileoProcessCrashDetector(Process p) {
		this.process = p;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			int exitValue = process.waitFor();
			if (exitValue > 255 || exitValue < 0) {
			}
		} catch (Throwable t) {
		}
	}
}
