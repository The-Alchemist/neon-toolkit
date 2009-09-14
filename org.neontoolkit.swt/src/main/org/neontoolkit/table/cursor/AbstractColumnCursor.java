/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.table.cursor;


import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;


/*
 * Created on 20.05.2008
 * Created by Dirk Wenke
 *
 * Function: 
 * Keywords: 
 */
public abstract class AbstractColumnCursor extends Canvas {
    Listener _cursorListener = new Listener() {
        /* (non-Javadoc)
         * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
         */
        public void handleEvent(Event event) {
            switch (event.type) {
            case SWT.FocusOut:
                setVisible(false);
                break;
            case SWT.Dispose:
                dispose(event);
                break;
            case SWT.KeyDown:
                getParent().notifyListeners(SWT.KeyDown, event);
                if (_selectedCell != null) {
                    _viewer.setSelection(new StructuredSelection(_selectedCell.getElement()));
                    _viewer.reveal(_selectedCell.getElement());
                }
                break;
            case SWT.MouseDoubleClick:
                getParent().notifyListeners(SWT.MouseDoubleClick, cloneEvent(event));
                break;
            case SWT.MouseDown:
                getParent().notifyListeners(SWT.MouseDown, cloneEvent(event));
                break;
            case SWT.Paint:
                paint(event);
                break;
            default:
                break;
            }
        }
    };

    Listener _parentListener = new Listener() {
        /* (non-Javadoc)
         * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
         */
        public void handleEvent(Event event) {
            switch (event.type) {
            case SWT.FocusIn:
                setVisible(true);
                forceFocus();
            case SWT.Resize:
                redraw();
                break;
            case SWT.Paint:
                redraw();
                break;
            default:
                break;
            }
        }
    };

    Listener _scrollListener = new Listener() {
        /* (non-Javadoc)
         * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
         */
        public void handleEvent(Event event) {
            selectCell(_selectedCell);
        }
    };

	private ColumnViewer _viewer;
	private ViewerCell _selectedCell;
	private ITableColorProvider _colorProvider;

