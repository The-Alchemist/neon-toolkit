/**
 *
 */
package com.ontoprise.ontostudio.search.owl.match;

import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;


/**
 * @author Nico Stieler
 * Created on: 28.09.2010
 */
public abstract class TreeParent implements ITreeParent {
    
    private ArrayList<ITreeObject> children;
    private String name;
    private ITreeParent parent;

    public TreeParent(String name) {
        children = new ArrayList<ITreeObject>();
        this.name = name;
    }
    @Override
    public void addChild(ITreeObject child) {
        children.add(child);
        child.setParent(this);

    }

    @Override
    public ITreeObject[] getChildren() {
        return (ITreeObject [])children.toArray(new ITreeObject[children.size()]);
    }

    @Override
    public boolean hasChildren() {
        return children.size()>0;
    }

    @Override
    public void removeChild(ITreeObject child) {
        children.remove(child);
        child.setParent(null);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setParent(ITreeParent parent){
        this.parent = parent;
    }
    @Override
    public ITreeParent getParent() {
        return parent;
    }

    @Override
    public int numberOfLeafs() {
        int out = 0;
        for(ITreeObject child : children)
            out += child.numberOfLeafs();
        return out;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }
    @Override
    public String toString() {
        return getName();
    }
    @Override
    public abstract Image getImage();

    @Override
    public void setFocus() {
    }

    @Override
    public void show(int index) {
    }
}
