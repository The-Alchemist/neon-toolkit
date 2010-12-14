package com.ontoprise.ontostudio.search.owl.match;

/**
 * @author Nico Stieler
 * Created on: 28.09.2010
 */
public interface ITreeParent extends ITreeObject {

    public void addChild(ITreeObject child);
    public void removeChild(ITreeObject child);
    public ITreeObject [] getChildren();
    public boolean hasChildren();
}
