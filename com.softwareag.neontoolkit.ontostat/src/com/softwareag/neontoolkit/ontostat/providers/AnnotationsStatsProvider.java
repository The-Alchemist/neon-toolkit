package com.softwareag.neontoolkit.ontostat.providers;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.softwareag.neontoolkit.ontostat.StatsProvider;

public class AnnotationsStatsProvider extends StatsProvider {

	public Object getValue(OWLModel model) {
		try {
			return model.getAllAnnotationProperties().size();
		} catch (Exception e) {
			return 0;
		}
	}
	
	public OWLAnnotationProperty[] getElements(OWLModel model) {
		try {
			return model.getAllAnnotationProperties().toArray(new OWLAnnotationProperty[0]);
		} catch (Exception e) {
			return null;
		}
	}
	

	public Class getType() {
		return OWLAnnotationProperty.class;
	}
	
	public Image getIconImage() {
		return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ANNOTATION_PROPERTY);
	}
}
