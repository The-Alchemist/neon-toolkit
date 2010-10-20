/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.preferences;

import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.neontoolkit.swt.widgets.RadioButtonComposite;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.syntax.ISyntaxManager;
import com.ontoprise.ontostudio.owl.model.OWLModelPlugin;

/* 
 * Created on: 24.04.2008
 * Created by: Werner Hihn
 *
 * Function:
 * Keywords:
 *
 */
/**
 * @author Werner Hihn
 * @author Nico Stieler
 */

public class OWLPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private IPreferenceStore _prefs;
    private RadioButtonComposite _syntaxChooser;

    private Button _importedCheckbox;
    private Button _showAxiomsCheckbox;
    private Button _displayToolbar;
    private Button _showActualOntology; 

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createContents(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(Messages.OWLPreferencePage_2); 
        GridData gData = new GridData();
        gData.horizontalAlignment = GridData.FILL;
        gData.grabExcessHorizontalSpace = true;
        group.setLayoutData(gData);
        FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
        fillLayout.marginWidth = 10;
        fillLayout.marginHeight = 5;
        group.setLayout(fillLayout);

        _importedCheckbox = new Button(group, SWT.CHECK);
        _importedCheckbox.setText(Messages.OWLPreferencePage_0); 

        boolean enabled = _prefs.getBoolean(OWLModelPlugin.SHOW_IMPORTED);
        _importedCheckbox.setSelection(enabled);

        _showAxiomsCheckbox = new Button(group, SWT.CHECK);
        _showAxiomsCheckbox.setText(Messages.OWLPreferencePage_4); 

        boolean showAxioms = _prefs.getBoolean(OWLModelPlugin.SHOW_AXIOMS);
        _showAxiomsCheckbox.setSelection(showAxioms);

        _displayToolbar = new Button(group, SWT.CHECK);
        _displayToolbar.setText(Messages.OWLPreferencePage_5); 
        enabled = _prefs.getBoolean(OWLModelPlugin.USE_TOOLBAR);
        _displayToolbar.setSelection(enabled);
        
        _showActualOntology = new Button(group, SWT.CHECK);
        _showActualOntology.setText(Messages.OWLPreferencePage_6); 
        enabled = _prefs.getBoolean(OWLModelPlugin.SHOW_ACTUAL_ONTOLOGY);
        _showActualOntology.setSelection(enabled);
        
        Set<ISyntaxManager> syntaxManagers = OWLPlugin.getDefault().getRegisteredSyntaxManagers();
        String[] managers = new String[syntaxManagers.size()];
        int i = 0;
        for (ISyntaxManager manager: syntaxManagers) {
            managers[i] = manager.getSyntaxName();
            i++;
        }

        Group group2 = new Group(parent, SWT.NONE);
        group2.setText(Messages.OWLPreferencePage_3); 
        gData = new GridData();
        gData.horizontalAlignment = GridData.FILL;
        gData.grabExcessHorizontalSpace = true;
        group2.setLayoutData(gData);
        fillLayout = new FillLayout(SWT.VERTICAL);
        fillLayout.marginWidth = 10;
        fillLayout.marginHeight = 5;
        group2.setLayout(fillLayout);
        _syntaxChooser = new RadioButtonComposite(group2, SWT.HORIZONTAL, managers);
        String syntax = _prefs.getString(OWLPlugin.SYNTAX);
        _syntaxChooser.setSelection(syntax.equals("") ? managers[0] : syntax); //$NON-NLS-1$

        gData = new GridData();
        gData.grabExcessHorizontalSpace = true;
        gData.grabExcessVerticalSpace = true;
        new Label(parent, SWT.NONE).setLayoutData(gData);

        return parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
        _prefs = OWLModelPlugin.getDefault().getPreferenceStore();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        BusyIndicator.showWhile(_displayToolbar.getDisplay(), new Runnable() {
        
            public void run() {
                _prefs.setValue(OWLModelPlugin.SHOW_IMPORTED, getShowImported());
                String syntax = _syntaxChooser.getSelection();
                if (syntax != null) {
                    _prefs.setValue(OWLPlugin.SYNTAX, syntax);
                }
                _prefs.setValue(OWLModelPlugin.SHOW_AXIOMS, getShowAxioms());
                _prefs.setValue(OWLModelPlugin.USE_TOOLBAR, getUseToolbar());
                _prefs.setValue(OWLModelPlugin.SHOW_ACTUAL_ONTOLOGY, getShowActualOntology());
            }

        });
        return true;
    }

    private boolean getShowActualOntology() {
        return _showActualOntology.getSelection();
    }
    private boolean getUseToolbar() {
        return _displayToolbar.getSelection();
    }

    private boolean getShowImported() {
        return _importedCheckbox.getSelection();
    }

    private boolean getShowAxioms() {
        return _showAxiomsCheckbox.getSelection();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        _prefs.setValue(OWLModelPlugin.SHOW_IMPORTED, false);
        _prefs.setValue(OWLModelPlugin.SHOW_AXIOMS, false);
        _prefs.setValue(OWLModelPlugin.USE_TOOLBAR, false);
        _prefs.setValue(OWLModelPlugin.SHOW_ACTUAL_ONTOLOGY, false);

        _importedCheckbox.setSelection(false);
        _showAxiomsCheckbox.setSelection(false);
        _displayToolbar.setSelection(true);
        _showActualOntology.setSelection(false);
        _syntaxChooser.setSelection(OWLPlugin.DEFAULT_SYNTAX);
    }
}
