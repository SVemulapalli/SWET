package com.github.sergueik.swet;
/**
 * Copyright 2014 - 2019 Serguei Kouzmine
 */

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.jface.dialogs.ErrorDialog;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.omg.SendingContext.RunTime;

import com.github.sergueik.swet.Utils;

import org.eclipse.swt.SWTException;

/**
 * Exception dialog for Selenium WebDriver Elementor Tool (SWET)
 * @author Serguei Kouzmine (kouzmine_serguei@yahoo.com)
 */

public class ExceptionDialogEx {

	private Shell shell = null;
	private static boolean debug = false;
	private static final Utils utils = Utils.getInstance();
	private static final ExceptionDialogEx instance = new ExceptionDialogEx();

	public static void setDebug(boolean debug) {
		ExceptionDialogEx.debug = debug;
	}

	public static ExceptionDialogEx getInstance() {
		return instance;
	}

	private static void testFunction1() throws Exception {
		testFunction2();
	}

	private static void testFunction2() throws Exception {
		testFunction3();
	}

	// throwing exception from a function to illustrate the calling stack
	private static void testFunction3() throws Exception {
		throw new Exception("This is a test exception by "
				+ instance.getClass().getName() + " " + utils.readManifestVersion());
	}

	public void render(Throwable e) {

		MultiStatus status;
		// Collect the exception stack trace
		Exception eCause = (Exception) e.getCause();
		if (eCause != null) {
			if (debug) {
				System.err.println("Cause: " + eCause.toString());
			}
			status = createMultiStatus(e.getLocalizedMessage(), eCause);
		} else {
			status = createMultiStatus(e.getLocalizedMessage(), e);
		}
		ErrorDialog.openError(shell, "Error", "Exception thrown by "
				+ instance.getClass().getName() + " " + utils.readManifestVersion()
		/* "Exception thrown" */, status);

	}

	private ExceptionDialogEx() {
		Display display = null;
		try {
			display = Display.getCurrent();
		} catch (SWTException e) {
			System.err.println(e.toString());
			throw new RuntimeException(e);
		}
		try {
			shell = Display.getCurrent().getActiveShell();
		} catch (NullPointerException e) {
			shell = new Shell(display);
		}
	}

	private static MultiStatus createMultiStatus(String description,
			Throwable t) {

		List<Status> childStatuses = new ArrayList<>();

		for (StackTraceElement stackTrace : t.getStackTrace()) {
			if (debug) {
				System.err.println(
						String.format("Adding stack trace: %s", stackTrace.toString()));
			}

			Status status = new Status(IStatus.ERROR,
					ExceptionDialogEx.getInstance().getClass().getPackage().toString(),
					stackTrace.toString());
			childStatuses.add(status);
		}
		for (StackTraceElement stackTrace : Thread.currentThread()
				.getStackTrace()) {
			if (debug) {
				System.err.println(
						String.format("Adding stack trace: %s", stackTrace.toString()));
			}
			Status status = new Status(IStatus.ERROR,
					ExceptionDialogEx.getInstance().getClass().getPackage().toString(),
					stackTrace.toString());
			childStatuses.add(status);
		}

		String summary = (description != null) ? description : t.toString();
		MultiStatus status = new MultiStatus(
				ExceptionDialogEx.getInstance().getClass().getPackage().toString(),
				IStatus.ERROR, childStatuses.toArray(new Status[] {}),
				(summary.length() > 120) ? summary.substring(0, 120) : summary, t);
		return status;
	}

	public static void main(String[] arg) {
		debug = true;
		try {
			testFunction1();
		} catch (Exception e) {
			// when using in SWT application,
			// need to defer initialization to after the application is started
			// to avoid org.eclipse.swt.SWTException: Invalid thread access
			// ExceptionDialogEx x = ExceptionDialogEx.getInstance();

			ExceptionDialogEx.getInstance().render(e);
		}
	}

}