package com.softwareag.neontoolkit.ontostat.providers;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.softwareag.neontoolkit.ontostat.StatsProvider;

public class SubClassesStatsProvider extends StatsProvider {

	@Override
	public Object getValue(OWLModel model) {
		try {
			return model.getOntology().getAxiomCount(AxiomType.SUBCLASS_OF);
		} catch (Exception e) {
			return 0;
		}
	}
	
	@Override
	public OWLAxiom[] getElements(OWLModel model) {
		try {
			return model.getOntology().getAxioms(AxiomType.SUBCLASS_OF).toArray(new OWLAxiom[0]);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Class getType() {
		return OWLClass.class;
	}

	public Image getIconImage() {
		return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CLAZZ);
	}
}