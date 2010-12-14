/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.commands;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;

import com.ontoprise.ontostudio.owl.gui.Messages;
import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.syntax.ISyntaxManager;
import com.ontoprise.ontostudio.owl.model.OWLModel;
import com.ontoprise.ontostudio.owl.model.OWLModelFactory;
import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

public class CreateValidUri extends OWLModuleChangeCommand {

    private String _result;

    public CreateValidUri(String project, String module, String uri, String defaultNamespace) throws NeOnCoreException {
        super(project, module, uri, new String[][]{{"", defaultNamespace}}); //$NON-NLS-1$
    }
    
    public CreateValidUri(String project, String module, String uri) throws NeOnCoreException {
        this(project, module, uri, ""); //$NON-NLS-1$
    }

    @Override
    protected void doPerform() throws CommandException {
        String result;
        try {
            result = parseUri((String) getArgument(2), getOntology(), getProjectName());
        } catch (NeOnCoreException e) {
            throw new CommandException(e);
        }

        // Note: the KAON2 internal parser currently supports no international URIs.
        // Since the OWL Editor implementation currently uses the KAON2 internal parser to construct axioms,
        // the implementation will fail later in some cases, if result contains "unallowed" characters.
        boolean checkForAllowedNonInternationalURICharacters = false;
        if (checkForAllowedNonInternationalURICharacters) {
            for (int i = 0; i < result.length(); i++) {
                char c = result.charAt(i);
                boolean isAllowed = new IsAllowedNonInternationalURICharacter(getProjectName(), getOntology(), c).isAllowed();
                if (!isAllowed) {
                    throw new IllegalArgumentException(Messages.CreateValidUri_0 + c + Messages.CreateValidUri_1 + i + Messages.CreateValidUri_2 + result + Messages.CreateValidUri_3); 
                }
            }
        }
        _result = result;
    }

    /**
     * @param uri, e.g. <http://w3.org/foo#bar> rdfs:comment owl:Thing myNS:bar someNameFromDefaultNamespace
     * @param ontology
     * @param project
     * @return a real URI, e.g. http://w3.org/foo#bar
     * @throws NeOnCoreException
     */
    public static String parseUri(String uri, String ontology, String project) throws NeOnCoreException {
        ISyntaxManager manager = OWLPlugin.getDefault().getSyntaxManager();
        OWLModel owlModel = OWLModelFactory.getOWLModel(ontology, project);
        return manager.parseUri(uri, owlModel);
    }

    public String getResult() throws CommandException {
        if (_result == null) {
            run();
        }
        return _result;
    }

}
