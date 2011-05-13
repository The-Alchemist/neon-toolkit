/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.move.property;

import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.participants.MoveProcessor;
import org.eclipse.ltk.core.refactoring.participants.MoveRefactoring;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.eclipse.ltk.ui.refactoring.RefactoringWizard;
import org.neontoolkit.refactor.IRefactoringFactory;

import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
/**
 * 
 * @author Nico Stieler
 */
public class MovePropertyRefactoringFactory implements IRefactoringFactory {

    @Override
    public ProcessorBasedRefactoring createRefactoring(Object... parameters) {
        MoveProcessor processor = null;
        if (parameters.length > 0 && parameters[0] instanceof PropertyTreeElement[] && ((PropertyTreeElement[])parameters[0]).length > 0) {
            if (((PropertyTreeElement[])parameters[0])[0] instanceof ObjectPropertyTreeElement) {
                processor = new MoveObjectPropertyProcessor((PropertyTreeElement[]) parameters[0], (PropertyTreeElement[]) parameters[1]);
                if (parameters.length == 3) {
                    if (parameters[2] == null) {
                        ((MoveObjectPropertyProcessor) processor).setDestination(MoveObjectPropertyProcessor.ROOT);
                    } else {
                        ((MoveObjectPropertyProcessor) processor).setDestination((PropertyTreeElement) parameters[2]);
                    }
                }
            } else if (((PropertyTreeElement[])parameters[0])[0] instanceof DataPropertyTreeElement) {
                processor = new MoveDataPropertyProcessor((PropertyTreeElement[]) parameters[0], (PropertyTreeElement[]) parameters[1]);
                if (parameters.length == 3) {
                    if (parameters[2] == null) {
                        ((MoveDataPropertyProcessor) processor).setDestination(MoveDataPropertyProcessor.ROOT);
                    } else {
                        ((MoveDataPropertyProcessor) processor).setDestination((PropertyTreeElement) parameters[2]);
                    }
                }
            } else if (((PropertyTreeElement[])parameters[0])[0] instanceof AnnotationPropertyTreeElement) {
                processor = new MoveAnnotationPropertyProcessor((PropertyTreeElement[]) parameters[0], (PropertyTreeElement[]) parameters[1]);
                if (parameters.length == 3) {
                    if (parameters[2] == null) {
                        ((MoveAnnotationPropertyProcessor) processor).setDestination(MoveAnnotationPropertyProcessor.ROOT);
                    } else {
                        ((MoveAnnotationPropertyProcessor) processor).setDestination((PropertyTreeElement) parameters[2]);
                    }
                }
            }
        }
        return processor != null ? new MoveRefactoring(processor) : null;
    }

    @Override
    public RefactoringWizard createWizard(Refactoring refactoring) {
        if (refactoring.getName().equals("com.ontoprise.ontostudio.owl.gui.refactor.moveDataProperty")) { //$NON-NLS-1$
            return new MoveDataPropertyWizard(refactoring);
        } else if (refactoring.getName().equals("com.ontoprise.ontostudio.owl.gui.refactor.moveObjectProperty")) { //$NON-NLS-1$
            return new MoveObjectPropertyWizard(refactoring);
        } else if (refactoring.getName().equals("com.ontoprise.ontostudio.owl.gui.refactor.moveAnnotationProperty")) { //$NON-NLS-1$
            return new MoveAnnotationPropertyWizard(refactoring);
        }
        return new MoveObjectPropertyWizard(refactoring);
    }

}
