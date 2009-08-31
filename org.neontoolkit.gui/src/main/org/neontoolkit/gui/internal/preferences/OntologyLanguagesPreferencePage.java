/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.internal.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.neontoolkit.gui.Messages;
import org.neontoolkit.gui.NeOnUIPlugin;
import org.neontoolkit.gui.preferences.AbstractPerspectiveSwitchPage;

/* 
 * Created on: 11.11.2004
 * Created by: Dirk Wenke
 *
 * Function:
 * Keywords:
 *
 */
/**
 * @author Werner Hihn
 */
public class OntologyLanguagesPreferencePage extends AbstractPerspectiveSwitchPage {
    @Override
    protected IPreferenceStore getInitialPreferenceStore() {
        return NeOnUIPlugin.getDefault().getPreferenceStore();
    }
    
    @Override
    protected String getGroupText() {
        return Messages.OntologyLanguagesPreferencePage_1;
    }

    @Override
    protected String getPreferenceKey() {
        return NeOnUIPlugin.ASK_FOR_PRESPECTIVE_SWITCH;
    }
}
