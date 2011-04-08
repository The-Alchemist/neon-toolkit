package com.softwareag.neontoolkit.ontostat.providers;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.softwareag.neontoolkit.ontostat.StatsProvider;
/**
 * 
 * @author Nico Stieler
 */
public class LogicalAxiomsStatsProvider extends StatsProvider {

    @Override
    public Object getLocalValue(OWLModel model) {
        try {
            return model.getOntology().getLogicalAxiomCount();//NICO TODO local
        } catch (Exception e) {
            return 0;
        }
    }
    @Override
    public Object getGlobalValue(OWLModel model) {
        try {
            OWLOntology[] ontos = model.getOntology().getImportsClosure().toArray(new OWLOntology[0]);
            int num =0;
            for (OWLOntology owlOntology: ontos) {
                num += owlOntology.getLogicalAxiomCount();
            }
            return num;
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
	public OWLAxiom[] getLocalElements(OWLModel model) {
        try {
            return model.getOntology().getLogicalAxioms().toArray(new OWLAxiom[0]);//NICO TODO local
        } catch (Exception e) {
            return null;
        }
	}
	@Override
	public OWLAxiom[] getGlobalElements(OWLModel model) {
        try {
            return model.getOntology().getLogicalAxioms().toArray(new OWLAxiom[0]);//NICO TODO global
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
