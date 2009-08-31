/*****************************************************************************
 * Copyright (c) 2008 ontoprise GmbH.
 *
 * All rights reserved.
 *
 *****************************************************************************/

package org.neontoolkit.gui.exception;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.neontoolkit.core.exception.NeOnCoreException;
import org.neontoolkit.gui.NeOnUIPlugin;

import com.sun.org.apache.xalan.internal.xsltc.compiler.CompilerException;

/* 
 * Created on: 12.10.2004
 * Created by: Juergen Baier
 *
 * Keywords: Datamodel, Exception
 */
/**
 * This class is responsible for handling exceptions. The default behaviour is
 * to simply log the exception.
 * 
 * <p>
 * Usage notes: <br>
 * 
 * <pre>
 * try {
 *     someDangerousCall();
 * } catch (MyException ex) {
 *     OntoStudioExceptionHandler handler = new OntoStudioExceptionHandler();
 *     handler.handleException(ex);
 *     // Re-throw or treat as handled.
 * }
 * </pre>
 * 
 * </p>
 */
public class NeonToolkitExceptionHandler {
    protected static Logger _log;
    
    public NeonToolkitExceptionHandler() {
    }
    
    protected static Logger getLogger() {
        if (_log == null) {
            _log = Logger.getLogger(NeonToolkitExceptionHandler.class);
        }
        return _log;
    }
    
    /**
     * Handles the exception by displaying an error dialog with 
     * - the error code of the <code>topLevelException</code> as title
     * - the localized <code>topLevelException</code> message as the top level exception
     * - the localized <code>reason</code> message as the reason
     * - the detailed stack trace of the <code>topLevelException</code>
     * 
     * @param topLevelException
     * @param reason
     * @param shell
     * @return
     */
    public IStatus handleException(final Throwable topLevelException, final Throwable reason, final Shell shell) {
        return handleException(true, topLevelException, reason, shell);
    }
    
    /**
     * Handles the exception by displaying an error dialog with 
     * - the error code of the <code>topLevelException</code> as title
     * - the localized <code>topLevelException</code> message as the top level exception
     * - the localized <code>reason</code> message as the reason
     * - the detailed stack trace of the <code>topLevelException</code>
     * 
     * @param topLevelException
     * @param reason
     * @param shell
     * @return
     */
    public IStatus handleException(final boolean showDialog, final Throwable topLevelException, final Throwable reason, final Shell shell) {
        logException(topLevelException);
        String reasonMsg = reason.getLocalizedMessage();
        if (reasonMsg == null) {
            reasonMsg = reason.toString();
        }
        final IStatus status = getStackTraceStatus(reasonMsg, topLevelException);
        NeOnUIPlugin.getDefault().getLog().log(status);
        
        if (showDialog) {
	        final String errorTitle = getErrorTitle(topLevelException);
	        final String errorMessage = topLevelException.getLocalizedMessage();
	        showErrorDialog(shell, status, errorTitle, errorMessage);
        }
        return status;
    }

    
    public IStatus handleException(final String message, final IStatus status, final Shell shell) {
    	Throwable reason = status.getException();
        logException(reason);
        
        final IStatus fStatus = status;
        NeOnUIPlugin.getDefault().getLog().log(status);
        
        final String errorTitle = getErrorTitle(reason);
        final String errorMessage = message != null ? message : reason.getLocalizedMessage();

        showErrorDialog(shell, fStatus, errorTitle, errorMessage);
        return status;
    }

