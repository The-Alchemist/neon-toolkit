/**
 * Copyright (c) 2008 ontoprise GmbH.
 */

package com.ontoprise.ontostudio.owl.model.commands.rename;

import java.lang.reflect.InvocationTargetException;

import org.neontoolkit.core.command.CommandException;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.semanticweb.owlapi.model.OWLEntity;

import com.ontoprise.ontostudio.owl.model.commands.OWLModuleChangeCommand;

/*
 * Created on 12.11.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public abstract class RenameOWLEntity extends OWLModuleChangeCommand {

    /**
     * @param project
     * @param module
     * @param arguments
     * @throws NeOnCoreException
     */
    public RenameOWLEntity(String project, String module, String oldId, String newId) throws NeOnCoreException {
        super(project, module, oldId, newId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ontoprise.ontostudio.flogic.datamodel.command.ModuleCommand#checkArguments()
     */
    @Override
    protected void checkArguments() throws IllegalArgumentException {
        super.checkArguments();
        if (isNull(2) || isNull(3)) {
            throw new IllegalArgumentException(NULL_ARGUMENT_MESSAGE);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.neontoolkit.datamodel.command.LoggedCommand#perform()
     */
    @Override
    protected void doPerform() throws CommandException {
        String oldUri = (String) getArgument(2);
        String newUri = (String) getArgument(3);
        if (!oldUri.equals(newUri)) {
            try {
                OWLEntity entity = getEntity(oldUri);
                // have to undo bugfix for http://buggy.ontoprise.de/bugs/show_bug.cgi?id=11245 here 
                // because of http://buggy.ontoprise.de/bugs/show_bug.cgi?id=11673
//                if (getOwlModel().getEntity(newUri).size() > 0) {
//                    throw new InformationAlreadyExistsException(Messages.getString("RenameOWLEntity_0")); //$NON-NLS-1$
//                } else {
                    getOwlModel().renameEntity(entity, newUri, null);
//                }
            } catch (NeOnCoreException e) {
                throw new CommandException(e);
            } catch (InterruptedException e) {
                throw new CommandException(e);
            } catch (InvocationTargetException e) {
                throw new CommandException(e);
            }
        }
    }

    protected abstract OWLEntity getEntity(String uri) throws NeOnCoreException;

}
