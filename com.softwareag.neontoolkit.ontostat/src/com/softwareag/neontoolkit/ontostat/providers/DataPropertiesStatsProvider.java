package com.softwareag.neontoolkit.ontostat.providers;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLDataProperty;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.softwareag.neontoolkit.ontostat.StatsProvider;

public class DataPropertiesStatsProvider extends StatsProvider {

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
	public Class getType() {
		return OWLDataProperty.class;
	}

	@Override
    public Image getIconImage() {
		return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATA_PROPERTY);
	}
}
