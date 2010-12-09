/**
 * Copyright (c) 2010 KUBO Atsuhiro <kubo@iteman.jp>,
 * All rights reserved.
 *
 * This file is part of MakeGood.
 *
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package com.piece_framework.makegood.aspect.org.eclipse.php.core;

import org.eclipse.core.filebuffers.IDocumentSetupParticipant;
import org.eclipse.jface.text.IDocument;

/**
 * An extension for the org.eclipse.core.filebuffers.documentSetup extension point.
 * This extension runs a weaving process for our aspects if another weaving process
 * had not been run. This extension does nothing for the original purpose.
 *
 * @since 1.2.0
 */
public class MakeGoodDocumentSetupParticipant implements IDocumentSetupParticipant {
    @Override
    public void setup(IDocument document) {
        if (MonitorTarget.endWeaving) return;
        new FragmentWeavingProcess().process();
    }
}
