/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.delete.axiom;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.DeleteRefactoring;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.neontoolkit.refactor.IRefactoringFactory;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.util.wizard.RemoveAxiomWizard;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.util.wizard.WizardConstants;

public class DeleteAxiomRefactoringFactory implements IRefactoringFactory {

    private OWLModel _owlModel;
    private String _sourceUri;
    private String _targetUri;

    @SuppressWarnings("unchecked")
    public ProcessorBasedRefactoring createRefactoring(Object... parameters) {

        _owlModel = (OWLModel) parameters[4];
        _sourceUri = (String) parameters[1];
        _targetUri = (String) parameters[3];

        DeleteAxiomProcessor proc = new DeleteAxiomProcessor((List<OWLAxiom>) parameters[0], // axioms
                (String) parameters[1], // description
                (OWLNamespaces) parameters[2], // namespaces
                (String) parameters[3], // id
                (OWLModel) parameters[4] // owlModel
        );
        return new DeleteRefactoring(proc);
    }

    @SuppressWarnings("unchecked")
    public RefactoringWizard createWizard(Refactoring refactoring) {
        Object[] elements = ((DeleteRefactoring) refactoring).getProcessor().getElements();
        List<OWLAxiom> axiomsToRemove = (List<OWLAxiom>) elements[0];

        return new RemoveAxiomWizard(refactoring, axiomsToRemove, new ArrayList<OWLEntity>(), _owlModel, _sourceUri, _targetUri, WizardConstants.ADD_DEPENDENT_MODE);
    }
}
