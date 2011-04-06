package com.softwareag.neontoolkit.ontostat.providers;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.softwareag.neontoolkit.ontostat.StatsProvider;
/**
 * 
 * @author Nico Stieler
 */
public class ObjectPropertiesStatsProvider extends StatsProvider {

    @Override
    public Object getLocalValue(OWLModel model) {
        try {
            return model.getAllObjectProperties(false).size();
        } catch (Exception e) {
            return 0;
        }
    }
    @Override
    public Object getGlobalValue(OWLModel model) {
        try {
            return model.getAllObjectProperties(true).size();
        } catch (Exception e) {
            return 0;
        }
    }
    @Override
    public Object getValue(OWLModel model) {
        try {
            return model.getAllObjectProperties().size();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public OWLAxiom[] getElements(OWLModel model) {
        try {
            return model.getAllObjectProperties().toArray(new OWLAxiom[0]);
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    public OWLAxiom[] getLocalElements(OWLModel model) {
        try {
            return model.getAllObjectProperties(false).toArray(new OWLAxiom[0]);
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    public OWLAxiom[] getGlobalElements(OWLModel model) {
        try {
            return model.getAllObjectProperties(true).toArray(new OWLAxiom[0]);
        } catch (Exception e) {
            return null;
        }
    }
	
	@Override
	public Class<?> getType() {
		return OWLObjectProperty.class;
	}

	@Override
    public Image getIconImage() {
		return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.OBJECT_PROPERTY);
	}
}
