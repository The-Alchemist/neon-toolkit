/**
 * Written by the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.search.owl.ui;

import java.util.LinkedList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.neontoolkit.gui.navigator.ITreeDataProvider;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.gui.navigator.elements.TreeElement;

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
    private OWLSearchResultPage searchResultPage;
    private SearchResult result;
    
    SearchTreeContentProvider(OWLSearchResultPage owlSearchResultPage) {
        this.searchResultPage = owlSearchResultPage;
        this.invisibleRoot = new RootTreeObject();
    }

    @Override
    public void dispose() {
        invisibleRoot = new RootTreeObject();
        searchResultPage.dispose();
    }
    
    @Override
    public Object[] getElements(Object parent) {
        if (parent.equals(this.searchResultPage)) {
            return getChildren(this.invisibleRoot);
        }
        return getChildren(parent);
    }
    
    @Override
    public Object getParent(Object child) {
        if (child instanceof ITreeObject) {
            return ((ITreeObject)child).getParent();
        }
        return null;
    }
    @Override
    public Object[] getChildren(Object parent) {
        if (parent.equals(this.result) && parent instanceof SearchResult) {
            Object[] out = this.invisibleRoot.getChildren();
            return out;
        } else {
            if(parent instanceof TreeParent){
                return ((TreeParent)parent).getChildren();
            }else{
                return new Object[0];
            }    
        }
    }

    @Override
    public boolean hasChildren(Object parent) {
        if(parent instanceof SearchResult){
            return this.invisibleRoot.hasChildren();
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
        TreeViewer viewer = (TreeViewer) searchResultPage.getViewer();
        invisibleRoot = new RootTreeObject();
        LinkedList<ProjectTreeObject> projectArray = new LinkedList<ProjectTreeObject>();
        LinkedList<LinkedList<OntologyTreeObject>> projectOntologyArray = new LinkedList<LinkedList<OntologyTreeObject>>();
        for(Object match : result.getElements()){
            AbstractOntologyTreeElement element = (AbstractOntologyTreeElement) ((OwlSearchMatch) match).getMatch();
            if (match instanceof TreeElement) {
                TreeElement te = ((TreeElement) match);
                ITreeDataProvider p = te.getProvider();
                p.getClass();
            }
            ProjectTreeObject actualProject = null;
            OntologyTreeObject actualOntology = null;
            boolean newproject = false;
            int i = 0;

            project: {
                for (; i < projectArray.size(); i++) {
                    if (projectArray.get(i).getName().equals(element.getProjectName())) {
                        actualProject = projectArray.get(i);
                        break project;
                    }
                }
                i = projectArray.size();
                newproject = true;
                actualProject = new ProjectTreeObject(element.getProjectName());
                projectArray.add(actualProject);
                invisibleRoot.addChild(actualProject);
            }
            if(actualProject != null){
                ontology: {
                    LinkedList<OntologyTreeObject> ontologyArray;
                    if (!newproject) {
                        ontologyArray = projectOntologyArray.get(i);
                       for (int j = 0; j < ontologyArray.size(); j++) {
                            if (ontologyArray.get(j).getName().equals(element.getOntologyUri())) {
                                actualOntology = ontologyArray.get(j);
                                break ontology;
                            }
                        }
                    } else {
                        ontologyArray = new LinkedList<OntologyTreeObject>();
                        projectOntologyArray.add(ontologyArray);
                    }
                    actualOntology = new OntologyTreeObject(element.getOntologyUri());
                    ontologyArray.add(actualOntology);
                    actualProject.addChild(actualOntology);
    
                }
                if(actualOntology != null){
                    actualOntology.addChild((OwlSearchMatch) match);
                }
            }
        }
        
            viewer.refresh();
    }


    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        result = (SearchResult) newInput;
        return;
    }

    public void clear() {
        searchResultPage.getViewer().refresh();
        invisibleRoot = new RootTreeObject();
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
        return match;//null or old??
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
        return match;//null or old??
    }
    
}
