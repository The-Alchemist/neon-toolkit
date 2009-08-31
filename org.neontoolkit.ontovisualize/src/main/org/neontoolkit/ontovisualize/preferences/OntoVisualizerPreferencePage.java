/*****************************************************************************
 * Copyright (c) 2007 ontoprise GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU General Public License (GPL)
 * which accompanies this distribution, and is available at
 * http://www.ontoprise.de/legal/gpl.html
 *****************************************************************************/

package org.neontoolkit.ontovisualize.preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.neontoolkit.ontovisualize.Messages;
import org.neontoolkit.ontovisualize.OntovisualizePlugin;


/*
 * Created by Werner Hihn
 */

public class OntoVisualizerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    public static final String DEFAULT_LEVEL = "default_level"; //$NON-NLS-1$
    public static final String DISPLAY_ATTS_OF_RANGE_CONCEPTS = "show_range_atts"; //$NON-NLS-1$

    private IPreferenceStore _store;
    private Text _show1Text;
    private Button _showAttsOfRangeConcepts;

    public OntoVisualizerPreferencePage() {
        super();
        setPreferenceStore(OntovisualizePlugin.getDefault().getPreferenceStore());
        _store = getPreferenceStore();
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        composite.setLayout(layout);

        Group flogicGroup = new Group(composite, SWT.NONE);
        flogicGroup.setText(Messages.OntoVisualizerPreferencePage_1);
        GridData gData = new GridData();
        gData.horizontalAlignment = GridData.FILL;
        gData.grabExcessHorizontalSpace = true;
        flogicGroup.setLayoutData(gData);
        GridLayout gridLayout = new GridLayout(2, false);
        gridLayout.horizontalSpacing = 2;
        flogicGroup.setLayout(gridLayout);
        
        Composite c = new Composite(flogicGroup, SWT.NONE);
        c.setLayout(new GridLayout(2, false));
        gData = new GridData();
        gData.grabExcessHorizontalSpace = true;
        gData.horizontalAlignment = GridData.FILL;
        c.setLayoutData(gData);
        new Label(flogicGroup, SWT.NONE);
        
        Label show1Label = new Label(c, SWT.NONE);
        _show1Text = new Text(c, SWT.BORDER | SWT.CENTER);
        _show1Text.setTextLimit(2);
        GridData data = new GridData();
        show1Label.setLayoutData(data);
        
        data = new GridData();
        data.widthHint = 25;
        _show1Text.setLayoutData(data);

        show1Label.setText(Messages.OntoVisualizerPreferencePage_0);
        String oldValue = _store.getString(DEFAULT_LEVEL);
        if (oldValue == null || oldValue.equals("")) { //$NON-NLS-1$
            oldValue = "0"; //$NON-NLS-1$
        }
        _show1Text.setText(oldValue);

        _showAttsOfRangeConcepts = new Button(flogicGroup, SWT.CHECK);
        boolean oldAttsOfRangeConcepts = _store.getBoolean(DISPLAY_ATTS_OF_RANGE_CONCEPTS);
        _showAttsOfRangeConcepts.setSelection(oldAttsOfRangeConcepts);
        _showAttsOfRangeConcepts.setText(Messages.OntoVisualizerPreferencePage_3);
        gData = new GridData();
        gData.grabExcessHorizontalSpace = true;
        gData.horizontalAlignment = GridData.FILL;
        _showAttsOfRangeConcepts.setLayoutData(gData);
        new Label(flogicGroup, SWT.NONE);

        return composite;
    }

    public void init(IWorkbench workbench) {

    }

    @Override
    protected void performApply() {
        String newValue = _show1Text.getText().trim();
        try {
            _store.setValue(DEFAULT_LEVEL, newValue);
        } catch (Throwable t) {
            MessageDialog.openError(_show1Text.getShell(), Messages.OntoVisualizerPreferencePage_4, Messages.OntoVisualizerPreferencePage_5);
            _show1Text.setText(_store.getString(DEFAULT_LEVEL));
        }
        _store.setValue(DISPLAY_ATTS_OF_RANGE_CONCEPTS, _showAttsOfRangeConcepts.getSelection());
    }

    @Override
    public boolean performOk() {
        performApply();
        return true;
    }

}
