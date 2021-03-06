/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

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
import org.neontoolkit.gui.navigator.elements.IProjectElement;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.io.actions.ILoadOntologyHandler;
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
 * @author Nico Stieler
 */

public class NewOWLOntologyHandler extends AbstractHandler implements ILoadOntologyHandler{

    private boolean _fixed;
    private String _selectionProjectString;
    private String _neededOntologyString;
    private MTreeView _mTreeView;

    /* (non-Javadoc)
     * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        MTreeView view;
        if(_mTreeView != null)
            view = _mTreeView;
        else
            view = HandlerUtil.getActivePart(arg0) instanceof MTreeView ? 
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
                        if(_fixed)
                            wizard.setFixed(_fixed, _selectionProjectString, _neededOntologyString);
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
    /**
     * @param _selection
     */
    @Override
    public void fixedProject(IStructuredSelection selection, String ontologyUri) {
        _fixed = true;
        if (selection != null) {
            Object sel = selection.getFirstElement();
            if (sel instanceof IProjectElement) {
                _selectionProjectString = ((IProjectElement) sel).getProjectName();
            }
        }
        _neededOntologyString = ontologyUri;
        _mTreeView = (MTreeView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MTreeView.ID);
    }
	

}
