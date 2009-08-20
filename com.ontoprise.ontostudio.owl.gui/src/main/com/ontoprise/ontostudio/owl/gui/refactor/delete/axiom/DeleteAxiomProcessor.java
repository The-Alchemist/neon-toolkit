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
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.DeleteArguments;
import org.eclipse.ltk.core.refactoring.participants.DeleteParticipant;
import org.eclipse.ltk.core.refactoring.participants.DeleteProcessor;
import org.eclipse.ltk.core.refactoring.participants.ParticipantManager;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.neontoolkit.core.natures.OntologyProjectNature;
import org.semanticweb.owlapi.model.OWLAxiom;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;

/* 
 * Created on: 30.05.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Refactor
 *
 */
/**
 * DeleteProcessor for the removal of a concept
 */

public class DeleteAxiomProcessor extends DeleteProcessor {
    public static final String IDENTIFIER = "com.ontoprise.ontostudio.owl.gui.refactor.delete.axiom.DeleteAxiomProcessor"; //$NON-NLS-1$

    private List<OWLAxiom> _axiomsToDelete;
    private String _description;
    private OWLNamespaces _namespaces;
    private String _id;
    private OWLModel _owlModel;

    public DeleteAxiomProcessor(List<OWLAxiom> axioms, String description, OWLNamespaces namespaces, String id, OWLModel owlModel) {
        _axiomsToDelete = axioms;
        _description = description;
        _namespaces = namespaces;
        _id = id;
        _owlModel = owlModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getElements()
     */
    @Override
    public Object[] getElements() {
        return new Object[] {_axiomsToDelete};
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getIdentifier()
     */
    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getProcessorName()
     */
    @Override
    public String getProcessorName() {
        return Messages.DeleteClazzProcessor_5; 
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#isApplicable()
     */
    @Override
    public boolean isApplicable() throws CoreException {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#checkInitialConditions(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public RefactoringStatus checkInitialConditions(IProgressMonitor pm) throws CoreException,OperationCanceledException {
        return new RefactoringStatus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#checkFinalConditions(org.eclipse.core.runtime.IProgressMonitor,
     * org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext)
     */
    @Override
    public RefactoringStatus checkFinalConditions(IProgressMonitor pm, CheckConditionsContext context) throws CoreException,OperationCanceledException {
        return new RefactoringStatus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#createChange(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public Change createChange(IProgressMonitor pm) throws CoreException,OperationCanceledException {
        return new AxiomDeleteChange(_axiomsToDelete, _description, _namespaces, _id, _owlModel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#loadParticipants(org.eclipse.ltk.core.refactoring.RefactoringStatus,
     * org.eclipse.ltk.core.refactoring.participants.SharableParticipants)
     */
    @Override
    public RefactoringParticipant[] loadParticipants(RefactoringStatus status, SharableParticipants sharedParticipants) throws CoreException {

        List<DeleteParticipant> result = new ArrayList<DeleteParticipant>();
        String[] natures = new String[] {OntologyProjectNature.ID};
        Object[] elements = getElements();

        for (int i = 0; i < elements.length; i++) {
            result.addAll(Arrays.asList(ParticipantManager.loadDeleteParticipants(status, this, elements[i], new DeleteArguments(), natures, sharedParticipants)));
        }
        return (RefactoringParticipant[]) result.toArray(new RefactoringParticipant[result.size()]);
    }

}
