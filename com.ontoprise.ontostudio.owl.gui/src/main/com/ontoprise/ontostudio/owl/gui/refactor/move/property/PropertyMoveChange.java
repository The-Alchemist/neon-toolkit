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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.gui.navigator.MTreeView;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.navigator.property.PropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.commands.MoveProperty;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.MoveAnnotationProperty;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.MoveDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.MoveObjectProperty;

/* 
 * Created on: 17.05.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Refactor
 *
 */
/**
 * Change that performs a property move refactoring.
 */

public class PropertyMoveChange extends Change {

    private PropertyTreeElement[] _elements;
    private PropertyTreeElement[] _parents;
    private PropertyTreeElement _target;

    public PropertyMoveChange(PropertyTreeElement[] elements, PropertyTreeElement[] parents, PropertyTreeElement target) {
        _elements = elements;
        _parents = parents;
        _target = target;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.Change#getName()
     */
    @Override
    public String getName() {
        String targetId = Messages.PropertyMoveChange_9; 
        if (_target != null) {
            targetId = _target.toString();
        }
        if (_elements.length > 1) {
            return Messages.PropertyMoveChange_0 + targetId; 
        } else {
            return Messages.PropertyMoveChange_2 + " " + _elements[0].toString() + " " //$NON-NLS-1$ //$NON-NLS-2$ 
                    + Messages.PropertyMoveChange_5 + " " + targetId;  //$NON-NLS-1$
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.Change#initializeValidationData(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void initializeValidationData(IProgressMonitor pm) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.Change#isValid(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException,OperationCanceledException {
        return new RefactoringStatus();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.Change#perform(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public Change perform(IProgressMonitor pm) throws CoreException {
        if (_elements.length == 0) {
            return null;
        }
        int work = _elements.length;

        pm.beginTask(Messages.PropertyMoveChange_1, work); 
        try {
            for (int i = 0; i < _elements.length; i++) {
                pm.subTask(Messages.PropertyMoveChange_2 + " " + _elements[i].getLocalName());  //$NON-NLS-1$
                MoveProperty command = null;
                String projectId = _elements[i].getProjectName();
                String ontologyUri = _elements[i].getOntologyUri();
                String id = _elements[i].getId();
                String parentId = _parents[i] != null ? _parents[i].getId() : null;
                String targetId = _target != null ? _target.getId() : null;
                if (_elements[i] instanceof AnnotationPropertyTreeElement) {
                    // Annotation Property
                    command = new MoveAnnotationProperty(projectId, ontologyUri, id, parentId, targetId);
                } else if (_elements[i] instanceof DataPropertyTreeElement) {
                    // Data Property
                    command = new MoveDataProperty(projectId, ontologyUri, id, parentId, targetId);
                } else if (_elements[i] instanceof ObjectPropertyTreeElement) {
                    // Object Property
                    command = new MoveObjectProperty(projectId, ontologyUri, id, parentId, targetId);
                }
                if (command != null) {
                    command.run();
                }
                pm.worked(1);
            }
        } catch (Exception e) {
            throw new CoreException(new Status(IStatus.ERROR, OWLPlugin.getDefault().getBundle().getSymbolicName(), IStatus.OK, e.getLocalizedMessage(), e));
        } finally {
            pm.done();
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    MTreeView navigator = (MTreeView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MTreeView.ID);  // getActivWorkbenchWindom is null (called by non GUI Thread)
                    OWLGUIUtilities.doJumpToEntity(_elements[0], navigator);     
                }
            }
            );
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.Change#getModifiedElement()
     */
    @Override
    public Object getModifiedElement() {
        return _target;
    }
}
