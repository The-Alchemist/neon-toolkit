package com.softwareag.neontoolkit.ontostat.providers;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.softwareag.neontoolkit.ontostat.StatsProvider;

public class ImportedOntologiesStatsProvider extends StatsProvider {

	@Override
    public Object getValue(OWLModel model) {
		try {
			return model.getImportedOntologiesURIs().size();
		} catch (Exception e) {
			return 0;
		}
	}
	
	@Override
    public OWLOntology[] getElements(OWLModel model) {
		try {
			return model.getOntology().getImports().toArray(new OWLOntology[0]);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
    public Class<?> getType() {
		return OWLOntology.class;
	}

	@Override
    public Image getIconImage() {
		return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ONTOLOGY);
	}
	
}
