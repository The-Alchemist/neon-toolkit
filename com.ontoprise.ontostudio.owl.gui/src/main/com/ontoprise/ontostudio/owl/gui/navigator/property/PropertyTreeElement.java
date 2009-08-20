/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.property;

import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;

public abstract class PropertyTreeElement extends AbstractOwlEntityTreeElement {

    public PropertyTreeElement(OWLEntity entity, String ontologyUri, String project, ITreeDataProvider provider) {
        super(entity, ontologyUri, project, provider);
    }

    public PropertyTreeElement(String uri, String ontologyUri, String projectId, ITreeDataProvider provider) {
        super(uri, ontologyUri, projectId, provider);
    }

}
