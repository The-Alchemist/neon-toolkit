/**
 *
 */
package com.ontoprise.ontostudio.search.owl.match;

import org.eclipse.swt.graphics.Image;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;

/**
 * @author Nico Stieler
 * Created on: 28.09.2010
 */
public class OntologyTreeObject extends TreeParent {

    /**
     * @param name
     */
    public OntologyTreeObject(String name) {
        super(name);
    }

    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ONTOLOGY);
    }

}
