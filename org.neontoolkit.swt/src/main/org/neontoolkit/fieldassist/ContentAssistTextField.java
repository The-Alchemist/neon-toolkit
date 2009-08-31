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
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;

@SuppressWarnings("deprecation") 
public class ContentAssistTextField extends org.eclipse.ui.fieldassist.ContentAssistField {
	
	private VerifyListener _verifyListener;
	
	public ContentAssistTextField(Composite parent, int style, IContentProposalProvider proposalProvider, String commandId, char[] autoActivationCharacters) {
		super(
				parent, 
				style, 
				new org.eclipse.jface.fieldassist.IControlCreator() {
					public Control createControl(Composite parent, int style) {
						return new Text(parent, style);
					}
				},
				new DummyContentAdapter(),
				proposalProvider, 
				commandId, 
				autoActivationCharacters);
		((DummyContentAdapter)getContentAssistCommandAdapter().getControlContentAdapter()).setTextContentAdapter(new TextContentAdapter() {
				@Override
				public void insertControlContents(Control control, String text, int cursorPosition) {
					setText(text);
					finishEditing();
				}
			});
		getContentAssistCommandAdapter().setFilterStyle(ContentAssistCommandAdapter.FILTER_NONE);
		getContentAssistCommandAdapter().setAutoActivationCharacters(null);
		getContentAssistCommandAdapter().setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_INSERT);

		
	}
	public void finishEditing() {
		closePopup(false);
	}
	
	public void cancelEditing() {
		closePopup(false);
	}
	
	public void setSelectedItem() {
    	closePopup(true);
	}
	
	private PopupDialog getPopup() {
		try {
			Field field = ContentProposalAdapter.class.getDeclaredField("popup"); //$NON-NLS-1$
			field.setAccessible(true);
			return (PopupDialog)field.get(getContentAssistCommandAdapter());
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
				}
				else {
					popup.close();
				}
				getControl().setFocus();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Text getTextControl() {
		return (Text)getControl();
	}
	
	/**
	 * Sets the label provider for the completion popup
	 * @param lableProvider
	 */
	public void setLabelProvider(ILabelProvider labelProvider) {
		getContentAssistCommandAdapter().setLabelProvider(labelProvider);
	}
	
	private String getText(String inserted) {
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

	private IContentProposal[] getProposals(String contents) {
		IContentProposal[] proposals = getContentAssistCommandAdapter().getContentProposalProvider().getProposals(contents, getTextControl().getCaretPosition());
		return proposals;
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
	}
	
	public void setText(String text) {
		char[] aac = getContentAssistCommandAdapter().getAutoActivationCharacters();
		getContentAssistCommandAdapter().setAutoActivationCharacters(new char[0]);
		getTextControl().setText(text);
		getContentAssistCommandAdapter().setAutoActivationCharacters(aac);
	}
}
