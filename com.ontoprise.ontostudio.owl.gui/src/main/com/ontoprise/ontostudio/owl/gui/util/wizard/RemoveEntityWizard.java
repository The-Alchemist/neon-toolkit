/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.wizard;

import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.util.wizard.WizardConstants;

public class RemoveEntityWizard extends RemoveAxiomWizard {

    private boolean _canFinish = false;

    public RemoveEntityWizard(Refactoring refactoring, List<OWLAxiom> axioms, List<OWLEntity> entities, OWLModel owlModel) {
        super(refactoring, axioms, entities, owlModel, "", "", WizardConstants.REMOVE_DEPENDENT_MODE); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if (page instanceof RemoveAxiomWizardPage0) {
            _page1.setDeleteMode(_page0.getDeleteMode());
            return _page1;
        } else if (page instanceof RemoveEntityWizardPage1) {
            _page2 = new RemoveAxiomWizardPage2(Messages.RemoveEntityWizard_0, _page1, _namespaces); 
            _page2.setAxiomsToRemove(_page1.getAxiomsToRemove());
            _page2.setWizard(this);
        }
        return _page2;
    }

    @Override
    protected void addUserInputPages() {
        _page0 = new RemoveAxiomWizardPage0(Messages.RemoveEntityWizard_1); 
        addPage(_page0);
        try {
	        _page1 = new RemoveEntityWizardPage1(Messages.RemoveEntityWizard_2, _axioms, _namespaces, _sourceEntityUri, _targetEntityUri, _deleteMode, _entities, _owlModel.getOntologyURI(), _owlModel.getProjectId()); 
	    } catch (NeOnCoreException e) {
	    	throw new RuntimeException(e);
	    }
        addPage(_page1);
        _page2 = new RemoveAxiomWizardPage2(Messages.RemoveEntityWizard_3, _page1, _namespaces); 
        addPage(_page2);
    }

    @Override
    public boolean canFinish() {
        return _canFinish && _page1.isPageComplete();
    }

    public void setCanFinish(boolean canFinish) {
        _canFinish = canFinish;
    }
}
