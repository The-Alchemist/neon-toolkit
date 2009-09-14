/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.neontoolkit.gui.NeOnUIPlugin;

public class OWLPluginPreferenceInitializer extends AbstractPreferenceInitializer {

    public OWLPluginPreferenceInitializer() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void initializeDefaultPreferences() {
        Preferences store = NeOnUIPlugin.getDefault().getPluginPreferences();
        store.setDefault(NeOnUIPlugin.ID_DISPLAY_PREFERENCE, NeOnUIPlugin.DISPLAY_QNAME);
        store.setDefault(OWLPlugin.SYNTAX, OWLPlugin.DEFAULT_SYNTAX);
    }

}
