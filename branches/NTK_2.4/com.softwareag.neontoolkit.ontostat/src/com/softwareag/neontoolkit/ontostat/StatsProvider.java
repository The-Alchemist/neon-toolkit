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
 *
 */
public abstract class StatsProvider {
	
	protected String title;
	protected Image iconImage;
	
	public StatsProvider() {
		title=null;
		iconImage=null;
	}
	
	public abstract Object getValue(OWLModel model);
	public abstract Class<?> getType();

	public OWLObject[] getElements(OWLModel model) {
		return null;
	}
	
	public Image getIconImage() {
		return iconImage;
	}
	
	public String getTitle() {
		return title;
	}
	
	protected void setDefaults(String title, Image iconImage) {
		this.title=title;
		this.iconImage=iconImage;
	}
}
