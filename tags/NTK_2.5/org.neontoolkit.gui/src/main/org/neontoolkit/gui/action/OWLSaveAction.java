/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 * based on SaveAction by IBM
 ******************************************************************************/
package org.neontoolkit.gui.action;

import java.util.Hashtable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.ISaveablesLifecycleListener;
import org.eclipse.ui.ISaveablesSource;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.Saveable;
import org.eclipse.ui.internal.SaveAction;
import org.eclipse.ui.internal.SaveablesList;
import org.eclipse.ui.internal.WorkbenchPage;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.navigator.elements.IOntologyElement;
import org.neontoolkit.gui.navigator.elements.IProjectElement;

@SuppressWarnings("restriction")
public class OWLSaveAction extends SaveAction implements ISelectionListener{
    
    private IWorkbenchPart currentSelectedPart;
    private IProjectElement currentSelection;


    /**
     * The default constructor.
     *
     * @param window the window
     */
    public OWLSaveAction(IWorkbenchWindow window) {
        super(window);
//        window.getSelectionService().addSelectionListener(MTreeView.ID, this);
        window.getSelectionService().addSelectionListener(this);
}

    /* (non-Javadoc)
     * Method declared on PageEventAction.
     */
    @Override
    public void pageActivated(IWorkbenchPage page) {
        super.pageActivated(page);
        updateState();
    }

    /* (non-Javadoc)
     * Method declared on PageEventAction.
     */
    @Override
    public void pageClosed(IWorkbenchPage page) {
        super.pageClosed(page);
        updateState();
    }
    
    /* (non-Javadoc)
     * Method declared on PartEventAction.
     */
    @Override
    public void partClosed(IWorkbenchPart part) {
        super.partClosed(part);
        updateState();
    }
    
    /* (non-Javadoc)
     * Method declared on PartEventAction.
     */
    @Override
    public void partOpened(IWorkbenchPart part) {
        super.partOpened(part);
        updateState();
    }
    
