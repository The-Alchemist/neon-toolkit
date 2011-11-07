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
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.util.wizard.WizardConstants;
/**
 * 
 * @author Nico Stieler
 */
public class RemoveAxiomWizard extends RefactoringWizard {

    public static final String DELETE_ENTITY = Messages.RemoveAxiomWizard_0;
    public static final String DELETE_SUBTREE = Messages.RemoveAxiomWizard_1;

    protected String _sourceEntityUri;
    protected String _targetEntityUri;
    protected List<OWLAxiom> _axioms;
    protected List<OWLEntity> _entities;
    protected List<OWLAxiom> _axiomsToDelete;
    protected List<OWLAxiom> _dependentAxioms;

    protected OWLModel _owlModel;
    protected OWLNamespaces _namespaces;

    protected boolean _classMode = false;
    protected int _mode;
    protected String _deleteMode = DELETE_ENTITY;

    protected RemoveAxiomWizardPage0 _page0;
    protected RemoveAxiomWizardPage1 _page1;
    protected RemoveAxiomWizardPage2 _page2;

    public RemoveAxiomWizard(Refactoring refactoring, List<OWLAxiom> axioms, List<OWLEntity> entities, OWLModel owlModel, String sourceEntityUri, String targetEntityString, int mode) {
        super(refactoring, WIZARD_BASED_USER_INTERFACE);
        setDefaultPageTitle(Messages.RemoveAxiomWizard_2);
        _sourceEntityUri = sourceEntityUri;
        _targetEntityUri = targetEntityString;
        _axioms = axioms;
        _entities = entities;
        _owlModel = owlModel;
        try {
            _namespaces = owlModel.getNamespaces();
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
        _mode = mode;
        // all entities should be of the same kind, so it' s sufficient to check the first one
        if (entities.size() > 0 && entities.get(0) instanceof OWLClass) {
            _classMode = true;
        }
    }

    @Override
    public void createPageControls(Composite pageContainer) {
        // do nothing - this leads to wizard pages being created
        // only when they are about to be shown.
    }

    @Override
    public boolean performFinish() {
        _axiomsToDelete = _page1.getAxiomsToRemove();
        _dependentAxioms = _page2.getDependentAxioms();

        if (_axiomsToDelete == null && _dependentAxioms == null) {
            return false; // cancel pressed
        }

        final List<String> addChanges = new ArrayList<String>();
        final List<String> removeChanges = new ArrayList<String>();
        for (OWLAxiom axiom: _axiomsToDelete) {
            removeChanges.add(OWLUtilities.toString(axiom));
        }
        for (OWLAxiom axiom: _dependentAxioms) {
            if (_mode == WizardConstants.ADD_DEPENDENT_MODE) {
                addChanges.add(OWLUtilities.toString(axiom));
            } else {
                removeChanges.add(OWLUtilities.toString(axiom));
            }
        }

        BusyIndicator.showWhile(null, new Runnable() {

            @Override
            public void run() {
                try {
                    new ApplyChanges(_owlModel.getProjectId(), _owlModel.getOntologyURI(), addChanges.toArray(new String[addChanges.size()]), removeChanges.toArray(new String[removeChanges.size()])).run();
                } catch (NeOnCoreException e) {
                    new NeonToolkitExceptionHandler().handleException(e);
                } catch (CommandException e) {
                    new NeonToolkitExceptionHandler().handleException(e);
                }
            }
        });
        return true;
    }

    @Override
    public boolean canFinish() {
        return _page2.canFinish();
    }

    @Override
    public IWizardPage getNextPage(IWizardPage page) {
        if (page instanceof RemoveAxiomWizardPage0) {
            _page1.setDeleteMode(_deleteMode);
            return _page1;
        } else if (page instanceof RemoveAxiomWizardPage1) {
            _page2 = new RemoveAxiomWizardPage2(Messages.RemoveAxiomWizard_3, _page1, _namespaces);
            _page2.setAxiomsToRemove(_page1.getAxiomsToRemove());
            _page2.setWizard(this);
        }
        return _page2;
    }

    @Override
    protected void addUserInputPages() {
        try {
        _page1 = new RemoveAxiomWizardPage1(Messages.RemoveAxiomWizard_4, _axioms, _namespaces, _sourceEntityUri, _targetEntityUri, _deleteMode, _entities, _owlModel.getOntologyURI(), _owlModel.getProjectId());
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
        addPage(_page1);
        _page2 = new RemoveAxiomWizardPage2(Messages.RemoveAxiomWizard_5, _page1, _namespaces);
        addPage(_page2);
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
    }

    public List<OWLAxiom> getAxiomsToDelete() {
        return _axiomsToDelete;
    }

    public List<OWLAxiom> getDependentAxioms() {
        return _dependentAxioms;
    }

    public List<OWLEntity> getEntities() {
        return _entities;
    }

    public OWLModel getOwlModel() {
        return _owlModel;
    }

    public void setDeleteMode(String deleteMode) {
        _deleteMode = deleteMode;
    }

}
