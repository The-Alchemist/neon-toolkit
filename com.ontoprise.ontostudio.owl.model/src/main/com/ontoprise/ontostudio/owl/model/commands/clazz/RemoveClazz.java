/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.clazz;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

public class RemoveClazz extends OWLModuleChangeCommand {

    /**
     * Don' t use this to completele remove a class. Only use for refactorings like moving a class. 
     * @param project
     * @param ontologyId
     * @param clazzId
     * @param superClazzId
     * @throws NeOnCoreException 
     */
    public RemoveClazz(String project, String ontologyId, String clazzId, String superClazzId) throws NeOnCoreException {
        this(project, ontologyId, clazzId, superClazzId, false);
    }

    /**
     * If <code>removeAllAxioms</code> is true, this will remove all axioms related to the passed <code>clazzId</code>.
     * @param project
     * @param ontologyId
     * @param clazzId
     * @param superClazzId
     * @param removeAllAxioms
     * @throws NeOnCoreException 
     */
    public RemoveClazz(String project, String ontologyId, String clazzId, String superClazzId, boolean removeAllAxioms) throws NeOnCoreException {
        super(project, ontologyId, clazzId, superClazzId, removeAllAxioms);
    }

    @Override
    public void doPerform() throws CommandException {
        String subClazzId = getArgument(2).toString();
        String superClazzId = getArgument(3) != null ? getArgument(3).toString() : null;
        boolean removeAllAxioms = ((Boolean) getArgument(4)).booleanValue();

        try {
            OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(getProjectName());
            OWLClass subClazz = factory.getOWLClass(OWLUtilities.toURI(subClazzId));
            if (removeAllAxioms) {
                // remove all found axioms
                Set<OWLAxiom> axiomsToRemove = getOwlModel().getReferencingAxioms(subClazz);
                List<String> axiomTexts = new ArrayList<String>();
                
                for (OWLAxiom a: axiomsToRemove) {
                    String axiomText = OWLUtilities.toString(a);
                    axiomTexts.add(axiomText);
                }
                new ApplyChanges(getProjectName(), getOntology(), new String[0], axiomTexts.toArray(new String[axiomTexts.size()])).perform();
            } else {
                if (superClazzId != null) {
                    // subclass
                    OWLClassExpression superClazzDesc = OWLUtilities.description(superClazzId, getOwlModel().getNamespaces(), getOwlModel().getOWLDataFactory());
        
                    OWLAxiom subClassOf = factory.getOWLSubClassOfAxiom(subClazz, superClazzDesc);
                    new ApplyChanges(getProjectName(), getOntology(), new String[0], new String[]{OWLUtilities.toString(subClassOf)}).perform();
                } else {
                    // root class
                    OWLEntity entity = factory.getOWLClass(OWLUtilities.toURI(subClazzId));
                    OWLDeclarationAxiom declaration = factory.getOWLDeclarationAxiom(entity);
                    new ApplyChanges(getProjectName(), getOntology(), new String[0], new String[]{OWLUtilities.toString(declaration)}).perform();
                }
            }
            
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
    }

}
