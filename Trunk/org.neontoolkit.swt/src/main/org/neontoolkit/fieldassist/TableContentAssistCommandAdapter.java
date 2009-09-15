/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

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
