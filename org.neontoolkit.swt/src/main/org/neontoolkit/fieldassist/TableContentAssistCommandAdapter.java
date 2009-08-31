/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.fieldassist;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.IControlContentAdapter;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;

public class TableContentAssistCommandAdapter extends ContentAssistCommandAdapter {

	public TableContentAssistCommandAdapter(Control control, IControlContentAdapter controlContentAdapter, IContentProposalProvider proposalProvider, String commandId, char[] autoActivationCharacters) {
		super(control, controlContentAdapter, proposalProvider, commandId,
				autoActivationCharacters);
	}

}
