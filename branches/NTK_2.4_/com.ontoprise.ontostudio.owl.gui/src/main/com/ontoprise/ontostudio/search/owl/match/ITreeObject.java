/**
 *
 */
package com.ontoprise.ontostudio.search.owl.match;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.swt.graphics.Image;


/**
 * @author Nico Stieler
 * Created on: 28.09.2010
 */
public interface ITreeObject extends IAdaptable {
    public String getName();
    public void setParent(ITreeParent parent);
    public ITreeParent getParent();
    public int numberOfLeafs();
    public Image getImage();
    public void show(int index);
    public void setFocus();
    public String getProjectId();
}
