package com.softwareag.neontoolkit.ontostat.providers;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLAxiom;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.softwareag.neontoolkit.ontostat.StatsProvider;

public class AxiomsStatsProvider extends StatsProvider {

	public Object getValue(OWLModel model) {
		try {
			return model.getOntology().getAxiomCount();
		} catch (Exception e) {
			return 0;
		}
	}
	
	public OWLAxiom[] getElements(OWLModel model) {
		try {
			return model.getOntology().getAxioms().toArray(new OWLAxiom[0]);
		} catch (Exception e) {
			return null;
		}
	}
	

	public Class getType() {
		return OWLAxiom.class;
	}
	
	public Image getIconImage() {
		return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.HAS_VALUE);
	}
}
