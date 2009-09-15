/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.util;

import java.awt.Toolkit;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Shell;

/* 
 * Created on: 01.06.2005
 * Created by: Werner Hihn
 *
 * Function: UI, Utilities
 */
/**
 * Utility class for the UI.
 */
public class UIUtils {

	/**
	 * calculates frame width relative to screen width
	 * 
	 * @param percentOfScreen - percentage of screen width allowed for frame width
	 * @throws NRuntimeException if percentOfScreen less or equal 0 or greater than 1
	 */
	public static int getFrameWidth(double percentOfScreen) {
		if ((percentOfScreen > 0) || (percentOfScreen <= 1)) {
		    // okay
		} else {
		    // default
		    percentOfScreen = 0.6;
		}
		double screenWidth = Toolkit.getDefaultToolkit().getScreenSize().getWidth();		
		return (int) (screenWidth * percentOfScreen);
	}
	
	
	/**
	 * calculates frame height relative to screen height
	 * 
	 * @param percentOfScreen - percentage of screen height allowed for frame height
	 * @throws NRuntimeException if percentOfScreen less or equal 0 or greater than 1
	 */
	public static int getFrameHeight(double percentOfScreen) {
		if ((percentOfScreen > 0) || (percentOfScreen <= 1)) {
		    // okay
		} else {
		    // default
			percentOfScreen = 0.6;
		}
		double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();		
		return (int) (screenHeight * percentOfScreen);
	}
	
	/**
	 * Shows a message dialog and additionally logs the status to the OntoStudio logfile.
	 * Use this method instead of OntoStudioExceptionHandler.hanleException() if you
	 * assume wrong user behaviour (like creating an attribute that already exists).
	 * 
	 * @param shell
	 * @param fStatus
	 * @param messageTitle
	 * @param message
	 */
	public static void showMessageDialog(final Shell shell, final String messageTitle, final String message) {
	    
        shell.getDisplay().syncExec(new Runnable() {
            public void run() {
                MessageDialog.openInformation(shell, messageTitle, message);
            }
        });
	}
    
	/**
	 * Returns the screensize of a String in pixel
	 * 
	 * @param g
	 * @param s
	 * @return
	 */
	public static int stringWidth(GC g, String s){
        return g.stringExtent(s).x;
    }
}
