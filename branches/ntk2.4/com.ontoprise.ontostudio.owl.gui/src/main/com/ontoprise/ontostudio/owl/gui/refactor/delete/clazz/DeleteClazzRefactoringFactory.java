/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.delete.clazz;

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
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.wizard.RemoveClazzWizard;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;

public class DeleteClazzRefactoringFactory implements IRefactoringFactory {

    public ProcessorBasedRefactoring createRefactoring(Object... parameters) {
        DeleteClazzProcessor proc = new DeleteClazzProcessor((ClazzTreeElement[]) parameters[0], (ClazzTreeElement[]) parameters[1]);
        return new DeleteRefactoring(proc);
    }

    public RefactoringWizard createWizard(Refactoring refactoring) {
        ClazzTreeElement[] elements = (ClazzTreeElement[]) ((DeleteRefactoring) refactoring).getProcessor().getElements();
        List<OWLAxiom> axiomsToRemove = new ArrayList<OWLAxiom>();
        List<OWLEntity> entities = new ArrayList<OWLEntity>();
        OWLModel owlModel = null;

        try {
            for (ClazzTreeElement element: elements) {
                String subClazzId = element.getId();
                owlModel = OWLModelFactory.getOWLModel(element.getOntologyUri(), element.getProjectName());
                OWLClass subClazz = OWLModelFactory.getOWLDataFactory(element.getProjectName()).getOWLClass(OWLUtilities.toIRI(subClazzId));
                if (!entities.contains(subClazz)) {
                    entities.add(subClazz);
                }
                for (OWLAxiom a: owlModel.getReferencingAxioms(subClazz)) {
                    if (!axiomsToRemove.contains(a)) {
                        axiomsToRemove.add(a);
                    }
                }
            }
        } catch (NeOnCoreException k2e) {
            new NeonToolkitExceptionHandler().handleException(k2e);
        }
        return new RemoveClazzWizard(refactoring, axiomsToRemove, entities, owlModel);
    }
}
