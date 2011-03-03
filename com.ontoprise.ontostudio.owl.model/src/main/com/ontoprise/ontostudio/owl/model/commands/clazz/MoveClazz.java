/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.commands.clazz;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import com.ontoprise.ontostudio.owl.model.Messages;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.OWLUtilities;
import com.ontoprise.ontostudio.owl.model.commands.ApplyChanges;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;
/**
 * 
 * @author Nico Stieler
 */
public class MoveClazz extends OWLModuleChangeCommand {

	public MoveClazz(String project, String ontologyId, String clazzId, String oldParentId, String newParentId, boolean cleanIndividualProperties) throws NeOnCoreException {
		super(project, ontologyId, clazzId, oldParentId, newParentId, new Boolean(cleanIndividualProperties));
	}

	@Override
	public void doPerform() throws CommandException {
		String clazzId = getArgument(2).toString();
		String oldSuperClazzId = getArgument(3) != null ? getArgument(3).toString() : null;
		String newSuperClazzId = getArgument(4) != null ? getArgument(4).toString() : null;

        try {
	        if (newSuperClazzId != null) {
	            //add a subclass
	        	if (clazzId.equals(newSuperClazzId)) {
	        		//class cannot be moved on itself
	        		throw new CommandException(Messages.getString("MoveClazz.0"));  //$NON-NLS-1$
	        	}
	        	if (oldSuperClazzId != null && oldSuperClazzId.equals(newSuperClazzId)) {
	        		//no move operation has to be performed
	        		return;
	        	}
	        	new CreateSubClazz(getProjectName(), getOntology(), clazzId, newSuperClazzId).perform();
	            OWLDataFactory factory = OWLModelFactory.getOWLDataFactory(getProjectName());
                if (oldSuperClazzId != null) {
	                // tkr: remove the old SubClassOf axiom only, do not remove clazzId itself
	                OWLSubClassOfAxiom axiom = factory.getOWLSubClassOfAxiom(factory.getOWLClass(OWLUtilities.toIRI(clazzId)), factory.getOWLClass(OWLUtilities.toIRI(oldSuperClazzId)));
	                if (getOwlModel().containsAxiom(axiom, false)) {
	                    new ApplyChanges(getProjectName(), getOntology(), new String[0], new String[]{OWLUtilities.toString(axiom)}).perform();
	                }
	                
	            } else {
	            	// this means a clazz from root level has been moved, so we have to remove the 
	            	// declaration so that the respective event removes the tree item. 
	    			try {
	    				OWLDeclarationAxiom decl = factory.getOWLDeclarationAxiom(factory.getOWLClass(OWLUtilities.toIRI(clazzId)));
                        new ApplyChanges(getProjectName(), getOntology(), new String[0], new String[]{OWLUtilities.toString(decl)}).perform();
					} catch (NeOnCoreException e) {
	                    // nothing to do, no explicit information seems to exist
			        }
	            }
	        } else {
	            //newSuperClazzId == null  =>> add a root class
	            if (oldSuperClazzId != null) {
	                new RemoveClazz(getProjectName(), getOntology(), clazzId, oldSuperClazzId).perform();
                    new CreateRootClazz(getProjectName(), getOntology(), clazzId).perform();
	            } else {
                    throw new CommandException(Messages.getString("MoveClazz.1"));  //$NON-NLS-1$
	            }
	        }
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }
	}

}
