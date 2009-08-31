/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

public class OWLModelPluginPreferenceInitializer extends AbstractPreferenceInitializer {

    public OWLModelPluginPreferenceInitializer() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void initializeDefaultPreferences() {
        Preferences store = OWLModelPlugin.getDefault().getPluginPreferences();

        store.setDefault(OWLModelPlugin.SHOW_IMPORTED, true);
        store.setDefault(OWLModelPlugin.SHOW_AXIOMS, false);
        store.setDefault(OWLModelPlugin.USE_TOOLBAR, false);
    }

}
