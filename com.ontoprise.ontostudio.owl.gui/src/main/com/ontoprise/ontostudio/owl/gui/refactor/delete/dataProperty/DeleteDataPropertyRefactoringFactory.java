/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.delete.dataProperty;

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
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.wizard.RemoveEntityWizard;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;

public class DeleteDataPropertyRefactoringFactory implements IRefactoringFactory {

    public ProcessorBasedRefactoring createRefactoring(Object... parameters) {
        DeleteDataPropertyProcessor proc = new DeleteDataPropertyProcessor((DataPropertyTreeElement[]) parameters[0], (DataPropertyTreeElement[]) parameters[1]);
        return new DeleteRefactoring(proc);
    }

    public RefactoringWizard createWizard(Refactoring refactoring) {
        DataPropertyTreeElement[] elements = (DataPropertyTreeElement[]) ((DeleteRefactoring) refactoring).getProcessor().getElements();
        List<OWLAxiom> axiomsToRemove = new ArrayList<OWLAxiom>();
        List<OWLEntity> entities = new ArrayList<OWLEntity>();
        OWLModel owlModel = null;
        try {
            for (DataPropertyTreeElement element: elements) {
                String prop = element.getId();
                owlModel = OWLModelFactory.getOWLModel(element.getOntologyUri(), element.getProjectName());
                OWLDataProperty property = OWLModelFactory.getOWLDataFactory(element.getProjectName()).getOWLDataProperty(OWLUtilities.toURI(prop));
                if (!entities.contains(property)) {
                    entities.add(property);
                }
                for (OWLAxiom a: owlModel.getReferencingAxioms(property)) {
                    if (!axiomsToRemove.contains(a)) {
                        axiomsToRemove.add(a);
                    }
                }

            }
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        return new RemoveEntityWizard(refactoring, axiomsToRemove, entities, owlModel);
    }

}
