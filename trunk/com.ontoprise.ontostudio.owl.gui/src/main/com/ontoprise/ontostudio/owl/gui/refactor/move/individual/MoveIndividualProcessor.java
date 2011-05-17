/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.move.individual;

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
import org.eclipse.ltk.core.refactoring.participants.MoveArguments;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.eclipse.ltk.core.refactoring.participants.MoveProcessor;
import org.eclipse.ltk.core.refactoring.participants.ParticipantManager;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.eclipse.swt.widgets.Shell;
import org.neontoolkit.core.natures.OntologyProjectNature;
import org.neontoolkit.gui.navigator.elements.IIndividualTreeElement;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;

/* 
 * Created on: 24.05.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Refactor
 */
/**
 * Processor for the instance move refactoring
 */

public class MoveIndividualProcessor extends MoveProcessor {
    public static final String IDENTIFIER = "com.ontoprise.ontostudio.gui.refactor.move.MoveInstanceProcessor"; //$NON-NLS-1$

    private IIndividualTreeElement[] _elements;
    private ClazzTreeElement _parent;
    private ClazzTreeElement _destination;

    /**
	 * 
	 */
    public MoveIndividualProcessor(IIndividualTreeElement[] elements, ClazzTreeElement parent) {
        _elements = elements;
        _parent = parent;
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
        return Messages.MoveIndividualProcessor_3;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#isApplicable()
     */
    @Override
    public boolean isApplicable() throws CoreException {
        if (_elements == null)
            return false;
        boolean applicable = true;
        for (int i = 0; i < _elements.length && applicable; i++) {
            if (!_elements[i].isDirect()) {
                applicable = false;
            }
        }
        if (!applicable) {
            MessageDialog.openInformation(new Shell(), Messages.MoveIndividualProcessor_4, Messages.MoveIndividualProcessor_5);
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
        return new IndividualMoveChange(_elements, _parent, _destination);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.participants.RefactoringProcessor#loadParticipants(org.eclipse.ltk.core.refactoring.RefactoringStatus,
     * org.eclipse.ltk.core.refactoring.participants.SharableParticipants)
     */
    @Override
    public RefactoringParticipant[] loadParticipants(RefactoringStatus status, SharableParticipants sharedParticipants) throws CoreException {

        List<MoveParticipant> result = new ArrayList<MoveParticipant>();
        ClazzTreeElement destination = getDestination();
        boolean updateReferences = true;
        String[] natures = new String[] {OntologyProjectNature.ID};
        if (destination != null) {
            Object[] elements = getElements();

            for (int i = 0; i < elements.length; i++) {
                result.addAll(Arrays.asList(ParticipantManager.loadMoveParticipants(status, this, elements[i], new MoveArguments(destination, updateReferences), natures, sharedParticipants)));
            }
        }
        return (RefactoringParticipant[]) result.toArray(new RefactoringParticipant[result.size()]);
    }

    public ClazzTreeElement getDestination() {
        return _destination;
    }

    public boolean hasDestinationSet() {
        return getDestination() != null;
    }

    public RefactoringStatus setDestination(ClazzTreeElement element) {
        _destination = element;
        return new RefactoringStatus();
    }

}
