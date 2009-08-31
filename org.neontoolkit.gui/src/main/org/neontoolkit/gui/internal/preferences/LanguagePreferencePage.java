/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.internal.preferences;

import java.util.Locale;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.NeOnUIPlugin;

/* 
 * Created on: 12.03.2006
 * Created by: Werner Hihn
 *
 * Keywords: UI, Languagues, Preferences
 */
/**
 * Proference page where the user can choose and order the languages which are displayed for the external representations and documentations.
 */
public class LanguagePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private IPreferenceStore _store;
    private Label _infoLabel;
    private List _list;
    private Button _createButton;
    private Button _removeButton;
    private Button _upButton;
    private Button _downButton;
    private Combo _systemLanguages;

    public LanguagePreferencePage() {
        super();
        setPreferenceStore(NeOnUIPlugin.getDefault().getPreferenceStore());
        _store = NeOnUIPlugin.getDefault().getPreferenceStore();
    }

    public LanguagePreferencePage(String title) {
        super(title);
    }

    public LanguagePreferencePage(String title, ImageDescriptor image) {
        super(title, image);
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        composite.setLayout(layout);

        GridData infoLabelData = new GridData();
        infoLabelData.horizontalAlignment = GridData.FILL;
        infoLabelData.horizontalSpan = 2;
        _infoLabel = new Label(composite, SWT.NONE);
        _infoLabel.setText(Messages.LanguagePreferencePage_0);
        _infoLabel.setLayoutData(infoLabelData);
        _list = new List(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        GridData listData = new GridData();
        listData.verticalIndent = 10;
        listData.verticalSpan = 3;
        listData.verticalAlignment = GridData.FILL;
        listData.horizontalAlignment = GridData.FILL;
        listData.widthHint = 100;
        listData.heightHint = 160;
        _list.setLayoutData(listData);
        _list.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                enableButtons();
            }

        });
        BusyIndicator.showWhile(_infoLabel.getDisplay(), new Runnable() {
            public void run() {
                readLanguages();
            }
        });

        GridData upButtonData = new GridData();
        upButtonData.horizontalAlignment = GridData.FILL;
        upButtonData.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
        upButtonData.verticalIndent = 10;
        _upButton = new Button(composite, SWT.PUSH);
        _upButton.setText(Messages.LanguagePreferencePage_1);
        _upButton.setLayoutData(upButtonData);
        _upButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selIndex = _list.getSelectionIndex();
                String item = _list.getItem(selIndex);
                _list.remove(selIndex);
                _list.add(item, selIndex - 1);
                _list.setSelection(selIndex - 1);
                enableButtons();
            }

        });

        GridData downButtonData = new GridData();
        downButtonData.horizontalAlignment = GridData.FILL;
        downButtonData.verticalAlignment = GridData.VERTICAL_ALIGN_BEGINNING;
        _downButton = new Button(composite, SWT.PUSH);
        _downButton.setText(Messages.LanguagePreferencePage_2);
        _downButton.setLayoutData(downButtonData);
        _downButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int selIndex = _list.getSelectionIndex();
                String item = _list.getItem(selIndex);
                if (selIndex + 1 < _list.getItemCount()) {
                    _list.remove(selIndex);
                    _list.add(item, selIndex + 1);
                    _list.setSelection(selIndex + 1);
                    enableButtons();
                }
            }

        });

        GridData removeButtonData = new GridData();
        removeButtonData.horizontalAlignment = GridData.FILL;
        removeButtonData.verticalAlignment = GridData.VERTICAL_ALIGN_END;
        _removeButton = new Button(composite, SWT.PUSH);
        _removeButton.setText(Messages.LanguagePreferencePage_3);
        _removeButton.setLayoutData(removeButtonData);
        _removeButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String[] selection = _list.getSelection();
                for (int i = 0; i < selection.length; i++) {
                    _list.remove(selection[i]);
                    enableButtons();
                }
            }

        });

        GridData newLabelData = new GridData();
        newLabelData.verticalIndent = 10;
        newLabelData.horizontalSpan = 2;
        Label newLabel = new Label(composite, SWT.NONE);
        newLabel.setText(Messages.LanguagePreferencePage_4);
        newLabel.setLayoutData(newLabelData);

        GridData newTextData = new GridData();
        newTextData.horizontalAlignment = GridData.FILL;

        _systemLanguages = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
        _systemLanguages.setLayoutData(newTextData);
        // Locale[] locales = Locale.getAvailableLocales();
        String[] locales = Locale.getISOLanguages();
        for (int i = 0; i < locales.length; i++) {
            _systemLanguages.add(locales[i]);
        }
        GridData createButtonData = new GridData();
        createButtonData.horizontalAlignment = GridData.FILL;
        _createButton = new Button(composite, SWT.PUSH);
        _createButton.setText(Messages.LanguagePreferencePage_5);
        _createButton.setLayoutData(createButtonData);
        _createButton.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                String locale = _systemLanguages.getText();
                if (!locale.equals("") && !alreadyPresent(locale)) { //$NON-NLS-1$
                    _list.add(_systemLanguages.getText());
                }
            }

        });
        GridData restartLabelData = new GridData();
        restartLabelData.horizontalAlignment = GridData.FILL;
        restartLabelData.horizontalSpan = 2;
        enableButtons();
        return composite;
    }

    private boolean alreadyPresent(String locale) {
        String[] items = _list.getItems();
        for (int i = 0; i < items.length; i++) {
            if (items[i].equals(locale)) {
                return true;
            }
        }
        return false;
    }

    private void readLanguages() {
        String languageString = _store.getString(NeOnUIPlugin.LANGUAGE_PREFERENCE);
        StringTokenizer tokenizer = new StringTokenizer(languageString, ";"); //$NON-NLS-1$
        while (tokenizer.hasMoreTokens()) {
            String lang = tokenizer.nextToken();
            if (!lang.equals("")) { //$NON-NLS-1$
                _list.add(lang);
            }
        }
    }

    private void enableButtons() {
        if (_list.getSelection().length == 0) {
            _upButton.setEnabled(false);
            _downButton.setEnabled(false);
            _removeButton.setEnabled(false);
        } else {
            if (_list.getSelection().length > 1) {
                _removeButton.setEnabled(true);
                _upButton.setEnabled(false);
                _downButton.setEnabled(false);
            } else {
                _upButton.setEnabled(true);
                _downButton.setEnabled(true);
                _removeButton.setEnabled(true);
            }
        }
        if (_list.getSelectionIndex() == _list.getItemCount() - 1) {
            _downButton.setEnabled(false);
        }
        if (_list.getSelectionIndex() == 0) {
            _upButton.setEnabled(false);
        }
    }

    public void init(IWorkbench workbench) {
    }

    @Override
    public boolean performOk() {
        performApply();
        return true;
    }

    @Override
    protected void performDefaults() {
        super.performDefaults();
        _list.removeAll();
        _list.add("de"); //$NON-NLS-1$
        _list.add("en"); //$NON-NLS-1$
        _list.add("fr"); //$NON-NLS-1$
        performApply();
    }

    @Override
    protected void performApply() {
        String oldValue = _store.getString(NeOnUIPlugin.LANGUAGE_PREFERENCE);
        String[] items = _list.getItems();

        StringBuilder resultBuilder = new StringBuilder();
        for (String item: items) {
            resultBuilder.append(item);
            resultBuilder.append(";"); //$NON-NLS-1$
        }

        String result = resultBuilder.toString();
        if (!oldValue.equals(result)) {
            _store.setValue(NeOnUIPlugin.LANGUAGE_PREFERENCE, result);
        }
    }

}
