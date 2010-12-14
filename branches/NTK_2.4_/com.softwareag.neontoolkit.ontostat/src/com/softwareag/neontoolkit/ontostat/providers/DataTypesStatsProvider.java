package com.softwareag.neontoolkit.ontostat.providers;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLDatatype;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.softwareag.neontoolkit.ontostat.StatsProvider;

public class DataTypesStatsProvider extends StatsProvider {

	@Override
    public Object getValue(OWLModel model) {
		try {
			return model.getAllDatatypes().size();
		} catch (Exception e) {
			return 0;
		}
	}
	
	@Override
    public OWLDatatype[] getElements(OWLModel model) {
		try {
			return model.getAllDatatypes().toArray(new OWLDatatype[0]);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
    public Class<?> getType() {
		return OWLDatatype.class;
	}
	
	@Override
    public Image getIconImage() {
		return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATATYPE);
	}


}
