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
public class RootTreeObject extends TreeParent {

    /**
     * @param name
     */
    public RootTreeObject() {
        super(""); //$NON-NLS-1$
    }

    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CLAZZ);
    }
    @Override
    public void setParent(ITreeParent parent){
    }

}
