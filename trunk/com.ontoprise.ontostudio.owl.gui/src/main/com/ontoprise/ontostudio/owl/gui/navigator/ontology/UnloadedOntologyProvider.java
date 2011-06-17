/**
 * written by the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.owl.gui.navigator.ontology;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Widget;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProjectListener;
import org.neontoolkit.gui.navigator.AbstractComplexTreeViewer;
import org.neontoolkit.gui.navigator.DefaultTreeDataProvider;
import org.neontoolkit.gui.navigator.ITreeElement;
import org.neontoolkit.gui.navigator.ITreeExtensionHandler;
import org.neontoolkit.gui.navigator.elements.AbstractOntologyTreeElement;
import org.neontoolkit.gui.navigator.elements.TreeElement;
import org.neontoolkit.gui.navigator.elements.TreeElementPath;
import org.neontoolkit.gui.navigator.project.ProjectTreeElement;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.navigator.project.OWLProjectTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.OWLGUIUtilities;
import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.util.OWLStoreMissingImports;

/**
 * @author Nico Stieler
 * Created on: 07.06.2011
 */
public class UnloadedOntologyProvider extends OntologyProvider {
    

    private OntologyProjectListener _listener;

    /**
     * Listener that gets notified if modules change and need to be marked for a future save
     * operation.
     */
    private class OntologyProjectListener implements IOntologyProjectListener{
        @Override
        public void ontologyModified(final String projectName, String ontology, boolean dirty) {
        }
        @Override
        public void ontologyStructureModified(final String projectName, String ontology, boolean modified) {
        }
        @Override
        public void projectAdded(String projectName) {
        }
        @Override
        public void projectRemoved(String projectName) {
        }       
        @Override
        public void projectRenamed(String oldProjectName, String newProjectName) {
        }       
        @Override
        public void ontologyRenamed(String projectName, String oldOntologyUri, String newOntologyUri) {
        }
        @Override
        public void ontologyAdded(String projectName, String ontologyUri) {
            try {
                OWLModel model = OWLModelFactory.getOWLModel(ontologyUri, projectName);
                for(String unloadedOntologyURI : model.getNotExistingImportedOntologiesURIs()){
                    if(OWLStoreMissingImports.isStillMissingMap(unloadedOntologyURI)){
                        try {
                            if(!OWLGUIUtilities.isOWLProject(projectName)) {
                                return;
                            }
                        } catch (NeOnCoreException e) {
                            throw new RuntimeException(e);
                        }
                        TreeElement unloadedOntology = getUnloadedOntologyTreeElement(unloadedOntologyURI, projectName);
                        if(unloadedOntology != null) {
                            addChild(getFolder(projectName), unloadedOntology);
                        }
                    }
                }
            } catch (NeOnCoreException e) {
                e.printStackTrace();
                //nothing to do
            }
        }
        @Override
        public void ontologyRemoved(String projectName, String ontologyUri) {
        }
    }   
    /**
     * 
     */
    public UnloadedOntologyProvider() {
        super();
        _listener = new OntologyProjectListener();
        NeOnCorePlugin.getDefault().addOntologyProjectListenerByLanguage(_listener, OWLManchesterProjectFactory.ONTOLOGY_LANGUAGE);
        
    }
    @Override
    public String getId() {
        return super.getId();
    }
    @Override
    public boolean isDirty(ITreeElement element) {
        return false;
    }
    @Override
    public ITreeElement[] getChildren(ITreeElement parentElement, int topIndex, int amount) {
        return new TreeElement[0];
    }
    @Override
    public ITreeElement[] getElements(ITreeElement parentElement, int topIndex, int amount) {
        if (parentElement instanceof OWLProjectTreeElement) {
            String projectId = ((OWLProjectTreeElement) parentElement).getProjectName();
            try {
                Set<OWLModel> ontologies = OWLModelFactory.getOWLModels(projectId);
                List<TreeElement> ontoNodes = new ArrayList<TreeElement>();
                for (OWLModel onto: ontologies) {
                    for(String uri : onto.getNotExistingImportedOntologiesURIs()){
                        TreeElement temp = new UnloadedOntologyTreeElement(projectId,uri, this);
                        ontoNodes.add(temp);
                    }
                }
                Collections.sort(ontoNodes, new Comparator<TreeElement>() {
                    @Override
                    public int compare(TreeElement o1, TreeElement o2) {
                        return o1.toString().compareToIgnoreCase(o2.toString());
                    }
                });
                return ontoNodes.toArray(new TreeElement[0]);

            } catch (NeOnCoreException e) {
                OWLPlugin.logError(e);
            }
        }
        return new TreeElement[0];
    }
    @Override
    public int getChildCount(ITreeElement parentElement) {
        int val = super.getChildCount(parentElement);
        System.out.println(val);
        return val;
    }
    @Override
    public ITreeExtensionHandler getExtensionHandler() {
        return super.getExtensionHandler();
    }
    @Override
    public Image getImage(ITreeElement element) {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.UNLOADED_ONTOLOGY);
    }
    @Override
    public ProjectTreeElement getFolder(String projectName) {
        return super.getFolder(projectName);
    }
    @Override
    public TreeElementPath[] getPathElements(ITreeElement element) {
        return super.getPathElements(element);
    }
    @Override
    public boolean isDragSupported() {
        return false;
    }
    @Override
    public boolean isDropSupported() {
        return false;
    }
    @Override
    public void setId(String id) {
        // ID should be changed
        super.setId(id);
    }
    @Override
    public boolean isSaveAsAllowed(ITreeElement element) {
        return false;
    }
    @Override
    public String getText(ITreeElement element) {
        assert element instanceof UnloadedOntologyTreeElement;
        UnloadedOntologyTreeElement ontology = (UnloadedOntologyTreeElement) element;
        if (((DefaultTreeDataProvider) element.getProvider()).isDirty(ontology)) {
            return ">" + element.toString(); //$NON-NLS-1$
        }
        return element.toString();
    }
    @Override
    public void replaceItems(ITreeElement oldElement, ITreeElement newElement) {
        super.replaceItems(oldElement, newElement);
    }
    @Override
    public void asyncExec(Runnable runnable) {
        super.asyncExec(runnable);
    }
    @Override
    protected void addChild(Object parent, Object child) {
        super.addChild(parent, child);
    }
    @Override
    protected void addChild(Object parent, Object child, IElementComparer comparer) {
        super.addChild(parent, child, comparer);
    }
    @Override
    protected void addChildAsync(Object parent, Object child, IElementComparer comparer) {
        super.addChildAsync(parent, child, comparer);
    }
    @Override
    public void dispose() {
        super.dispose();
    }
    @Override
    public void doSave(IProgressMonitor progress, ITreeElement element) {
        // empty implementation
    }
    @Override
    public void doSaveAs(ITreeElement element) {
        // empty implementation
    }
    @Override
    protected Widget[] findTreeItems(Object item) {
        return super.findTreeItems(item);
    }
    @Override
    protected AbstractOntologyTreeElement getOntologyTreeElement(String ontology, String projectName) {
        return null;
    }
    protected UnloadedOntologyTreeElement getUnloadedOntologyTreeElement(String ontology, String projectName) {
        try {
            if(!OWLGUIUtilities.isOWLProject(projectName)) {
                return null;
            }
        } catch (NeOnCoreException e) {
            throw new RuntimeException(e);
        }
        UnloadedOntologyTreeElement elem = new UnloadedOntologyTreeElement(projectName, ontology, UnloadedOntologyProvider.this);
        return elem;
    }
    @Override
    protected void insertChild(Object parent, Object child, Comparator<Object> comparator) {
        super.insertChild(parent, child, comparator);
    }
    @Override
    protected void refresh() {
        super.refresh();
    }
    @Override
    protected void refresh(Object element) {
        super.refresh(element);
    }
    @Override
    protected void remove(Object element) {
        super.remove(element);
    }
    @Override
    protected void removeChild(Object parent, Object child) {
        super.removeChild(parent, child);
    }
    @Override
    protected void removeChild(Object parent, Object child, IElementComparer comparer) {
        super.removeChild(parent, child, comparer);
    }
    @Override
    public void setExtensionHandler(ITreeExtensionHandler handler) {
        super.setExtensionHandler(handler);
    }
    @Override
    public void syncExec(Runnable runnable) {
        super.syncExec(runnable);
    }
    @Override
    public AbstractComplexTreeViewer getViewer() {
        return super.getViewer();
    }
    
}
