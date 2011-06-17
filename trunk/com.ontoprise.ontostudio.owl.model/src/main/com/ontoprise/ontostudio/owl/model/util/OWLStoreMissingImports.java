/**
 * written by the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.owl.model.util;

import java.util.HashMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.semanticweb.owlapi.model.MissingImportEvent;
import org.semanticweb.owlapi.model.MissingImportListener;

import com.ontoprise.ontostudio.owl.model.Messages;

/**
 * @author Nico Stieler
 * Created on: 07.06.2011
 */
public class OWLStoreMissingImports {
    private static MissingImportListener _missingImportListener;
    private static HashMap<String, Boolean> isStillMissingMap = new HashMap<String,Boolean>();

    public static MissingImportListener getListener() {
        if(_missingImportListener == null)
            _missingImportListener= new MissingImportListener() {
                @Override
                public void importMissing(MissingImportEvent event) {
                    isStillMissingMap.put(event.getImportedOntologyURI().toString(), true);
                    showWarningDialog();
                }
            };
        return _missingImportListener;
    }
    public static boolean isStillMissingMap(String key){
        Boolean value = isStillMissingMap.get(key);
        return (value != null && value);
    }
    public static void loadedMissing(String key){
        isStillMissingMap.remove(key);
    }
    private static void showWarningDialog() {
        try{
            final Shell shell = new Shell(new Display());
            final String title = Messages.OWLStoreMissingImports_title;
            final String message =  Messages.OWLStoreMissingImports_message;
            shell.getDisplay().syncExec(new Runnable() {
                @Override
                public void run() {
                    MessageDialog.openWarning(shell, title, message);
                }
            });
        } catch (RuntimeException e) {
            // nothing to do
        }
    }
}
