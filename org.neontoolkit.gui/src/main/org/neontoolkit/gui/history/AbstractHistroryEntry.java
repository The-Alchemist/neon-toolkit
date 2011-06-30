/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 ******************************************************************************/
package org.neontoolkit.gui.history;

import java.util.LinkedList;

/**
 * @author Nico Stieler
 * Created on: 30.06.2011
 */
public abstract class AbstractHistroryEntry implements IOWLHistoryEntry{

    private LinkedList<Integer> historyPositions;
    /**
     * @param string
     * @param ontologyUri
     * @param projectName
     */
    public AbstractHistroryEntry() {
        this.historyPositions = new LinkedList<Integer>();
    }
    @Override
    public void setHistoryPosition(final int historyPosition) {
        this.historyPositions.add(historyPosition);
    }
    @Override
    public int getHistoryPosition() {
        if(!this.historyPositions.isEmpty())
            return this.historyPositions.getLast();
        return -1;
    }
    @Override
    public boolean isEmpty(int position) {
        return isEmpty() || getHistoryPosition() > position;
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    /**
     * @param position
     */
    public void remove(Integer position) {
        historyPositions.remove(position);
    }
}
