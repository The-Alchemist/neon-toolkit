package com.softwareag.neontoolkit.ontostat.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.Section;

import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLMainIDPropertyPage;
import com.softwareag.neontoolkit.ontostat.StatsPlugin;
import com.softwareag.neontoolkit.ontostat.StatsProvider;
/**
 * 
 * @author Nico Stieler
 */
@SuppressWarnings("nls")
public class OntologyStatsPropertyPage extends AbstractOWLMainIDPropertyPage {

	
	private Section _statsSection;
	private Composite _statsComp;
	
	@Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_statsSection);
        return sections;
	}

	@Override
    protected String getTitle() {
		return "Statistics";
	}
	  
    
    @Override
    public void refreshComponents() {
        super.refreshComponents();
        initStatsSection();
        layoutSections();
        _form.reflow(true);
    }

    @Override
    public void updateComponents() {
        super.updateComponents();
        initStatsSection();
        layoutSections();
        _form.reflow(true);
    }

    
	@Override
    protected void createMainArea(Composite composite) {
//      PlatformUI.getWorkbench().getHelpSystem().setHelp(composite, IHelpContextIds.OWL_ONTOLOGY_VIEW);
      super.createMainArea(composite);
      Composite body = prepareForm(composite);

      createStatsArea(body);

      _form.reflow(true);
  }
	
    private void createStatsArea(Composite composite) {
    	_statsSection = _toolkit.createSection(composite, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
    	_statsSection.setText("Statistics");
    	_statsSection.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                _form.reflow(true);
            }
        });
        _statsComp = _toolkit.createComposite(_statsSection, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns=4;
        _statsComp.setLayout(gridLayout);
        ColumnLayoutData data = new ColumnLayoutData();
        _statsComp.setLayoutData(data);

        (new Label(_statsComp, SWT.NONE)).setText("sss");
        _toolkit.adapt(_statsComp);
        _statsSection.setClient(_statsComp);
        createTitleRow();
    }

    private void createTitleRow(){
        (new Label(_statsComp, SWT.NONE)).setText("");
        (new Label(_statsComp, SWT.NONE)).setText("");
        (new Label(_statsComp, SWT.NONE)).setText("local");
        (new Label(_statsComp, SWT.NONE)).setText("incl. imports");
        
    }
    private void initStatsSection() {
        clearComposite(_statsComp);
        createTitleRow();
        

       	Object otesel =getMainPage().getSelection().getFirstElement();
       	if (otesel instanceof OntologyTreeElement) {
       		_project=((OntologyTreeElement)otesel).getProjectName();
       		_ontologyUri=((OntologyTreeElement)otesel).getOntologyUri();
       	}
    	if (_ontologyUri!=null) {
    		updateOwlModel();
    		if (_owlModel==null)
    			return;
    	}
    	
        StatsProvider[] providers = StatsPlugin.getDefault().getStatsProviders();
        for (StatsProvider provider:providers) {

            Label image = new Label(_statsComp, SWT.NONE);
            image.setImage(provider.getIconImage());
            
            Label title = new Label(_statsComp, SWT.NONE);
            title.setText(provider.getTitle());
        	
            Text statLocalText = new Text(_statsComp, SWT.SINGLE | SWT.BORDER);
        	GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
            gridData.widthHint=50;
            statLocalText.setLayoutData(gridData);
            statLocalText.setEditable(false);
            statLocalText.setText(provider.getLocalValue(_owlModel).toString());
            
            Text statGlobalText = new Text(_statsComp, SWT.SINGLE | SWT.BORDER);
            GridData gridData2 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
            gridData2.widthHint=50;
            statGlobalText.setLayoutData(gridData);
            statGlobalText.setEditable(false);
            statGlobalText.setText(provider.getGlobalValue(_owlModel).toString());

            if(provider.getImageTooltip() != null){
                image.setToolTipText(provider.getImageTooltip());
            }
            if(provider.getTitleTooltip() != null){
                title.setToolTipText(provider.getTitleTooltip());
            }
            if(provider.getLocalTooltip() != null){
                statLocalText.setToolTipText(provider.getLocalTooltip());
            }
            if(provider.getGlobalTooltip() != null){
                statGlobalText.setToolTipText(provider.getGlobalTooltip());
            }
        }        

    }

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
