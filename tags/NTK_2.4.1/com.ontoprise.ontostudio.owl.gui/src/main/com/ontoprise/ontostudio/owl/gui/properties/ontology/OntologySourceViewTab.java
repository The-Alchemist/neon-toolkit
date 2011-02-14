/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties.ontology;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.coode.owlapi.functionalrenderer.OWLFunctionalSyntaxRenderer;
import org.coode.owlapi.owlxml.renderer.OWLXMLObjectRenderer;
import org.coode.owlapi.owlxml.renderer.OWLXMLWriter;
import org.coode.owlapi.rdf.rdfxml.RDFXMLRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.Section;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.properties.IImagePropertyPage;
import org.semanticweb.owlapi.io.OWLRendererException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.ShortFormProvider;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxObjectRenderer;
import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxPrefixNameShortFormProvider;
import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxRenderer;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.navigator.ontology.OntologyTreeElement;
import com.ontoprise.ontostudio.owl.gui.properties.AbstractOWLIdPropertyPage;
import com.ontoprise.ontostudio.owl.gui.util.textfields.AxiomText;

/**
 * This class is essentially a copy of AnnotationsPropertyPage2. 
 * This is not good but due to the current EPV framework I did not manage to unify both into one class :( 
 * 
 * @author Nico Stieler
 */
public class OntologySourceViewTab extends AbstractOWLIdPropertyPage implements IImagePropertyPage{
    private static final int FUNCTIONAL = 0;
    private static final int MANCHESTER = 1;
    private static final int OWLRDF = 2;
    private static final int OWLXML = 3;

    /*
     * JFace Forms variables
     */
    private Section _frameSection;
    private Composite _frameComp;
    private AxiomText _frameText;
    
    Button _functionalButton;
    Button _manchesterButton;
    Button _owlRdfButton;
    Button _owlXmlButton;
    
    private Section _axiomSection;
    private Composite _axiomComp;
    private AxiomText _axiomText;
    
    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.owl.gui.properties.BasicOWLEntityPropertyPage#createMainArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public Composite createContents(Composite parent) {
        Composite body = prepareForm(parent);

        _frameSection = _toolkit.createSection(body, Section.TITLE_BAR | Section.TWISTIE | Section.EXPANDED);
        _frameSection.setText(Messages.OntologySourceViewTab_0);
        _frameComp  =  _toolkit.createComposite(_frameSection, SWT.NONE);
        _toolkit.adapt(_frameComp);

        _frameSection.setClient(_frameComp);
        _frameComp.setLayout(new GridLayout());
        
        
        Listener listener = new Listener() {
            @Override
            public void handleEvent(Event event) {
                initContents();
            }
        };
        Composite radioBar = new Composite(_frameComp, SWT.NULL);
        radioBar.setLayout(new RowLayout());

        Label label = new Label(radioBar, SWT.NULL);
        label.setText(Messages.OntologySourceViewTab_1);
                
        _functionalButton = new Button(radioBar, SWT.RADIO);
        _functionalButton.setText(Messages.OntologySourceViewTab_4);
        _functionalButton.setSelection(false);
        _functionalButton.addListener(SWT.Selection, listener);
        
        _manchesterButton = new Button(radioBar, SWT.RADIO);
        _manchesterButton.setText(Messages.OntologySourceViewTab_3);
        _manchesterButton.setSelection(false);
        _manchesterButton.addListener(SWT.Selection, listener);
        
        _owlRdfButton = new Button(radioBar, SWT.RADIO);
        _owlRdfButton.setText(Messages.OntologySourceViewTab_2);
        _owlRdfButton.setSelection(true);
        _owlRdfButton.addListener(SWT.Selection, listener);
        
        _owlXmlButton = new Button(radioBar, SWT.RADIO);
        _owlXmlButton.setText(Messages.OntologySourceViewTab_8);
        _owlXmlButton.setSelection(false);
        _owlXmlButton.addListener(SWT.Selection, listener);
        
        _axiomSection = _toolkit.createSection(body, Section.TITLE_BAR | Section.TWISTIE);
        _axiomSection.setText(Messages.OntologySourceViewTab_5); 

        _axiomComp  =  _toolkit.createComposite(_axiomSection, SWT.NONE);
        _toolkit.adapt(_axiomComp);

        _axiomSection.setClient(_axiomComp);
        _axiomComp.setLayout(new GridLayout());
        
        initContents();
        return _frameSection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.owl.gui.properties.BasicOWLEntityPropertyPage#getSections()
     */
    @Override
    protected List<Section> getSections() {
        List<Section> sections = new ArrayList<Section>();
        sections.add(_frameSection);
        return sections;
    }
    
    @Override
    public void dispose() {
        super.dispose();
    }

    @Override
    public void refresh() {
        super.refresh();
        initContents();

        layoutSections();
        _form.reflow(true);
    }

