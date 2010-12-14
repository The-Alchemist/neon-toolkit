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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.neontoolkit.gui.NeOnUIPlugin;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;


/**
 * @author werner
 * 
 */
public class ShowNBInstancesActionOWL  implements IWorkbenchWindowActionDelegate {

    @Override
    public void dispose() {
    }

    @Override
    public void init(IWorkbenchWindow window) {
    }

    @Override
    public void run(IAction action) {
        IPreferenceStore store = NeOnUIPlugin.getDefault().getPreferenceStore();
        Boolean current = store.getBoolean(OWLPlugin.SHOW_NB_INSTANCES_PREFERENCE);
        store.setValue(OWLPlugin.SHOW_NB_INSTANCES_PREFERENCE, !current);
        store.firePropertyChangeEvent(NeOnUIPlugin.ID_DISPLAY_PREFERENCE, NeOnUIPlugin.DISPLAY_LOCAL, OWLPlugin.DISPLAY_LANGUAGE);
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }

  
}
