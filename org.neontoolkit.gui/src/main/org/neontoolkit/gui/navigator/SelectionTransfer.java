/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.gui.navigator;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.neontoolkit.gui.NeOnUIPlugin;

/* 
 * Created on: 29.07.2005
 * Created by: Dirk Wenke
 *
 * Keywords: UI, Transfer, Drag, Drop
 */
/**
 * Transfer class used for DND operations from the OntologyNavigator.
 */
public class SelectionTransfer extends ByteArrayTransfer {
	private static final String TYPE_NAME = "selection-transfer-format" + (new Long(System.currentTimeMillis())).toString(); //$NON-NLS-1$;
	private static final int TYPEID = registerType(TYPE_NAME);
	
	private static final SelectionTransfer _singleton = new SelectionTransfer();
	
	private ISelection _selection;
	
	public static SelectionTransfer getInstance() {
	    return _singleton;
	}

    @Override
	protected int[] getTypeIds() {
        return new int[] {TYPEID};
    }

    @Override
	protected String[] getTypeNames() {
        return new String[] {TYPE_NAME};
    }

	@Override
	public void javaToNative(Object object, TransferData transferData) {
		byte[] check = TYPE_NAME.getBytes();
		super.javaToNative(check, transferData);
	}

	@Override
	public Object nativeToJava(TransferData transferData) {
		Object result = super.nativeToJava(transferData);
		if (isInvalidNativeType(result)) {
			ILog log = NeOnUIPlugin.getDefault().getLog();
			log.log(
				new Status(
					IStatus.ERROR, 
					NeOnUIPlugin.PLUGIN_ID, 
					IStatus.ERROR, 
					"Wrong native transfer type!", null));	//$NON-NLS-1$
		}
		return _selection;
	}

	private boolean isInvalidNativeType(Object result) {
		return !(result instanceof byte[]) || !TYPE_NAME.equals(new String((byte[]) result));
	}

	public void setSelection(ISelection selection) {
	    _selection = selection;
	}
	public ISelection getSelection() {
	    return _selection;
	}
}
