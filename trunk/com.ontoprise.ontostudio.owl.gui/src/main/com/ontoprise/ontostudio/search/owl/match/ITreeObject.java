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
    public abstract String getName();
    public abstract void setParent(ITreeParent parent);
    public abstract ITreeParent getParent();
    public abstract String toString();
    public abstract int numberOfLeafs();
    public abstract Image getImage();
    public abstract void show(int index);
    public abstract void setFocus();
    public abstract String getProjectId();
}
