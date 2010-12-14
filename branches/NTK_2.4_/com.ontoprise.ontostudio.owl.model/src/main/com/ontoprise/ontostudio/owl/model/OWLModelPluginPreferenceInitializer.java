/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
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
/**
 * 
 * @author Nico Stieler
 */
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
        store.setDefault(OWLModelPlugin.SHOW_ACTUAL_ONTOLOGY, false); 
        store.setDefault(OWLModelPlugin.EDIT_IMPORTED_AXIOMS_WITHOUT_ASKING, false); 
        store.setDefault(OWLModelPlugin.REMOVE_IMPORTED_AXIOMS_WITHOUT_ASKING, false); 
        store.setDefault(OWLModelPlugin.SHOW_RESTRICTION_IN_CLASS_TAXONOMY_TAB, false); 
    }

}
