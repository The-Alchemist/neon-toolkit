/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.navigator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;

/**
 * @author Werner Hihn
 * @author Nico Stieler 
 */
public class RenameOwlEntityOntologySelectionDialog extends Dialog {

    private String _entityUri;
    private String _ontologyUri;
    private String _projectName;

    private List<String> _selectedOntos;
    private List<Button> _checkboxList;

    /**
     * @param parentShell
     */
    protected RenameOwlEntityOntologySelectionDialog(Shell parentShell, String entityUri, String ontologyUri, String projectName) {
        super(parentShell);
        _entityUri = entityUri;
        _ontologyUri = ontologyUri;
        _projectName = projectName;
        _checkboxList = new ArrayList<Button>();
        _selectedOntos = new ArrayList<String>();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.verticalSpacing = 3;
        layout.marginLeft = 10;
        layout.marginTop = 10;
        layout.marginRight = 10;
        layout.marginBottom = 10;
        comp.setLayout(layout);
        getShell().setText(Messages.RenameOwlEntityOntologySelectionDialog_0 + _entityUri + Messages.RenameOwlEntityOntologySelectionDialog_1);

        // info label
        Label infoLabel = new Label(comp, SWT.WRAP);
        infoLabel.setText(Messages.RenameOwlEntityOntologySelectionDialog_2);
        GridData data = new GridData(GridData.FILL, GridData.FILL, false, true);
        data.widthHint = 350;
        infoLabel.setLayoutData(data);

        new Label(comp, SWT.NONE);
        try {
            Set<OWLModel> allOntos = OWLModelFactory.getOWLModels(_projectName);
            List<String> allOntologiesInProject = new ArrayList<String>();
            for (OWLModel o: allOntos) {
                allOntologiesInProject.add(o.getOntologyURI());
            }
            allOntologiesInProject.remove(allOntologiesInProject.indexOf(_ontologyUri));

            Set<OWLModel> importedOntos = OWLModelFactory.getOWLModel(_ontologyUri, _projectName).getAllImportedOntologies();
            Set<OWLModel> importingOntos = OWLModelFactory.getOWLModel(_ontologyUri, _projectName).getAllImportingOntologies();

            // selected ontology
            new Label(comp, SWT.NONE).setText(Messages.RenameOwlEntityOntologySelectionDialog_3);
            Button selectedOntoButton = new Button(comp, SWT.CHECK);
            selectedOntoButton.setText(_ontologyUri);
            selectedOntoButton.setSelection(true);
            selectedOntoButton.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
            _checkboxList.add(selectedOntoButton);
            new Label(comp, SWT.NONE);

            // imported ontologies
            if (importedOntos.size() > 0) {
                new Label(comp, SWT.NONE).setText(Messages.RenameOwlEntityOntologySelectionDialog_4);
                for (OWLModel o: importedOntos) {
                    String ontologyUri = o.getOntologyURI();
                    Button b = new Button(comp, SWT.CHECK);
                    b.setText(ontologyUri);
                    b.setSelection(true);
                    b.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
                    _checkboxList.add(b);
                    allOntologiesInProject.remove(allOntologiesInProject.indexOf(ontologyUri));
                }
                new Label(comp, SWT.NONE);
            }

            // importing ontologies
            if (importingOntos.size() > 0) {
                new Label(comp, SWT.NONE).setText(Messages.RenameOwlEntityOntologySelectionDialog_5);
                for (OWLModel o: importingOntos) {
                    String ontologyUri = o.getOntologyURI();
                    Button b = new Button(comp, SWT.CHECK);
                    b.setText(ontologyUri);
                    b.setSelection(true);
                    b.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
                    _checkboxList.add(b);
                    //can be that is also imported ontology, so it has been already removed from allOntologiesInProject 
                    if (allOntologiesInProject.contains(ontologyUri)){
                        allOntologiesInProject.remove(allOntologiesInProject.indexOf(ontologyUri));
                    }
                }
                new Label(comp, SWT.NONE);
            }

            // other ontologies
            if (allOntologiesInProject.size() > 0) {
                new Label(comp, SWT.NONE).setText(Messages.RenameOwlEntityOntologySelectionDialog_6);
                for (String ontologyUri: allOntologiesInProject) {
                    Button b = new Button(comp, SWT.CHECK);
                    b.setText(ontologyUri);
                    b.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
                    _checkboxList.add(b);
                }
            }
            
            Composite checkUncheckButtonComp = new Composite(comp, SWT.NONE);
            data = new GridData();
            data.horizontalAlignment = GridData.FILL;
            data.grabExcessHorizontalSpace = true;
            checkUncheckButtonComp.setLayout(new GridLayout(2, true));
            
            Button checkAllButton = new Button(checkUncheckButtonComp, SWT.PUSH);
            data = new GridData();
            checkAllButton.setLayoutData(data);
            checkAllButton.setText(Messages.RenameOwlEntityOntologySelectionDialog_7);
            checkAllButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    for (Button b: _checkboxList) {
                        b.setSelection(true);
                    }
                }
            });

            Button uncheckAllButton = new Button(checkUncheckButtonComp, SWT.PUSH);
            data = new GridData();
            uncheckAllButton.setLayoutData(data);
            uncheckAllButton.setText(Messages.RenameOwlEntityOntologySelectionDialog_8);
            uncheckAllButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    for (Button b: _checkboxList) {
                        b.setSelection(false);
                    }
                }
            });
            

        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        return comp;
    }

    @Override
    protected void okPressed() {
        for (Button b: _checkboxList) {
            if (b.getSelection()) {
                _selectedOntos.add(b.getText());
            }
        }
        super.okPressed();
    }

    public List<String> getSelectedOntologyUris() {
        return _selectedOntos;
    }

    @Override
    protected int getShellStyle() {
        return super.getShellStyle() | SWT.RESIZE;
    }

}
