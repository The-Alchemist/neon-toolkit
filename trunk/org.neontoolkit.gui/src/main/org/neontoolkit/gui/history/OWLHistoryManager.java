/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 ******************************************************************************/
package org.neontoolkit.gui.history;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.INavigationHistory;
import org.eclipse.ui.INavigationLocation;
import org.neontoolkit.gui.action.OWLNavigationHistoryAction;
import org.neontoolkit.gui.navigator.elements.TreeElement;

/**
 * @author Nico Stieler
 * Created on: 08.03.2011
 * 
 * The OWLHistoryManager manages the history mechanism:
 * - stores the history provides some functions to access it
 * 
 * 
 */
public class OWLHistoryManager implements INavigationHistory{

    private IOWLHistoryEntry[] history;
    private int first;
    private int last;
    private int maxLength;
    private int currentPosition;
    private OWLNavigationHistoryAction backwardAction;
    private OWLNavigationHistoryAction forwardAction;
    private TreeElement waitForTreeElement;
    
    private static OWLHistoryManager instance;
    
    public static OWLHistoryManager getInstance(){
        if(instance == null){
            instance = new OWLHistoryManager();
        }
        return instance;
    }
   
    
    public OWLHistoryManager(){
        maxLength = 100;
        history = new IOWLHistoryEntry[maxLength];
        first = -1;
        last = -1;
        currentPosition = -1;
    }
    //should not be called while navigating in history
    public void addHistoryElement(IOWLHistoryEntry object){
        IOWLHistoryEntry lastNonEmptyElement = getLastNonEmptyElement();
        if(object.equals(lastNonEmptyElement))
            return;
//            object.setEmpty(true);
        if(waitForTreeElement != null){
            if(object != null && object.getTreeElement().equals(waitForTreeElement))
                waitFor(null);
            return;
        }
        currentPosition++;
        last = currentPosition;
        history[currentPosition % maxLength] = object;
        object.setHistoryPosition(currentPosition);
        first = Math.max(last - (this.maxLength - 1), first);
        if(first == -1){
            first = 0;
        }
        updateActions();
    }
    public int changeMaxLength(int newMaxLength){
        if(newMaxLength > 0 && newMaxLength != this.maxLength){
            IOWLHistoryEntry[] oldHistory = history;
            history = new IOWLHistoryEntry[newMaxLength];
            if(newMaxLength < this.maxLength){
                first = Math.max(last - (newMaxLength - 1), first);//change first if history is bigger when the new limit
            }
            for(int i = first; i <= last; i++){
                history[i % newMaxLength] = oldHistory[i % this.maxLength];
            }
            this.maxLength = newMaxLength;
        }
        return this.maxLength;
    }
    public boolean hasNext(){
        int pointer = currentPosition;
        while(pointer++ < last){
            if(!getHistoryElement(pointer % maxLength).isEmpty()){
                return true;
            }
        }
        return false;
    }
    public boolean hasPrevious(){
        int pointer = currentPosition;
        while(pointer-- > first){
            if(!getHistoryElement(pointer % maxLength).isEmpty()){
                return true;
            }
        }
        return false;
    }
    public IOWLHistoryEntry getNext(){
        IOWLHistoryEntry value;
        while(true){
            value = getHistoryElement((++currentPosition) % maxLength);
            if(!value.isEmpty())
                break;
        }
        updateActions();
        return value;
    }
    public IOWLHistoryEntry getPrevious(){
        IOWLHistoryEntry value;
        while(true){
            value = getHistoryElement((--currentPosition) % maxLength);
            if(!value.isEmpty())
                break;
        }
        updateActions();
        return value;
    }
    public IOWLHistoryEntry[] getForwardEntries(){
        int length = Math.min(10, last - currentPosition);
        IOWLHistoryEntry[] entries = new IOWLHistoryEntry[length];
        int emptyCounter = 0;
        for(int i = 1 ; i <= length; i++){
            int nextPosition = currentPosition + i + emptyCounter;
            if(nextPosition > last){
                IOWLHistoryEntry[] oldEntities = entries;
                entries = new IOWLHistoryEntry[i-1];
                for(int j = 0; j < i-1; j++){
                    entries[j] = oldEntities[j];
                }
                break;
            }
            entries[i-1] = (getHistoryElement(nextPosition));
            if(entries[i-1] == null || entries[i-1].isEmpty()){
                emptyCounter++;
                i--;
            }
        }
        return entries;
    }
    public IOWLHistoryEntry[] getBackwardEntries(){
        int length = Math.min(10, currentPosition - first);
        IOWLHistoryEntry[] entries = new IOWLHistoryEntry[length];
        int emptyCounter = 0;
        for(int i = 1 ; i <= length; i++){
            int nextPosition = currentPosition - i - emptyCounter;
            if(nextPosition < first){
                IOWLHistoryEntry[] oldEntities = entries;
                entries = new IOWLHistoryEntry[i-1];
                for(int j = 0; j < i-1; j++){
                    entries[j] = oldEntities[j];
                }
                break;
            }
            entries[i-1] = (getHistoryElement(nextPosition));
            if(entries[i-1] == null || entries[i-1].isEmpty()){
                emptyCounter++;
                i--;
            }
        }
        return entries;
    }
    
    IOWLHistoryEntry getHistoryElement(int position){
        return history[position % maxLength];
    }
    @Override
    public INavigationLocation getCurrentLocation() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public INavigationLocation[] getLocations() {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void markLocation(IEditorPart arg0) {
        // TODO Auto-generated method stub
    }   
    public void setForwardAction(OWLNavigationHistoryAction forwardAction){
        this.forwardAction = forwardAction;
    } 
    public void setBackwardAction(OWLNavigationHistoryAction backwardAction){
        this.backwardAction = backwardAction;
    }
    private void updateActions(){
        if(forwardAction != null)
            forwardAction.update();
        if(backwardAction != null)
            backwardAction.update();
    }
    /**
     * @param owlHistoryEntry
     */
    public boolean hasValidJumpToPosition(IOWLHistoryEntry owlHistoryEntry) {
        int position = owlHistoryEntry.getHistoryPosition();
        return (first <= position && position <= last && !getHistoryElement(position).isEmpty());
    }
    /**
     * @param owlHistoryEntry
     */
    public boolean entitySelected(IOWLHistoryEntry owlHistoryEntry) {
        if(hasValidJumpToPosition(owlHistoryEntry)){
            currentPosition = owlHistoryEntry.getHistoryPosition();
            updateActions();
            return true;
        }
        return false;
    }
    /**
     * @return current selected Entity iff there exists one
     */
    public IOWLHistoryEntry getCurrentSelection(){
        if(currentPosition != -1) 
            return getHistoryElement(currentPosition);
        return null;
    }
    /**
     * @return the first non empty Element before the current selection iff there exists one
     */
    private IOWLHistoryEntry getLastNonEmptyElement() {
        int cP = currentPosition;
        while(true){
            if(cP != -1 && cP >= first) {
                IOWLHistoryEntry currentElement = getHistoryElement(cP);
                if(currentElement.isEmpty()){
                    cP--;
                    continue;
                }else{
                    return currentElement;
                }
                
            }else{
                return null;
            }
        }
    }


    /**
     * @param treeElement
     */
    public void waitFor(TreeElement treeElement) {
        waitForTreeElement = treeElement;
    }
}
