/*****************************************************************************
 * written by the NeOn Technologies Foundatiom Ltd.
 * based on classes with a copyright by the ontoprise GmbH.
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.gui.util.textfields;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

import com.ontoprise.ontostudio.owl.gui.properties.table.proposal.DatatypeAndClassProposalProvider;
import com.ontoprise.ontostudio.owl.model.OWLModel;

/**
 * @author mer
 * @author Nico Stieler
 * 
 *         This class represents a StyledText field for OWL datatypes and an http://schema.ontoprise.com/reserved#owlIndividual.
 * 
 *         It uses syntax highlighting. It offers auto-completion (for datatypes) It is restricted to a single line. New-Lines are not permitted. It can be
 *         edited. It supports CTRL-click to jump to the datatype entity.
 * 
 *         For display, it uses the namespace-setting as chosen by the user, also for editing.
 */
public class DatatypeAndClassText extends AbstractOwlTextField {

    public static final int WIDTH = 200;

    /**
     * 
     */
    public DatatypeAndClassText(Composite parent, OWLModel owlModel) {
        this(parent,owlModel,owlModel);
    }
    /**
     * 
     */
    public DatatypeAndClassText(Composite parent, OWLModel localOwlModel, OWLModel sourceOwlModel) {
        super(parent, localOwlModel, sourceOwlModel);

        GridData data = new GridData();
        data.widthHint = WIDTH;
        data.verticalAlignment = SWT.TOP;
        data.horizontalAlignment = SWT.FILL;
        data.grabExcessHorizontalSpace = true;

        createTextWidget(parent, data, new DatatypeAndClassProposalProvider(localOwlModel, sourceOwlModel), false, true);
    }
}
