/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.table;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.neontoolkit.swt.Messages;
import org.neontoolkit.table.celleditors.ObjectComboBoxCellEditor;
import org.neontoolkit.table.model.ITableContentModel;
import org.neontoolkit.table.model.ITableRow;


public class StandardTableCellModifier implements ICellModifier {
	private StandardTableViewer _viewer;
	private ITableContentModel _model;
	
	public StandardTableCellModifier(StandardTableViewer viewer, ITableContentModel model) {
		_viewer = viewer;
		_model = model;
	}
	
	private boolean rowsRemoveable() {
		return (_viewer.getStyle() & StandardTableViewer.REMOVEABLE_ROWS) > 0;
	}

	public boolean canModify(Object element, String property) {
        // Find the index of the column
        int column = _viewer.getColumnIndex(property);
        CellEditor[] _editors = _viewer.getCellEditors();
        ITableRow row = (ITableRow) element;
        if (_editors[column] instanceof CheckboxCellEditor && row.isEmpty()) {
        	return false;
        }
        if (rowsRemoveable() && column == _editors.length - 1) {
            if (!row.isEmpty() && row.isRemoveable()) {
                _model.removeRow(row);
            }
            return false;
        } else {
            return row.getCellAt(column).isChangeable();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
     *      java.lang.String)
     */
    public Object getValue(Object element, String property) {
        // Find the index of the column
        int column = _viewer.getColumnIndex(property);
        CellEditor[] _editors = _viewer.getCellEditors();

        if (element instanceof ITableRow) {
            ITableRow row = (ITableRow) element;
            String value = ((ITableRow) element).getCellAt(column).toString(); 
            if (_editors[column].getClass() == TextCellEditor.class) {
                return value;
            } else if (_editors[column] instanceof ComboBoxCellEditor) {
                ComboBoxCellEditor ed = (ComboBoxCellEditor) _editors[column];
                String[] items = ed.getItems();
                for (int i = 0; i < items.length; i++) {
                    if (value.equals(items[i])) {
                        return new Integer(i);
                    }
                }
                return new Integer(-1);
            } else if (_editors[column] instanceof CheckboxCellEditor) {
                return new Boolean(value);
            } else {
                return row.getCellAt(column).toString();
            }
        }
        return ""; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
     *      java.lang.String, java.lang.Object)
     */
    public void modify(Object element, String property, Object value) {
        // Find the index of the column
        int column = _viewer.getColumnIndex(property);
        CellEditor[] _editors = _viewer.getCellEditors();

        TableItem item = (TableItem) element;

        if (item.getData() instanceof ITableRow) {
            ITableRow row = (ITableRow) item.getData();
            if (_editors[column] instanceof TextCellEditor) {
                row.getCellAt(column).setString(value.toString());
            } else if (_editors[column] instanceof ComboBoxCellEditor) {
                if (((Integer) value).intValue() == -1) {
                    //item not found in editor
                    row.getCellAt(column).setString(((CCombo) _editors[column].getControl()).getText());
                } else {
                	String string;
                	if (_editors[column] instanceof ObjectComboBoxCellEditor) {
                		string = ((ObjectComboBoxCellEditor) _editors[column]).getObjectItems()[((Integer) value).intValue()].toString();
                	}
                	else {
                		string = ((ComboBoxCellEditor) _editors[column]).getItems()[((Integer) value).intValue()];
                	}
                    row.getCellAt(column).setString(string);
                }
            } else if (_editors[column] instanceof CheckboxCellEditor) {
                row.getCellAt(column).setString(((Boolean) value).toString());
            } else {
                row.getCellAt(column).setString(value.toString());
            }
            _viewer.getTableViewer().update(row, null);

            Table table =_viewer.getTableViewer().getTable(); 
            if (table.indexOf(item) == table.getItemCount() - 1 && !row.isEmpty() && (_viewer.getStyle() & StandardTableViewer.MULTI_ROWED) > 0) {
                _viewer.getTableViewer().add(_model.addRow());
            }
        } else {
        	throw new IllegalArgumentException(Messages.StandardTableCellModifier_0); 
        }
    }
    
    protected ITableContentModel getModel() {
    	return _model;
    }
    
    protected StandardTableViewer getViewer() {
    	return _viewer;
    }
}
