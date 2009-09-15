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

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.MoveRefactoring;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.neontoolkit.refactor.IRefactoringFactory;

import com.ontoprise.ontostudio.owl.gui.individualview.IndividualViewItem;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;

public class MoveIndividualRefactoringFactory implements IRefactoringFactory {

    public static final String MOVE_INDIVIDUAL_REFACTORING_ID = "com.ontoprise.ontostudio.owl.gui.refactor.moveIndividual"; //$NON-NLS-1$

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.refactor.IRefactoringFactory#createRefactoring(java.lang.Object[])
     */
    public ProcessorBasedRefactoring createRefactoring(Object... parameters) {
        Object parent = parameters[1];
        MoveIndividualProcessor processor = null;
        if (parent instanceof ClazzTreeElement) {
            processor = new MoveIndividualProcessor((IndividualViewItem[]) parameters[0], (ClazzTreeElement) parameters[1]);
        } else if (parent instanceof ClazzTreeElement[]) {
            ClazzTreeElement[] parents = (ClazzTreeElement[]) parent;
            processor = new MoveIndividualProcessor((IndividualViewItem[]) parameters[0], (ClazzTreeElement) parents[0]);
        }
        if (parameters.length > 2 && processor != null) {
            processor.setDestination((ClazzTreeElement) parameters[2]);
        }
        return new MoveRefactoring(processor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.refactor.IRefactoringFactory#createWizard(org.eclipse.ltk.core.refactoring.Refactoring)
     */
    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new MoveIndividualWizard(refactoring);
    }

}
