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

import java.lang.reflect.InvocationTargetException;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

public class RemoveDataProperty extends OWLModuleChangeCommand {

    public RemoveDataProperty(String project, String ontologyId, String propertyId, String superPropertyId, boolean deleteSubProperties) throws NeOnCoreException {
        super(project, ontologyId, propertyId, superPropertyId, new Boolean(deleteSubProperties));
    }

    @Override
    public void doPerform() throws CommandException {

        boolean deleteSubProperties = ((Boolean) getArgument(4)).booleanValue();
        String subPropertyId = getArgument(2).toString();
        String superPropertyId = getArgument(3) != null ? getArgument(3).toString() : null;

        try {
            OWLNamespaces ns = getOwlModel().getNamespaces();
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();

            // delete subproperties, or shift them one level higher
            String[] subPropUris = new GetSubDataProperties(getProjectName(), getOntology(), superPropertyId).getResults();
            if (deleteSubProperties) {
                for (String uri: subPropUris) {
                    new RemoveDataProperty(getProjectName(), getOntology(), uri, subPropertyId, deleteSubProperties).perform();
                }
            } else {
                // move subproperties one level higher
                for (String uri: subPropUris) {
                    new MoveDataProperty(getProjectName(), getOntology(), uri, subPropertyId, superPropertyId).perform();
                }
            }

            if (superPropertyId == null) {
                OWLDataProperty dataProperty = OWLModelFactory.getOWLDataFactory(getProjectName()).getOWLDataProperty(OWLUtilities.toURI(subPropertyId));
                getOwlModel().delEntity(dataProperty, null);
            } else {
                new ApplyChanges(getProjectName(), getOntology(), new OWLAxiom[0], new OWLAxiom[]{factory.getOWLSubDataPropertyOfAxiom(OWLUtilities.dataProperty(subPropertyId, ns, factory), OWLUtilities.dataProperty(superPropertyId, ns, factory))}).perform();
            }

        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        } catch (InterruptedException e) {
            throw new CommandException(e);
        } catch (InvocationTargetException e) {
            throw new CommandException(e);
        }
    }

}
