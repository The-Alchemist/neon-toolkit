package com.softwareag.neontoolkit.ontostat.providers;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.softwareag.neontoolkit.ontostat.StatsProvider;

public class ImportedOntologiesClosureStatsProvider extends StatsProvider {

	public Object getValue(OWLModel model) {
		try {
			return model.getAllImportedOntologiesURIs().size();
		} catch (Exception e) {
			return 0;
		}
	}
	
	public OWLOntology[] getElements(OWLModel model) {
		try {
			return model.getOntology().getImportsClosure().toArray(new OWLOntology[0]);
		} catch (Exception e) {
			return null;
		}
	}
	

	public Class getType() {
		return OWLOntology.class;
	}

	public Image getIconImage() {
		return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ONTOLOGY);
	}
	
}
