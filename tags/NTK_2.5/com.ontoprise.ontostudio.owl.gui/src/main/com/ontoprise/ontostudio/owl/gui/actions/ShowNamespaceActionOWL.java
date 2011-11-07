/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.action.ShowNamespaceAction;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;

/**
 * @author werner
 * 
 */
public class ShowNamespaceActionOWL extends ShowNamespaceAction {

    private Menu _languageSubMenu;
    private List<MenuItem> _items;

    @Override
    protected void hookSubMenu(Control parent, Menu parentMenu) {
        MenuItem languageSubMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
        languageSubMenuItem.setText(Messages.ShowNamespaceActionOWL_0); 
        _languageSubMenu = new Menu(parent.getShell(), SWT.DROP_DOWN);
        languageSubMenuItem.setMenu(_languageSubMenu);

        String[] languages = NeOnUIPlugin.getDefault().getLanguages();
        for (final String lang: languages) {
            final MenuItem item = new MenuItem(_languageSubMenu, SWT.CHECK);
            item.setText(lang);
            getItemList().add(item);
            item.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    IPreferenceStore store = NeOnUIPlugin.getDefault().getPreferenceStore();
                    deselect();
                    ((MenuItem) e.getSource()).setSelection(true);
                    store.setValue(OWLPlugin.SPECIFIC_LANGUAGE_PREFERENCE, lang);
                    store.setValue(NeOnUIPlugin.ID_DISPLAY_PREFERENCE, OWLPlugin.DISPLAY_LANGUAGE);
                    store.firePropertyChangeEvent(NeOnUIPlugin.ID_DISPLAY_PREFERENCE, NeOnUIPlugin.DISPLAY_LOCAL, OWLPlugin.DISPLAY_LANGUAGE);
                }

            });
        }

    }
    
    private List<MenuItem> getItemList() {
        if (_items == null) {
            _items = new ArrayList<MenuItem>();
        }
        return _items;
    }
    
    @Override
    protected void deselect() {
        super.deselect();
        List<MenuItem> itemList = getItemList();
        for (MenuItem item: itemList) {
            item.setSelection(false);
        }
    }

}
