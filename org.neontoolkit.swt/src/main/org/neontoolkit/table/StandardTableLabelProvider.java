/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.table;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.neontoolkit.swt.Messages;
import org.neontoolkit.swt.SwtImages;
import org.neontoolkit.swt.SwtPlugin;
import org.neontoolkit.table.model.ICheckboxTableCell;
import org.neontoolkit.table.model.ITableRow;


public class StandardTableLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {
    public static final Image REMOVE_IMAGE = SwtImages.get(SwtImages.REMOVE_ROW);
    public static final Image CHECKED_IMAGE = SwtImages.get(SwtImages.CHECKBOX_CHECKED);
    public static final Image UNCHECKED_IMAGE = SwtImages.get(SwtImages.CHECKBOX_UNCHECKED);
    public static final Image EMPTY_IMAGE = SwtImages.get(SwtImages.EMPTY);
	
    private Color _grayColor;
	private boolean _removeEnabled;
	
	public StandardTableLabelProvider(boolean removeEnabled) {
		_removeEnabled = removeEnabled;
	}

	public Image getColumnImage(Object element, int columnIndex) {
        if (element instanceof ITableRow) {
            ITableRow row = (ITableRow) element;
            if (columnIndex >= row.getCellCount()) {
            	//table has more columns than this row, check if it is the column for the remove option
            	if (columnIndex == row.getContentModel().getColumnCount() && _removeEnabled && !row.isEmpty() && row.isRemoveable()) {
            		//show remove image
                    return REMOVE_IMAGE;
            	}
            	else {
            		//Empty cell
            		return EMPTY_IMAGE;
            	}
            }
            else {
                if (row.getCellAt(columnIndex) instanceof ICheckboxTableCell && !row.isEmpty()) {
                    return getCheckImage(row.getCellAt(columnIndex).getString());
                }
                return EMPTY_IMAGE;
            }
        }
        SwtPlugin.logError(Messages.StandardTableLabelProvider_0, null); 
        return null;
    }

	public String getColumnText(Object element, int columnIndex) {
        if (element instanceof ITableRow) {
        	ITableRow row = (ITableRow)element;
        	if (columnIndex >= row.getCellCount()) {
        		return ""; //$NON-NLS-1$
        	}
        	else {
        		if (row.getCellAt(columnIndex) instanceof ICheckboxTableCell) {
        			return ""; //$NON-NLS-1$
        		}
        		else {
        			return row.getCellAt(columnIndex).toString();
        		}
        	}
        }
        SwtPlugin.logError(Messages.StandardTableLabelProvider_0, null); 
        return null;
	}

	public Color getBackground(Object element, int columnIndex) {
        return null;
	}

	public Color getForeground(Object element, int columnIndex) {
        if (element instanceof ITableRow) {
            ITableRow row = (ITableRow)element;
            if (columnIndex < row.getCellCount()) {
                if (!row.getCellAt(columnIndex).isEnabled()) {
                    return getGrayColor();
                }
            }
        }
		return null;
	}

    private Image getCheckImage(String val) {
        boolean retVal = false;
        try {
            retVal = new Boolean(val).booleanValue();
        } catch (IllegalArgumentException iae) {
        }
        if (retVal) {
            return CHECKED_IMAGE;
        } else {
            return UNCHECKED_IMAGE;
        }
    }
    
    private Color getGrayColor() {
    	if (_grayColor == null) {
    		_grayColor = Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_NORMAL_SHADOW);
    	}
    	return _grayColor;
    }
}
