/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package org.neontoolkit.core.command;

/**
 * Marker interface for commands that do not modify data in the datamodel.
 * This might be commands just retrieving data from the datamodel or commands
 * that do not access the datamodel at all.
 */
public interface IChangelessCommand {

}
