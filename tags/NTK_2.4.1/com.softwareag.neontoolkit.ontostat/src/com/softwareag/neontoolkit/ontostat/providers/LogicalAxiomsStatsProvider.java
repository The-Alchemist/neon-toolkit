package com.softwareag.neontoolkit.ontostat.providers;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLAxiom;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.softwareag.neontoolkit.ontostat.StatsProvider;

public class LogicalAxiomsStatsProvider extends StatsProvider {

	@Override
    public Object getValue(OWLModel model) {
		try {
			return model.getOntology().getLogicalAxiomCount();
		} catch (Exception e) {
			return 0;
		}
	}
	
	@Override
    public OWLAxiom[] getElements(OWLModel model) {
		try {
			return model.getOntology().getLogicalAxioms().toArray(new OWLAxiom[0]);
		} catch (Exception e) {
			return null;
		}
	}
	
	@Override
    public Class<?> getType() {
		return OWLAxiom.class;
	}
	
	@Override
    public Image getIconImage() {
		return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.HAS_VALUE);
	}
}
