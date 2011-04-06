package com.softwareag.neontoolkit.ontostat.providers;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLDataProperty;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.softwareag.neontoolkit.ontostat.StatsProvider;
/**
 * 
 * @author Nico Stieler
 */
public class DataPropertiesStatsProvider extends StatsProvider {

    @Override
    public Object getLocalValue(OWLModel model) {
        try {
            return model.getAllDataProperties(false).size();
        } catch (Exception e) {
            return 0;
        }
    }
    @Override
    public Object getGlobalValue(OWLModel model) {
        try {
            return model.getAllDataProperties(true).size();
        } catch (Exception e) {
            return 0;
        }
    }
    @Override
    public Object getValue(OWLModel model) {
        try {
            return model.getAllDataProperties().size();
        } catch (Exception e) {
            return 0;
        }
    }

	@Override
	public OWLDataProperty[] getElements(OWLModel model) {
		try {
			return model.getAllDataProperties().toArray(new OWLDataProperty[0]);
		} catch (Exception e) {
			return null;
		}
	}
	@Override
	public OWLDataProperty[] getLocalElements(OWLModel model) {
        try {
            return model.getAllDataProperties(false).toArray(new OWLDataProperty[0]);
        } catch (Exception e) {
            return null;
        }
	}
	@Override
	public OWLDataProperty[] getGlobalElements(OWLModel model) {
        try {
            return model.getAllDataProperties(true).toArray(new OWLDataProperty[0]);
        } catch (Exception e) {
            return null;
        }
	}
	@Override
	public Class<?> getType() {
		return OWLDataProperty.class;
	}

	@Override
    public Image getIconImage() {
		return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATA_PROPERTY);
	}
}
