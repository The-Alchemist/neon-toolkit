/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.preferences;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.neontoolkit.swt.widgets.RadioButtonComposite;

/**
 * @author Dirk Wenke
 *
 */
public abstract class AbstractPerspectiveSwitchPage extends PreferencePage implements IWorkbenchPreferencePage {
    public static final String ALWAYS = "Always"; //$NON-NLS-1$
    public static final String NEVER = "Never"; //$NON-NLS-1$
    public static final String PROMPT = "Prompt"; //$NON-NLS-1$

    protected RadioButtonComposite _perspectiveChooser;
    
    public AbstractPerspectiveSwitchPage(String title) {
        super(title);
    }

    /**
     * 
     */
    public AbstractPerspectiveSwitchPage() {
        super();
    }

    /**
     * @param title
     * @param image
     */
    public AbstractPerspectiveSwitchPage(String title, ImageDescriptor image) {
        super(title, image);
    }

    @Override
    protected Control createContents(Composite parent) {
        createSwitchGroup(parent);
        createBottomArea(parent);
        return parent;
    }
    
    protected void createSwitchGroup(Composite parent) {
        Group group = new Group(parent, SWT.NONE);
        GridData gData = new GridData();
        gData.horizontalAlignment = GridData.FILL;
        gData.grabExcessHorizontalSpace = true;
        group.setText(getGroupText()); 
        group.setLayoutData(gData);

        FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
        fillLayout.marginWidth = 10;
        fillLayout.marginHeight = 5;
        group.setLayout(fillLayout);
        
        _perspectiveChooser = new RadioButtonComposite(group, SWT.HORIZONTAL, new String[]{ALWAYS, NEVER, PROMPT});

        selectPerspectiveToggle(getPreferenceStore().getString(getPreferenceKey()));
    }
    
    public void createBottomArea(Composite parent) {
        GridData gData = new GridData();
        gData.grabExcessHorizontalSpace = true;
        gData.grabExcessVerticalSpace = true;
        new Label(parent, SWT.NONE).setLayoutData(gData);
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(getInitialPreferenceStore());
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    @Override
    public boolean performOk() {
        getPreferenceStore().setValue(getPreferenceKey(), getPerspectiveToggle());
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.preference.PreferencePage#performDefaults()
     */
    @Override
    protected void performDefaults() {
        super.performDefaults();
    }
    
    private void selectPerspectiveToggle(String value) {
        if (MessageDialogWithToggle.ALWAYS.equals(value)) {
            _perspectiveChooser.setSelection(ALWAYS);
        }
        else if (MessageDialogWithToggle.NEVER.equals(value)) {
            _perspectiveChooser.setSelection(NEVER);
        }
        else {
            _perspectiveChooser.setSelection(PROMPT);
        }
    }

    private String getPerspectiveToggle() {
        String selection = _perspectiveChooser.getSelection();
        if (ALWAYS.equals(selection)) {
            return MessageDialogWithToggle.ALWAYS;
        }
        else if (NEVER.equals(selection)) {
            return MessageDialogWithToggle.NEVER;
        }
        else {
            return MessageDialogWithToggle.PROMPT;
        }
    }
    
    /**
     * Returns the text that is displayed in the the Group header
     * @return
     */
    protected abstract String getGroupText();

    /**
     * Must return the preference store to store and retrieve the user settings. 
     * @return
     */
    protected abstract IPreferenceStore getInitialPreferenceStore();
    
    protected abstract String getPreferenceKey();
}
