/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.core.command;


/*
 * Created on 28.05.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
/**
 * Basic interfaces for commands in OntoStudio. Commands are created with a set of 
 * parameters and classes implementing this interface provide methods to execute the
 * command and to retrieve the parameters.
 */
public interface ICommand {

	/**
	 * Executes this command. If the command cannot be run successfully, a ControlException
	 * is thrown
	 * @throws ControlException
	 */
	void run() throws CommandException;
	
	/**
	 * Returns the argument at the specified position.
	 * @param index
	 * @return
	 */
	Object getArgument(int index);
}
