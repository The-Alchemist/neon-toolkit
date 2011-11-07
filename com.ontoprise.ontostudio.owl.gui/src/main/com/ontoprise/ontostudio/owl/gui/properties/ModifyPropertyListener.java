/*****************************************************************************
 * Written by the NeOn Technologies Foundation
 *
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.gui.properties;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyListener;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.util.IRIUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLOntology;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.syntax.ISyntaxManager;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.annotationproperties.GetAnnotationPropertyRanges;

/**
 * @author Nico Stieler
 * Created on: 07.02.2011
 * 
 * class to handle the auto complete the type of a property
 */
public abstract class ModifyPropertyListener implements ModifyListener {

    /**
     * handle the auto complete the type of a data property
     * 
     * @param propertyText
     * @param type
     * @param systemChanged
     * @param userChanged
     * @param owlModel
     */ 
    protected void autoComplete1DataProperty(String propertyText, StyledText type, boolean[] systemChanged, boolean[] userChanged, OWLModel owlModel){
//        autoComplete1DataProperty(propertyText, propertyText, type, systemChanged, userChanged, owlModel);
        try{
            if(!userChanged[0]){
                ISyntaxManager manager = OWLPlugin.getDefault().getSyntaxManager();
                String uri = manager.parseUri(propertyText, owlModel);
                
    //            OWLDataProperty entity = _factory.getOWLDataProperty(IRI.create(uri));             
                OWLDataProperty entity = OWLUtilities.dataProperty(IRIUtils.ensureValidIRISyntax(uri));
                Set<OWLOntology> ontologies = new HashSet<OWLOntology>(); 
                Set<OWLModel> models = owlModel.getAllImportedOntologies();
                ontologies.add(owlModel.getOntology());
                for(OWLModel om : models){
                    ontologies.add(om.getOntology());
                }
                Set<OWLDataRange> ranges = entity.getRanges(ontologies);
                if(!(ranges == null || ranges.isEmpty())){
                    for(OWLDataRange range : ranges){
                        systemChanged[0] = true;
                        type.setText(range.toString());
                        break;
                    }
                }else{
                    systemChanged[0] = false;
                    type.setText(""); //$NON-NLS-1$
                }
            }
        } catch (NeOnCoreException e) {
            // nothing to do
        }
    }
        
    /**
     * handle the auto complete the type of a data property
     * 
     * @param propertyText
     * @param stringToCheckforEmpty
     * @param type
     * @param systemChanged
     * @param userChanged
     * @param owlModel
     */
    protected void autoComplete1DataProperty(String propertyText, String stringToCheckforEmpty, StyledText type, boolean[] systemChanged, boolean[] userChanged, OWLModel owlModel){
        try{
            if(!userChanged[0]){
                if(stringToCheckforEmpty.trim().isEmpty()){
                    systemChanged[0] = false;
                    type.setText(""); //$NON-NLS-1$
                }else{
                    ISyntaxManager manager = OWLPlugin.getDefault().getSyntaxManager();
                    String uri = manager.parseUri(propertyText, owlModel);
                    
        //            OWLDataProperty entity = _factory.getOWLDataProperty(IRI.create(uri));             
                    OWLDataProperty entity = OWLUtilities.dataProperty(IRIUtils.ensureValidIRISyntax(uri));
                    Set<OWLOntology> ontologies = new HashSet<OWLOntology>(); 
                    Set<OWLModel> models = owlModel.getAllImportedOntologies();
                    ontologies.add(owlModel.getOntology());
                    for(OWLModel om : models){
                        ontologies.add(om.getOntology());
                    }
                    Set<OWLDataRange> ranges = entity.getRanges(ontologies);
                    if(!(ranges == null || ranges.isEmpty())){
                        for(OWLDataRange range : ranges){
                            systemChanged[0] = true;
                            type.setText(IRIUtils.ensureValidIRISyntax(owlModel.getNamespaces().expandString(range.toString())));
                            break;
                        }
                    }else{
                        systemChanged[0] = false;
                        type.setText(""); //$NON-NLS-1$
                    }
                }
            }
        } catch (NeOnCoreException e) {
            // nothing to do
        }
    }
    
    /**
     * handle the auto complete the type of a annotation property
     * 
     * @param propertyText
     * @param type
     * @param systemChanged
     * @param userChanged
     * @param owlModel
     */
    protected void autoComplete1AnnotationProperty(String propertyText, StyledText type, boolean[] systemChanged, boolean[] userChanged, OWLModel owlModel){
        try{
            if(!userChanged[0]){
                if(propertyText.trim().isEmpty()){
                    systemChanged[0] = false;
                    type.setText(""); //$NON-NLS-1$
                }else{
                    ISyntaxManager manager = OWLPlugin.getDefault().getSyntaxManager();
                    String uri = manager.parseUri(propertyText, owlModel);
                    LinkedList<IRI> ranges = new LinkedList<IRI>(new GetAnnotationPropertyRanges(owlModel.getProjectId(), owlModel.getOntologyURI(),uri).getURIOfResults());
                    
                    Set<OWLModel> models = owlModel.getAllImportedOntologies();
                    for(OWLModel om : models){
                        try{
                            if(ranges == null)
                                ranges = new LinkedList<IRI>(new GetAnnotationPropertyRanges(om.getProjectId(), om.getOntology().toString(),uri).getURIOfResults());
                            else
                                if(ranges.isEmpty())
                                    ranges.addAll(new LinkedList<IRI>(new GetAnnotationPropertyRanges(om.getProjectId(), om.getOntology().toString(),uri).getURIOfResults()));
                                else
                                    break;
                        }catch (CommandException e1) {    
                            continue;
                        }
                    }
                    if(!(ranges == null || ranges.isEmpty())){
                        for(IRI range : ranges){
                            systemChanged[0] = true;
                            type.setText(IRIUtils.ensureValidIRISyntax(owlModel.getNamespaces().expandString(range.toString())));
                            break;
                        }
                    }else{
                        systemChanged[0] = false;
                        type.setText(""); //$NON-NLS-1$
                    }
                }
            }
        } catch (NeOnCoreException e) {
            // nothing to do
        } catch (CommandException e) {
            // nothing to do
        }
    }
    /**
     * helping method for auto complete the type of a property
     * 
     * @param typeText
     * @param systemChanged
     * @param userChanged
     */
    protected void autoComplete2Type(StyledText typeText, boolean[] systemChanged, boolean[] userChanged){
        if(systemChanged[0]){
            systemChanged[0] = false;
        }else{
            if(typeText.getText().trim().isEmpty()){
                userChanged[0] = false;
            }else{
                userChanged[0] = true;
            }
        }
    }
}
