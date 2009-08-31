/**
 * Copyright (c) 2008 ontoprise GmbH.
 */

package com.ontoprise.ontostudio.owl.model.commands.rename;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;

/*
 * Created on 12.11.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public class RenameOWLAnnotationProperty extends RenameOWLEntity {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException 
     */
    public RenameOWLAnnotationProperty(String project, String module, String oldId, String newId) throws NeOnCoreException {
        super(project, module, oldId, newId);
    }

    @Override
    protected OWLEntity getEntity(String uri) throws NeOnCoreException {
        return OWLModelFactory.getOWLDataFactory(getProjectName()).getOWLAnnotationProperty(OWLUtilities.toURI(uri));
    }
}
