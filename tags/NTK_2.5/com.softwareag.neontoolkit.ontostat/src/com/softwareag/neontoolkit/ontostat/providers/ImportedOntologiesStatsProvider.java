package com.softwareag.neontoolkit.ontostat.providers;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.softwareag.neontoolkit.ontostat.StatsProvider;
/**
 * 
 * @author Nico Stieler
 */
public class ImportedOntologiesStatsProvider extends StatsProvider {

    private String local = "direct"; //$NON-NLS-1$
    private String global = "closure"; //$NON-NLS-1$
    @Override
    public Object getLocalValue(OWLModel model) {
        try {
            return model.getImportedOntologiesURIs().size();
        } catch (Exception e) {
            return 0;
        }
    }
    @Override
    public Object getGlobalValue(OWLModel model) {
        try {
            return model.getAllImportedOntologiesURIs().size();
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
    public String getLocalTooltip() {
        return getSpecificTooltip(local);
    }
    @Override
    public String getGlobalTooltip() {
        return getSpecificTooltip(global);
    }
    public String getSpecificTooltip(String specification) {
        if(super.getTooltip() == null){
            return specification;
        }
        return super.getTooltip() + "(" + specification + ")"; //$NON-NLS-1$//$NON-NLS-2$
    }
    @Override
    public OWLOntology[] getLocalElements(OWLModel model) {
        try {
            return model.getOntology().getImports().toArray(new OWLOntology[0]);
        } catch (Exception e) {
            return null;
        }
    }
	@Override
	public OWLObject[] getGlobalElements(OWLModel model) {
        try {
            return model.getOntology().getImportsClosure().toArray(new OWLOntology[0]);
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
