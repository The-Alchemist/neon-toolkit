/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.refactor.GenericRefactoringExecutionStarter;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.navigator.property.annotationProperty.AnnotationPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.dataProperty.DataPropertyTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.CreateAnnotationProperty;
import com.ontoprise.ontostudio.owl.model.commands.dataproperties.CreateDataProperty;
import com.ontoprise.ontostudio.owl.model.commands.objectproperties.CreateObjectProperty;

/* 
 * Created on: 31.03.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Navigator, Drop
 */
/**
 * Drop handler for drag and drop of concepts.
 */
public class PropertyDropHandler implements DropTargetListener {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.dnd.DropTargetListener#dragEnter(org.eclipse.swt.dnd.DropTargetEvent)
     */
    public void dragEnter(DropTargetEvent event) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.dnd.DropTargetListener#dragLeave(org.eclipse.swt.dnd.DropTargetEvent)
     */
    public void dragLeave(DropTargetEvent event) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.dnd.DropTargetListener#dragOperationChanged(org.eclipse.swt.dnd.DropTargetEvent)
     */
    public void dragOperationChanged(DropTargetEvent event) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.dnd.DropTargetListener#dragOver(org.eclipse.swt.dnd.DropTargetEvent)
     */
    public void dragOver(DropTargetEvent event) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.dnd.DropTargetListener#drop(org.eclipse.swt.dnd.DropTargetEvent)
     */
    @SuppressWarnings("unchecked")
    public void drop(DropTargetEvent event) {
        // elements have to be PropertyTreeElements, as specified in the extension
        Object dropElem = event.data;

        if (dropElem instanceof IStructuredSelection && event.item instanceof TreeItem) {
            IStructuredSelection elems = (IStructuredSelection) dropElem;
            IOntologyElement target = (IOntologyElement) event.item.getData();
            if (target == null) {
                return;
            }
            // only for properties in one ontology
            String targetModuleId = target.getOntologyUri();

            Map<PropertyTreeElement,Object> properties = new HashMap<PropertyTreeElement,Object>();
            List<IStatus> errors = new ArrayList<IStatus>();

            for (Iterator i = elems.iterator(); i.hasNext();) {
                TreeItem item = (TreeItem) i.next();
                Object nextElem = item.getData();
                Object nextElemParent = item.getParentItem() != null ? item.getParentItem().getData() : null;
                if (nextElem instanceof PropertyTreeElement) {
                    PropertyTreeElement elem = (PropertyTreeElement) nextElem;
                    if (!targetModuleId.equals(elem.getOntologyUri())) {
                        IStatus errorStatus = new Status(IStatus.ERROR, OWLPlugin.getDefault().getBundle().getSymbolicName(), IStatus.OK, elem.getId() + " : " + Messages.PropertyDropHandler_1, //$NON-NLS-1$ 
                                null);
                        errors.add(errorStatus);
                    } else {
                        properties.put(elem, nextElemParent);
                    }
                }
            }

            if (event.detail == DND.DROP_COPY) {
                errors.addAll(doCopy(properties.keySet().toArray(new PropertyTreeElement[0]), target));
            } else if (event.detail == DND.DROP_MOVE) {
                PropertyTreeElement[] elements = properties.keySet().toArray(new PropertyTreeElement[0]);
                PropertyTreeElement[] parentElement = new PropertyTreeElement[elements.length];
                PropertyTreeElement targetProperty = target instanceof PropertyTreeElement ? (PropertyTreeElement) target : null;

                for (int j = 0; j < elements.length; j++) {
                    Object parent = properties.get(elements[j]);
                    parentElement[j] = parent instanceof PropertyTreeElement ? (PropertyTreeElement) parent : null;
                }
                errors.addAll(doMove(elements, targetProperty, parentElement));
            }
            if (errors.size() > 0) {
                IStatus status;
                String errorMessage;
                if (errors.size() == 1) {
                    status = errors.get(0);
                    errorMessage = Messages.PropertyDropHandler_10;
                } else {
                    status = new MultiStatus(OWLPlugin.getDefault().getBundle().getSymbolicName(), IStatus.ERROR, errors.toArray(new IStatus[0]), "", //$NON-NLS-1$
                            null);
                    errorMessage = Messages.PropertyDropHandler_3;
                }
                new NeonToolkitExceptionHandler().showStatusDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), status, Messages.PropertyDropHandler_4, errorMessage);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.dnd.DropTargetListener#dropAccept(org.eclipse.swt.dnd.DropTargetEvent)
     */
    public void dropAccept(DropTargetEvent event) {
        // TODO Auto-generated method stub

    }

    protected List<IStatus> doCopy(PropertyTreeElement[] elems, Object target) {
        List<IStatus> errors = new ArrayList<IStatus>();
        for (int i = 0; i < elems.length; i++) {
            if (target instanceof PropertyTreeElement) {
                // add a subproperty
                try {
                    PropertyTreeElement targetElem;
                    if (elems[i] instanceof AnnotationPropertyTreeElement) {
                        targetElem = (AnnotationPropertyTreeElement) target;
                        new CreateAnnotationProperty(targetElem.getProjectName(), targetElem.getOntologyUri(), elems[i].getId(), targetElem.getId()).run();
                    } else if (elems[i] instanceof DataPropertyTreeElement) {
                        targetElem = (DataPropertyTreeElement) target;
                        new CreateDataProperty(targetElem.getProjectName(), targetElem.getOntologyUri(), elems[i].getId(), targetElem.getId()).run();
                    } else if (elems[i] instanceof ObjectPropertyTreeElement) {
                        targetElem = (ObjectPropertyTreeElement) target;
                        new CreateObjectProperty(targetElem.getProjectName(), targetElem.getOntologyUri(), elems[i].getId(), targetElem.getId()).run();
                    }
                } catch (CommandException e) {
                    Shell shell = new Shell();
                    MessageDialog.openInformation(shell, Messages.PropertyDropHandler_0, Messages.PropertyDropHandler_2);
                } catch (NeOnCoreException e) {
                    Shell shell = new Shell();
                    MessageDialog.openInformation(shell, Messages.PropertyDropHandler_0, Messages.PropertyDropHandler_2);
                }
            } else {
                IStatus errorStatus = new Status(IStatus.ERROR, OWLPlugin.getDefault().getBundle().getSymbolicName(), IStatus.OK, elems[i].getId() + " : " + Messages.PropertyDropHandler_5, //$NON-NLS-1$ 
                        null);
                errors.add(errorStatus);
            }
        }
        return errors;
    }

    protected List<IStatus> doMove(PropertyTreeElement[] elems, Object target, PropertyTreeElement[] parents) {
        List<IStatus> errors = new ArrayList<IStatus>();
        try {
            if (elems.length > 0 && elems[0] instanceof ObjectPropertyTreeElement) {
                GenericRefactoringExecutionStarter.startRefactoring(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "com.ontoprise.ontostudio.owl.gui.refactor.moveObjectProperty", //$NON-NLS-1$
                        elems,
                        parents,
                        target);
            } else if (elems.length > 0 && elems[0] instanceof DataPropertyTreeElement) {
                GenericRefactoringExecutionStarter.startRefactoring(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "com.ontoprise.ontostudio.owl.gui.refactor.moveDataProperty", //$NON-NLS-1$
                        elems,
                        parents,
                        target);
            }

        } catch (CoreException e) {
            IStatus errorStatus = new Status(IStatus.ERROR, OWLPlugin.getDefault().getBundle().getSymbolicName(), IStatus.OK, Messages.PropertyDropHandler_2, null);
            errors.add(errorStatus);
        }
        return errors;
    }
}
