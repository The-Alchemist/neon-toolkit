/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.annotations;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;

import com.ontoprise.ontostudio.owl.model.OWLUtilities;

/**
 * @author werner
 * 
 */
public class GetEntityAnnotationHits extends AbstractGetAnnotationHits {

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetEntityAnnotationHits(String project, String module, String entityUri) {
        super(project, module, entityUri);
    }

    @Override
    protected OWLAnnotationSubject getAnnotationSubject() throws NeOnCoreException {
        String expandedURI = getOwlModel().getNamespaces().expandString((String)getArgument(2));
        return OWLUtilities.toIRI(expandedURI);
    }



}
