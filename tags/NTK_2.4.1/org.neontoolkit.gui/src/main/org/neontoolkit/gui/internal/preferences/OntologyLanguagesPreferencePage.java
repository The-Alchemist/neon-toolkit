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
