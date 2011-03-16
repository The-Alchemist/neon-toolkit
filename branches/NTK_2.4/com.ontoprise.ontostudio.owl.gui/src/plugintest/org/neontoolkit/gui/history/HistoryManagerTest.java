/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 ******************************************************************************/
package org.neontoolkit.gui.history;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.ontoprise.ontostudio.owl.gui.history.OWLHistoryEntry;


/**
 * @author Nico Stieler
 * Created on: 08.03.2011
 */
public class HistoryManagerTest {
    
    IOWLHistoryEntry test0 = new OWLHistoryEntry("test0", "onto1", "p1"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    IOWLHistoryEntry test1 = new OWLHistoryEntry("test1", "onto1", "p1"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    IOWLHistoryEntry test2 = new OWLHistoryEntry("test2", "onto1", "p1"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    IOWLHistoryEntry test3 = new OWLHistoryEntry("test3", "onto1", "p1"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    IOWLHistoryEntry test4 = new OWLHistoryEntry("test4", "onto1", "p1"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    IOWLHistoryEntry test5 = new OWLHistoryEntry("test5", "onto1", "p1"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    IOWLHistoryEntry test6 = new OWLHistoryEntry("test6", "onto1", "p1"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    IOWLHistoryEntry test7 = new OWLHistoryEntry("test7", "onto1", "p1"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    
    
    @Before
    public void beforeEach(){
        OWLHistoryManager.reset();
        OWLHistoryManager.addHistoryElement(test0);
        OWLHistoryManager.addHistoryElement(test1);
        OWLHistoryManager.addHistoryElement(test2);
        OWLHistoryManager.addHistoryElement(test3);
        OWLHistoryManager.addHistoryElement(test4);
        OWLHistoryManager.addHistoryElement(test5);
    }

    @Test
    public void testHasNext(){
        Assert.assertEquals(false, OWLHistoryManager.hasNext());
    }
    @Test
    public void testHasPrevious(){
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
    }
    @Test
    public void testGetPrevious(){
        Assert.assertEquals(test4, OWLHistoryManager.getPrevious());
    }
    @Test
    public void testGetPreviousHasNext(){
        Assert.assertEquals(test4, OWLHistoryManager.getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.hasNext());
    }
    @Test
    public void testGetPreviousgetNext(){
        Assert.assertEquals(test4, OWLHistoryManager.getPrevious());
        Assert.assertEquals(test5, OWLHistoryManager.getNext());
    }
    @Test
    public void testReduceSize(){
        OWLHistoryManager.changeMaxLength(10);
        Assert.assertEquals(test0, OWLHistoryManager.getHistoryElement(0));
        Assert.assertEquals(test1, OWLHistoryManager.getHistoryElement(1));
        Assert.assertEquals(test2, OWLHistoryManager.getHistoryElement(2));
        Assert.assertEquals(test3, OWLHistoryManager.getHistoryElement(3));
        Assert.assertEquals(test4, OWLHistoryManager.getHistoryElement(4));
        Assert.assertEquals(test5, OWLHistoryManager.getHistoryElement(5));
        
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
        Assert.assertEquals(test4, OWLHistoryManager.getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
        Assert.assertEquals(test3, OWLHistoryManager.getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
        Assert.assertEquals(test2, OWLHistoryManager.getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
        Assert.assertEquals(test1, OWLHistoryManager.getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
        Assert.assertEquals(test0, OWLHistoryManager.getPrevious());
        Assert.assertEquals(false, OWLHistoryManager.hasPrevious());
    }
    @Test
    public void testReduceSizeLessThenLength(){
        OWLHistoryManager.changeMaxLength(4);
        Assert.assertEquals(test2, OWLHistoryManager.getHistoryElement(2));
        Assert.assertEquals(test3, OWLHistoryManager.getHistoryElement(3));
        Assert.assertEquals(test4, OWLHistoryManager.getHistoryElement(0));
        Assert.assertEquals(test5, OWLHistoryManager.getHistoryElement(1));
        
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
        Assert.assertEquals(test4, OWLHistoryManager.getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
        Assert.assertEquals(test3, OWLHistoryManager.getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
        Assert.assertEquals(test2, OWLHistoryManager.getPrevious());
        Assert.assertEquals(false, OWLHistoryManager.hasPrevious());
    }
    @Test
    public void testNavigateAndAddAgain(){
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
        Assert.assertEquals(test4, OWLHistoryManager.getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
        Assert.assertEquals(test3, OWLHistoryManager.getPrevious());
        OWLHistoryManager.addHistoryElement(test6);
        Assert.assertEquals(false, OWLHistoryManager.hasNext());
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
        Assert.assertEquals(test3, OWLHistoryManager.getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
        Assert.assertEquals(test2, OWLHistoryManager.getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
        Assert.assertEquals(test1, OWLHistoryManager.getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.hasNext());
        Assert.assertEquals(test2, OWLHistoryManager.getNext());
        OWLHistoryManager.addHistoryElement(test7);
        Assert.assertEquals(false, OWLHistoryManager.hasNext());
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
        Assert.assertEquals(test2, OWLHistoryManager.getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
        Assert.assertEquals(test1, OWLHistoryManager.getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.hasPrevious());
        Assert.assertEquals(test0, OWLHistoryManager.getPrevious());
        Assert.assertEquals(false, OWLHistoryManager.hasPrevious());
    }
}
