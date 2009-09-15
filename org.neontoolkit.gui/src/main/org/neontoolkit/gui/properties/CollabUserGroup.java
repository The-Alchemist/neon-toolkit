/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.NeOnUIPlugin;

/**
 * @author Dirk Wenke
 *
 */
public class CollabUserGroup {
    private Composite _parent;
    private FormToolkit _toolkit;
    private ScrolledForm _form;
    private Section _section;
    
    private Composite _userGroup;
    private Label _users;
    
    private String _project;
    private String _ontology;
    
    public CollabUserGroup(Composite composite) {
        this(composite, null, null);
    }
    
    public CollabUserGroup(Composite composite, FormToolkit toolkit, ScrolledForm form) {
        _parent = composite;
        _toolkit = toolkit;
        _form = form;
        createContents();
    }
    
    public void createContents() {
        if (_toolkit != null) {
            _section = _toolkit.createSection(_parent, Section.DESCRIPTION | Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
            _section.setText(Messages.CollabUserGroup_0);
            _section.setDescription(Messages.CollabUserGroup_1);
            _section.addExpansionListener(new ExpansionAdapter() {
                @Override
                public void expansionStateChanged(ExpansionEvent e) {
                    if (_form != null) {
                        _form.reflow(true);
                    }
                }
            });
            _userGroup = _toolkit.createComposite(_section, SWT.NONE);
            ColumnLayoutData data = new ColumnLayoutData();
            _userGroup.setLayoutData(data);
            _toolkit.adapt(_userGroup);
            _section.setClient(_userGroup);
        }
        else {
            Group userGroup = new Group(_parent, SWT.NONE);
            userGroup.setText(Messages.CollabUserGroup_0);
            _userGroup = userGroup;
            _userGroup.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
        }
        _userGroup.setLayout(new GridLayout(2, false));
        
        Label userLabel = new Label(_userGroup, SWT.NONE);
        userLabel.setText(Messages.CollabUserGroup_3);
        userLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false));
        
        _users = new Label(_userGroup, SWT.WRAP);
        _users.setText(new String());
        _users.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false));
    }

    public void setProject(String project) {
        _project = project;
    }
    
    public void setOntology(String ontologyUri) {
        _ontology = ontologyUri;
    }
    
    public void update() {
        if (_project == null) {
            return;
        }
        _users.setText(getUsersString());
    }
    
    public void setVisible(boolean visible) {
        if (!visible) {
            _users.setText(new String());
            if (_section != null) {
                _section.setVisible(false);
            } 
        } else {
            if (_section != null) {
                _section.setVisible(true);
            }
        }
        _userGroup.setVisible(visible);
        _userGroup.getParent().layout();
    }

    private String getUsersString() {
        StringBuffer usersString = new StringBuffer();
        try {
            IOntologyProject ontoProject = OntologyProjectManager.getDefault().getOntologyProject(_project);
            if (ontoProject != null) {
            	String[] users = ontoProject.getUsers(_ontology);
                if (users != null) {
                	for (String user:users) {
                        usersString.append(user);
                        usersString.append(SWT.LF);
                	}
                }
            }
        } catch (NeOnCoreException ke) {
            NeOnUIPlugin.getDefault().logError(Messages.CollabUserGroup_4, ke);
            usersString.append(Messages.CollabUserGroup_5);
        }
        return usersString.toString();
    }
}
