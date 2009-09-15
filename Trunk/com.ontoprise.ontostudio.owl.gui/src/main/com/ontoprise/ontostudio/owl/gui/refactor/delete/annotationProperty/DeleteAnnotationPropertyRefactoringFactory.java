/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.delete.annotationProperty;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.DeleteRefactoring;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.refactor.IRefactoringFactory;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.wizard.RemoveAxiomWizard;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.util.wizard.WizardConstants;

public class DeleteAnnotationPropertyRefactoringFactory implements IRefactoringFactory {

    public ProcessorBasedRefactoring createRefactoring(Object... parameters) {
        DeleteAnnotationPropertyProcessor proc = new DeleteAnnotationPropertyProcessor((AnnotationPropertyTreeElement[]) parameters[0], (AnnotationPropertyTreeElement[]) parameters[1]);
        return new DeleteRefactoring(proc);
    }

    public RefactoringWizard createWizard(Refactoring refactoring) {
        AnnotationPropertyTreeElement[] elements = (AnnotationPropertyTreeElement[]) ((DeleteRefactoring) refactoring).getProcessor().getElements();
        List<OWLAxiom> axiomsToRemove = new ArrayList<OWLAxiom>();
        List<OWLEntity> entities = new ArrayList<OWLEntity>();
        OWLModel owlModel = null;
        try {
            for (AnnotationPropertyTreeElement element: elements) {
                String prop = element.getId();
                owlModel = OWLModelFactory.getOWLModel(element.getOntologyUri(), element.getProjectName());
                OWLAnnotationProperty property = owlModel.getOWLDataFactory().getOWLAnnotationProperty(OWLUtilities.toURI(prop));
                entities.add(property);
                axiomsToRemove.addAll(owlModel.getReferencingAxioms(property));

            }
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        return new RemoveAxiomWizard(refactoring, axiomsToRemove, entities, owlModel, "", "", WizardConstants.REMOVE_DEPENDENT_MODE); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
