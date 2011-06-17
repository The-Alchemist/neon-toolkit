/**
 * Written by the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.owl.gui.navigator.ontology;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ObjectPluginAction;
import org.neontoolkit.gui.navigator.MTreeView;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.io.actions.LoadOntologyFromWebHandler;
import com.ontoprise.ontostudio.owl.gui.io.actions.OpenOntologyHandler;
import com.ontoprise.ontostudio.owl.gui.util.RadioButtonsDialog;

/**
 * @author Nico Stieler
 * Created on: 08.06.2011
 */
@SuppressWarnings("restriction")
public class LoadUnloadedOntologyHandler implements IObjectActionDelegate {


    @Override
    public void run(IAction action) {
        if(action instanceof ObjectPluginAction){
            ISelection selection = ((ObjectPluginAction)action).getSelection();
            if(selection != null && 
                    selection instanceof TreeSelection && 
                    ((TreeSelection)selection).getFirstElement() instanceof UnloadedOntologyTreeElement){
                TreeSelection treeSelection = (TreeSelection)selection;
                UnloadedOntologyTreeElement unloadedOntologyTreeElement = (UnloadedOntologyTreeElement)((TreeSelection)selection).getFirstElement();
                String ontologyUri = unloadedOntologyTreeElement.getOntologyUri();
                doPerform(ontologyUri, treeSelection);
            }
        }
    }
    public void run(String ontologyUri, IStructuredSelection treeSelection) {
        doPerform(ontologyUri, treeSelection);
    }
    
    private void doPerform(String ontologyUri, IStructuredSelection treeSelection) {
        MTreeView view = ((MTreeView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MTreeView.ID));
        view.getTreeViewer().getTree().getShell();
        String message = Messages.LoadUnloadedOntologyHandler_dialog_message_0 + ontologyUri + Messages.LoadUnloadedOntologyHandler_dialog_message_1;
        String[] choices = new String[]{
                Messages.LoadUnloadedOntologyHandler_dialog_loadUnloadedOnto_local,
                Messages.LoadUnloadedOntologyHandler_dialog_loadUnloadedOnto_web,
                Messages.LoadUnloadedOntologyHandler_dialog_createUnloadedOnto_new};
        RadioButtonsDialog dialog = new RadioButtonsDialog(
                view.getTreeViewer().getTree().getShell(),
                Messages.LoadUnloadedOntologyHandler_dialog_title, 
                message,
                Messages.LoadUnloadedOntologyHandler_dialog_radioButtons_title,
                choices);
        if(dialog.open() == Dialog.OK){
            String selected = dialog.getSelected();
            try {
                if(selected.equals(Messages.LoadUnloadedOntologyHandler_dialog_createUnloadedOnto_new)){
                    NewOWLOntologyHandler handler = new NewOWLOntologyHandler();
                    handler.fixedProject(treeSelection, ontologyUri);
                    handler.execute(new ExecutionEvent());
                }else if(selected.equals(Messages.LoadUnloadedOntologyHandler_dialog_loadUnloadedOnto_local)){
                    OpenOntologyHandler handler = new OpenOntologyHandler();
                    handler.fixedProject(treeSelection, ontologyUri);
                    handler.execute(null);
                }else if(selected.equals(Messages.LoadUnloadedOntologyHandler_dialog_loadUnloadedOnto_web)){
                    LoadOntologyFromWebHandler handler = new LoadOntologyFromWebHandler();
                    handler.fixedProject(treeSelection, ontologyUri);
                    handler.execute(null);
                }
                ((MTreeView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(MTreeView.ID)).getTreeViewer().refresh();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            }
        }
    }
    @Override
    public void selectionChanged(IAction arg0, ISelection arg1) {
    }
    @Override
    public void setActivePart(IAction arg0, IWorkbenchPart arg1) {
        
    }


}
