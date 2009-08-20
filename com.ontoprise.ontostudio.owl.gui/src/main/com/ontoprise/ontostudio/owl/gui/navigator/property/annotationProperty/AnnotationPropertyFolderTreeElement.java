/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty;

import org.neontoolkit.gui.navigator.ITreeDataProvider;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlFolderTreeElement;

/**
 * TreeElement used for object property folders in the tree
 */
public class AnnotationPropertyFolderTreeElement extends AbstractOwlFolderTreeElement {

    private static final String FOLDER_NAME = Messages.AnnotationPropertyFolderTreeElement_0; 

    /**
     * @param localId
     * @param namespace
     * @param provider
     * @param isRoot
     * @param projectName
     * @param ontologyId
     */
    public AnnotationPropertyFolderTreeElement(String projectName, String ontologyId, ITreeDataProvider provider) {
        super(FOLDER_NAME, projectName, ontologyId, provider);
    }

}
