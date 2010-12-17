/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty;

import org.neontoolkit.gui.navigator.ITreeDataProvider;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlFolderTreeElement;

/**
 * TreeElement used for datatype property folders in the tree
 */
public class DataPropertyFolderTreeElement extends AbstractOwlFolderTreeElement {

    private static final String FOLDER_NAME = Messages.DataPropertyFolderTreeElement_0; 

    /**
     * @param localId
     * @param namespace
     * @param provider
     * @param isRoot
     * @param projectName
     * @param ontologyId
     */
    public DataPropertyFolderTreeElement(String projectName, String ontologyId, ITreeDataProvider provider) {
        super(FOLDER_NAME, projectName, ontologyId, provider);
    }

}
