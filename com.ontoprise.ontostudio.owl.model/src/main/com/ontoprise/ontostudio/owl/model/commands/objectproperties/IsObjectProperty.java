/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.objectproperties;

import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.OWLOntologyRequestCommand;

/**
 * @author werner
 * @author Nico Stieler
 * 
 */
public class IsObjectProperty extends OWLOntologyRequestCommand {

    private Boolean _isObjectProperty;

    /**
     * @param project
     * @param ontology
     * @param arguments
     */
    public IsObjectProperty(String project, String ontology, String propertyUri) {
        super(project, ontology, propertyUri);
    }

    @Override
    protected void perform() throws CommandException {
        String propertyUri = (String) getArgument(2);

        String expandedURI;
        try {
            expandedURI = getOwlModel().getNamespaces().expandString(propertyUri);
            OWLObjectProperty prop = OWLModelFactory.getOWLDataFactory(getProjectName()).getOWLObjectProperty(OWLUtilities.toIRI(expandedURI));
            Set<OWLObjectProperty> properties = getOwlModel().getAllObjectProperties(true);
            _isObjectProperty = new Boolean(properties.contains(prop));
            if(!_isObjectProperty){
                if(prop.equals(getOwlModel().getOWLDataFactory().getOWLTopObjectProperty()) ||
                        prop.equals(getOwlModel().getOWLDataFactory().getOWLBottomObjectProperty())){
                    _isObjectProperty = new Boolean(true);
                }
            }
        } catch (NeOnCoreException e) {
            _isObjectProperty = new Boolean(false);
            throw new CommandException(e);
        }
    }

    public boolean isObjectProperty() throws CommandException {
        if (_isObjectProperty == null) {
            perform();
        }
        return _isObjectProperty.booleanValue();
    }
}
