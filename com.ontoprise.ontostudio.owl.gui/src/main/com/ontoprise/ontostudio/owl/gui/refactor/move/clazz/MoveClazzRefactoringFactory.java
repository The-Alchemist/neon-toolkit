/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.move.clazz;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.MoveRefactoring;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.neontoolkit.refactor.IRefactoringFactory;

import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;

public class MoveClazzRefactoringFactory implements IRefactoringFactory {

    public ProcessorBasedRefactoring createRefactoring(Object... parameters) {
        MoveClazzProcessor processor = new MoveClazzProcessor((ClazzTreeElement[]) parameters[0], (ClazzTreeElement[]) parameters[1]);
        if (parameters.length == 3) {
            if (parameters[2] == null) {
                processor.setDestination(MoveClazzProcessor.ROOT);
            } else {
                processor.setDestination((ClazzTreeElement) parameters[2]);
            }
        }
        return new MoveRefactoring(processor);
    }

    public RefactoringWizard createWizard(Refactoring refactoring) {
        return new MoveClazzWizard(refactoring);
    }

}
