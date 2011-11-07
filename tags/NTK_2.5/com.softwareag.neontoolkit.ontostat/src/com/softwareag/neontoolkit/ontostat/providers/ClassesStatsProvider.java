package com.softwareag.neontoolkit.ontostat.providers;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLClass;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.softwareag.neontoolkit.ontostat.StatsProvider;
/**
 * 
 * @author Nico Stieler
 */
public class ClassesStatsProvider extends StatsProvider {

    @Override
    public Object getLocalValue(OWLModel model) {
        try {
            return model.getAllClasses(false).size();
        } catch (Exception e) {
            return 0;
        }
    }
    @Override
    public Object getGlobalValue(OWLModel model) {
        try {
            return model.getAllClasses(true).size();
        } catch (Exception e) {
            return 0;
        }
    }
	@Override
    public OWLClass[] getElements(OWLModel model) {
		try {
			return model.getAllClasses().toArray(new OWLClass[0]);
		} catch (Exception e) {
			return null;
		}
	}
	@Override
	public OWLClass[] getLocalElements(OWLModel model) {
        try {
            return model.getAllClasses(false).toArray(new OWLClass[0]);
        } catch (Exception e) {
            return null;
        }
	}
	@Override
	public OWLClass[] getGlobalElements(OWLModel model) {
        try {
            return model.getAllClasses(true).toArray(new OWLClass[0]);
        } catch (Exception e) {
            return null;
        }
	}
	@Override
    public Class<?> getType() {
		return OWLClass.class;
	}

	@Override
    public Image getIconImage() {
		return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CLAZZ);
	}
	
}