    /**
     * Handles the exception by displaying an error dialog with 
     * - the error code of the <code>reason</code> as title (if <code>reason</code> is an <code>OntopriseException</code>).
     * - the localized <code>reason</code> message as the top level exception
     * - the localized cause of the <code>reason</code> as the reason
     * - the detailed stack trace of the <code>topLevelException</code>
     * 
     * @param message
     * @param reason
     * @param shell
     * @return
     */
    public IStatus handleException(String message, Throwable reason, final Shell shell) {
        IStatus status = null;
        if (reason instanceof NeOnCoreException) {
            NeOnCoreException ex = (NeOnCoreException)reason;
            if (reason.getCause() != null) {
                reason = ex.getCause();
            }
        }
        if (reason instanceof CoreException) {
            status = ((CoreException) reason).getStatus();
        } else {
            status = getStackTraceStatus(reason.getLocalizedMessage(), reason);
        }
        return handleException(message, status, shell);
        
    }
    /**
     * @param throwable
     * @return
     */
    protected IStatus getStackTraceStatus(final String message, final Throwable throwable) {
        IStatus status;
        StackTraceElement[] st = throwable.getStackTrace();
        IStatus[] ste = new IStatus[st.length + 1];
        ste[0] = new Status(IStatus.ERROR, NeOnUIPlugin.getDefault().getBundle().getSymbolicName(), 0, throwable.toString(), null);
        for (int i = 0; i < st.length; i++) {
            ste[i + 1] = new Status(IStatus.ERROR, NeOnUIPlugin.getDefault().getBundle().getSymbolicName(), 0, "    at " + st[i].toString(), null); //$NON-NLS-1$
        }
        
        String multiStatusMessage = message;
        if (multiStatusMessage == null) {
            multiStatusMessage = throwable.getLocalizedMessage() == null ? throwable.getClass().getName() : throwable.getLocalizedMessage();
        }

        if (throwable instanceof CompilerException) {
        	StringBuffer resultMessage = new StringBuffer();
        	// CompilerException has many lines, reduce the number of lines
        	StringTokenizer tokenizer = new StringTokenizer(multiStatusMessage, "\n\r", false); //$NON-NLS-1$
        	resultMessage.append(tokenizer.nextToken());
        	resultMessage.append(tokenizer.nextToken());
        	StringBuffer nextLine = new StringBuffer();
        	while (tokenizer.hasMoreTokens()) {
        		nextLine.append(tokenizer.nextToken());
        		if (tokenizer.hasMoreTokens()) {
        			nextLine.append(", "); //$NON-NLS-1$
        		}
        		if (nextLine.length() > 30) {
        			resultMessage.append(nextLine.toString());
        			resultMessage.append("\n"); //$NON-NLS-1$
        			nextLine = new StringBuffer();
        		}
        	}
        	resultMessage.append(nextLine.toString());
        	multiStatusMessage = resultMessage.toString();
        }
        
        multiStatusMessage = removeToManyLines(multiStatusMessage);
        
        status = new MultiStatus(NeOnUIPlugin.getDefault().getBundle().getSymbolicName(),
                0,
                ste,
                multiStatusMessage,
                throwable);
        return status;
    }

    private String getErrorTitle(final Throwable throwable) {
        final String errorTitle;
        if (throwable instanceof NeOnCoreException) {
            NeOnCoreException ex = (NeOnCoreException) throwable;
            errorTitle = "Error: " + ex.getErrorCode(); //$NON-NLS-1$
        } else {
            errorTitle = "Error"; //$NON-NLS-1$
        }
        return errorTitle;
    }
    
    /**
     * Log the given <code>message</code> to both log4j and to eclipse .log (in workspace).
     * 
     * @param message
     */
    public static void logInfo(String message) {
        getLogger().info(message);
        IStatus status = new Status(IStatus.INFO, NeOnUIPlugin.getDefault().getBundle().getSymbolicName(), IStatus.OK, message, null);
        NeOnUIPlugin.getDefault().getLog().log(status);

    }
    
    /**
     * Handles the exception by displaying an error dialog with 
     * - the error code of the <code>topLevelException</code> as title
     * - the localized <code>topLevelException</code> message as the top level exception
     * - the localized <code>reason</code> message as the reason
     * - the detailed stack trace of the <code>topLevelException</code>
     * 
     * @param topLevelException
     * @param reason
     * @param shell
     * @return
     */
    public IStatus handleWarning(final Throwable topLevelException, final Throwable reason, final Shell shell) {
        return handleWarning(true, topLevelException, reason, shell);
    }
    
