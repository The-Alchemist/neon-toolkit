 /*****************************************************************************
 * written by the NeOn technologies Foundation Ltd.
 ******************************************************************************/
package org.neontoolkit.gui.history;

import org.eclipse.swt.graphics.Image;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.navigator.elements.TreeElement;

/**
 * @author Nico Stieler
 * Created on: 10.03.2011
 * 
 * The interface <code>IOWLHistoryEntry</code> provides the data structure for the history mechanism.
 */
public interface IOWLHistoryEntry {

    /**
     * @return returns the tree element of the represented Entity
     */
    public TreeElement getTreeElement();
    /**
     * restores the this entity in the EPV
     */
    public void restoreLocation() throws NeOnCoreException;
    /**
     * @param historyPosition
     */
    public void setHistoryPosition(int historyPosition);
    /**
     * @return returns the URI of the represented Entity
     */
    public String getEntityURI();
    /**
     * @return returns the ontology ID of the represented Entity
     */
    public String getOntologyUri();
    /**
     * @return returns the project name of the represented Entity
     */
    public String getProjectName();
    /**
     * @return returns the corresponding image of the represented Entity
     */
    public Image getImage();
    /**
     * @return returns the position of the represented Entity in the HistoryArray
     */
    public int getHistoryPosition();
    /**
     * @return returns if this entry is Empty: If it should not be handles by the history, we call it an empty entry
     */
    public boolean isEmpty();
    /**
     * sets the EMPTY status in this entry
     * @return true iff the value could be set
     */
    public boolean setEmpty(boolean empty);
    
}
