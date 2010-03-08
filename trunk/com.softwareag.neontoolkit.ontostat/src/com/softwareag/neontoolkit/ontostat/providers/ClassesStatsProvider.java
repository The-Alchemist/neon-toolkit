package com.softwareag.neontoolkit.ontostat.providers;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLClass;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.softwareag.neontoolkit.ontostat.StatsProvider;

public class ClassesStatsProvider extends StatsProvider {

	public Object getValue(OWLModel model) {
		try {
			return model.getAllClasses().size();
		} catch (Exception e) {
			return 0;
		}
	}
	
	public OWLClass[] getElements(OWLModel model) {
		try {
			return model.getAllClasses().toArray(new OWLClass[0]);
		} catch (Exception e) {
			return null;
		}
	}
	

	public Class getType() {
		return OWLClass.class;
	}

	public Image getIconImage() {
		return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CLAZZ);
	}
	
}

