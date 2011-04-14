/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.project;

import org.neontoolkit.gui.history.IOWLHistoryEntry;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.project.ProjectTreeElement;

import com.ontoprise.ontostudio.owl.gui.history.OWLProjectHistoryEntry;

/**
 * TreeElement used for the projects shown in the tree.
 */
public class OWLProjectTreeElement extends ProjectTreeElement {

    private OWLProjectHistoryEntry _historyEntry;

    /**
     * @param provider
     */
    public OWLProjectTreeElement(String projectName, ITreeDataProvider provider) {
        super(projectName, provider);
    }

    @Override
    public IOWLHistoryEntry getOWLHistoryEntry() {
        if(_historyEntry == null){
            _historyEntry = new OWLProjectHistoryEntry(this);
        }
        return _historyEntry;
    }
}
