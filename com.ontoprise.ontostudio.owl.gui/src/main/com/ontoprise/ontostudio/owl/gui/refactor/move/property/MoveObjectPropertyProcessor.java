/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.move.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.MoveArguments;
import org.eclipse.ltk.core.refactoring.participants.MoveParticipant;
import org.eclipse.ltk.core.refactoring.participants.MoveProcessor;
import org.eclipse.ltk.core.refactoring.participants.ParticipantManager;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.neontoolkit.core.natures.OntologyProjectNature;

import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;

/* 
 * Created on: 18.05.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Refactor
 */
/**
 * Processor for the move concept refactoring.
 */

public class MoveObjectPropertyProcessor extends MoveProcessor {
    public static final String IDENTIFIER = "com.ontoprise.ontostudio.owl.gui.refactor.move.MovePropertyProcessor"; //$NON-NLS-1$
    public static final PropertyTreeElement ROOT = new ObjectPropertyTreeElement("root", "root", "root", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private PropertyTreeElement[] _elements;
    private PropertyTreeElement[] _parents;
    private PropertyTreeElement _destination;

    /**
	 * 
	 */
    public MoveObjectPropertyProcessor(PropertyTreeElement[] elements, PropertyTreeElement[] parents) {
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
        return "com.ontoprise.ontostudio.owl.gui.refactor.moveObjectProperty";  //$NON-NLS-1$
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
        return new PropertyMoveChange(_elements, _parents, getDestinationAsTreeElement());
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
        PropertyTreeElement destination = getDestinationAsTreeElement();
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

    public PropertyTreeElement getDestinationAsTreeElement() {
        return _destination != ROOT ? _destination : null;
    }

    public boolean hasDestinationSet() {
        return _destination != null;
    }

    public RefactoringStatus setDestination(PropertyTreeElement element) {
        _destination = element;
        return new RefactoringStatus();
    }

}
