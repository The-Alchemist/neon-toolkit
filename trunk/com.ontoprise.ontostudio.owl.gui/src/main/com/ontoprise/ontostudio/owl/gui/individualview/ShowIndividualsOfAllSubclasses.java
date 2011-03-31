/**
 * Written by the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.owl.gui.individualview;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.neontoolkit.gui.NeOnUIPlugin;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;

/**
 * @author Nico Stieler
 * Created on: 29.03.2011
 */
public class ShowIndividualsOfAllSubclasses  implements IWorkbenchWindowActionDelegate {

    @Override
    public void dispose() {
    }

    @Override
    public void init(IWorkbenchWindow window) {
        IPreferenceStore store = NeOnUIPlugin.getDefault().getPreferenceStore();
        store.setValue(OWLPlugin.SHOW_INSTANCES_OF_ALL_SUBCLASSES_PREFERENCE, false);
    }

    @Override
    public void run(IAction action) {
        IPreferenceStore store = NeOnUIPlugin.getDefault().getPreferenceStore();
        Boolean oldValue = store.getBoolean(OWLPlugin.SHOW_INSTANCES_OF_ALL_SUBCLASSES_PREFERENCE);
        Boolean newValue = !oldValue;
        store.setValue(OWLPlugin.SHOW_INSTANCES_OF_ALL_SUBCLASSES_PREFERENCE, newValue);
        store.firePropertyChangeEvent(NeOnUIPlugin.ID_DISPLAY_PREFERENCE, NeOnUIPlugin.DISPLAY_LOCAL, OWLPlugin.DISPLAY_LANGUAGE);
    }

    @Override
    public void selectionChanged(IAction arg0, ISelection arg1) {
        
    }
}
