/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 ******************************************************************************/
package org.neontoolkit.gui.history;

import org.neontoolkit.core.exception.NeOnCoreException;

/**
 * @author Nico Stieler
 * Created on: 15.03.2011
 * 
 * <code>EmptyOWLHistoryEntry</code> is the implementation of <code>IOWLHistoryEntry</code> for empty entities
 * 
 */
public class EmptyOWLHistoryEntry implements IOWLHistoryEntry {

    @Override
    public void restoreLocation() throws NeOnCoreException {
    }
    @Override
    public void setHistoryPosition(int historyPosition) {
    }
    @Override
    public String getEntityURI() {
        return null;
    }
    @Override
    public String getOntologyUri() {
        return null;
    }
    @Override
    public String getProjectName() {
        return null;
    }
    @Override
    public int getHistoryPosition() {
        return 0;
    }
    @Override
    public boolean isEmpty() {
        return true;
    }
    @Override
    public boolean setEmpty(boolean empty) {
        //Nothing to do, is always empty
        return false;
    }
}
