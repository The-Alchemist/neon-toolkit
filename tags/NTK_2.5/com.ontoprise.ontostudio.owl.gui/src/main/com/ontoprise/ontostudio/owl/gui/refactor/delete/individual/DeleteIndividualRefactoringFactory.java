/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.delete.individual;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.DeleteRefactoring;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.refactor.IRefactoringFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;

import com.ontoprise.ontostudio.owl.gui.individualview.IIndividualTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.wizard.RemoveAxiomWizard;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.util.wizard.WizardConstants;
/**
 * 
 * @author Nico Stieler
 */
public class DeleteIndividualRefactoringFactory implements IRefactoringFactory {

    public static final String DELETE_INDIVIDUAL_REFACTORING_ID = "com.ontoprise.ontostudio.owl.gui.refactor.deleteIndividual"; //$NON-NLS-1$
    
    @Override
    public ProcessorBasedRefactoring createRefactoring(Object... parameters) {
        DeleteIndividualProcessor proc = new DeleteIndividualProcessor((IIndividualTreeElement[]) parameters[0], (ClazzTreeElement) parameters[1]);
        return new DeleteRefactoring(proc);
    }
    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        IIndividualTreeElement[] elements = (IIndividualTreeElement[]) ((DeleteRefactoring) refactoring).getProcessor().getElements();
        List<OWLAxiom> axiomsToRemove = new ArrayList<OWLAxiom>();
        List<OWLEntity> entities = new ArrayList<OWLEntity>();
        OWLModel owlModel = null;
        try {
            for (IIndividualTreeElement element: elements) {
                String individualUri = element.getId();
                
                owlModel = OWLModelFactory.getOWLModel(element.getOntologyUri(), element.getProjectName());
//                OWLIndividual individual = OWLModelFactory.getOWLDataFactory(element.getProjectName()).getOWLNamedIndividual(OWLUtilities.toIRI(individualUri));
                
                OWLIndividual individual = 
                    OWLUtilities.individual(individualUri);

                //OWLAnonymousIndividual's are no entities and are not defined as entities. So only the axioms will be removed
                if (!entities.contains(individual) && individual instanceof OWLEntity) {
                    entities.add((OWLEntity)individual);
                }
                for (OWLAxiom a: owlModel.getReferencingAxioms(individual)) {
                    if (!axiomsToRemove.contains(a)) {
                        axiomsToRemove.add(a);
                    }
                }
            }
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        } 
        return new RemoveAxiomWizard(refactoring, axiomsToRemove, entities, owlModel, "", "", WizardConstants.ADD_DEPENDENT_MODE); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
