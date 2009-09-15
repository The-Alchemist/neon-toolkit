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

import com.ontoprise.ontostudio.owl.gui.individualview.IndividualViewItem;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.wizard.RemoveAxiomWizard;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.util.wizard.WizardConstants;

public class DeleteIndividualRefactoringFactory implements IRefactoringFactory {

    public static final String DELETE_INDIVIDUAL_REFACTORING_ID = "com.ontoprise.ontostudio.owl.gui.refactor.deleteIndividual"; //$NON-NLS-1$
    
    public ProcessorBasedRefactoring createRefactoring(Object... parameters) {
        DeleteIndividualProcessor proc = new DeleteIndividualProcessor((IndividualViewItem[]) parameters[0], (ClazzTreeElement) parameters[1]);
        return new DeleteRefactoring(proc);
    }

    public RefactoringWizard createWizard(Refactoring refactoring) {
        IndividualViewItem[] elements = (IndividualViewItem[]) ((DeleteRefactoring) refactoring).getProcessor().getElements();
        List<OWLAxiom> axiomsToRemove = new ArrayList<OWLAxiom>();
        List<OWLEntity> entities = new ArrayList<OWLEntity>();
        OWLModel owlModel = null;
        try {
            for (IndividualViewItem element: elements) {
                String individualUri = element.getId();
                owlModel = OWLModelFactory.getOWLModel(element.getOntologyUri(), element.getProjectName());
                OWLIndividual individual = OWLModelFactory.getOWLDataFactory(element.getProjectName()).getOWLNamedIndividual(OWLUtilities.toURI(individualUri));
                if (!(individual instanceof OWLEntity)) {
                    // TODO: migration
                    throw new UnsupportedOperationException("TODO: migration"); //$NON-NLS-1$
                }
                if (!entities.contains(individual)) {
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
