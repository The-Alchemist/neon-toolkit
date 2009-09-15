/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.dataproperties;

import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLDataProperty;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * 
 */
public class IsDataProperty extends OWLOntologyRequestCommand {

    private Boolean _isDataProperty = null;

    public IsDataProperty(String project, String ontologyUri, String propertyUri) {
        super(project, ontologyUri, propertyUri);
    }

    @Override
    protected void perform() throws CommandException {
        String propertyUri = (String) getArgument(2);
        String expandedURI;
        try {
            expandedURI = getOwlModel().getNamespaces().expandString(propertyUri);

            OWLDataProperty prop = OWLModelFactory.getOWLDataFactory(getProjectName()).getOWLDataProperty(OWLUtilities.toURI(expandedURI));
            Set<OWLDataProperty> properties = getOwlModel().getAllDataProperties(true);
            _isDataProperty = new Boolean(properties.contains(prop));
        } catch (NeOnCoreException e) {
            _isDataProperty = new Boolean(false);
            throw new CommandException(e);
        }
    }
    
    public boolean isDataProperty() throws CommandException {
        if (_isDataProperty == null) {
            perform();
        }
        return _isDataProperty.booleanValue();
    }

}
