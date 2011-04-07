/*****************************************************************************
 * written by the NeOn Technologies Foundation Ltd.
 * based on NavigationHistoryAction by IBM
 ******************************************************************************/
package org.neontoolkit.gui.action;

import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.internal.IWorkbenchHelpContextIds;
import org.eclipse.ui.internal.NavigationHistoryAction;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.history.IOWLHistoryEntry;
import org.neontoolkit.gui.history.OWLHistoryManager;
/**
 * The <code>OWLNavigationHistoryAction</code> moves navigation history
 * back and forward.
 * 
 * @author Nico Stieler
 * Created on: 10.03.2011
 */
@SuppressWarnings("restriction")
public class OWLNavigationHistoryAction extends NavigationHistoryAction
//PageEventAction 
{

    private boolean forward;
    
    private Menu historyMenu;
    
    private class MenuCreator implements IMenuCreator {
        @Override
        public void dispose() {
            if (historyMenu != null) {
                for (int i = 0; i < historyMenu.getItemCount(); i++) {
                    MenuItem menuItem = historyMenu.getItem(i);
                    menuItem.setData(null);
                }
                historyMenu.dispose();
                historyMenu = null;
            }
        }
        @Override
        public Menu getMenu(Menu parent) {
            dispose();
            historyMenu = new Menu(parent);
            IWorkbenchPage page = getWorkbenchWindow().getActivePage();
            if (page == null) {
                return historyMenu;
            }
            IOWLHistoryEntry[] entries;
            if (forward) {
                entries = OWLHistoryManager.getForwardEntries();
            } else {
                entries = OWLHistoryManager.getBackwardEntries();
            }
            for (int i = 0; i < entries.length; i++) {
                String text = entries[i].toString();
                if (text != null) {
                    MenuItem item = new MenuItem(historyMenu, SWT.NONE);
                    item.setData(entries[i]);
                    item.setText(text);
                    item.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            try {
                                ((IOWLHistoryEntry)e.widget.getData()).restoreLocation();
                            } catch (NeOnCoreException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                }
            }
            return historyMenu;
        }
        @Override
        public Menu getMenu(Control parent) {
            dispose();
            historyMenu = new Menu(parent);
            IWorkbenchPage page = getWorkbenchWindow().getActivePage();
            if (page == null) {
                return historyMenu;
            }
            IOWLHistoryEntry[] entries;
            if (forward) {
                entries = OWLHistoryManager.getForwardEntries();
            } else {
                entries = OWLHistoryManager.getBackwardEntries();
            }
            for (int i = 0; i < entries.length; i++) {
                String text = entries[i].toString();
                if (text != null) {
                    MenuItem item = new MenuItem(historyMenu, SWT.NONE);
                    item.setData(entries[i]);
                    item.setText(text);
                    item.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            try {
                                ((IOWLHistoryEntry)e.widget.getData()).restoreLocation();
                            } catch (NeOnCoreException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                }
            }
            return historyMenu;
        }
    }
    /**
     * Create a new instance of <code>NavigationHistoryAction</code>
     *
     * @param window the workbench window this action applies to
     * @param forward if this action should move history forward of backward
     */
    public OWLNavigationHistoryAction(IWorkbenchWindow window, boolean forward) {
        super(window, forward);
        ISharedImages sharedImages = window.getWorkbench().getSharedImages();
        if (forward) {
            OWLHistoryManager.setForwardAction(this);
            setText(WorkbenchMessages.NavigationHistoryAction_forward_text);
            setToolTipText(WorkbenchMessages.NavigationHistoryAction_forward_toolTip);
            window.getWorkbench().getHelpSystem().setHelp(this,IWorkbenchHelpContextIds.NAVIGATION_HISTORY_FORWARD);
            setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD));
            setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_FORWARD_DISABLED));
            setActionDefinitionId("org.eclipse.ui.navigate.forwardHistory"); //$NON-NLS-1$
        } else {
            OWLHistoryManager.setBackwardAction(this);
            setText(WorkbenchMessages.NavigationHistoryAction_backward_text);
            setToolTipText(WorkbenchMessages.NavigationHistoryAction_backward_toolTip);
            window.getWorkbench().getHelpSystem().setHelp(this,IWorkbenchHelpContextIds.NAVIGATION_HISTORY_BACKWARD);
            setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK));
            setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_BACK_DISABLED));
            setActionDefinitionId("org.eclipse.ui.navigate.backwardHistory"); //$NON-NLS-1$
        }
        setEnabled(false);
        this.forward = forward;
        setMenuCreator(new MenuCreator());
        
    }
    /* (non-Javadoc)
     * Method declared on PageEventAction.
     */
    @Override
    public void pageClosed(IWorkbenchPage page) {
        super.pageClosed(page);
        setEnabled(false);
    }
    /* (non-Javadoc)
     * Method declared on PageEventAction.
     */
    @Override
    public void pageActivated(IWorkbenchPage page) {
        super.pageActivated(page);
    }
    /* (non-Javadoc)
     * Method declared on IAction.
     */
    @Override
    public void run() {
        try {
            if (forward) {
                if(OWLHistoryManager.hasNext())
                        OWLHistoryManager.getNext().restoreLocation();
            } else {
                if(OWLHistoryManager.hasPrevious())
                    OWLHistoryManager.getPrevious().restoreLocation();
            }
        } catch (NeOnCoreException e) {
            e.printStackTrace();
        }   
    }
    
    @Override
    public void update() {
        IOWLHistoryEntry[] entries;
        if (forward) {
            setEnabled(OWLHistoryManager.hasNext());
            entries = OWLHistoryManager.getForwardEntries();
            if (entries.length > 0) {
                IOWLHistoryEntry entry = entries[0];
                String text = NLS.bind(WorkbenchMessages.NavigationHistoryAction_forward_toolTipName, entry.toString());
                setToolTipText(text);
            } else {
                setToolTipText(WorkbenchMessages.NavigationHistoryAction_forward_toolTip);
            }
        } else {
            setEnabled(OWLHistoryManager.hasPrevious());
            entries = OWLHistoryManager.getBackwardEntries();
            if (entries.length > 0) {
                IOWLHistoryEntry entry = entries[0];
                String text = NLS.bind(WorkbenchMessages.NavigationHistoryAction_backward_toolTipName, entry.toString());
                setToolTipText(text);
            } else {
                setToolTipText(WorkbenchMessages.NavigationHistoryAction_backward_toolTip);
            }
        }
    }
}
