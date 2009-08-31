/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/
package org.neontoolkit.fieldassist;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.jface.dialogs.PopupDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.IContentProposalListener2;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.neontoolkit.swt.SwtPlugin;


public class ContentAssistTextCellEditor extends TextCellEditor {
	private IContentProposalProvider _proposalProvider;
	private IControlContentAdapter _contentAdapter;
	private String _commandId;
	private char[] _autoActivationChars;
	@SuppressWarnings("deprecation") 
	private org.eclipse.ui.fieldassist.ContentAssistField _assistField;
	private Composite _parent;
	private boolean _proposalsOnly = false;
	private VerifyListener _verifyListener;
	private ColumnViewerEditorActivationEvent _activationEvent;
	
	public ContentAssistTextCellEditor(Composite parent, int style, IContentProposalProvider proposalProvider, IControlContentAdapter adapter, String commandId, char[] autoActivationCharacters) {
		_proposalProvider = proposalProvider;
		_commandId = commandId;
		_autoActivationChars = autoActivationCharacters;
		_parent = parent;
		if (adapter != null) {
			_contentAdapter = adapter;
		}
		else {
			_contentAdapter = new TextContentAdapter() {
				@Override
				public void insertControlContents(Control control, String text, int cursorPosition) {
					boolean propOnly = _proposalsOnly;
					showProposalsOnly(false);
					setText(text);
					showProposalsOnly(propOnly);
					finishEditing();
				}
			};
		}
		setStyle(style);
		create(parent);
	}
	
