/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands;

import java.util.ArrayList;
import java.util.List;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLDataFactory;

import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;

/**
 * @author werner
 *
 */
public class ApplyChanges extends OWLModuleChangeCommand {
    private static String[] toString(OWLAxiom[] axioms) {
        if (axioms == null) {
            return null;
        }
        if (axioms.length == 0) {
            return new String[0];
        }
        String[] strings = new String[axioms.length];
        for (int i = 0; i < axioms.length; i++) {
            strings[i] = OWLUtilities.toString(axioms[i]);
        }
        return strings;
    }

    public ApplyChanges(String project, String ontology, OWLAxiom[] addChanges, OWLAxiom[] removeChanges) throws NeOnCoreException {
        this (project, ontology, toString(addChanges), toString(removeChanges));
    }
    /**
     * @param project
     * @param ontology
     * @param arguments
     * @throws NeOnCoreException
     */
    public ApplyChanges(String project, String ontology, String[] addChanges, String[] removeChanges) throws NeOnCoreException {
        super(project, ontology, addChanges, removeChanges);
    }

    @Override
    public void doPerform() throws CommandException {
        String[] addChanges = (String[]) getArgument(2);
        String[] removeChanges = (String[]) getArgument(3);
        List<OWLAxiomChange> changes = new ArrayList<OWLAxiomChange>();
        
        
        try {
            OWLNamespaces namespaces = getOwlModel().getNamespaces();
            OWLDataFactory factory = getOwlModel().getOWLDataFactory();
            for (String removeChange: removeChanges) {
                OWLAxiom axiom = OWLUtilities.axiom(removeChange, namespaces, factory);
                OWLAxiomChange changeEvent = getOwlModel().getRemoveAxiom(axiom);
                if (!changes.contains(changeEvent)) {
                    changes.add(changeEvent);
                }
            }
            for (String addChange: addChanges) {
                OWLAxiom axiom = OWLUtilities.axiom(addChange, namespaces, factory);
                OWLAxiomChange changeEvent = getOwlModel().getAddAxiom(axiom);
                if (!changes.contains(changeEvent)) {
                    changes.add(changeEvent);
                }
            }
            
            getOwlModel().applyChanges(changes);
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
