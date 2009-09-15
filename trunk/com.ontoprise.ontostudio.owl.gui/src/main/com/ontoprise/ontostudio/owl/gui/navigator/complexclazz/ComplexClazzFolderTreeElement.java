/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.complexclazz;

import org.neontoolkit.gui.navigator.ITreeDataProvider;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlFolderTreeElement;

/**
 * TreeElement used for class folders in the tree
 */
public class ComplexClazzFolderTreeElement extends AbstractOwlFolderTreeElement {

    private static final String FOLDER_NAME = Messages.ComplexClazzFolderTreeElement_0; 

    public ComplexClazzFolderTreeElement(String projectName, String ontologyId, ITreeDataProvider provider) {
        super(FOLDER_NAME, projectName, ontologyId, provider);
    }

}