    @Override
    public void update() {
        super.update();
        initContents();

        layoutSections();
        _form.reflow(true);
    }

    private void initContents() {
        try {
            if(_ontologyUri != null) {
                updateOwlModel();
                if(_owlModel != null) {
                    if(_frameText == null) {
                        _frameText = new AxiomText(_frameComp, _owlModel, _owlModel);
                        _frameText.getStyledText().setEditable(false);
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
            
            StringWriter frameText;

            if(getMainPage().getSelection().getFirstElement() instanceof OntologyTreeElement){
                int type = 0;
                if(_functionalButton.getSelection()) {
                    type = FUNCTIONAL;
                } else if(_manchesterButton.getSelection()) {
                    type = MANCHESTER;
                } else if(_owlRdfButton.getSelection()) {
                    type = OWLRDF;
                } else if(_owlXmlButton.getSelection()) {
                    type = OWLXML;
                }
                frameText = renderOntology(type);
    
            } else {
                frameText = new StringWriter();
                frameText.append(Messages.OntologySourceViewTab_6); 
            }

            _frameText.getStyledText().setText(frameText.toString());

        } catch (Exception e) {
            // ignore
            // at start-up time, the _owlModel is always null
        }
        
        try {
            if(_ontologyUri != null) {
                updateOwlModel();
                if(_owlModel != null) {
                    if(_axiomText == null) {
                        _axiomText = new AxiomText(_axiomComp, _owlModel, _owlModel);
                        _axiomText.getStyledText().setEditable(false);
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
            
            StringWriter axiomText;

            if(getMainPage().getSelection().getFirstElement() instanceof OntologyTreeElement){
                axiomText = renderAxioms();
    
            } else {
                axiomText = new StringWriter();
                axiomText.append(Messages.OntologySourceViewTab_7); 
            }

            _axiomText.getStyledText().setText(axiomText.toString());

        } catch (Exception e) {
            return;
            // ignore
            // at start-up time, the _owlModel is always null
        }

    }


    /**
     * @param buffer
     * @throws NeOnCoreException 
     * @throws NeOnCoreException 
     * @throws OWLRendererException 
     * @throws IOException 
     * @throws OWLException 
     */
    private StringWriter renderOntology(int type) throws NeOnCoreException, OWLRendererException {
        
        OWLOntology ontology = _owlModel.getOntology();
        StringWriter writer = new StringWriter();

        switch(type){
            case FUNCTIONAL: {
                OWLFunctionalSyntaxRenderer renderer = new OWLFunctionalSyntaxRenderer(ontology.getOWLOntologyManager());
                renderer.render(ontology, writer);
                break;
            } case MANCHESTER: {
                ManchesterOWLSyntaxRenderer renderer = new ManchesterOWLSyntaxRenderer(ontology.getOWLOntologyManager());
                renderer.render(ontology, writer);
                break;
            } case OWLXML: {
                OWLXMLWriter xmlWriter = new OWLXMLWriter(writer, ontology);
                OWLXMLObjectRenderer renderer = new OWLXMLObjectRenderer(ontology, xmlWriter);
                renderer.visit(ontology);
            } case OWLRDF: {
                RDFXMLRenderer renderer = new RDFXMLRenderer(ontology.getOWLOntologyManager(), ontology, writer);
                try {
                    renderer.render();
                } catch (IOException e) {
                    writer.append(e.getLocalizedMessage());
                    writer.append("\n"); //$NON-NLS-1$
                    PrintWriter s = new PrintWriter(writer);
                    e.printStackTrace(s);
                    s.close();
                }
            }
        }

        writer.flush();
        return writer;
    }
        
    /**
     * @param owlEntity
     * @throws NeOnCoreException 
     */
    private StringWriter  renderAxioms() throws NeOnCoreException {

        OWLOntology ontology = _owlModel.getOntology();
        OWLOntologyManager owlOntologyManager = ontology.getOWLOntologyManager();
        ShortFormProvider entityShortFormProvider = new ManchesterOWLSyntaxPrefixNameShortFormProvider(owlOntologyManager, ontology);
        StringWriter writer = new StringWriter();
        
        Collection<OWLAxiom> axioms = ontology.getAxioms();
        SortedSet<OWLAxiom> sortedAxioms = new TreeSet<OWLAxiom>(axioms);

        
        boolean first = true;
        for (OWLAxiom owlAxiom: sortedAxioms) {
            if(!first) {
                writer.append("\n"); //$NON-NLS-1$
            } else {
                first = false;
            }
            
            ManchesterOWLSyntaxObjectRenderer r = new ManchesterOWLSyntaxObjectRenderer(writer, entityShortFormProvider);
            owlAxiom.accept(r);
        }

        writer.flush();
        return writer;
    }
        
    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ONTOLOGY);
    }
}
