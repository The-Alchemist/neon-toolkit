/**
 * Written by the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.owl.gui.views.domainview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.neontoolkit.gui.NeOnUIPlugin;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;

/**
 * @author Nico Stieler
 * Created on: 10.05.2011
 */
public class ShowPropertiesOfAllSuperclasses extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        IPreferenceStore store = NeOnUIPlugin.getDefault().getPreferenceStore();
        Boolean oldValue = store.getBoolean(OWLPlugin.SHOW_PROPERTIES_OF_ALL_SUPERCLASSES_PREFERENCE);
        Boolean newValue = !oldValue;
        store.setValue(OWLPlugin.SHOW_PROPERTIES_OF_ALL_SUPERCLASSES_PREFERENCE, newValue);
        store.firePropertyChangeEvent(NeOnUIPlugin.ID_DISPLAY_PREFERENCE, NeOnUIPlugin.DISPLAY_LOCAL, OWLPlugin.DISPLAY_LANGUAGE);
        return null;
    }
}
