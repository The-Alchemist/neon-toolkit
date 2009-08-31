/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.exception.InternalNeOnException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.refactor.GenericRefactoringExecutionStarter;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectOneOf;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLSameIndividualAxiom;

import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;

public class OWLAxiomUtils {

    public static String OWL_INDIVIDUAL = "http://schema.ontoprise.com/reserved#owlIndividual"; //$NON-NLS-1$
    public static String OWL_INDIVIDUAL_LOCAL = "owlIndividual"; //$NON-NLS-1$

    private static OWLDataFactory getFactory(String projectId) {
        try {
            return OWLModelFactory.getOWLDataFactory(projectId);
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static OWLAxiom createNewAxiom(OWLAxiom oldAxiom, String descriptionToRemove, String projectId) {
        if (oldAxiom instanceof OWLEquivalentClassesAxiom) {
            return createNewEquivalentClassesAxiom(oldAxiom, descriptionToRemove, projectId);
        } else if (oldAxiom instanceof OWLDisjointClassesAxiom) {
            return createNewDisjointClassesAxiom(oldAxiom, descriptionToRemove, projectId);
        } else if (oldAxiom instanceof OWLDifferentIndividualsAxiom) {
            return createNewDifferentFromAxiom(oldAxiom, descriptionToRemove, projectId);
        } else if (oldAxiom instanceof OWLEquivalentDataPropertiesAxiom) {
            return createNewEquivalentDataProperties(oldAxiom, descriptionToRemove, projectId);
        } else if (oldAxiom instanceof OWLEquivalentObjectPropertiesAxiom) {
            return createNewEquivalentObjectProperties(oldAxiom, descriptionToRemove, projectId);
        } else if (oldAxiom instanceof OWLDisjointObjectPropertiesAxiom) {
            return createNewDisjointObjectProperties(oldAxiom, descriptionToRemove, projectId);
        } else if (oldAxiom instanceof OWLSameIndividualAxiom) {
            return createNewSameAsAxiom(oldAxiom, descriptionToRemove, projectId);
        } else {
            return null;
        }
    }

    public static OWLEquivalentClassesAxiom createNewEquivalentClassesAxiom(OWLAxiom oldAxiom, String descriptionToRemove, String projectId) {
        Set<OWLClassExpression> descriptions = ((OWLEquivalentClassesAxiom) oldAxiom).getClassExpressions();
        Set<OWLClassExpression> clonedDescriptions = new HashSet<OWLClassExpression>();
        for (OWLClassExpression desc: descriptions) {
            if (desc instanceof OWLObjectIntersectionOf) {
                Set<OWLClassExpression> descriptionsLocal = ((OWLObjectIntersectionOf) desc).getOperands();
                OWLObjectIntersectionOf objectAnd = createNewObjectAndDescription(descriptionsLocal, descriptionToRemove, projectId);
                clonedDescriptions.add(objectAnd);
            } else if (desc instanceof OWLObjectUnionOf) {
                Set<OWLClassExpression> descriptionsLocal = ((OWLObjectUnionOf) desc).getOperands();
                OWLObjectUnionOf objectOr = createNewObjectOrDescription(descriptionsLocal, descriptionToRemove, projectId);
                clonedDescriptions.add(objectOr);
            } else if (desc instanceof OWLObjectOneOf) {
                OWLObjectOneOf objectOneOf = (OWLObjectOneOf) desc;
                Set<OWLIndividual> individualsLocal = objectOneOf.getIndividuals();
                clonedDescriptions.add(createNewObjectOneOfDescription(individualsLocal, descriptionToRemove, projectId));
            } else {
                if (!OWLUtilities.toString(desc).equals(descriptionToRemove)) {
                    clonedDescriptions.add(desc);
                }
            }
        }
        if (clonedDescriptions.size() < 2) {
            return null;
        }
        OWLDataFactory factory = getFactory(projectId);
        return factory.getOWLEquivalentClassesAxiom(clonedDescriptions);
    }

    private static OWLObjectUnionOf createNewObjectOrDescription(Set<OWLClassExpression> oldDescriptions, String descriptionToRemove, String projectId) {
        Set<OWLClassExpression> newDescriptions = new HashSet<OWLClassExpression>();
        for (OWLClassExpression d: oldDescriptions) {
            if (d instanceof OWLObjectIntersectionOf) {
                Set<OWLClassExpression> descriptionsLocal = ((OWLObjectIntersectionOf) d).getOperands();
                newDescriptions.add(createNewObjectAndDescription(descriptionsLocal, descriptionToRemove, projectId));
            } else if (d instanceof OWLObjectUnionOf) {
                Set<OWLClassExpression> descriptionsLocal = ((OWLObjectUnionOf) d).getOperands();
                newDescriptions.add(createNewObjectOrDescription(descriptionsLocal, descriptionToRemove, projectId));
            } else if (d instanceof OWLObjectOneOf) {
                Set<OWLIndividual> individualsLocal = ((OWLObjectOneOf) d).getIndividuals();
                newDescriptions.add(createNewObjectOneOfDescription(individualsLocal, descriptionToRemove, projectId));
            } else {
                if (!OWLUtilities.toString(d).equals(descriptionToRemove)) {
                    newDescriptions.add(d);
                }
            }
        }
        OWLDataFactory factory = getFactory(projectId);
        return factory.getOWLObjectUnionOf(newDescriptions);
    }

    private static OWLClassExpression createNewObjectOneOfDescription(Set<OWLIndividual> individuals, String descriptionToRemove, String projectId) {
        Set<OWLIndividual> individualsLocal = new HashSet<OWLIndividual>();
        for (OWLIndividual i: individuals) {
            if (!OWLUtilities.toString(i).equals(descriptionToRemove)) {
                individualsLocal.add(i);
            }
        }
        OWLDataFactory factory = getFactory(projectId);
        return factory.getOWLObjectOneOf(individualsLocal);
    }

    private static OWLObjectIntersectionOf createNewObjectAndDescription(Set<OWLClassExpression> oldDescriptions, String descriptionToRemove, String projectId) {
        Set<OWLClassExpression> newDescriptions = new HashSet<OWLClassExpression>();
        for (OWLClassExpression d: oldDescriptions) {
            if (d instanceof OWLObjectIntersectionOf) {
                Set<OWLClassExpression> descriptionsLocal = ((OWLObjectIntersectionOf) d).getOperands();
                newDescriptions.add(createNewObjectAndDescription(descriptionsLocal, descriptionToRemove, projectId));
            } else if (d instanceof OWLObjectUnionOf) {
                Set<OWLClassExpression> descriptionsLocal = ((OWLObjectUnionOf) d).getOperands();
                newDescriptions.add(createNewObjectOrDescription(descriptionsLocal, descriptionToRemove, projectId));
            } else if (d instanceof OWLObjectOneOf) {
                Set<OWLIndividual> individualsLocal = ((OWLObjectOneOf) d).getIndividuals();
                newDescriptions.add(createNewObjectOneOfDescription(individualsLocal, descriptionToRemove, projectId));
            } else {
                if (!OWLUtilities.toString(d).equals(descriptionToRemove)) {
                    newDescriptions.add(d);
                }
            }
        }
        OWLDataFactory factory = getFactory(projectId);
        return factory.getOWLObjectIntersectionOf(newDescriptions);
    }

    public static OWLDisjointClassesAxiom createNewDisjointClassesAxiom(OWLAxiom oldAxiom, String descriptionToRemove, String projectId) {
        Set<OWLClassExpression> descriptions = ((OWLDisjointClassesAxiom) oldAxiom).getClassExpressions();
        Set<OWLClassExpression> clonedDescriptions = new HashSet<OWLClassExpression>();
        for (OWLClassExpression desc: descriptions) {
            if (!OWLUtilities.toString(desc).equals(descriptionToRemove)) {
                clonedDescriptions.add(desc);
            }
        }
        if (clonedDescriptions.size() < 2) {
            return null;
        }
        OWLDataFactory factory = getFactory(projectId);
        return factory.getOWLDisjointClassesAxiom(clonedDescriptions);
    }

    public static OWLDifferentIndividualsAxiom createNewDifferentFromAxiom(OWLAxiom oldAxiom, String idToRemove, String idToAdd, String projectId) {
        Set<OWLIndividual> individuals = ((OWLDifferentIndividualsAxiom) oldAxiom).getIndividuals();
        Set<OWLIndividual> clonedIndividuals = new HashSet<OWLIndividual>();
        for (OWLIndividual ind: individuals) {
            if (!OWLUtilities.toString(ind).equals(idToRemove)) {
                clonedIndividuals.add(ind);
            }
        }
        OWLDataFactory factory = getFactory(projectId);
        OWLNamedIndividual newIndividual = factory.getOWLNamedIndividual(OWLUtilities.toURI(idToAdd));
        clonedIndividuals.add(newIndividual);
        return factory.getOWLDifferentIndividualsAxiom(clonedIndividuals);
    }

    public static OWLDifferentIndividualsAxiom createNewDifferentFromAxiom(OWLAxiom oldAxiom, String idToRemove, String projectId) {
        Set<OWLIndividual> individuals = ((OWLDifferentIndividualsAxiom) oldAxiom).getIndividuals();
        Set<OWLIndividual> clonedIndividuals = new HashSet<OWLIndividual>();
        for (OWLIndividual ind: individuals) {
            if (!OWLUtilities.toString(ind).equals(idToRemove)) {
                clonedIndividuals.add(ind);
            }
        }
        if (clonedIndividuals.size() < 2) {
            return null;
        }
        OWLDataFactory factory = getFactory(projectId);
        return factory.getOWLDifferentIndividualsAxiom(clonedIndividuals);
    }

    public static OWLSameIndividualAxiom createNewSameAsAxiom(OWLAxiom oldAxiom, String idToRemove, String idToAdd, String projectId) {
        Set<OWLIndividual> individuals = ((OWLSameIndividualAxiom) oldAxiom).getIndividuals();
        Set<OWLIndividual> clonedIndividuals = new HashSet<OWLIndividual>();
        for (OWLIndividual ind: individuals) {
            if (!OWLUtilities.toString(ind).toString().equals(idToRemove)) {
                clonedIndividuals.add(ind);
            }
        }
        OWLDataFactory factory = getFactory(projectId);
        OWLNamedIndividual newIndividual = factory.getOWLNamedIndividual(OWLUtilities.toURI(idToAdd));
        clonedIndividuals.add(newIndividual);
        return factory.getOWLSameIndividualAxiom(clonedIndividuals);
    }

    public static OWLSameIndividualAxiom createNewSameAsAxiom(OWLAxiom oldAxiom, String idToRemove, String projectId) {
        Set<OWLIndividual> individuals = ((OWLSameIndividualAxiom) oldAxiom).getIndividuals();
        Set<OWLIndividual> clonedIndividuals = new HashSet<OWLIndividual>();
        for (OWLIndividual ind: individuals) {
            if (!OWLUtilities.toString(ind).equals(idToRemove)) {
                clonedIndividuals.add(ind);
            }
        }
        if (clonedIndividuals.size() < 2) {
            return null;
        }
        OWLDataFactory factory = getFactory(projectId);
        return factory.getOWLSameIndividualAxiom(clonedIndividuals);
    }

    public static OWLEquivalentObjectPropertiesAxiom createNewEquivalentObjectProperties(OWLAxiom oldAxiom, String propertyToRemove, String projectId) {
        Set<OWLObjectPropertyExpression> props = ((OWLEquivalentObjectPropertiesAxiom) oldAxiom).getProperties();
        Set<OWLObjectPropertyExpression> clonedProps = new HashSet<OWLObjectPropertyExpression>();
        for (OWLObjectPropertyExpression p: props) {
            if (!propertyToRemove.equals(OWLUtilities.toString(p))) {
                clonedProps.add(p);
            }
        }
        if (clonedProps.size() < 2) {
            return null;
        }
        OWLDataFactory factory = getFactory(projectId);
        return factory.getOWLEquivalentObjectPropertiesAxiom(clonedProps);
    }

    public static OWLDisjointObjectPropertiesAxiom createNewDisjointObjectProperties(OWLAxiom oldAxiom, String propertyToRemove, String projectId) {
        Set<OWLObjectPropertyExpression> props = ((OWLDisjointObjectPropertiesAxiom) oldAxiom).getProperties();
        Set<OWLObjectPropertyExpression> clonedProps = new HashSet<OWLObjectPropertyExpression>();
        for (OWLObjectPropertyExpression p: props) {
            if (!propertyToRemove.equals(OWLUtilities.toString(p))) {
                clonedProps.add(p);
            }
        }
        if (clonedProps.size() < 2) {
            return null;
        }
        OWLDataFactory factory = getFactory(projectId);
        return factory.getOWLDisjointObjectPropertiesAxiom(clonedProps);
    }

    public static OWLEquivalentDataPropertiesAxiom createNewEquivalentDataProperties(OWLAxiom oldAxiom, String propertyToRemove, String projectId) {
        Set<OWLDataPropertyExpression> props = ((OWLEquivalentDataPropertiesAxiom) oldAxiom).getProperties();
        Set<OWLDataPropertyExpression> clonedProps = new HashSet<OWLDataPropertyExpression>();
        for (OWLDataPropertyExpression p: props) {
            if (!propertyToRemove.equals(OWLUtilities.toString(p))) {
                clonedProps.add(p);
            }
        }
        if (clonedProps.size() < 2) {
            return null;
        }
        OWLDataFactory factory = getFactory(projectId);
        return factory.getOWLEquivalentDataPropertiesAxiom(clonedProps);
    }

    public static void triggerRemovePressed(List<OWLAxiom> axioms, String description, OWLNamespaces namespaces, String id, OWLModel owlModel) throws NeOnCoreException {
        try {
            GenericRefactoringExecutionStarter.startRefactoring(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "com.ontoprise.ontostudio.owl.gui.refactor.deleteAxiom", //$NON-NLS-1$
                    axioms,
                    description,
                    namespaces,
                    id,
                    owlModel); 
        } catch (CoreException e) {
            throw new InternalNeOnException(e);
        }
    }

    public static void triggerRemovePressed(List<OWLAxiom> axioms, OWLEntity entity, OWLNamespaces namespaces, String id, OWLModel owlModel, int mode) throws NeOnCoreException {
        try {
            GenericRefactoringExecutionStarter.startRefactoring(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "com.ontoprise.ontostudio.owl.gui.refactor.deleteAxiom", //$NON-NLS-1$
                    axioms,
                    id,
                    namespaces,
                    entity.getURI().toString(),
                    owlModel); 
        } catch (CoreException e) {
            throw new InternalNeOnException(e);
        }
    }

}
