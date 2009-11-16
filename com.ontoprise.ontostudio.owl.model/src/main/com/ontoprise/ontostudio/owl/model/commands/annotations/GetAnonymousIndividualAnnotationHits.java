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
import org.semanticweb.owlapi.model.OWLAnonymousIndividual;

/**
 * @author werner
 * 
 */
public class GetAnonymousIndividualAnnotationHits extends AbstractGetAnnotationHits {

    /**
     * @param project
     * @param module
     * @param arguments
     */
    public GetAnonymousIndividualAnnotationHits(String project, String module, OWLAnonymousIndividual anonymousIndividual) {
        super(project, module, anonymousIndividual);
    }

    @Override
    protected OWLAnnotationSubject getAnnotationSubject() throws NeOnCoreException {
        return (OWLAnonymousIndividual)getArgument(2);
    }



}
