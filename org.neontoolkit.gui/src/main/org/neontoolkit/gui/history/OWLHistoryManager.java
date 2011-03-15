/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 ******************************************************************************/
package org.neontoolkit.gui.history;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.INavigationHistory;
import org.eclipse.ui.INavigationLocation;
import org.neontoolkit.gui.action.OWLNavigationHistoryAction;

/**
 * @author Nico Stieler
 * Created on: 08.03.2011
 * 
 * The OWLHistoryManager manages the history mechanism:
 * - stores the history provides some functions to access it
 */
public class OWLHistoryManager implements INavigationHistory{

    private static IOWLHistoryEntry[] history;
    private static int first;
    private static int last;
    private static int maxLength;
    private static int currentPosition;
    private static OWLNavigationHistoryAction backwardAction;
    private static OWLNavigationHistoryAction forwardAction;
   
    static{
        reset();
    }
    
    static void reset(){
        maxLength = 100;
        history = new IOWLHistoryEntry[maxLength];
        first = -1;
        last = -1;
        currentPosition = -1;
    }
    //should not be called while navigating in history
    public static void addHistoryElement(IOWLHistoryEntry object){
        currentPosition++;
        last = currentPosition;
        history[currentPosition % maxLength] = object;
        object.setHistoryPosition(currentPosition);
        first = Math.max(last - (OWLHistoryManager.maxLength - 1), first);
        if(first == -1){
            first = 0;
        }
        updateActions();
    }
    
    public static int changeMaxLength(int newMaxLength){
        if(newMaxLength > 0 && newMaxLength != OWLHistoryManager.maxLength){
            IOWLHistoryEntry[] oldHistory = history;
            history = new IOWLHistoryEntry[newMaxLength];
            if(newMaxLength < OWLHistoryManager.maxLength){
                first = Math.max(last - (newMaxLength - 1), first);//change first if history is bigger when the new limit
            }
            for(int i = first; i <= last; i++){
                history[i % newMaxLength] = oldHistory[i % OWLHistoryManager.maxLength];
            }
            OWLHistoryManager.maxLength = newMaxLength;
        }
        return OWLHistoryManager.maxLength;
    }
    public static boolean hasNext(){
        int pointer = currentPosition;
        while(pointer++ < last){
            if(!getHistoryElement(pointer % maxLength).isEmpty()){
                return true;
            }
        }
        return false;
    }
    public static boolean hasPrevious(){
        int pointer = currentPosition;
        while(pointer-- > first){
            if(!getHistoryElement(pointer % maxLength).isEmpty()){
                return true;
            }
        }
        return false;
    }
    public static IOWLHistoryEntry getNext(){
        IOWLHistoryEntry value;
        while(true){
            value = getHistoryElement((++currentPosition) % maxLength);
            if(!value.isEmpty())
                break;
        }
        updateActions();
        return value;
    }
    public static IOWLHistoryEntry getPrevious(){
        IOWLHistoryEntry value;
        while(true){
            value = getHistoryElement((--currentPosition) % maxLength);
            if(!value.isEmpty())
                break;
        }
        updateActions();
        return value;
    }
    public static IOWLHistoryEntry[] getForwardEntries(){
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
    public static IOWLHistoryEntry[] getBackwardEntries(){
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
    
    static IOWLHistoryEntry getHistoryElement(int position){
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
    public static void setForwardAction(OWLNavigationHistoryAction forwardAction){
        OWLHistoryManager.forwardAction = forwardAction;
    } 
    public static void setBackwardAction(OWLNavigationHistoryAction backwardAction){
        OWLHistoryManager.backwardAction = backwardAction;
    }
    private static void updateActions(){
        if(forwardAction != null)
            forwardAction.update();
        if(backwardAction != null)
            backwardAction.update();
    }
    /**
     * @param owlHistoryEntry
     */
    public static boolean hasValidJumpToPosition(IOWLHistoryEntry owlHistoryEntry) {
        int position = owlHistoryEntry.getHistoryPosition();
        return (first <= position && position <= last && !getHistoryElement(position).isEmpty());
    }
    /**
     * @param owlHistoryEntry
     */
    public static boolean entitySelected(IOWLHistoryEntry owlHistoryEntry) {
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
    public static IOWLHistoryEntry getCurrentSelection(){
        if(currentPosition != -1) 
            return getHistoryElement(currentPosition);
        return null;
    }
}