	public ContentAssistTextCellEditor(Composite parent, int style, IContentProposalProvider proposalProvider, String commandId, char[] autoActivationCharacters) {
		this(
				parent, 
				style, 
				proposalProvider,
				null,
				commandId, 
				autoActivationCharacters);
	}
	
	
	@SuppressWarnings("deprecation") 
	@Override
	protected Control createControl(Composite parent) {
		_assistField = new org.eclipse.ui.fieldassist.ContentAssistField(
				parent, 
				SWT.NONE,
				new org.eclipse.jface.fieldassist.IControlCreator() {
					public Control createControl(Composite parent, int style) {
						parent.setBackground(_parent.getBackground());
						Control control = ContentAssistTextCellEditor.super.createControl(parent);
						return control;
					}
				},
				_contentAdapter, _proposalProvider,_commandId,_autoActivationChars);
		_assistField.getContentAssistCommandAdapter().setFilterStyle(ContentAssistCommandAdapter.FILTER_NONE);
		_assistField.getContentAssistCommandAdapter().setAutoActivationCharacters(null);
		_assistField.getContentAssistCommandAdapter().setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_INSERT);
		_assistField.getLayoutControl().setBackground(parent.getBackground());
		return _assistField.getLayoutControl();
	}
	
	public void finishEditing() {
		closePopup(false);
        fireApplyEditorValue();
		deactivate();
	}
	
	public void cancelEditing() {
		closePopup(false);
		fireCancelEditor();
		deactivate();
	}
	
	public void setSelectedItem() {
    	closePopup(true);
	}
	
	@SuppressWarnings("deprecation") 
	@Override
	protected void keyReleaseOccured(KeyEvent keyEvent) {
        if (keyEvent.character == SWT.ESC && isPopupVisible()) { 
        	// Escape character and popup visible => do nothing
        }
//        else if (keyEvent.character == SWT.TAB || keyEvent.character == SWT.CR) {
//        	setSelectedItem();
//        }
        else {
        	if (_proposalsOnly && keyEvent.character == SWT.CR && !isPopupVisible()) {
        		IContentProposal[] props = getProposals();
        		if (props != null && props.length > 0) {
        			try {
        				if (getTextControl().getText().length() > 0) {
							Class<?> clazz = ContentProposalAdapter.class;
							Method method = clazz.getDeclaredMethod("proposalAccepted",new Class[]{IContentProposal.class}); //$NON-NLS-1$
							method.setAccessible(true);
							method.invoke(_assistField.getContentAssistCommandAdapter(), new Object[]{props[0]});
        				}
						finishEditing();
        			} catch (Exception e) {
        				SwtPlugin.logError(e);
        			}
        		}
        		else {
        			cancelEditing();
        		}
        	}
        	else {
        		super.keyReleaseOccured(keyEvent);
        	}
        }
	}
	
	/**
	 * Returns true if the popup is currently visible, false otherwise.
	 * @return
	 */
	@SuppressWarnings("deprecation") 
	private boolean isPopupVisible() {
		try {
			Field field = ContentProposalAdapter.class.getDeclaredField("popup"); //$NON-NLS-1$
			field.setAccessible(true);
			Object popup = field.get(_assistField.getContentAssistCommandAdapter());
			return popup != null;
		} catch (NoSuchFieldException nsfe) {
			nsfe.printStackTrace();
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
		}
		return false;
	}
	
	@SuppressWarnings("deprecation") 
	private PopupDialog getPopup() {
		try {
			Field field = ContentProposalAdapter.class.getDeclaredField("popup"); //$NON-NLS-1$
			field.setAccessible(true);
			return (PopupDialog)field.get(_assistField.getContentAssistCommandAdapter());
		} catch (NoSuchFieldException nsfe) {
		} catch (IllegalAccessException iae) {
		}
		return null;
	}
	
	/**
	 * Forces the popup to close. If store is <code>true</code>, the curent selected element is stored
	 * as selection, otherwise none selection is stored.
	 * @param store parameter indicating whether the current selection should be stored.
	 */
	public void closePopup(boolean store) {
		try {
			PopupDialog popup = getPopup();
			if (popup != null) {
				if (store) {
					Class<?>[] classes = ContentProposalAdapter.class.getDeclaredClasses();
					Method method = classes[0].getDeclaredMethod("acceptCurrentProposal",new Class[]{}); //$NON-NLS-1$
					method.setAccessible(true);
					method.invoke(popup, new Object[]{});
				} else {
					popup.close();
				}
				this.setFocus();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void doSetValue(Object value) {
		if (value == null) {
			value = ""; //$NON-NLS-1$
		}
		if (_proposalsOnly && !value.equals("")) { //$NON-NLS-1$
			if (getProposals(value.toString()).length == 0) {
				value = ""; //$NON-NLS-1$
			}
		}
		super.doSetValue(value);
	}
	
	public Text getTextControl() {
		return (Text)((Composite)getControl()).getChildren()[0];
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#focusLost()
	 */
	@Override
	protected void focusLost() {
		PopupDialog popup = getPopup();
		if (isActivated()) {
			if (popup == null || !popup.getShell().isVisible()) {
				if (_proposalsOnly) {
					closePopup(true);
					fireApplyEditorValue();
					deactivate();
				}
				else {
					super.focusLost();
				}
			}
		}
	}
	
	/**
	 * Sets the label provider for the completion popup
	 * @param lableProvider
	 */
	@SuppressWarnings("deprecation") 
	public void setLabelProvider(ILabelProvider labelProvider) {
		_assistField.getContentAssistCommandAdapter().setLabelProvider(labelProvider);
	}

	private IContentProposal[] getProposals() {
		return getProposals(_contentAdapter.getControlContents(getTextControl()));
	}
	
	private IContentProposal[] getProposals(String contents) {
		IContentProposal[] proposals = _proposalProvider.getProposals(contents, getTextControl().getCaretPosition());
		return proposals;
	}
	
	private String getText(String inserted) {
		if (inserted.trim().equals("")) { //$NON-NLS-1$
			return inserted;
		}
		StringBuffer buf = new StringBuffer(getTextControl().getText());
		Point selection = getTextControl().getSelection();
		if (selection.x == selection.y) {
			buf.insert(getTextControl().getCaretPosition(), inserted);
		}
		else {
			buf.replace(selection.x, selection.y, inserted);
		}
		return buf.toString();
	}
	
	public void showProposalsOnly(boolean proposalsOnly) {
		if (_verifyListener == null) {
			_verifyListener = new VerifyListener(){
				public void verifyText(VerifyEvent e) {
					if (getProposals(getText(e.text)).length == 0) {
						e.doit = false;
					}
				}
			};
		}
		if (proposalsOnly) {
			getTextControl().addVerifyListener(_verifyListener);
		}
		else {
			getTextControl().removeVerifyListener(_verifyListener);
		}
		_proposalsOnly = proposalsOnly;
	}
	
	@SuppressWarnings("deprecation") 
	public void setText(String text) {
		ContentAssistCommandAdapter commandAdapter = _assistField.getContentAssistCommandAdapter();
		char[] aac = commandAdapter.getAutoActivationCharacters();
		commandAdapter.setAutoActivationCharacters(new char[0]);
		getTextControl().setText(text);
		commandAdapter.setAutoActivationCharacters(aac);
	}

	@SuppressWarnings("deprecation") 
	public void addContentProposalListener(IContentProposalListener listener) {
		_assistField.getContentAssistCommandAdapter().addContentProposalListener(listener);
	}

    @SuppressWarnings("deprecation") 
    public void addContentProposalListener(IContentProposalListener2 listener) {
        _assistField.getContentAssistCommandAdapter().addContentProposalListener(listener);
    }
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#activate(org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent)
	 */
	@Override
	public void activate(ColumnViewerEditorActivationEvent activationEvent) {
		super.activate(activationEvent);
		_activationEvent = activationEvent;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.TextCellEditor#doSetFocus()
	 */
	@Override
	protected void doSetFocus() {
		super.doSetFocus();
		if (_activationEvent != null && _activationEvent.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED) {
			if (_activationEvent.keyCode != SWT.CR) {
				getTextControl().setText(""+_activationEvent.character); //$NON-NLS-1$
				getTextControl().setSelection(1,1);
			}
		}
	}
}
