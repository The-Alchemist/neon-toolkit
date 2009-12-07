/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.refactor.move.clazz;

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
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.commands.clazz.MoveClazz;

/* 
 * Created on: 17.05.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Refactor
 *
 */
/**
 * Change that performs a concept move refactoring.
 */

public class ClazzMoveChange extends Change {

    private ClazzTreeElement[] _elements;
    private ClazzTreeElement[] _parents;
    private ClazzTreeElement _target;

    public ClazzMoveChange(ClazzTreeElement[] elements, ClazzTreeElement[] parents, ClazzTreeElement target) {
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
        String targetId = Messages.ClazzMoveChange_6; 
        if (_target != null) {
            targetId = _target.toString();
        }
        if (_elements.length > 1) {
            return Messages.ClazzMoveChange_0 + " " + targetId;  //$NON-NLS-1$
        } else {
            return Messages.ClazzMoveChange_2 + " " + _elements[0].toString() + " " //$NON-NLS-1$ //$NON-NLS-2$ 
                    + Messages.ClazzMoveChange_5 + " " + targetId;  //$NON-NLS-1$
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
    
        pm.beginTask(Messages.ClazzMoveChange_1, work); 
        try {
            for (int i = 0; i < _elements.length; i++) {
                pm.subTask(Messages.ClazzMoveChange_2 + " " + _elements[i].toString());  //$NON-NLS-1$
                String projectId = _elements[i].getProjectName();
                String ontologyUri = _elements[i].getOntologyUri();
                String id = _elements[i].getId();
                String parentId = _parents[i] != null ? _parents[i].getId() : null;
                String targetId = _target != null ? _target.getId() : null;
                new MoveClazz(projectId, ontologyUri, id, parentId, targetId, false).run();
                pm.worked(1);
            }
        } catch (Exception e) {
            throw new CoreException(new Status(IStatus.ERROR, OWLPlugin.getDefault().getBundle().getSymbolicName(), IStatus.OK, e.getLocalizedMessage(), e));
        } finally {
            pm.done();
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    MTreeView navigator = (MTreeView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MTreeView.ID);
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
