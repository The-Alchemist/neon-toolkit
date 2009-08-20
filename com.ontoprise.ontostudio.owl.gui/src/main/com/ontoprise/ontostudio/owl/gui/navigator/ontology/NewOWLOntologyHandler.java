/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator.ontology;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.navigator.MTreeView;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.navigator.project.OWLProjectTreeElement;
import com.ontoprise.ontostudio.owl.gui.wizard.NewOntologyWizard;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;

/* 
 * Created on: 01.07.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Action
 */
/**
 * Action to create a new module in the tree
 */

public class NewOWLOntologyHandler extends AbstractHandler {

    /* (non-Javadoc)
     * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        MTreeView view = HandlerUtil.getActivePart(arg0) instanceof MTreeView ? 
                (MTreeView)HandlerUtil.getActivePart(arg0) : null;

        NewOntologyWizard wizard = null;

        try {
            IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            Shell shell = null;
            if (window != null) {
                shell = window.getShell();
            } else {
                shell = new Shell();
            }
            IStructuredSelection selection = null;
            if (view != null) {
                selection = (IStructuredSelection) view.getTreeViewer().getSelection();
            }
            if (canRun(selection)) {
                IExtension extension = Platform.getExtensionRegistry().getExtension(getPrefix());
                IConfigurationElement[] confElements = extension.getConfigurationElements();
                for (int i = 0; i < confElements.length; i++) {
                    IConfigurationElement confElement = confElements[i];
                    if ("wizard".equals(confElement.getName()) && getId().equals(confElement.getAttribute("id"))) { //$NON-NLS-1$ //$NON-NLS-2$             
                        wizard = (NewOntologyWizard) confElement.createExecutableExtension("class"); //$NON-NLS-1$
                        WizardDialog wizardDialog = new WizardDialog(shell, wizard);
                        if (selection != null && (selection instanceof IStructuredSelection)) {
                            Object element = ((IStructuredSelection) selection).getFirstElement();
                            if (element != null) {
                                wizard.setSelection(selection);
                            }
                        }
                        wizard.setWindowTitle(Messages.NewOntologyHandler_1); 
                        wizardDialog.open();
                        return null;
                    }
                }
                MessageDialog.openInformation(shell, Messages.NewOntologyHandler_3, 
                        Messages.NewOntologyHandler_2
                        );
            }
        } catch (CoreException e) {
            NeOnUIPlugin.getDefault().logError("", e); //$NON-NLS-1$
        }
        return null;
    }
    
    private String getId() {
        return NewOntologyWizard.getId();
    }

    private String getPrefix() {
        return "com.ontoprise.ontostudio.owl.gui.new"; //$NON-NLS-1$
    }

	protected boolean canRun(IStructuredSelection sel) {
		if (sel == null) {
			//emtpy selection, project has to be chosen in the dialog
			return true;
		}
		if (sel.getFirstElement() instanceof OWLProjectTreeElement) {
			String projectName = ((OWLProjectTreeElement)sel.getFirstElement()).getProjectName();
			try {
                OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE.equals(NeOnCorePlugin.getDefault().getOntologyProject(projectName).getOntologyLanguage());
            } catch (NeOnCoreException e) {
                throw new RuntimeException(e);
            }
		}
		return true;
	}

}