	/**
	 * @param parent
	 * @param style
	 */
	public AbstractColumnCursor(ColumnViewer parent) {
		super((Composite)parent.getControl(), SWT.NONE);
		_viewer = parent;
		setBackground(parent.getControl().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		hookListener();
	}
	
	private void hookListener() {
        addListener(SWT.Dispose, _cursorListener);
		addListener(SWT.Paint, _cursorListener);
		addListener(SWT.KeyDown, _cursorListener);
		addListener(SWT.MouseDown, _cursorListener);
		addListener(SWT.MouseDoubleClick, _cursorListener);
		addListener(SWT.FocusOut, _cursorListener);
		
        getParent().addListener(SWT.FocusIn, _parentListener);
        getParent().addListener(SWT.Resize, _parentListener);
        getParent().addListener(SWT.Paint, _parentListener);

        ScrollBar hBar = getParent().getHorizontalBar();
		if (hBar != null) {
			hBar.addListener(SWT.Selection, _scrollListener);
		}
		ScrollBar vBar = getParent().getVerticalBar();
		if (vBar != null) {
			vBar.addListener(SWT.Selection, _scrollListener);
		}
	}
	
	private void dispose(Event event) {
		ScrollBar hBar = getParent().getHorizontalBar();
		if (hBar != null) {
			hBar.removeListener(SWT.Selection, _scrollListener);
		}
		ScrollBar vBar = getParent().getVerticalBar();
		if (vBar != null) {
			vBar.removeListener(SWT.Selection, _scrollListener);
		}
	}
	
	public void selectCell(ViewerCell cell) {
		_selectedCell = cell;
		if (_selectedCell != null) {
			setBounds(cell.getBounds());
			setVisible(true);
			setFocus();
			redraw();
		}
	}

	protected void paint(Event event) {
		if (event.gc == null){
			return;
		}
		
		if (_selectedCell == null || _selectedCell.getItem().isDisposed()) {
			_selectedCell = null;
			return;
		}
		setBounds(_selectedCell.getBounds());
		
		GC gc = event.gc;
		Display display = getDisplay();
		gc.setBackground(getCellBackground());
		gc.setForeground(getCellForeground());
		gc.fillRectangle(event.x, event.y, event.width, event.height);

		int x = 0;
		Point size = getSize();
		Image image = _selectedCell.getImage();
//		if (image == null) {
//		    image = SwtImages.get(SwtImages.EMPTY);
//		}
		if (image != null) {
			Rectangle imageSize = image.getBounds();
			int imageY = (size.y - imageSize.height) / 2;
			gc.drawImage(image, x, imageY);
			x += imageSize.width;
		}
		String text = _selectedCell.getText();
		if (text != "") { //$NON-NLS-1$
			Rectangle bounds = _selectedCell.getBounds();
			Point extent = gc.stringExtent(text);
			// Temporary code - need a better way to determine table trim
			String platform = SWT.getPlatform();
			if ("win32".equals(platform)) { //$NON-NLS-1$
				if (getColumnCount() == 0 || _selectedCell.getColumnIndex() == 0) {
					x += 2; 
				} else {
					switch (getAlignment(_selectedCell.getColumnIndex())) {
						case SWT.LEFT:
							x += 6;
							break;
						case SWT.RIGHT:
							x = bounds.width - extent.x - 6;
							break;
						case SWT.CENTER:
							x += (bounds.width - x - extent.x) / 2;
							break;
					}
				}
			}  else {
				if (getColumnCount() == 0) {
					x += 5; 
				} else {
                    switch (getAlignment(_selectedCell.getColumnIndex())) {
						case SWT.LEFT:
							x += 5;
							break;
						case SWT.RIGHT:
							x = bounds.width- extent.x - 2;
							break;
						case SWT.CENTER:
							x += (bounds.width - x - extent.x) / 2 + 2;
							break;
					}
				}
			}
			int textY = (size.y - extent.y) / 2;
			gc.drawString(text, x, textY);
		}
		gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
		gc.drawFocus(0, 0, size.x, size.y);
	}
	
	private Event cloneEvent(Event event) {
		Event cEvent = new Event();
		cEvent.button = event.button;
		cEvent.character = event.character;
		cEvent.count = event.count;
		cEvent.data = event.data;
		cEvent.detail = event.detail;
		cEvent.display = event.display;
		cEvent.doit = event.doit;
		cEvent.end = event.end;
		cEvent.gc = event.gc;
		cEvent.height = event.height;
		cEvent.index = event.index;
		cEvent.item = _selectedCell != null ? _selectedCell.getControl() : null;
		cEvent.keyCode = event.keyCode;
		cEvent.start = event.start;
		cEvent.stateMask = event.stateMask;
		cEvent.text = event.text;
		cEvent.time = event.time;
		cEvent.type = event.type;
		cEvent.widget = event.widget;
		cEvent.width = event.width;
		Point p = _viewer.getControl().toControl(toDisplay(event.x, event.y));
		cEvent.x = p.x;
		cEvent.y = p.y;
		
		return cEvent;
	}

    public ViewerCell getSelectedCell() {
        return _selectedCell;
    }

    private Color getCellBackground() {
        if (getColorProvider() != null && _selectedCell != null) {
            Color color = _colorProvider.getBackground(_selectedCell.getElement(), _selectedCell.getColumnIndex());
            if (color != null) {
                return color;
            }
        }
        return getBackground();
    }

    private Color getCellForeground() {
        if (getColorProvider() != null && _selectedCell != null) {
            Color color = _colorProvider.getForeground(_selectedCell.getElement(), _selectedCell.getColumnIndex());
            if (color != null) {
                return color;
            }
        }
        return getForeground();
    }
    
    private ITableColorProvider getColorProvider() {
        if (_colorProvider == null) {
            if (_viewer.getLabelProvider() instanceof ITableColorProvider) {
                _colorProvider = (ITableColorProvider)_viewer.getLabelProvider();
            }
        }
        return _colorProvider;
    }

    public abstract int getColumnCount();
	
	public abstract int getAlignment(int columnIndex);
}
