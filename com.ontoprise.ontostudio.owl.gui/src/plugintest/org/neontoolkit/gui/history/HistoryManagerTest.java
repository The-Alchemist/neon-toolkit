/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 ******************************************************************************/
package org.neontoolkit.gui.history;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.TreeProviderManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;

import com.ontoprise.ontostudio.owl.gui.history.OWLHistoryEntry;
import com.ontoprise.ontostudio.owl.gui.history.OWLProjectHistoryEntry;
import com.ontoprise.ontostudio.owl.gui.individualview.IndividualViewContentProvider;
import com.ontoprise.ontostudio.owl.gui.navigator.clazz.ClazzTreeElement;
import com.ontoprise.ontostudio.owl.gui.navigator.property.objectProperty.ObjectPropertyTreeElement;


/**
 * @author Nico Stieler
 * Created on: 08.03.2011
 */
public class HistoryManagerTest {
    OWLDataFactory factory = OWLManager.getOWLDataFactory();
    String ontology = "http://test.com";//$NON-NLS-1$
    String project = "p1";//$NON-NLS-1$
    ITreeDataProvider classProvider = TreeProviderManager.getDefault().getProvider(MTreeView.ID, IndividualViewContentProvider.class);
    IOWLHistoryEntry test0 = new OWLHistoryEntry(new ClazzTreeElement(new OWLClassImpl(factory, IRI.create("http://test.com#test0")), ontology, project, classProvider), ontology, project); //$NON-NLS-1$ 
    IOWLHistoryEntry test1 = new OWLHistoryEntry(new ClazzTreeElement(new OWLClassImpl(factory, IRI.create("http://test.com#test1")), ontology, project, classProvider), ontology, project); //$NON-NLS-1$ 
    IOWLHistoryEntry test2 = new OWLHistoryEntry(new ClazzTreeElement(new OWLClassImpl(factory, IRI.create("http://test.com#test2")), ontology, project, classProvider), ontology, project); //$NON-NLS-1$ 
    IOWLHistoryEntry test3 = new OWLHistoryEntry(new ClazzTreeElement(new OWLClassImpl(factory, IRI.create("http://test.com#test3")), ontology, project, classProvider), ontology, project); //$NON-NLS-1$ 
    IOWLHistoryEntry test4 = new OWLHistoryEntry(new ClazzTreeElement(new OWLClassImpl(factory, IRI.create("http://test.com#test4")), ontology, project, classProvider), ontology, project); //$NON-NLS-1$ 
    IOWLHistoryEntry test5 = new OWLHistoryEntry(new ClazzTreeElement(new OWLClassImpl(factory, IRI.create("http://test.com#test5")), ontology, project, classProvider), ontology, project); //$NON-NLS-1$ 
    IOWLHistoryEntry test6 = new OWLHistoryEntry(new ClazzTreeElement(new OWLClassImpl(factory, IRI.create("http://test.com#test6")), ontology, project, classProvider), ontology, project); //$NON-NLS-1$ 
    IOWLHistoryEntry test7 = new OWLHistoryEntry(new ClazzTreeElement(new OWLClassImpl(factory, IRI.create("http://test.com#test7")), ontology, project, classProvider), ontology, project); //$NON-NLS-1$ 
    IOWLHistoryEntry test8 = new OWLHistoryEntry(new ClazzTreeElement(new OWLClassImpl(factory, IRI.create("http://test.com#test8")), ontology, project, classProvider), ontology, project); //$NON-NLS-1$ 
    IOWLHistoryEntry test9 = new OWLHistoryEntry(new ClazzTreeElement(new OWLClassImpl(factory, IRI.create("http://test.com#test9")), ontology, project, classProvider), ontology, project); //$NON-NLS-1$ 
   
    
    @Before
    public void beforeEach(){
        OWLHistoryManager.getInstance().addHistoryElement(test0);
        OWLHistoryManager.getInstance().addHistoryElement(test1);
        OWLHistoryManager.getInstance().addHistoryElement(test2);
        OWLHistoryManager.getInstance().addHistoryElement(test3);
        OWLHistoryManager.getInstance().addHistoryElement(test4);
        OWLHistoryManager.getInstance().addHistoryElement(test5);
    }

    @Test
    public void testHasNext(){
        Assert.assertEquals(false, OWLHistoryManager.getInstance().hasNext());
    }
    @Test
    public void testHasPrevious(){
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
    }
    @Test
    public void testGetPrevious(){
        Assert.assertEquals(test4, OWLHistoryManager.getInstance().getPrevious());
    }
    @Test
    public void testGetPreviousHasNext(){
        Assert.assertEquals(test4, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasNext());
    }
    @Test
    public void testGetPreviousgetNext(){
        Assert.assertEquals(test4, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(test5, OWLHistoryManager.getInstance().getNext());
    }
    @Test
    public void testReduceSize(){
        OWLHistoryManager.getInstance().changeMaxLength(10);
        Assert.assertEquals(test0, OWLHistoryManager.getInstance().getHistoryElement(0));
        Assert.assertEquals(test1, OWLHistoryManager.getInstance().getHistoryElement(1));
        Assert.assertEquals(test2, OWLHistoryManager.getInstance().getHistoryElement(2));
        Assert.assertEquals(test3, OWLHistoryManager.getInstance().getHistoryElement(3));
        Assert.assertEquals(test4, OWLHistoryManager.getInstance().getHistoryElement(4));
        Assert.assertEquals(test5, OWLHistoryManager.getInstance().getHistoryElement(5));
        
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
        Assert.assertEquals(test4, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
        Assert.assertEquals(test3, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
        Assert.assertEquals(test2, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
        Assert.assertEquals(test1, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
        Assert.assertEquals(test0, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(false, OWLHistoryManager.getInstance().hasPrevious());
    }
    @Test
    public void testReduceSizeLessThenLength(){
        OWLHistoryManager.getInstance().changeMaxLength(4);
        Assert.assertEquals(test2, OWLHistoryManager.getInstance().getHistoryElement(2));
        Assert.assertEquals(test3, OWLHistoryManager.getInstance().getHistoryElement(3));
        Assert.assertEquals(test4, OWLHistoryManager.getInstance().getHistoryElement(0));
        Assert.assertEquals(test5, OWLHistoryManager.getInstance().getHistoryElement(1));
        
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
        Assert.assertEquals(test4, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
        Assert.assertEquals(test3, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
        Assert.assertEquals(test2, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(false, OWLHistoryManager.getInstance().hasPrevious());
    }
    @Test
    public void testNavigateAndAddAgain(){
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
        Assert.assertEquals(test4, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
        Assert.assertEquals(test3, OWLHistoryManager.getInstance().getPrevious());
        OWLHistoryManager.getInstance().addHistoryElement(test6);
        Assert.assertEquals(false, OWLHistoryManager.getInstance().hasNext());
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
        Assert.assertEquals(test3, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
        Assert.assertEquals(test2, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
        Assert.assertEquals(test1, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasNext());
        Assert.assertEquals(test2, OWLHistoryManager.getInstance().getNext());
        OWLHistoryManager.getInstance().addHistoryElement(test7);
        Assert.assertEquals(false, OWLHistoryManager.getInstance().hasNext());
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
        Assert.assertEquals(test2, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
        Assert.assertEquals(test1, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(true, OWLHistoryManager.getInstance().hasPrevious());
        Assert.assertEquals(test0, OWLHistoryManager.getInstance().getPrevious());
        Assert.assertEquals(false, OWLHistoryManager.getInstance().hasPrevious());
    }
}
