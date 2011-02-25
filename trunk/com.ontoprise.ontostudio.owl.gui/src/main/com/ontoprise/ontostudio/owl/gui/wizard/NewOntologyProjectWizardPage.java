/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.wizard;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.gui.navigator.project.ProjectNameValidator;

import com.ontoprise.ontostudio.owl.gui.Messages;

/* 
 * Created on: 06.11.2004
 * Created by: Dirk Wenke
 *
 * Function: UI, Project, Wizard
 */
/**
 * This class provides the wizard page that is displayed in the NewOntologyProject wizard.
 */
public class NewOntologyProjectWizardPage extends WizardPage {

    private static final String DEFAULT_PROJECT_NAME = "NewOntologyProject"; //$NON-NLS-1$

    private Text _projectText;
    
	public NewOntologyProjectWizardPage(ISelection selection) {
        super("wizardPage"); //$NON-NLS-1$
        setTitle(Messages.NewOntologyProjectWizardPage_1); 
        setDescription(Messages.NewOntologyProjectWizardPage_2); 
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 2;
        layout.verticalSpacing = 9;
        GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);

        Label label = new Label(container, SWT.NULL);
        label.setText(Messages.NewOntologyProjectWizardPage_3); 
        label.setLayoutData(gd);

        _projectText = new Text(container, SWT.BORDER | SWT.SINGLE);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        _projectText.setLayoutData(data);
        _projectText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });
        
        initProjectName();
        dialogChanged();
        setControl(container);
    }

    /**
     * Ensures that both text fields are set.
     */
    protected void dialogChanged() {
        String projectName = getProjectName();

        String message = ProjectNameValidator.getDefaultValidator().isValid(projectName);
        updateStatus(message);
    }

    protected void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    public void setProjectName(String name) {
        _projectText.setText(name); 
    }

    public String getProjectName() {
        return _projectText.getText();
    }

    private void initProjectName() {
        String projectName = (getProjectName().equals("") ? DEFAULT_PROJECT_NAME : getProjectName()); //$NON-NLS-1$
        
        boolean increment = true;
        while (increment) {
            IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
            IResource resource = root.findMember(new Path(projectName));
            if (resource != null && resource.exists()) {
                if (projectName.startsWith(DEFAULT_PROJECT_NAME) && !projectName.equals(DEFAULT_PROJECT_NAME)) {
                    String counterStr = projectName.substring(DEFAULT_PROJECT_NAME.length());
                    try {
                        int counter = Integer.parseInt(counterStr);
                        counter++;
                        projectName = DEFAULT_PROJECT_NAME + String.valueOf(counter);
                    } catch (Exception e) {
                        System.out.println(e.getClass());
                    }
                } else if (projectName.equals(DEFAULT_PROJECT_NAME)) {
                    projectName = DEFAULT_PROJECT_NAME + "1"; //$NON-NLS-1$
                }
            } else {
                increment = false;
            }
        }
        setProjectName(projectName);
    }
    
    @Override
    public void performHelp() {
        String helpContextId = org.neontoolkit.gui.IHelpContextIds.OWL_CREATE_ONTOLOGY_PROJECT;
        PlatformUI.getWorkbench().getHelpSystem().displayHelp(helpContextId);
    }
}
