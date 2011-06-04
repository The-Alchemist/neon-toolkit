/**
 *
 */
package com.ontoprise.ontostudio.owl.gui.ontologyimportsgraph.contextandtooltip;

import net.sourceforge.jpowergraph.DefaultGraph;
import net.sourceforge.jpowergraph.layout.Layouter;
import net.sourceforge.jpowergraph.lens.LegendLens;
import net.sourceforge.jpowergraph.lens.RotateLens;
import net.sourceforge.jpowergraph.lens.ZoomLens;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.neontoolkit.jpowergraph.Messages;
import org.neontoolkit.jpowergraph.manipulator.contextandtooltip.OntoPowerGraphContextMenuListener;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.ontologyimportsgraph.nodes.contentprovider.OntologyNodeContentProvider;
import com.ontoprise.ontostudio.owl.gui.ontologyimportsgraph.ui.OntologyImportsGraphPropertyPage;

/**
 * @author Nico Stieler
 * Created on: 04.06.2011
 */
public class ImportingGraphContextMenuListener extends OntoPowerGraphContextMenuListener {



    /**
     * @param theGraph
     * @param theLegendLens
     * @param theZoomLens
     * @param theZoomLevels
     * @param theRotateLens
     * @param theRotateAngles
     */
    public ImportingGraphContextMenuListener(DefaultGraph theGraph, LegendLens theLegendLens, ZoomLens theZoomLens, Integer[] theZoomLevels, RotateLens theRotateLens, Integer[] theRotateAngles) {
        super(theGraph, theLegendLens, theZoomLens, theZoomLevels, theRotateLens, theRotateAngles);
        // TODO Auto-generated constructor stub
    }
    /**
     * @param theLayouter
     * @param theGraph
     * @param theLegendLens
     * @param theZoomLens
     * @param theZoomLevels
     * @param theRotateLens
     * @param theRotateAngles
     */
    public ImportingGraphContextMenuListener(Layouter theLayouter, DefaultGraph theGraph, LegendLens theLegendLens, ZoomLens theZoomLens, Integer[] theZoomLevels, RotateLens theRotateLens, Integer[] theRotateAngles) {
        super(theLayouter, theGraph, theLegendLens, theZoomLens, theZoomLevels, theRotateLens, theRotateAngles);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void fillBackgroundContextMenu(Menu theMenu) {

        final MenuItem showImported = new MenuItem(theMenu, SWT.ICON);
//        Image
        if(OntologyNodeContentProvider.SHOWIMPORTED)
            showImported.setImage(OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CHECK));
        showImported.setText(Messages.ImportingGraphContextMenuListener_Imported);
        showImported.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                doSelected();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
                doSelected();
            }
            private void doSelected(){
                OntologyImportsGraphPropertyPage.updateImported();
            }
        });
        showImported.setEnabled(true);
        
        MenuItem showImporting = new MenuItem(theMenu, SWT.ICON);
        if(OntologyNodeContentProvider.SHOWIMPORTING)
            showImporting.setImage(OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.CHECK));
        showImporting.setText(Messages.ImportingGraphContextMenuListener_Imported);
        showImporting.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent arg0) {
                doSelected();
            }
            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
                doSelected();
            }
            private void doSelected(){
                OntologyImportsGraphPropertyPage.updateImporting();
            }
        });
        showImporting.setEnabled(true);

        new MenuItem(theMenu, SWT.SEPARATOR);
        super.fillBackgroundContextMenu(theMenu);
        
    }
}
