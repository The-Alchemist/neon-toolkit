/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core.command;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.neontoolkit.core.NeOnCorePlugin;

/* 
 * Created on: 27.10.2005
 * Created by: Dirk Wenke
 *
 * Keywords: Datamodel, Logging
 */
/**
 * This class is used to log precedure calls to generate a script which can later be 
 * executed.
 */
public class CommandLogger {
    private static int LOG_HISTORY_LENGTH = 5;
    private static String LOG_NAME = "commandtrace"; //$NON-NLS-1$

    public static boolean _enabled = true;
    private static FileWriter _writer2;
    
    private static FileWriter getWriter() {
    	if (_writer2 == null) {
            String pathSuffix = LOG_NAME+System.currentTimeMillis()+".log"; //$NON-NLS-1$
            try {
                File pluginDir = NeOnCorePlugin.getDefault().getStateLocation().toFile();
                File metaFolder = pluginDir.getParentFile().getParentFile();
                String[] files = metaFolder.list();
                File oldestLog = null;
                int logCounter = 0;
                for (String fileName:files) {
                	if (fileName.startsWith(LOG_NAME)) {
                		logCounter++;
                		File logFile = new File(metaFolder, fileName);
                		if (oldestLog == null) {
                			oldestLog = logFile;
                		}
                		else {
                			oldestLog = (logFile.lastModified() > oldestLog.lastModified()) ?
                					oldestLog :
                					logFile;
                		}
                	}
                }
                if (logCounter >= LOG_HISTORY_LENGTH && oldestLog != null) {
                	//more log files than the defined history size
                	// => remove oldest logfile
                	oldestLog.delete();
                }
                File file = new File(metaFolder, pathSuffix);
                _writer2 = new FileWriter(file);
            } catch (IOException ioe) {
                _enabled = false;
                ioe.printStackTrace();
            } catch (NullPointerException npe) {
                _enabled = false;
            }
    	}
    	return _writer2;
    }

    public static void log(String commandName) {
        log(commandName, new Class[0], new Object[0]);
    }

    public static void log(String commandName, Class<?> type, Object parameter) {
        log(commandName, new Class[] {type}, new Object[] {parameter});
    }

    public static void log(String commandName, Class<?>[] types, Object[] parameters) {
        if (_enabled) {
        	log(getWriter(), commandName, types, parameters);
        }
    }

    public synchronized static void log(FileWriter writer, String commandName, Class<?>[] types, Object[] parameters) {
		if (_enabled) {
			try {
				writer.write(commandName);
				for (int i = 0; i < types.length; i++) {
					writer.write(" "); //$NON-NLS-1$
					writer.write(types[i].getName());
					writer.write(" "); //$NON-NLS-1$
					writer.write(ObjectRepresentation.toLoggedString(types[i], parameters[i]));
				}
				writer.write("\n"); //$NON-NLS-1$
				writer.flush();
			} catch (Exception ioe) {
				ioe.printStackTrace();
			}
		}
    }
}
