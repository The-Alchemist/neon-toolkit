/**
 *
 */
package com.ontoprise.ontostudio.search.owl.ui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.elements.TreeElement;

import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.search.owl.match.ITreeObject;
import com.ontoprise.ontostudio.search.owl.match.ITreeParent;
import com.ontoprise.ontostudio.search.owl.match.OntologyTreeObject;
import com.ontoprise.ontostudio.search.owl.match.OwlSearchMatch;
import com.ontoprise.ontostudio.search.owl.match.ProjectTreeObject;
import com.ontoprise.ontostudio.search.owl.match.RootTreeObject;
import com.ontoprise.ontostudio.search.owl.match.TreeParent;


/**
 * @author Nico Stieler
 * Created on: 16.09.2010
 */
public class SearchTreeContentProvider implements ITreeContentProvider,IStructuredContentProvider{

    private RootTreeObject invisibleRoot;
//    private DrillDownAdapter drillDownAdapter;
    private OWLSearchResultPage searchResultPage;
    private SearchResult result;
//    private Viewer viewer;
    
    SearchTreeContentProvider(OWLSearchResultPage owlSearchResultPage) {
        this.searchResultPage = owlSearchResultPage;
        this.invisibleRoot = new RootTreeObject();
    }

    public void dispose() {
    }
    public Object[] getElements(Object parent) {
        if (parent.equals(this.searchResultPage)) {
            return getChildren(this.invisibleRoot);
        }
        return getChildren(parent);
    }
    public Object getParent(Object child) {
        if (child instanceof ITreeObject) {
            return ((ITreeObject)child).getParent();
        }
        return null;
    }
    @SuppressWarnings("null")
    public Object [] getChildren(Object parent) {
//        System.out.println("------------------getChildren-------------------");
//        System.out.println("parent: "+ parent);
//        if(parent instanceof SearchResult)
//            System.out.println("\tparent: "+ ((SearchResult)parent).getMatchCount());
//        try {
//            Thread.sleep(500);//NICO 
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//            System.out.println("parent: "+ parent);
//            if(parent instanceof SearchResult)
//                System.out.println("\tparent: "+ ((SearchResult)parent).getMatchCount());
        
        if(parent.equals(this.result) && parent instanceof SearchResult){
            if(parent != null){
                this.invisibleRoot = new RootTreeObject();
                LinkedList<ProjectTreeObject> projectArray = new LinkedList<ProjectTreeObject>();
                LinkedList<LinkedList<OntologyTreeObject>> projectOntologyArray = new LinkedList<LinkedList<OntologyTreeObject>>();
                for(Object match : ((SearchResult)parent).getElements()){
//                    TreeViewer viewer = (TreeViewer) searchResultPage.getViewer();
                    AbstractOwlEntityTreeElement element = (AbstractOwlEntityTreeElement) ((OwlSearchMatch)match).getMatch();
                    if(match instanceof TreeElement){
                        TreeElement te =((TreeElement) match);
                        ITreeDataProvider p = te.getProvider();
                        p.getClass();
//                       
                    }
//                    System.out.println("Project: " + element.getProjectName());
//                    System.out.println("\tOntology: " + element.getOntologyUri());
//                    System.out.println("\t\tEntityName: " + element.getId());
                    ProjectTreeObject actualProject = null;
                    OntologyTreeObject actualOntology = null;
                    boolean newproject = false;
                    int i = 0;
                    
                    project:
                    {
                        for(;i < projectArray.size();i++){
                            if(projectArray.get(i).getName().equals(element.getProjectName())){
                                actualProject = projectArray.get(i);
                                break project;
                            }
                        }
                        i = projectArray.size();
                        newproject = true;
//                        actualProject = new ProjectSearchMatch(new OWLProjectTreeElement(element.getProjectName(), new OntologyProvider()));
                        actualProject = new ProjectTreeObject(element.getProjectName());
                        projectArray.add(actualProject);
                        invisibleRoot.addChild(actualProject);
                    }
                    
                    ontology:
                    {
                        LinkedList<OntologyTreeObject> ontologyArray;
                        if(!newproject){
                            ontologyArray = projectOntologyArray.get(i);
                            for(int j = 0;j < ontologyArray.size();j++){
                                if(ontologyArray.get(j).getName().equals(element.getOntologyUri())){
                                    actualOntology = ontologyArray.get(j);
                                    break ontology;
                                }
                            }
                        }else{
                            ontologyArray = new LinkedList<OntologyTreeObject>();
                            projectOntologyArray.add(ontologyArray);
                        }
//                        actualOntology = new OntologySearchMatch(new OntologyTreeElement(element.getProjectName(), element.getOntologyUri(), new OntologyProvider()));
                        actualOntology = new OntologyTreeObject(element.getOntologyUri());
                        ontologyArray.add(actualOntology);
                        actualProject.addChild(actualOntology);
                        
                    }
                    actualOntology.addChild((OwlSearchMatch) match);
//                    System.out.println(element.g);
//                    new OntologyTreeElement(element.getProjectName(), element.getOntologyUri(), new OntologyProvider());
////                  new ProjectTreeElement(element.getProjectName(), new OWLOntologyProjectProvider());
//                  Object[] objects = new Object[]{element.getProjectName(),element.getOntologyUri()};//project and ontology   classes   or   treeelements
//                  TreePath treePath = new TreePath(objects);
//                  viewer.add(treePath,match);   
//                  System.out.println(viewer.getInput());
//                  viewer.get
                }
//                Object[] out2 =  invisibleRoot.getChildren();
//                System.out.println("length: " + out2.length);
                Object[] out =  this.invisibleRoot.getChildren();
                return out;
            }else{
                return new Object[0];
            }
        }else{
            if (parent instanceof TreeParent) {
                return ((TreeParent)parent).getChildren();
            }
        }
        return new Object[0];
    }

