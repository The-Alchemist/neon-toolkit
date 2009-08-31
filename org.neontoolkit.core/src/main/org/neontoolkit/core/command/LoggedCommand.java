/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.core.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/* 
 * Created on 07.09.2007
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
/**
 * Abstract command implementation which automatically logs a created command
 */
public abstract class LoggedCommand implements ICommand {
	public static final String NULL_ARGUMENT_MESSAGE = "Null argument is not allowed!"; //$NON-NLS-1$
	
	public static String[][] propertiesToStringArray(Properties properties) {
	    if (properties == null) {
	        return null;
	    }
        List<String[]> result = new ArrayList<String[]>();
        for (Object keyObject:properties.keySet()) {
            String key = (String)keyObject;
            result.add(new String[]{key, properties.get(key).toString()});
        }
        return result.toArray(new String[0][]);

	}
	
	public static Properties stringArrayToProperties(String[][] props) {
        if (props == null) {
            return null;
        }
	    Properties properties = new Properties();
        for (String[] entry: props) {
            properties.setProperty(entry[0], entry[1]);
        }
        return properties;
	}
	
	private Object[] _arguments;

	/**
	 * Default constructor for this command with a variable set of arguments.
	 * Only primitive objects are allowed as parameters, such as String, 
	 * Integer, Double, etc. 
	 * @param arguments
	 */
	protected LoggedCommand(Object... arguments) {
		assert arguments != null;
		_arguments = arguments;
		checkArguments();
	}
	
	/**
	 * This method is called in the constructor. Subclasses may override to
	 * verify whether the arguments are all valid. If an invalid argument is
	 * identifier (e.g. one argument is <code>null</code> which must not be) an
	 * IllegalArgumentException should be thrown.
	 * @throws IllegalArgumentException
	 */
	protected void checkArguments() throws IllegalArgumentException {
		//default implementation does nothing
	}

	/*
	 * (non-Javadoc)
	 * @see org.neontoolkit.datamodel.command.ICommand#getArgument(int)
	 */
	public Object getArgument(int i) {
		return _arguments[i];
	}

	/**
	 * The implementation of the run method is logging this command and
	 * calling the perform method afterwards.
	 * @see org.neontoolkit.core.command.ICommand#run()
	 */
	public void run() throws CommandException {
		writeToLog();
		perform();
	}
	
	/**
	 * This method is performing the real action of this command. It is called by
	 * the run method after this call has been logged. Additionally it can directly
	 * be called by subclasses to avoid logging.
	 * @throws ControlException
	 */
	protected abstract void perform()throws CommandException;

	/**
	 * internal method to write the call to the log.
	 */
	private void writeToLog() {
		Object[] arguments = getLoggedArguments();
		Class<?>[] argumentClasses = new Class<?>[arguments.length];
		for (int i=0; i<arguments.length; i++) {
			argumentClasses[i] = arguments[i] != null ? arguments[i].getClass() : Object.class;
		}
		CommandLogger.log(getClass().getName(), argumentClasses, arguments);
	}
	
	/**
	 * Returns all arguments that have to be logged in the correct order.
	 * Subclasses may override this method to log only arguments that have
	 * been passed in the constructor.
	 * @return
	 */
	protected Object[] getLoggedArguments() {
		return _arguments;
	}

	/**
	 * Helper method that returns true if a string is null or only the empty string.
	 * @param s
	 * @return
	 */
	protected boolean emptyString(String s) {
		return s == null || s.trim().length() == 0;
	}
	
	/**
	 * Helper method that returns true, if two arguments are equal and false otherwise.
	 * If both arguments are <code>null</code> true is returned.
	 * @param o1
	 * @param o2
	 * @return
	 */
	protected boolean isEqual(Object o1, Object o2){
		return o1 == null ? o2 == null : o1.equals(o2);
	}
	
	/**
	 * Returns true, if the argument with the given index is null, false otherwise.
	 * @param argumentIndex
	 * @return
	 */
	protected boolean isNull(int argumentIndex) {
		return _arguments[argumentIndex] == null;
	}
}
