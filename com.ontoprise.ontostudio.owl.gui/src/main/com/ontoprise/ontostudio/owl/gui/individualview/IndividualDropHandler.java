/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.individualview;

import java.util.ArrayList;

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
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.TreeProviderManager;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.refactor.GenericRefactoringExecutionStarter;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzHierarchyProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.individual.CreateIndividual;

/* 
 * Created on: 03.03.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Instance, Drop
 */
/**
 * Drop handler managing dragging and dropping of instances on concepts.
 */
public class IndividualDropHandler implements DropTargetListener {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.dnd.DropTargetListener#dragEnter(org.eclipse.swt.dnd.DropTargetEvent)
     */
    public void dragEnter(DropTargetEvent event) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.dnd.DropTargetListener#dragLeave(org.eclipse.swt.dnd.DropTargetEvent)
     */
    public void dragLeave(DropTargetEvent event) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.swt.dnd.DropTargetListener#dragOperationChanged(org.eclipse.swt.dnd.DropTargetEvent)
     */
    public void dragOperationChanged(DropTargetEvent event) {
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
    public void drop(DropTargetEvent event) {
        // elements have to be IndividualViewItems, as specified in the extension
        Object dropElem = event.data;

        if (dropElem instanceof IStructuredSelection && event.item instanceof TreeItem) {
            IStructuredSelection elems = (IStructuredSelection) dropElem;
            AbstractOntologyTreeElement target = (AbstractOntologyTreeElement) event.item.getData();
            if (target == null) {
                return;
            }
            // only for classes in one ontology
            String targetOntologyUri = target.getOntologyUri();
            Object[] items = elems.toArray();
            ArrayList<IndividualViewItem> individuals = new ArrayList<IndividualViewItem>();
            for (int i = 0; i < items.length; i++) {
                TreeItem item = (TreeItem) items[i];
                Object nextElem = item.getData();
                // Object nextElemParent = item.getParentItem() != null ? item.getParentItem().getData() : null;
                if (nextElem instanceof IndividualViewItem) {
                    IndividualViewItem elem = (IndividualViewItem) nextElem;
                    individuals.add(elem);
                    if (!targetOntologyUri.equals(elem.getOntologyUri())) {
                        Shell shell = new Shell();
                        MessageDialog.openInformation(shell, Messages.IndividualDropHandler_0, Messages.IndividualDropHandler_1);
                        return;
                    }
                }
            }

            if (event.detail == DND.DROP_COPY) {
                doCopy(individuals.toArray(new IndividualViewItem[0]), target);
            } else if (event.detail == DND.DROP_MOVE) {
                doMove(individuals.toArray(new IndividualViewItem[0]), target);
            } else {
                Shell shell = new Shell();
                MessageDialog.openInformation(shell, Messages.IndividualDropHandler_0, Messages.IndividualDropHandler_1);
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

    protected void doCopy(IndividualViewItem[] elem, Object target) {
        if (target instanceof ClazzTreeElement) {
            // instance is dropped on a concept
            ClazzTreeElement targetElem = (ClazzTreeElement) target;
            try {
                for (int i = 0; i < elem.length; i++) {
                    new CreateIndividual(targetElem.getProjectName(), targetElem.getOntologyUri(), targetElem.getId(), elem[i].getId()).perform();
                }

            } catch (CommandException e) {
                Shell shell = new Shell();
                MessageDialog.openInformation(shell, Messages.IndividualDropHandler_0, Messages.IndividualDropHandler_2);
            } catch (NeOnCoreException e) {
                Shell shell = new Shell();
                MessageDialog.openInformation(shell, Messages.IndividualDropHandler_0, Messages.IndividualDropHandler_2);
            }
        } else {
            Shell shell = new Shell();
            MessageDialog.openInformation(shell, Messages.IndividualDropHandler_0, Messages.IndividualDropHandler_5);
        }
    }

    protected void doMove(IndividualViewItem elem[], Object target) {
        if (target instanceof ClazzTreeElement) {
            try {
                ClazzTreeElement newParent = (ClazzTreeElement) target;

                ArrayList<ClazzTreeElement> oldParents = new ArrayList<ClazzTreeElement>();
                ArrayList<IndividualViewItem> movedInstances = new ArrayList<IndividualViewItem>();

                for (int i = 0; i < elem.length; i++) {
                    String clazzUri = elem[i].getClazz();
                    OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(elem[i].getProjectName());
                    OWLClass clazz = factory.getOWLClass(OWLUtilities.toURI(clazzUri));
                    ClazzTreeElement oldParent = new ClazzTreeElement(clazz, elem[i].getOntologyUri(), elem[i].getProjectName(), TreeProviderManager.getDefault().getProvider(MTreeView.ID, ClazzHierarchyProvider.class));
                    if (!oldParent.equals(newParent)) {
                        oldParents.add(oldParent);
                        movedInstances.add(elem[i]);
                    }
                }
                if (oldParents.size() > 0) {
                    GenericRefactoringExecutionStarter.startRefactoring(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "com.ontoprise.ontostudio.owl.gui.refactor.moveIndividual", //$NON-NLS-1$
                            movedInstances.toArray(new IndividualViewItem[0]),
                            oldParents.get(0),
                            newParent);
                }
            } catch (Exception e) {
                Shell shell = new Shell();
                MessageDialog.openInformation(shell, Messages.IndividualDropHandler_0, Messages.IndividualDropHandler_2);
            }
        }
    }
}
