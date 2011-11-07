package com.softwareag.neontoolkit.ontostat;

import org.eclipse.swt.graphics.Image;
import org.semanticweb.owlapi.model.OWLObject;

import com.ontoprise.ontostudio.owl.model.OWLModel;

/**
 * This class is part of the extension point for Statistics providers. Each 
 * implementation gives information about one axiom type or a collection of such.
 * The title and icon properties can be parameterized in with the extension or 
 * hardcoded in the extending class.
 * 
 * @author Boyan Yurukov
 * @author Nico Stieler
 * 
 */
public abstract class StatsProvider {
	
	protected String title;
    protected Image iconImage;
    protected String tooltip;
	
	public StatsProvider() {
		title=null;
		iconImage=null;
	}

    public abstract Object getLocalValue(OWLModel model);
    public abstract Object getGlobalValue(OWLModel model);
	public abstract Class<?> getType();

    public OWLObject[] getElements(OWLModel model) {
        return null;
    }
    public OWLObject[] getLocalElements(OWLModel model){
        return null;
    }
    public OWLObject[] getGlobalElements(OWLModel model){
        return null;
    }
	
	public Image getIconImage() {
		return iconImage;
	}

    public String getTitle() {
        return title;
    }
    public String getImageTooltip() {
        return tooltip;
    }
    public String getTitleTooltip() {
        return tooltip;
    }
    public String getLocalTooltip() {
        return tooltip;
    }
    public String getGlobalTooltip() {
        return tooltip;
    }
    public String getTooltip() {
        return tooltip;
    }
	
	protected void setDefaults(String title, Image iconImage,String tooltip) {
		this.title = title;
		this.iconImage = iconImage;
		this.tooltip = tooltip;
	}
}
