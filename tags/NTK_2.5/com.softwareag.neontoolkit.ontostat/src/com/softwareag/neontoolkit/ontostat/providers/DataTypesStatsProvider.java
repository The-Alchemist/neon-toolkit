package com.softwareag.neontoolkit.ontostat.providers;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLDatatype;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.softwareag.neontoolkit.ontostat.StatsProvider;
/**
 * 
 * @author Nico Stieler
 */
public class DataTypesStatsProvider extends StatsProvider {

    @Override
    public Object getLocalValue(OWLModel model) {
        try {
            return model.getAllDatatypes(false).size();//NICO TODO local
        } catch (Exception e) {
            return 0;
        }
    }
    @Override
    public Object getGlobalValue(OWLModel model) {
        try {
            return model.getAllDatatypes(true).size();//NICO TODO global
        } catch (Exception e) {
            return 0;
        }
    }
	@Override
    public OWLDatatype[] getElements(OWLModel model) {
		try {
			return model.getAllDatatypes().toArray(new OWLDatatype[0]);
		} catch (Exception e) {
			return null;
		}
	}
    @Override
    public OWLDatatype[] getLocalElements(OWLModel model) {
        try {
            return model.getAllDatatypes(false).toArray(new OWLDatatype[0]);//NICO TODO local
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    public OWLDatatype[] getGlobalElements(OWLModel model) {
        try {
            return model.getAllDatatypes(true).toArray(new OWLDatatype[0]);//NICO TODO global
        } catch (Exception e) {
            return null;
        }
    }
	@Override
    public Class<?> getType() {
		return OWLDatatype.class;
	}
	
	@Override
    public Image getIconImage() {
		return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.DATATYPE);
	}


}
