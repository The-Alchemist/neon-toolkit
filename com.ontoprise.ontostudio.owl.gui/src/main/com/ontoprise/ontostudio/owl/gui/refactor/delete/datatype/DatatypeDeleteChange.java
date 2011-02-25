/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.delete.datatype;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.neontoolkit.core.util.IRIUtils;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.navigator.datatypes.DatatypeTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.DatatypeManager;
import com.ontoprise.ontostudio.owl.model.commands.datatypes.RemoveDatatype;

/**
 * Change that performs the removal of a Datatype.
 */

public class DatatypeDeleteChange extends Change {
    private DatatypeTreeElement[] _datatypes;

    public DatatypeDeleteChange(DatatypeTreeElement[] datatypes) {
        _datatypes = datatypes;
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
        if (_datatypes == null || _datatypes.length == 0) {
            return null;
        }
        int work = 1;

        pm.beginTask(Messages.DataPropertyDeleteChange_1, work); 
        try {
            for (int i = 0; i < _datatypes.length; i++) {
                pm.subTask(Messages.DataPropertyDeleteChange_2 + _datatypes[i].getLocalName()); 
                new RemoveDatatype(_datatypes[i].getProjectName(), _datatypes[i].getOntologyUri(), _datatypes[i].getId()).run();
                DatatypeManager.INSTANCE.unregisterDatatypeHandler(IRIUtils.ensureValidIdentifierSyntax(_datatypes[i].getId()));
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
        return _datatypes;
    }

}
