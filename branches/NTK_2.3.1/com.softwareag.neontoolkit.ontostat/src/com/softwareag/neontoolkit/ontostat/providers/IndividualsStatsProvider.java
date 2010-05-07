package com.softwareag.neontoolkit.ontostat.providers;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLIndividual;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.softwareag.neontoolkit.ontostat.StatsProvider;

public class IndividualsStatsProvider extends StatsProvider {

	public Object getValue(OWLModel model) {
		try {
			return model.getOntology().getIndividualsInSignature().size();
		} catch (Exception e) {
			return 0;
		}	
	}
	
	public OWLIndividual[] getElements(OWLModel model) {
		try {
			return model.getOntology().getIndividualsInSignature().toArray(new OWLIndividual[0]);
		} catch (Exception e) {
			return null;
		}
	}
	

	public Class getType() {
		return OWLIndividual.class;
	}
	public Image getIconImage() {
		return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.INDIVIDUAL);
	}


}
