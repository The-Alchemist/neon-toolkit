/**
 * Written by the NeOn Technologies Foundation Ltd.
 */
package com.ontoprise.ontostudio.owl.gui.navigator.ontology;

import java.util.LinkedList;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.exception.NeonToolkitExceptionHandler;
import org.neontoolkit.gui.navigator.elements.IProjectElement;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.vocab.OWLDataFactoryVocabulary;

import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;

/**
 * @author Nico Stieler
 * Created on: 29.03.2011
 */
public class AssertAllNotAssertedIndividualsHandler implements IObjectActionDelegate {

    private ISelection selection;

    @Override
    public void run(IAction action) {
        try {
            System.out.println("somebody pushed me"); //$NON-NLS-1$
            LinkedList<String> newAxiomsList = new LinkedList<String>();
            if(this.selection != null && 
                    selection instanceof TreeSelection && 
                    ((TreeSelection)selection).getFirstElement() instanceof OntologyTreeElement){
                OntologyTreeElement ontologyTreeElement = (OntologyTreeElement)((TreeSelection)selection).getFirstElement();
                String ontologyUri = ontologyTreeElement.getOntologyUri();
                String project = ontologyTreeElement.getProjectName();
                OWLModel owlModel = OWLModelFactory.getOWLModel(
                        ontologyUri, 
                        project);
                OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(project);
                for(OWLIndividual individual : owlModel.getAllUnassertedIndividuals()){
                    newAxiomsList.add(
                            OWLUtilities.toString(
                                    factory.getOWLClassAssertionAxiom(
                                            OWLDataFactoryVocabulary.OWLThing, individual)));
                }
                String[] newAxioms = newAxiomsList.toArray(new String[newAxiomsList.size()]);
                new ApplyChanges(project, ontologyUri, newAxioms, new String[0]).run();
            }
        } catch (NeOnCoreException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        } catch (CommandException e) {
            new NeonToolkitExceptionHandler().handleException(e);
        }
        
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
        
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart ActivePart) {
        // TODO Auto-generated method stub
        
    }

}
