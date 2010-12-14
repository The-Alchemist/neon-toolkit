/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.io.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

import com.ontoprise.ontostudio.owl.gui.io.WebImportOntologyWizard;

/**
 * Created on: 21.09.2009
 * Created by: michael
 *
 */
public class LoadOntologyFromWebHandler extends AbstractHandler{

    
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        // Grab the selection out of the tree and convert it to a
        // StructuredSelection for use by the wizard.
        StructuredSelection currentSelection = null;
        
        
        // get the wizard from the child class.
        IWorkbenchWizard wizard = constructWizard();

        // Get the workbench and initialize, the wizard.
        IWorkbench workbench = PlatformUI.getWorkbench();
        wizard.init(workbench, currentSelection);

        // Open the wizard dialog with the given wizard.
        WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
        dialog.open();
        
        
        return null;
    }

    private IWorkbenchWizard constructWizard() {
        return new WebImportOntologyWizard();
    }


}
