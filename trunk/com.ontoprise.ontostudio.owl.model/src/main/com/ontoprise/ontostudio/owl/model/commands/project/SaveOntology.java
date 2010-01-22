/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.project;

import java.net.URI;
import java.util.Iterator;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.coode.owlapi.turtle.TurtleOntologyFormat;
import org.eclipse.core.resources.IResource;
import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.core.project.IOntologyProject;
import org.neontoolkit.core.project.OntologyProjectManager;
import org.semanticweb.owlapi.io.OWLFunctionalSyntaxOntologyFormat;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import com.ontoprise.ontostudio.owl.model.OWLConstants;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLNamespaces;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

public class SaveOntology extends OWLModuleChangeCommand {

	public SaveOntology(String project, String ontologyUri) throws NeOnCoreException {
		this(project, ontologyUri, null, null);
	}

    public SaveOntology(String project, String ontologyUri, String physicalUri, String fileFormat) throws NeOnCoreException {
        super(project, ontologyUri, physicalUri, fileFormat);
    }

	@Override
	protected void doPerform() throws CommandException {
        //save ontology
        try {
            Object physicalUri = getArgument(2);
            IOntologyProject project = OntologyProjectManager.getDefault().getOntologyProject(getProjectName());
            OWLOntologyManager manager = project.getAdapter(OWLOntologyManager.class);
            OWLOntology ontology = manager.getOntology(IRI.create(getOntology()));
            if (ontology != null) {
                URI physicalURI = manager.getPhysicalURIForOntology(ontology);
                if(physicalUri != null) {
                    physicalURI = URI.create((String) physicalUri);
                }
                
                String fileFormat = (String)getArgument(3);
                PrefixOWLOntologyFormat ontologyFormat = getOwlOntologyFormat(fileFormat);

                OWLModel model = OWLModelFactory.getOWLModel(getOntology(), getProjectName());
                OWLNamespaces namespaces = model.getNamespaces();
                Iterator<String> prefixes = namespaces.prefixes();
                while (prefixes.hasNext()) {
                    String prefix = prefixes.next();
                    String namespace = namespaces.getNamespaceForPrefix(prefix);
                    ontologyFormat.setPrefix(prefix + ":", namespace);
                }
                
                manager.saveOntology(ontology, ontologyFormat, physicalURI);
            }
            project.getResource().refreshLocal(IResource.DEPTH_INFINITE, null);
            project.setOntologyDirty(getOntology(), false);
        } catch (Exception e) {
            throw new CommandException(e);
        } finally {
            // hack to avoid pending file handles, see issue 12863
            System.gc();
        }
    }

	
    PrefixOWLOntologyFormat getOwlOntologyFormat(String fileFormat) {
        if((fileFormat == null) || (fileFormat.equals(OWLConstants.OWL_EXTENSION))) {
            return new RDFXMLOntologyFormat();
        } else if((fileFormat.equals(OWLConstants.OWLXML_EXTENSION))) {
            return new OWLXMLOntologyFormat();
        } else if((fileFormat.equals(OWLConstants.FUNCTIONAL_SYNTAX_EXTENSION))) {
            return new OWLFunctionalSyntaxOntologyFormat();
        } else if((fileFormat.equals(OWLConstants.MANCHESTER_SYNTAX_EXTENSION))) {
            return new ManchesterOWLSyntaxOntologyFormat();
        } else if((fileFormat.equals(OWLConstants.TURTLE_EXTENSION))) {
            return new TurtleOntologyFormat();
        } else {
            // default
            return new RDFXMLOntologyFormat();
        }
    }

}
