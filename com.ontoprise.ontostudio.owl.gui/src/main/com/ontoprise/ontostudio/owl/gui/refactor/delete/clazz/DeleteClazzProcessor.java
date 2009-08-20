/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.delete.clazz;

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

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;

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

public class DeleteClazzProcessor extends DeleteProcessor {
    public static final String IDENTIFIER = "com.ontoprise.ontostudio.owl.gui.refactor.delete.clazz.DeleteClazzProcessor"; //$NON-NLS-1$

    private ClazzTreeElement[] _elements;
    private ClazzTreeElement[] _parents;

    public DeleteClazzProcessor(ClazzTreeElement[] elements, ClazzTreeElement[] parents) {
        _elements = elements;
        _parents = parents;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getElements()
     */
    @Override
    public Object[] getElements() {
        return _elements;
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
        return new ClazzDeleteChange(_elements, _parents);
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
