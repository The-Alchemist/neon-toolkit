/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.delete.datatype;

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
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.navigator.datatypes.DatatypeTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.wizard.RemoveAxiomWizard;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.util.wizard.WizardConstants;

public class DeleteDatatypeRefactoringFactory implements IRefactoringFactory {

	public ProcessorBasedRefactoring createRefactoring(Object... parameters) {
		DeleteDatatypeProcessor proc = new DeleteDatatypeProcessor(
				(DatatypeTreeElement[])parameters[0]);
		return new DeleteRefactoring(proc);
	}

	public RefactoringWizard createWizard(Refactoring refactoring) {
		DatatypeTreeElement[] elements = (DatatypeTreeElement[]) ((DeleteRefactoring)refactoring).getProcessor().getElements();
		List<OWLAxiom> axiomsToRemove = new ArrayList<OWLAxiom>();
		List<OWLEntity> entities = new ArrayList<OWLEntity>();
		OWLModel owlModel = null;
        try {
			for(DatatypeTreeElement element: elements) {
				String datattypeUri = element.getId();
				owlModel = OWLModelFactory.getOWLModel(element.getOntologyUri(), element.getProjectName());
	            OWLDatatype datatype = owlModel.getOWLDataFactory().getOWLDatatype(OWLUtilities.toURI(datattypeUri));
	            if (!entities.contains(datatype)) {
	            	entities.add(datatype);
	            }
	            for (OWLAxiom a: owlModel.getReferencingAxioms(datatype)) {
	            	if (!axiomsToRemove.contains(a)) {
	            		axiomsToRemove.add(a);
		            }
				}
	            
			}
		} catch (NeOnCoreException e) {
			new NeonToolkitExceptionHandler().handleException(e);
		}
		return new RemoveAxiomWizard(refactoring, axiomsToRemove, entities,
				owlModel, "", "", WizardConstants.REMOVE_DEPENDENT_MODE); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
