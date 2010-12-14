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
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;

/**
 * @author werner
 * 
 */
public class AddEntityAnnotation extends AbstractAddAnnotation {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public AddEntityAnnotation(String project, String module, String entityUri, String[] newValues) throws NeOnCoreException {
        super(project, module, entityUri, newValues);
    }
    
    @Override
    protected OWLAnnotationSubject getAnnotationSubject() throws NeOnCoreException{
        OWLNamespaces namespaces = getOwlModel().getNamespaces();
        String entityUri = (String) getArgument(2);
        String expandedURI = namespaces.expandString(entityUri);
        return IRI.create(expandedURI);
    }

}
