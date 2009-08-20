/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.delete.dataProperty;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.RemoveDataProperty;

/**
 * Change that performs the removal of a class.
 */

public class DataPropertyDeleteChange extends Change {
    private DataPropertyTreeElement[] _props;
    private DataPropertyTreeElement[] _parents;
    private boolean _removeSubs;

    public DataPropertyDeleteChange(DataPropertyTreeElement[] clazzes, DataPropertyTreeElement[] parents, boolean removeSubs) {
        _props = clazzes;
        _parents = parents;
        _removeSubs = removeSubs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.Change#getName()
     */
    @Override
    public String getName() {
        return Messages.DataPropertyDeleteChange_0; 
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ltk.core.refactoring.Change#initializeValidationData(org.eclipse.core.runtime.IProgressMonitor)
     */
    @Override
    public void initializeValidationData(IProgressMonitor pm) {
        // TODO Auto-generated method stub

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
        if (_props == null || _props.length == 0) {
            return null;
        }
        int work = 1;

        pm.beginTask(Messages.DataPropertyDeleteChange_1, work); 
        try {
            for (int i = 0; i < _props.length; i++) {
                pm.subTask(Messages.DataPropertyDeleteChange_2 + _props[i].getLocalName()); 
                new RemoveDataProperty(_props[i].getProjectName(), _props[i].getOntologyUri(), _props[i].getId(), _parents[i] != null ? _parents[i].getId() : null, _removeSubs).run();
                pm.worked(1);
            }
        } catch (Exception e) {
            throw new CoreException(new Status(IStatus.ERROR, OWLPlugin.getDefault().getBundle().getSymbolicName(), IStatus.OK, e.getLocalizedMessage(), e));
        } finally {
            pm.done();
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
        return _props;
    }

}
