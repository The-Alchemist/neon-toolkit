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
import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.gui.navigator.project.OWLProjectTreeElement;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLMainIDPropertyPage;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.softwareag.neontoolkit.ontostat.StatsPlugin;
import com.softwareag.neontoolkit.ontostat.StatsProvider;

@SuppressWarnings("nls")
public class ProjectStatsPropertyPage extends AbstractOWLMainIDPropertyPage {

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
        gridLayout.numColumns = 3;
        _statsComp.setLayout(gridLayout);
        ColumnLayoutData data = new ColumnLayoutData();
        _statsComp.setLayoutData(data);

        (new Label(_statsComp, SWT.NONE)).setText("sss");
        _toolkit.adapt(_statsComp);
        _statsSection.setClient(_statsComp);
    }

    private void initStatsSection() {
        clearComposite(_statsComp);

       	Object otesel = getMainPage().getSelection().getFirstElement();
       	if (otesel instanceof OWLProjectTreeElement) {
       		_project=((OWLProjectTreeElement)otesel).getProjectName();
       	}

        StatsProvider[] providers = StatsPlugin.getDefault().getStatsProviders();
        for (StatsProvider provider:providers) {
        	(new Label(_statsComp, SWT.NONE)).setImage(provider.getIconImage());
        	(new Label(_statsComp, SWT.NONE)).setText(provider.getTitle());
        	
            Text statText = new Text(_statsComp, SWT.SINGLE | SWT.BORDER);
        	GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
            gridData.widthHint=50;
            statText.setLayoutData(gridData);
            statText.setEditable(false);
            String value = "";
            int intValue = 0;
            try {
                for (OWLModel owlModel: OWLModelFactory.getOWLModels(_project)) {
                    Object o = provider.getValue(owlModel);
                    if(o instanceof Integer) {
                        intValue += (Integer)o;
                    } else {
                        value += o.toString();
                    }
                }
            } catch (NeOnCoreException e) {
                e.printStackTrace();
            }
            if(value.length() > 0) {
                statText.setText(value);
            } else {
                statText.setText(""+intValue);
            }
        }        
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}
}