    /**
     * Handles the exception by displaying an error dialog with 
     * - the error code of the <code>topLevelException</code> as title
     * - the localized <code>topLevelException</code> message as the top level exception
     * - the localized <code>reason</code> message as the reason
     * - the detailed stack trace of the <code>topLevelException</code>
     * 
     * @param topLevelException
     * @param reason
     * @param shell
     * @return
     */
    public IStatus handleWarning(final boolean showDialog, final Throwable topLevelException, final Throwable reason, final Shell shell) {
        logException(topLevelException);
        String reasonMsg = reason.getLocalizedMessage();
        if (reasonMsg == null) {
            reasonMsg = reason.toString();
        }
        final IStatus status = getStackTraceStatus(reasonMsg, topLevelException);
        NeOnUIPlugin.getDefault().getLog().log(status);
        
        if (showDialog) {
            final String errorTitle = getErrorTitle(topLevelException);
            final String errorMessage = topLevelException.getLocalizedMessage();
            showWarningDialog(shell, status, errorTitle, errorMessage);
        }
        return status;
    }

    /**
     * Handles the exception by displaying an error dialog with 
     * - the error code of the <code>reason</code> as title (if <code>reason</code> is an <code>OntopriseException</code>).
     * - the localized <code>reason</code> message as the top level exception
     * - the localized cause of the <code>reason</code> as the reason
     * - the detailed stack trace of the <code>topLevelException</code>
     * 
     * @param message
     * @param reason
     * @param shell
     * @return
     */
    public IStatus handleWarning(final String message, final Throwable reason, final Shell shell) {
        logException(reason);
        
        IStatus status = null;
        if (reason instanceof CoreException) {
            status = ((CoreException) reason).getStatus();
        } else {
            status = getStackTraceStatus(reason.getLocalizedMessage(), reason);
        }
        
        final IStatus fStatus = status;
        NeOnUIPlugin.getDefault().getLog().log(status);
        
        final String errorTitle = getErrorTitle(reason);
        final String errorMessage = message != null ? message : reason.getLocalizedMessage();
        
        showWarningDialog(shell, fStatus, errorTitle, errorMessage);
        return status;
    }
    
    public void showStatusDialog(final Shell shell, final IStatus fStatus, final String errorTitle, final String errorMessage) {
        shell.getDisplay().syncExec(new Runnable() {
            public void run() {
                ErrorDialog dialog = new ErrorDialog(shell, errorTitle, errorMessage,
                        fStatus, IStatus.OK | IStatus.INFO | IStatus.WARNING | IStatus.ERROR);
                dialog.open();
            }
        });
    }
    
    private void showWarningDialog(final Shell shell, final IStatus fStatus, final String errorTitle, final String errorMessage) {
        shell.getDisplay().syncExec(new Runnable() {
            public void run() {
                ErrorDialog dialog = new ErrorDialog(shell, errorTitle, errorMessage,
                        fStatus, IStatus.WARNING);
                dialog.open();
            }
        });
    }

    private void showErrorDialog(Shell shell, final IStatus fStatus, final String errorTitle, final String errorMessage) {
        if (shell == null) {
            shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        }
        final Shell finalShell = shell;
        shell.getDisplay().syncExec(new Runnable() {
            public void run() {
                ErrorDialog dialog = new ErrorDialog(finalShell, errorTitle, errorMessage, fStatus, IStatus.ERROR);
                dialog.open();
            }
        });
    }
    
    /**
     * josp 2009-04-17: fixed<br>
     * Issue 4237 - Error message too long in error message dialog of "Simple Query View"
     */
    private String removeToManyLines(String message) {
        if (message == null) {
            return null;
        }
        int MAX_LINE_COUNT = 10;
        String[] lines = message.split("\n"); //$NON-NLS-1$
        if (lines.length < MAX_LINE_COUNT) {
            return message;
        }
        StringBuilder trimedMessage = new StringBuilder();
        for (int i = 0; i < MAX_LINE_COUNT; i++) {
            trimedMessage.append(lines[i]).append("\n"); //$NON-NLS-1$
        }
        trimedMessage.append("... ").append(lines.length - MAX_LINE_COUNT); //$NON-NLS-1$
        trimedMessage.append(" more lines\n"); //$NON-NLS-1$
        return trimedMessage.toString();
    }
    
    protected void logException(Throwable throwable) {
        if (throwable == null) {
            return;
        }
        getLogger().error(throwable.getLocalizedMessage(), throwable);
    }

    public void handleException(Throwable t) {
        logException(t);
    }
}
