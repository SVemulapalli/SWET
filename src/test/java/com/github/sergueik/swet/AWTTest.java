package com.github.sergueik.swet;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class AWTTest {

	private final static String command = "notepad.exe";
	// Create an instance of Robot class
	private final static Runtime runtime = Runtime.getRuntime();
	private static Robot robot = null;
	private static final int delay = 100;
	private static final int launchDelay = 5000;
	// low-level event is generated by a component object (
	private static int[] rawKeys = { KeyEvent.VK_H, KeyEvent.VK_E, KeyEvent.VK_L,
			KeyEvent.VK_L, KeyEvent.VK_O, KeyEvent.VK_SPACE, };

	@BeforeClass
	public static void load() throws IOException {
		runtime.exec(command);
		sleep(launchDelay);
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@After
	public void writeNewLine() {
		robot.keyPress(KeyEvent.VK_ENTER);
	}

	@Ignore
	@Test
	public void basicTest() throws AWTException, InterruptedException {

		for (int cnt = 0; cnt != rawKeys.length; cnt++) {
			robot.keyPress(rawKeys[cnt]);
			Thread.sleep(delay);
		}
		writeString("Hello AWT!");
	}

	// @Ignore
	@Test
	public void writeStringTest() {
		writeString("Hello AWT!");
		robot.keyPress(KeyEvent.VK_ENTER);
		writeString(
				",-./0123456789;=ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz[\\]0123456789*+-./");
		// prints
		// -.0123456789=ФИСВУАПРШОЛДЬТЩЗЙКЫЕГМЦЧНЯфисвуапршолдьтщзйкыегмцчня\0123456789-.
	}

	@Ignore
	@Test
	public void printAllCharactersTest() {

		// print range character codes brute force
		for (int code = 17; code < 128; code++) {
			try {
				robot.keyPress(code);
				robot.delay(delay);
				robot.keyRelease(code);
			} catch (IllegalArgumentException e) {
				System.out.println(
						"Failed to press " + code + " " + KeyEvent.getKeyText(code));
				// Failed to press 91 Open Bracket
				// Failed to press 93 Close Bracket
				// Failed to press 44 Comma
				// Failed to press 47 Slash
			}
		}
	}

	public static void sleep(Integer milliSeconds) {
		try {
			Thread.sleep((long) milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void writeString(String s) {
		for (int code = 0; code < s.length(); code++) {
			char _char = s.charAt(code);
			if (Character.isUpperCase(_char)) {
				robot.keyPress(KeyEvent.VK_SHIFT);
			}
			try {
				robot.keyPress(Character.toUpperCase(_char));
				robot.keyRelease(Character.toUpperCase(_char));
			} catch (IllegalArgumentException e) {
				System.err.println("Failed to press " + KeyEvent.getKeyText(code));
				//
			}
			if (Character.isUpperCase(_char)) {
				robot.keyRelease(KeyEvent.VK_SHIFT);
			}
		}
		robot.delay(delay);
	}
}
