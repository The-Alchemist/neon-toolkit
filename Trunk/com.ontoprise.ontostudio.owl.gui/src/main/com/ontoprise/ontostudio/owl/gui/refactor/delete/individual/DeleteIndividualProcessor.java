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
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.DeleteArguments;
import org.eclipse.ltk.core.refactoring.participants.DeleteParticipant;
import org.eclipse.ltk.core.refactoring.participants.DeleteProcessor;
import org.eclipse.ltk.core.refactoring.participants.ParticipantManager;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.swt.widgets.Shell;
import org.neontoolkit.core.natures.OntologyProjectNature;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.individualview.IndividualViewItem;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;

/* 
 * Created on: 01.06.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Refactor
 *
 */
/**
 * DeleteProcessor for the removal of instances
 */

public class DeleteIndividualProcessor extends DeleteProcessor {
    public static final String IDENTIFIER = "com.ontoprise.ontostudio.owl.gui.refactor.delete.DeleteIndividualProcessor"; //$NON-NLS-1$
    public static final String OPTION_REMOVE_REFERRING_INSTANCE_PROPERTIES = "DeleteIndividualProcessor.removeReferringInstanceProperties"; //$NON-NLS-1$

    private IndividualViewItem[] _individuals;
    private ClazzTreeElement _clazz;

    public DeleteIndividualProcessor(IndividualViewItem[] instances, ClazzTreeElement clazz) {
        _individuals = instances;
        _clazz = clazz;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#getElements()
     */
    @Override
    public Object[] getElements() {
        return _individuals;
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
        return Messages.DeleteIndividualProcessor_2; 
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#isApplicable()
     */
    @Override
    public boolean isApplicable() throws CoreException {
        if (_individuals == null)
            return false;
        boolean applicable = true;
        for (int i = 0; i < _individuals.length && applicable; i++) {
            if (!_individuals[i].isDirect()) {
                applicable = false;
            }
        }
        if (!applicable) {
            MessageDialog.openInformation(new Shell(), Messages.DeleteIndividualProcessor_3, Messages.DeleteIndividualProcessor_4);  
        }
        return applicable;
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
        return new IndividualDeleteChange(_individuals, _clazz);
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
