/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.properties;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.properties.IImagePropertyPage;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.ShortFormProvider;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxFrameRenderer;
import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxObjectRenderer;
import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxPrefixNameShortFormProvider;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.OWLSharedImages;
import com.ontoprise.ontostudio.owl.gui.navigator.AbstractOwlEntityTreeElement;
import com.ontoprise.ontostudio.owl.gui.util.textfields.AxiomText;

/**
 * This class shows the source text of an entity, i.e. a frame-like representation of it in Manchester Syntax.
 *  
 * @author mer
 */
public class SourceViewTab extends AbstractOWLIdPropertyPage implements IImagePropertyPage{

    /**
     * 
     */
    private static final int MAX_NUMBER_OF_AXIOMS = 1000;
    /*
     * JFace Forms variables
     */
    private Section _frameSection;
    private Composite _frameComp;
    private AxiomText _frameText;
    
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
        _frameSection.setText(Messages.SourceViewTab_0); 

        _frameComp  =  _toolkit.createComposite(_frameSection, SWT.NONE);
        _toolkit.adapt(_frameComp);

        _frameSection.setClient(_frameComp);
        _frameComp.setLayout(new GridLayout());
        
        _axiomSection = _toolkit.createSection(body, Section.TITLE_BAR | Section.TWISTIE);
        _axiomSection.setText(Messages.SourceViewTab_1); 

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
            
            String frameText;

            if(getMainPage().getSelection().getFirstElement() instanceof AbstractOwlEntityTreeElement){
                AbstractOwlEntityTreeElement selectedTreeElement = (AbstractOwlEntityTreeElement)getMainPage().getSelection().getFirstElement();
                OWLEntity owlObject = selectedTreeElement.getEntity();
                frameText = renderFrames(owlObject);
    
            } else {
                frameText = Messages.SourceViewTab_2; 
            }

            _frameText.getStyledText().setText(frameText);

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
            
            String axiomText;

            if(getMainPage().getSelection().getFirstElement() instanceof AbstractOwlEntityTreeElement){
                AbstractOwlEntityTreeElement selectedTreeElement = (AbstractOwlEntityTreeElement)getMainPage().getSelection().getFirstElement();
                OWLEntity owlObject = selectedTreeElement.getEntity();
                axiomText = renderAxioms(owlObject);
    
            } else {
                axiomText = Messages.SourceViewTab_3; 
            }

            _axiomText.getStyledText().setText(axiomText);

        } catch (Exception e) {
            return;
            // ignore
            // at start-up time, the _owlModel is always null
        }

    }


    /**
     * @param owlEntity
     * @throws NeOnCoreException 
     */
    private String  renderFrames(OWLEntity owlEntity) throws NeOnCoreException {
        OWLOntology ontology = _owlModel.getOntology();
        OWLOntologyManager owlOntologyManager = ontology.getOWLOntologyManager();
        ShortFormProvider entityShortFormProvider = new ManchesterOWLSyntaxPrefixNameShortFormProvider(owlOntologyManager, ontology);
        StringWriter writer = new StringWriter();
        
        ManchesterOWLSyntaxFrameRenderer r = new ManchesterOWLSyntaxFrameRenderer(owlOntologyManager, ontology, writer, entityShortFormProvider);
        r.writeFrame(owlEntity);

        return writer.toString();
    }
        
    /**
     * @param owlEntity
     * @throws NeOnCoreException 
     */
    private String  renderAxioms(OWLEntity owlEntity) throws NeOnCoreException {
        boolean showImports = false;
//        int idDisplayStyle = NeOnUIPlugin.getDefault().getIdDisplayStyle();

        OWLOntology ontology = _owlModel.getOntology();
        OWLOntologyManager owlOntologyManager = ontology.getOWLOntologyManager();
        ShortFormProvider entityShortFormProvider = new ManchesterOWLSyntaxPrefixNameShortFormProvider(owlOntologyManager, ontology);
        StringWriter writer = new StringWriter();
        
        Collection<OWLAxiom> axioms = _owlModel.getAxioms((OWLEntity)owlEntity, showImports);
        SortedSet<OWLAxiom> sortedAxioms; 

        int size = axioms.size();
        if(size <= MAX_NUMBER_OF_AXIOMS) {
            sortedAxioms = new TreeSet<OWLAxiom>(axioms);
        } else {
            sortedAxioms = new TreeSet<OWLAxiom>();
            int i=0;
            for (OWLAxiom owlAxiom: axioms) {
                sortedAxioms.add(owlAxiom);
                if(++i > MAX_NUMBER_OF_AXIOMS){
                    break;
                }
            }
            axioms = null;
        }
        
        boolean first = true;
        for (OWLAxiom owlAxiom: sortedAxioms) {
            if(!first) {
                writer.append("\n"); //$NON-NLS-1$
            } else {
                first = false;
                if(size > MAX_NUMBER_OF_AXIOMS) {
                    writer.append("// There are "+size+" axioms for this entity.\n");  //$NON-NLS-1$//$NON-NLS-2$
                    writer.append("// Only the first "+MAX_NUMBER_OF_AXIOMS+" axioms will be listed here.\n\n"); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
            
            ManchesterOWLSyntaxObjectRenderer r = new ManchesterOWLSyntaxObjectRenderer(writer, entityShortFormProvider);
            owlAxiom.accept(r);

//            buffer.append(OWLGUIUtilities.getEntityLabel(
//                    (String[]) owlAxiom.accept(OWLPlugin.getDefault().getSyntaxManager().getVisitor(_owlModel, idDisplayStyle))));
        }
        return writer.toString();
        
//        return buffer.toString();
    }
        
    @Override
    public Image getImage() {
        return OWLPlugin.getDefault().getImageRegistry().get(OWLSharedImages.ONTOLOGY);
    }
}
