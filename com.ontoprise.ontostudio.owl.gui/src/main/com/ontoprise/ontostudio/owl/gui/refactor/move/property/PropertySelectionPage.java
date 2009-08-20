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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.neontoolkit.gui.navigator.ComplexTreeViewer;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeExtensionHandler;
import org.neontoolkit.gui.navigator.MainTreeDataProvider;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;

/* 
 * Created on: 18.05.2006
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Refactor
 */
/**
 * Wizard page that displays a concept tree, so that the user can select the destination of the move refactoring.
 */

public abstract class PropertySelectionPage extends UserInputWizardPage {

    /**
	 * 
	 */
    public PropertySelectionPage(String name) {
        super(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        Composite result = new Composite(parent, SWT.NONE);
        setControl(result);
        result.setLayout(new GridLayout());

        createHeader(result);
        boolean showConceptTree = !isDestinationSet();
        if (showConceptTree) {
            Label label = new Label(result, SWT.NONE);
            label.setText(Messages.PropertySelectionPage_0);
            createTreeViewer(result);
        }
        createOptions(result);
        Dialog.applyDialogFont(result);
    }

    protected void createHeader(Composite composite) {
        //default implementation does nothing
    }

    public abstract void createOptions(Composite c);

    public abstract void verifyDestination(Object selected);

    public abstract boolean isDestinationSet();

    public abstract OntologyTreeElement getOntology();

    private void createTreeViewer(Composite c) {
        ComplexTreeViewer treeViewer = new ComplexTreeViewer(c, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = convertWidthInCharsToPixels(40);
        gd.heightHint = convertHeightInCharsToPixels(15);
        treeViewer.getTree().setLayoutData(gd);
        ITreeExtensionHandler handler = getExtensionHandler(treeViewer);
        MainTreeDataProvider prov = new MainTreeDataProvider(handler);
        prov.setRootProvider(getRootProvider(handler));
        treeViewer.setLabelProvider(prov);
        treeViewer.setContentProvider(prov);
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection sel = event.getSelection();
                if (sel instanceof IStructuredSelection) {
                    verifyDestination(((IStructuredSelection) sel).getFirstElement());
                }
            }
        });
        treeViewer.setInput(getOntology());
        treeViewer.expandToLevel(2);
    }

    protected abstract ITreeExtensionHandler getExtensionHandler(ComplexTreeViewer treeViewer);
    
    protected abstract ITreeDataProvider getRootProvider(ITreeExtensionHandler handler);

}
