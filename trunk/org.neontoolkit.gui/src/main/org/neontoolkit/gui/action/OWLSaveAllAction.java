/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 * based on SaveAllAction by IBM
 ******************************************************************************/
package org.neontoolkit.gui.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.ISaveablesLifecycleListener;
import org.eclipse.ui.ISaveablesSource;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.Saveable;
import org.eclipse.ui.internal.SaveAllAction;
import org.eclipse.ui.internal.SaveablesList;
import org.eclipse.ui.internal.WorkbenchPage;
import org.neontoolkit.gui.navigator.MTreeView;

/**
 * Global action that saves all targets in the
 * workbench that implement ISaveTarget interface.
 * The action keeps track of opened save targets
 * and their 'save' state. If none of the currently
 * opened targets needs saving, it will disable.
 * This action is somewhat different from all
 * other global actions in that it works on
 * multiple targets at the same time i.e. it
 * does not disconnect from the target when it
 * becomes deactivated.
 */
@SuppressWarnings("restriction")
public class OWLSaveAllAction extends SaveAllAction {
    /**
     * List of parts (element type: <code>IWorkbenchPart</code>)
     * against which this class has outstanding property listeners registered.
     */
    private List<IWorkbenchPart> partsWithListeners = new ArrayList<IWorkbenchPart>(1);
    private IWorkbenchPart openPart;
    
    /**
     * The default constructor.
     *
     * @param window the window
     */
    public OWLSaveAllAction(IWorkbenchWindow window) {
        super(window);
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
        if (part instanceof ISaveablePart) {
            part.removePropertyListener(this);
            partsWithListeners.remove(part);
            updateState();
        }
    }
    
    /* (non-Javadoc)
     * Method declared on PartEventAction.
     */
    @Override
    public void partOpened(IWorkbenchPart part) {
        super.partOpened(part);
        if (part instanceof ISaveablePart) {
            part.addPropertyListener(this);
            partsWithListeners.add(part);
            openPart = part;
            updateState();
            openPart = null;
        }
    }
    
    /* (non-Javadoc)
     * Method declared on IPropertyListener.
     */
    @Override
    public void propertyChanged(Object source, int propID) {
        if (source instanceof ISaveablePart) {
            if (propID == ISaveablePart.PROP_DIRTY) {
                updateState();
            }
        }
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
                if(dirtyPart instanceof MTreeView)
                    ((MTreeView)dirtyPart).doSaveAll(monitor);
                else{
                    dirtyPart.doSave(monitor);
                }
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
        if (openPart != null && openPart.getSite().getPage().equals(getActivePage()) && ((ISaveablePart) openPart).isDirty()) {
            setEnabled(true);
        }
        else {
            WorkbenchPage page = (WorkbenchPage) getActivePage();
            if (page == null) {
                setEnabled(false);
            } else {
//                ISaveablePart[] dirtyParts = page.getDirtyParts();
                if (page.getDirtyParts().length > 0) {
                    setEnabled(true);
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
    }

    /* (non-Javadoc)
     * Method declared on PageEventAction.
     */
    @Override
    public void dispose() {
        super.dispose();
        for(IWorkbenchPart part : partsWithListeners){
            part.removePropertyListener(this);
        }
        partsWithListeners.clear();
    }
}
