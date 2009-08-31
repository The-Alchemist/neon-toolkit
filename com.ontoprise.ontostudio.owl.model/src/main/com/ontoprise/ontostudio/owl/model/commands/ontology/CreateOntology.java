/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Created on: 16.06.2009
 * Created by: diwe
 ******************************************************************************/
package com.ontoprise.ontostudio.owl.model.commands.ontology;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.neontoolkit.core.NeOnCorePlugin;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.command.DatamodelCommand;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.ontoprise.ontostudio.owl.model.OWLManchesterProjectFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;

/**
 * @author diwe
 *
 */
public class CreateOntology extends DatamodelCommand {
    
    public CreateOntology(String project, String ontologyUri, String defaultNamespace, String fileName) {
        super(project, ontologyUri, defaultNamespace, fileName);
    }

    @Override
    protected void perform() throws CommandException {
        try {
            IOntologyProject ontologyProject = NeOnCorePlugin.getDefault().getOntologyProject(getProjectName());
            String ontologyURI = (String)getArgument(1);
            String defaultNamespace = (String)getArgument(2);
            String fileName = (String)getArgument(3);
            
            // TODO migration: hack to make the adapter work for the moment... 
            // will most likely not work for the export since the filename is missing
            if (!OWLManchesterProjectFactory.FACTORY_ID.equals(ontologyProject.getProjectFactoryId())) {
                
                // KAON2 context
                ontologyProject.createOntology(ontologyURI, defaultNamespace);
                getOntologyProject().readAndDispatchWhileWaitingForEvents();
            } else {
                
                // Manchester context
                IProject project = NeOnCorePlugin.getDefault().getProject(getProjectName());
                if (fileName == null || "".equals(fileName)) {
                    fileName = ontologyProject.getNewOntologyFilenameFromURI(ontologyURI, null);
                }
                String physicalUri = project.getFile(fileName).getLocationURI().toString();
                // MAPI remove OWLOntologyManager
                OWLOntologyManager manager = ontologyProject.getAdapter(OWLOntologyManager.class);
                OWLOntology ontology = manager.createOntology(IRI.create(ontologyURI));
                manager.setPhysicalURIForOntology(ontology, URI.create(physicalUri));
                ontologyProject.readAndDispatchWhileWaitingForEvents();
                
                // The manchester implementation has no event for new created ontologies, we need to update manually.
                ontologyProject.addOntology(ontologyURI);

                try {
                    if (defaultNamespace != null && !"".equals(defaultNamespace)) {
                        ontologyProject.setDefaultNamespace(ontologyURI, defaultNamespace);
                    }
                    ontologyProject.setNamespacePrefix(ontologyURI, "owl", OWLNamespaces.OWL_NS);
                    ontologyProject.setNamespacePrefix(ontologyURI, "owl",OWLNamespaces.OWL_NS); //$NON-NLS-1$
                    ontologyProject.setNamespacePrefix(ontologyURI, "owl2",org.semanticweb.owlapi.vocab.Namespaces.OWL2.toString()); //$NON-NLS-1$
                    ontologyProject.setNamespacePrefix(ontologyURI, "swrl",OWLNamespaces.SWRL_NS); //$NON-NLS-1$
                    ontologyProject.setNamespacePrefix(ontologyURI, "swrlb",OWLNamespaces.SWRLB_NS); //$NON-NLS-1$
                    ontologyProject.setNamespacePrefix(ontologyURI, "swrlx",OWLNamespaces.SWRLX_NS); //$NON-NLS-1$
                    ontologyProject.setNamespacePrefix(ontologyURI, "rdf",OWLNamespaces.RDF_NS); //$NON-NLS-1$
                    ontologyProject.setNamespacePrefix(ontologyURI, "rdfs",OWLNamespaces.RDFS_NS); //$NON-NLS-1$
                    ontologyProject.setNamespacePrefix(ontologyURI, "xsd",OWLNamespaces.XSD_NS); //$NON-NLS-1$
                } catch (NeOnCoreException e) {
                    throw new CommandException(e);
                }
            }
            
        } catch (OWLOntologyCreationException e) {
            throw new CommandException(e);
        } catch (NeOnCoreException nce) {
            throw new CommandException(nce);
        }
    }
}
