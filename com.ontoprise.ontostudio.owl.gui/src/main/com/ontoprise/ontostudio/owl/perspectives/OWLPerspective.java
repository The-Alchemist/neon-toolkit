/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.neontoolkit.gui.navigator.MTreeView;
import org.neontoolkit.gui.properties.EntityPropertiesView;

import com.ontoprise.ontostudio.owl.gui.OWLPlugin;
import com.ontoprise.ontostudio.owl.gui.individualview.IndividualView;

/**
 * This class provides the OWL perspective.
 */
public class OWLPerspective implements IPerspectiveFactory {

    public static final String ID = "com.ontoprise.ontostudio.owl.perspectives.OWLPerspective"; //$NON-NLS-1$

    public void defineActions(IPageLayout layout) {

        // Add shown views
        layout.addShowViewShortcut(OWLPlugin.OWL_ONTOLOGY_NAVIGATOR);
        layout.addShowViewShortcut(IndividualView.ID);
        layout.addShowViewShortcut(EntityPropertiesView.ID);

        layout.addActionSet(OWLPlugin.TOOLBARACTIONS);

    }

    public void defineLayout(IPageLayout layout) {
        // Editors are placed for free.
        String editorArea = layout.getEditorArea();

        // Place navigator and outline to left of
        // editor area.
        layout.addView(MTreeView.ID, IPageLayout.TOP, 0.75f, editorArea);
        layout.setEditorAreaVisible(false);

        IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, 0.25f, MTreeView.ID); //$NON-NLS-1$
        right.addView(EntityPropertiesView.ID);

        IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.5f, MTreeView.ID); //$NON-NLS-1$
        bottom.addView(IndividualView.ID);
    }

    public void createInitialLayout(IPageLayout layout) {
        defineActions(layout);
        defineLayout(layout);
    }
}