    /* (non-Javadoc)
     * Method declared on Action.
     */
    @Override
    public void run() {
        if (getWorkbenchWindow() == null) {
            // action has been disposed
            return;
        }
        WorkbenchPage page = (WorkbenchPage) getActivePage();
        if (page != null) {
//            page.saveAllEditors(false, true);
            ISaveablePart[] dirtyParts = page.getDirtyParts();
            IProgressMonitor monitor = new IProgressMonitor(){
                @Override
                public void beginTask(String arg0, int arg1) {
                    System.out.println("beginTask"); //$NON-NLS-1$
                    // TODO Auto-generated method stub
                }
                @Override
                public void done() {
                    System.out.println("done"); //$NON-NLS-1$
                    // TODO Auto-generated method stub
                }
                @Override
                public void internalWorked(double arg0) {
                    System.out.println("internalWorked"); //$NON-NLS-1$
                    // TODO Auto-generated method stub
                }
                @Override
                public boolean isCanceled() {
                    System.out.println("isCanceled"); //$NON-NLS-1$
                    // TODO Auto-generated method stub
                    return false;
                }
                @Override
                public void setCanceled(boolean arg0) {
                    System.out.println("setCanceled"); //$NON-NLS-1$
                    // TODO Auto-generated method stub
                }
                @Override
                public void setTaskName(String arg0) {
                    System.out.println("setTaskName"); //$NON-NLS-1$
                    // TODO Auto-generated method stub
                }
                @Override
                public void subTask(String arg0) {
                    System.out.println("subTask"); //$NON-NLS-1$
                    // TODO Auto-generated method stub
                }
                @Override
                public void worked(int arg0) {
                    System.out.println("worked"); //$NON-NLS-1$
                    // TODO Auto-generated method stub
                }
            };
            for(ISaveablePart dirtyPart : dirtyParts){
                dirtyPart.doSave(monitor);
            }
            updateState();
        }
    }
    /**
     * Updates availability depending on number of
     * targets that need saving.
     */
    @Override
    protected void updateState() {
        WorkbenchPage page = (WorkbenchPage) getActivePage();
        if (page == null) {
            setEnabled(false);
        } else {
            if (page.getDirtyParts().length > 0) {
                ISaveablePart[] dirtyParts = page.getDirtyParts();
                if(!(currentSelectedPart instanceof MTreeView))
                    //only if there is a dirtyPart != MTreeView
                    for(ISaveablePart dirtyPart : dirtyParts){
                        if(!(dirtyPart instanceof MTreeView)){
                            setEnabled(true);
                            break;
                        }
                    } 
                else{
                    if(currentSelection instanceof IOntologyElement){
                        String projectName = currentSelection.getProjectName();
                        String ontologyUri = ((IOntologyElement)currentSelection).getOntologyUri();
                        String currentselectionString = projectName + ", " + ontologyUri; //$NON-NLS-1$
                        for(ISaveablePart dirtyPart : dirtyParts){
                            //use information of current Selection
                            if(dirtyPart instanceof MTreeView){
                                try{
                                    Hashtable<String,Boolean> dirtyTable = ((MTreeView)dirtyPart).getDirtyTable();
                                    Boolean dirty = dirtyTable.get(currentselectionString);
                                    if(dirty == null || dirty == false){
                                        setEnabled(false);
                                    }else{
                                        setEnabled(true);
                                        break;
                                    }
                                }catch(NullPointerException e){
                                    //nothing to do
                                }
                            }else{
                                setEnabled(true);
                                break;
                            }
                        } 
                    }else{
                        //NICO TODO
                        String projectName = currentSelection.getProjectName();
                        String currentselectionString;
                        
                        outer:
                        for(ISaveablePart dirtyPart : dirtyParts){
                            //use information of current Selection
                            if(dirtyPart instanceof MTreeView){
                                try{
                                    Hashtable<String,Boolean> dirtyTable = ((MTreeView)dirtyPart).getDirtyTable();
                                    for(String key : dirtyTable.keySet()){
                                        if(key.startsWith(projectName)){
                                            currentselectionString = key;
                                            Boolean dirty = dirtyTable.get(currentselectionString);
                                            if(dirty == null || dirty == false){
                                                setEnabled(false);
                                            }else{
                                                setEnabled(true);
                                                break outer;
                                            }
                                        }
                                    }
                                }catch(NullPointerException e){
                                    //nothing to do
                                }
                            }else{
                                setEnabled(true);
                                break;
                            }
                        } 
                    }
                }
            } else {
                SaveablesList saveablesList = (SaveablesList) page
                        .getWorkbenchWindow().getWorkbench().getService(
                                ISaveablesLifecycleListener.class);
                ISaveablesSource[] nonPartSources = saveablesList.getNonPartSources();
                for (int i = 0; i < nonPartSources.length; i++) {
                    Saveable[] saveables = nonPartSources[i].getSaveables();
                    for (int j = 0; j < saveables.length; j++) {
                        if (saveables[j].isDirty()) {
                            setEnabled(true);
                            return;
                        }
                    }
                }
                setEnabled(false);
            }
        }
    }


    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        this.currentSelectedPart = part;
        if(part instanceof MTreeView){
            if(selection instanceof TreeSelection){
                if(((TreeSelection)selection).getFirstElement() instanceof IProjectElement){
                    currentSelection = (IProjectElement)((TreeSelection)selection).getFirstElement();
//                    if(((TreeSelection)selection).getFirstElement() instanceof IOntologyElement){
//                        String projectName = ((IProjectElement)((TreeSelection)selection).getFirstElement()).getProjectName();
//                        String ontologyUri = ((IOntologyElement)((TreeSelection)selection).getFirstElement()).getOntologyUri();
//                        this.currentSelection = projectName + ", " + ontologyUri; //$NON-NLS-1$
//                    }else{
//                        //NICO TODO
//                    }
                }
            }
        }else{
            this.currentSelection = null;
        }
        updateState();
    }
}
