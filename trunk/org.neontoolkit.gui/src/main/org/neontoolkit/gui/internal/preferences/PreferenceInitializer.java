/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.internal.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.neontoolkit.gui.NeOnUIPlugin;

/* 
 * Created on: 23.07.2004
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Preferences
 */
/**
 * This class initializes the preferences of this plugin
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    @Override
	public void initializeDefaultPreferences() {
        Preferences store = NeOnUIPlugin.getDefault().getPluginPreferences();

        store.setDefault(NeOnUIPlugin.LANGUAGE_PREFERENCE, "de;en;fr"); //$NON-NLS-1$
        store.setDefault(NeOnUIPlugin.ID_DISPLAY_PREFERENCE, NeOnUIPlugin.DISPLAY_LOCAL);
    }

}
