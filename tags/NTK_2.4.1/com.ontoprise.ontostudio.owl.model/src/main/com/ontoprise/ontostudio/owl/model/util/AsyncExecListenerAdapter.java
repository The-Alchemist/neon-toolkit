/*****************************************************************************
 * Copyright (c) 2009 ontoprise GmbH.
 *
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/

package com.ontoprise.ontostudio.owl.model.util;

import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;

/**
 * Dispatches data model events to the GUI thread which are then executed by an adopted {@link OWLOntologyChangeListener}.
 * 
 * @author krekeler
 */
public class AsyncExecListenerAdapter implements OWLOntologyChangeListener {
    /** The adopted listener. */
    private final Object _listener;
    /** Indicates if events shall be skipped after the closed event has been fired. */
    private final boolean _skipPostClosedEvents;
    /** Indicates if the closed event has been fired. */
    private volatile boolean _closed = false;

    /**
     * Create a new adapter.
     * 
     * <p>All pending events will be processed, i.e. <code>skipPostClosedEvents</code> is <code>false</code> as described for 
     * {@link AsyncExecListenerAdapter#AsyncExecDatamodelListenerAdapter(boolean skipPostClosedEvents, OWLOntologyChangeListener listener)}.</p>
     * 
     * @param listener                           The adpoted listener.
     */
    public AsyncExecListenerAdapter(Object listener) {
        this(false, listener);
    }

    /**
     * Create a new adapter.
     * 
     * @param skipPostClosedEvents               If <code>true</code>, no pending event --- besides the "closed"/"deleted" event itself --- 
     *                                           is passed to <code>listener</code> once the "closed"/"deleted" event has been fired for an ontology. 
     *                                           <em><b>Important:</b></em> in this case, this object <em><b>must not</em></b> be registered to
     *                                           more than one ontology as listener since the implementation is not aware of such a use case!
     * 
     *                                           <p>This option is helpful if <code>listener</code> relies on the condition, 
     *                                           that an ontology to which it listens to, is not closed when processing an event for it.</p>
     *                                           
     *                                           <p>Note that <code>listener</code> therefore must grant that it does not close/delete an ontology while processing an event from it. 
     *                                           In addition, no other thread than the GUI thread must close/delete an ontology to which this object is registered as listener.<p/>
     * 
     *                                           <p>However, if the collab server is used, another client might delete an ontology to which this object is registered, 
     *                                           which will implicitly close the clients ontology proxy. 
     *                                           In this case, the ontology (proxy) might get closed while <code>listener</code> is already processing an event for it. 
     *                                           The implementation of this class cannot prevent such a situation.</p>
     *                                           
     * @param listener                           The adopted listener.
     */
    public AsyncExecListenerAdapter(boolean skipPostClosedEvents, Object listener) {
        _skipPostClosedEvents = skipPostClosedEvents;
        _listener = listener;
    }

    @Override
    public void ontologiesChanged(final List<? extends OWLOntologyChange> changes) {
        if (_listener instanceof OWLOntologyChangeListener) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    if (_skipPostClosedEvents && _closed) {
                        return;
                    }
                    try {
                        ((OWLOntologyChangeListener)_listener).ontologiesChanged(changes);
                    } catch (OWLException e) {
                        throw new RuntimeException();
                    }
                }
            });
        }
    }
}
