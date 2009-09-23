/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.io;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.neontoolkit.io.filter.FileFilter;


/**
 * This class provides the wizard page that is displayed in the ImportOntologyWizard.
 */
public class WebImportOntologyWizardPage extends FileSystemImportSelectionPage {

	private static String _oldUri;
	private Text _uri;

    public WebImportOntologyWizardPage(FileFilter filter) {
    	super(filter);
		setTitle("Web Import Wizard"); //$NON-NLS-1$
		setDescription("Load ontologies from the Web via a URL."); //$NON-NLS-1$
	}

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);

        GridData gd0 = new GridData(GridData.FILL_HORIZONTAL);
        gd0.grabExcessHorizontalSpace = true;
        container.setLayoutData(gd0);
        
        int colums = 2;
        GridLayout layout = new GridLayout(colums, false);
        container.setLayout(layout);
        
        Label label2 = new Label(container, SWT.NONE);
        label2.setText("Ontology Project:"); //$NON-NLS-1$

        GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
        gd2.grabExcessHorizontalSpace = true;
        _projectsCombo = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
        _projectsCombo.setLayoutData(gd2);
        _projectsCombo.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
            	checkStatus();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        
        Label label1 = new Label(container, SWT.NULL);
        label1.setText("Physical URI of Ontology:"); //$NON-NLS-1$

        GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
        gd1.grabExcessHorizontalSpace = true;
        _uri = new Text(container, SWT.BORDER | SWT.SINGLE);
        _uri.setLayoutData(gd1);
        _uri.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
            	checkStatus();
            }
        });
        
        initValues();
        checkStatus();
        setControl(container);
    }

    @Override
    public void initValues() {
            super.initValues();
            _uri.setText(_oldUri);
    }
    
    private URI getUri(String uri) throws URISyntaxException {
    	return new URI(uri);
    }

    public String getProjectName() {
    	int index = _projectsCombo.getSelectionIndex();
    	if (index != -1) {
            return _projectsCombo.getItem(index);
    	}
    	return ""; //$NON-NLS-1$
    }

    public String getPhysicalUriAsString() {
        return _uri.getText().trim();
    }

    public URI getPhysicalUri() {
        try {
			return getUri(_uri.getText().trim());
		} catch (URISyntaxException e) {
			return null;
		}
    }
    
    @Override
	public void checkStatus() {
    	String status = null;
        boolean warning = false;
        boolean error = false;
        boolean pageComplete = true;

        //check for possible errors
        try {
			new URI(_uri.getText().trim());
		} catch (URISyntaxException e1) {
            status = "Illegal URI syntax"; //$NON-NLS-1$
            pageComplete = false;
            error=true;
		}
        if (getSelectedProject().length() == 0) {
            status = "Please select a project to store the ontology"; //$NON-NLS-1$
            pageComplete = false;
            warning= true;
        }
        
 		if(error) {
            setMessage(status, IMessageProvider.ERROR);        	
        } else if(warning ) {
            setMessage(status, IMessageProvider.WARNING);        	
        } else {
        	updateStatus(status);
        }
        setPageComplete(pageComplete);
        _oldUri = getPhysicalUriAsString();
    }
    
}
