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

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.util.IRIUtils;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.model.Messages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;

/**
 * @author werner
 * @author Nico Stieler
 * 
 */
public class SetPropertyAttribute extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param propertyUri
     * @param propertyType Object or DataProperty. use constants {@link OWLCommandUtils#OBJECT_PROP} and {@link OWLCommandUtils#DATA_PROP}.
     * @param attributeMode functional, inverse_functional, symmetric, transitive. use constants {@link OWLCommandUtils#FUNCTIONAL}, {@link OWLCommandUtils#INVERSE_FUNCTIONAL}, {@link OWLCommandUtils#SYMMETRIC} and {@link OWLCommandUtils#TRANSITIVE} and more from OWL2
     * @param trueOrFalse
     * @throws NeOnCoreException
     */
    public SetPropertyAttribute(String project, String module, String propertyUri, String propertyType, String attributeMode, boolean trueOrFalse) throws NeOnCoreException {
        super(project, module, propertyUri, propertyType, attributeMode, new Boolean(trueOrFalse));
    }

    @Override
    protected void doPerform() throws CommandException {
        String propertyUri = (String) getArgument(2);
        String propertyType = (String) getArgument(3);
        String attributeMode = (String) getArgument(4);
        boolean trueOrFalse = ((Boolean) getArgument(5)).booleanValue();

        try {
            OWLModel owlModel = OWLModelFactory.getOWLModel(getOntology(), getProjectName());
            OWLOntology ontology = owlModel.getOntology();
            OWLDataFactory factory = owlModel.getOWLDataFactory();
            OWLAxiom axiom = null;
            if (propertyType.equals(OWLCommandUtils.DATA_PROP)) {
                if (attributeMode.equals(OWLCommandUtils.FUNCTIONAL)) {
                    axiom = factory.getOWLFunctionalDataPropertyAxiom(OWLUtilities.dataProperty(IRIUtils.ensureValidIRISyntax(propertyUri), ontology));
                } else {
                    throw new IllegalArgumentException(Messages.getString("SetPropertyAttribute.0") + attributeMode); //$NON-NLS-1$
                }
            } else {
                OWLObjectPropertyExpression objectProperty = OWLUtilities.objectProperty(IRIUtils.ensureValidIRISyntax(propertyUri), ontology);
                if (attributeMode.equals(OWLCommandUtils.FUNCTIONAL)) {
                    axiom = factory.getOWLFunctionalObjectPropertyAxiom(objectProperty);
                } else if (attributeMode.equals(OWLCommandUtils.INVERSE_FUNCTIONAL)) {
                    axiom = factory.getOWLInverseFunctionalObjectPropertyAxiom(objectProperty);
                } else if (attributeMode.equals(OWLCommandUtils.SYMMETRIC)) {
                    axiom = factory.getOWLSymmetricObjectPropertyAxiom(objectProperty);
                } else if (attributeMode.equals(OWLCommandUtils.REFLEXIVE)) {
                    axiom = factory.getOWLReflexiveObjectPropertyAxiom(objectProperty);
                } else if (attributeMode.equals(OWLCommandUtils.IRREFLEXIVE)) {
                    axiom = factory.getOWLIrreflexiveObjectPropertyAxiom(objectProperty);
                } else if (attributeMode.equals(OWLCommandUtils.SYMMETRIC)) {
                    axiom = factory.getOWLSymmetricObjectPropertyAxiom(objectProperty);
                } else if (attributeMode.equals(OWLCommandUtils.ASYMMETRIC)) {
                    axiom = factory.getOWLAsymmetricObjectPropertyAxiom(objectProperty);
                } else if (attributeMode.equals(OWLCommandUtils.TRANSITIVE)) {
                    axiom = factory.getOWLTransitiveObjectPropertyAxiom(objectProperty);
                } else {
                    throw new IllegalArgumentException(Messages.getString("SetPropertyAttribute.4") + attributeMode); //$NON-NLS-1$
                }
            }
            if (trueOrFalse) {
                new ApplyChanges(getProjectName(), getOntology(), new OWLAxiom[] {axiom}, new OWLAxiom[0]).perform();
            } else {
                new ApplyChanges(getProjectName(), getOntology(), new OWLAxiom[0], new OWLAxiom[] {axiom}).perform();
            }
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

    }

}
