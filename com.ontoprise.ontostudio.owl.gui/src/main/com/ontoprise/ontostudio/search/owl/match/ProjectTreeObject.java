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
public class ProjectTreeObject extends TreeParent {

    /**
     * @param name
     */
    public ProjectTreeObject(String name) {
        super(name);
    }

    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.PROJECT);
    }


}