    public boolean hasChildren(Object parent) {
        if(parent instanceof SearchResult){
            if(parent != null){
                return this.invisibleRoot.hasChildren();
            }else{
                return false;
            }
        }else{
            if (parent instanceof TreeParent)
                return ((TreeParent)parent).hasChildren();
        }
        return false;
    }

    public void elementsChanged(Object[] updatedElements) {
        if (result == null) {
            return;
        }
        int addCount = 0;
        int removeCount = 0;
        TreeViewer viewer = (TreeViewer) searchResultPage.getViewer();
        Set<Object> updated = new HashSet<Object>();
        Set<Object> added = new HashSet<Object>();
        Set<Object> removed = new HashSet<Object>();
        for (int i = 0; i < updatedElements.length; i++) {
            if (searchResultPage.getDisplayedMatchCount(updatedElements[i]) > 0) {
                if (viewer.testFindItem(updatedElements[i]) != null) {
                    updated.add(updatedElements[i]);
                } else {
                    added.add(updatedElements[i]);
                }
                addCount++;
            } else {
                removed.add(updatedElements[i]);
                removeCount++;
            }
        }   
        outer:
        for(Object match : removed){
            for(ITreeObject project : invisibleRoot.getChildren()){
                int projectCounter = 0;
                if(project instanceof ITreeParent)
                    for(ITreeObject ontology : ((ITreeParent)project).getChildren()){
                        int ontologyCounter = 0;
                        if(ontology instanceof ITreeParent)
                            for(ITreeObject entity : ((ITreeParent)ontology).getChildren()){
                                if(entity instanceof OwlSearchMatch){
                                    OwlSearchMatch owlSearchMatch = (OwlSearchMatch)entity;
                                    if(owlSearchMatch.equals(match)){
                                        viewer.remove(new Object[]{project,ontology,owlSearchMatch});

                                        if(((ITreeParent)ontology).numberOfLeafs() == ++ontologyCounter)
                                            viewer.remove(new Object[]{project,ontology});
                                        if(((ITreeParent)project).numberOfLeafs() == ++projectCounter)
                                            viewer.remove(new Object[]{project});
                                        continue outer;
                                    }
                                }
                            }
                    }
            }
        }
        
        
        
//        System.out.println("------------------elementsChanged-------------------");
//        System.out.println("added: " + added.size());
//        for(Object match : added)
//            System.out.println("\tadded: " + match);
//        System.out.println("updated: " + updated.size());
//        for(Object match : updated)
//            System.out.println("\tupdated: " + match);
//        System.out.println("removed: " + removed.size());
//        for(Object match : removed)
//            System.out.println("\tremoved: " + match);
//        for(Object match : added){
//            if(match instanceof OwlSearchMatch){
//                OwlSearchMatch searchMatch = (OwlSearchMatch) match ;
//                if(searchMatch.getMatch() instanceof AbstractOwlEntityTreeElement){
//                    AbstractOwlEntityTreeElement element = (AbstractOwlEntityTreeElement) searchMatch.getMatch();
//                    OwlSearchMatch sM = (OwlSearchMatch)match;
////                    System.out.println("added    Project: " + element.getProjectName());
////                    System.out.println("added    \tOntology: " + element.getOntologyUri());
////                    System.out.println("added    \t\tEntityName: " + element.getId());
////                    OntologyTreeElement ote = new OntologyTreeElement(element.getProjectName(), element.getOntologyUri(), new OntologyProvider());
////    //                new ProjectTreeElement(element.getProjectName(), new OWLOntologyProjectProvider());
////                    Object[] objects = new Object[]{element.getProjectName(),element.getOntologyUri()};//project and ontology   classes   or   treeelements
////                    TreePath treePath = new TreePath(objects);
////    //                ((OwlSearchMatch)match).
////                    ITreeElementPath[] paths = sM.getPaths();
////                    for(ITreeElementPath path : paths){
////                        objects = new Object[2];
////                        for(int i = 0; i < path.length(); i++){
////                            System.out.println("\t" + path.get(i)); //$NON-NLS-1$
////                            if(i < 2)
////                            objects[i] = path.get(i);
////                        }
////                        treePath = new TreePath(objects);
////                    }
////                    
////                    viewer.getTree().add(parentElementOrTreePath, childElement)getTree().
////                    viewer.add(new Object[]{},sM.getMatch());   
////                    viewer.add(viewer.getTree(),sM.getMatch()); 
//                    
////                    invisibleRoot = new RootTreeObject();
////                    ProjectTreeObject project1 = new ProjectTreeObject(element.getProjectName());
////                    OntologyTreeObject ontology1 = new OntologyTreeObject(element.getOntologyUri());
////                    ontology1.addChild(sM);
////                    project1.addChild(ontology1);
////                    invisibleRoot.addChild(project1);
////                    viewer.setInput(getChildren(result));
////                    viewer.expandToLevel(0);
////                    viewer.getTree().
////                    System.out.println(viewer.getInput());
//                }
//            }
//        }
//
//        for(Object match : updated){
//            if(match instanceof OwlSearchMatch){
//                OwlSearchMatch searchMatch = (OwlSearchMatch) match ;
//                if(searchMatch.getMatch() instanceof AbstractOwlEntityTreeElement){
//                    AbstractOwlEntityTreeElement element = (AbstractOwlEntityTreeElement) searchMatch.getMatch();
//                    OwlSearchMatch sM = (OwlSearchMatch)match;
//                    System.out.println("updated    Project: " + element.getProjectName());
//                    System.out.println("updated    \tOntology: " + element.getOntologyUri());
//                    System.out.println("updated    \t\tEntityName: " + element.getId());
//        //            new OntologyTreeElement(element.getProjectName(), element.getOntologyUri(), new OntologyProvider());
//        ////            new ProjectTreeElement(element.getProjectName(), new OWLOntologyProjectProvider());
//        //            Object[] objects = new Object[]{element.getProjectName(),element.getOntologyUri()};//project and ontology   classes   or   treeelements
//        //            TreePath treePath = new TreePath(objects);
//        ////            ((OwlSearchMatch)match).
//        //            ITreeElementPath[] paths = sM.getPaths();
//        //            for(ITreeElementPath path : paths){
//        //                objects = new Object[2];
//        //                for(int i = 0; i < path.length(); i++){
//        //                    System.out.println("\t" + path.get(i)); //$NON-NLS-1$
//        //                    if(i < 2)
//        //                    objects[i] = path.get(i);
//        //                }
//        //                treePath = new TreePath(objects);
//        //            }
//                    
//                    viewer.update(sM.getMatch(),null);   
//                    System.out.println(viewer.getInput());
//                }else{
//                    if(searchMatch.getMatch() instanceof OntologyTreeElement){
//                        System.out.println("TODO update OntologyTreeElement");
//                    }else{
//                        if(searchMatch.getMatch() instanceof OWLProjectTreeElement){
//                            System.out.println("TODO update OWLProjectTreeElement");
//                        }
//                    }
//                }
//            }   
//        }
        
////      viewer.add(added.toArray(), null);
////      viewer.update(updated.toArray(), null); //NICO has be done, see added
////        viewer.remove(removed.toArray()); //NICO has be done, see added
    }


    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        result = (SearchResult) newInput;
//        if(newInput == null){
            this.invisibleRoot = new RootTreeObject();
//            viewer.
        return;
    }

    public void clear() {
        searchResultPage.getViewer().refresh();
    }
    /**
     * 
     * @param match - base match
     * @return the next match of match
     */
    public OwlSearchMatch getNextMatch(OwlSearchMatch match){
        OwlSearchMatch firstMatch = null;
        boolean actualFound = false;
        
        for(ITreeObject project : invisibleRoot.getChildren()){
            if(project instanceof ITreeParent){//project
                for(ITreeObject ontology : ((ITreeParent)project).getChildren()){
                    if(ontology instanceof ITreeParent){//ontology
                        for(ITreeObject object : ((ITreeParent)ontology).getChildren()){
                            if(object instanceof OwlSearchMatch){//entity
                                OwlSearchMatch entity = (OwlSearchMatch)object;
                                if(firstMatch == null) 
                                    firstMatch = entity;
                                if(actualFound)
                                    return entity;
                                if(match.equals(entity))
                                    actualFound = true;
                                
                            }
                        }
                    }
                }
            }
        }
        if(actualFound)
            return firstMatch;
        return match;//NICO null or old??
    }
    /**
     * 
     * @param match - base match
     * @return the previous match of match
     */
    public OwlSearchMatch getPreviousMatch(OwlSearchMatch match){
        OwlSearchMatch previousMatch = null;
        boolean firstMatch = true;
        boolean getLast = false;
        
        while(!getLast){
            for(ITreeObject project : invisibleRoot.getChildren()){
                if(project instanceof ITreeParent){//project
                    for(ITreeObject ontology : ((ITreeParent)project).getChildren()){
                        if(ontology instanceof ITreeParent){//ontology
                            for(ITreeObject object : ((ITreeParent)ontology).getChildren()){
                                if(object instanceof OwlSearchMatch){//entity
                                    OwlSearchMatch entity = (OwlSearchMatch)object;
                                    if(firstMatch) {
                                        if(match.equals(entity))
                                            getLast = true;
                                        firstMatch = false;
                                    }else if(match.equals(entity))
                                        return previousMatch;
                                    previousMatch = entity;
                                    
                                }
                            }
                        }
                    }
                }
            }
        }
        if(getLast){
            try {
                ITreeParent lastProject = (ITreeParent) invisibleRoot.getChildren()[invisibleRoot.getChildren().length-1];
                ITreeParent lastOntology = (ITreeParent) lastProject.getChildren()[lastProject.getChildren().length-1];
                return (OwlSearchMatch) lastOntology.getChildren()[lastOntology.getChildren().length-1];
            } catch (Exception e) {
                // nothing todo
            }
        }
        return match;//NICO null or old??
    }
    
}
