/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.wizard;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;

public class RemoveClazzWizardPage1 extends RemoveEntityWizardPage1 {

    protected RemoveClazzWizardPage1(String pageName, List<OWLAxiom> axioms, OWLNamespaces namespaces, String sourceEntityUri, String targetEntityUri, String deleteMode, List<OWLEntity> entities, String ontologyUri, String projectId) {
        super(pageName, axioms, namespaces, sourceEntityUri, targetEntityUri, deleteMode, entities, ontologyUri, projectId);
    }

    @Override
    protected void setCanFinish(boolean canFinish) {
        ((RemoveClazzWizard) getWizard()).setCanFinish(true);
    }

    @Override
    protected List<OWLAxiom> getChildrenForAxiom(OWLAxiom parentElement) throws NeOnCoreException {
        OWLModel owlModel = ((RemoveClazzWizard) getWizard()).getOwlModel();
        if (_deleteMode.equals(RemoveClazzWizard.DELETE_CLAZZ)) {
            // DELETE_CLAZZ
            return null;
        } else if (_deleteMode.equals(RemoveClazzWizard.DELETE_CLAZZ_WITH_INDIVIDUALS)) {
            // DELETE_CLAZZ_WITH_INDIVIDUALS
            if (parentElement instanceof OWLClassAssertionAxiom) {
                return getIndividualAxioms((OWLClassAssertionAxiom) parentElement, owlModel);
            }
        } else {
            // DELETE_SUBTREE
            if (parentElement instanceof OWLSubClassOfAxiom) {
                OWLClassExpression subClass = ((OWLSubClassOfAxiom) parentElement).getSubClass();

                if (subClass instanceof OWLClass) {
                    Set<OWLAxiom> resultList = new LinkedHashSet<OWLAxiom>(owlModel.getReferencingAxioms((OWLClass)subClass));
                    resultList.remove(parentElement); // remove same axiom
                    List<OWLAxiom> clonedList = new ArrayList<OWLAxiom>();
                    clonedList.addAll(resultList);
                    for (OWLAxiom a: clonedList) {
                        if (a instanceof OWLClassAssertionAxiom) {
                            List<OWLAxiom> individuals = getIndividualAxioms((OWLClassAssertionAxiom) a, owlModel);
                            resultList.addAll(individuals);
                        }
                    }
                    return new ArrayList<OWLAxiom>(resultList);
                }
                return null;
            }
            return null;
        }
        return null;
    }

    private List<OWLAxiom> getIndividualAxioms(OWLClassAssertionAxiom classMember, OWLModel owlModel) throws NeOnCoreException {
        Set<OWLAxiom> dependentsList = new LinkedHashSet<OWLAxiom>(owlModel.getReferencingAxioms(classMember.getIndividual()));
        dependentsList.remove(classMember); // remove same axiom
        return new ArrayList<OWLAxiom>(dependentsList);
    }
}
