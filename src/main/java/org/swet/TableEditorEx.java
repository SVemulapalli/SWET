package org.swet;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.By;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import org.openqa.selenium.By;

import org.swet.YamlHelper;

// origin : 
// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/TableEditorexample.htm
// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/SWTTableSimpleDemo.htm 
// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/SWTTableEditor.htm
// http://www.java2s.com/Tutorial/Java/0280__SWT/TableCellEditorComboTextandButton.htm
// http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/DemonstratesTableEditor.htm
// http://www.java2s.com/Tutorial/Java/0280__SWT/UsingTableEditor.htm

public class TableEditorEx {

	private static Map<String, String> elementSelectedByToselectorChoiceTable = new HashMap<>();
	static {
		// temporarily converting legacy SWD "Element selected By" keys to
		// selectorTable keys
		elementSelectedByToselectorChoiceTable.put("ElementXPath", "xpath");
		elementSelectedByToselectorChoiceTable.put("ElementCssSelector",
				"cssSelector");
		elementSelectedByToselectorChoiceTable.put("ElementText", "text");
		elementSelectedByToselectorChoiceTable.put("ElementId", "id");
		// TODO:
		elementSelectedByToselectorChoiceTable.put("ElementLinkText", "linkText");
		elementSelectedByToselectorChoiceTable.put("ElementTagName", "tagName");
	}
	private static Map<String, String> methodTable = new HashMap<>();
	static {
		// these are currently free-hand, would become discoverable methods of
		// keyword-driven framework class
		methodTable.put("CLICK", "clickButton");
		methodTable.put("CLICK_BUTTON", "clickButton");
		methodTable.put("CLICK_CHECKBOX", "clickCheckBox");
		methodTable.put("CLICK_LINK", "clickLink");
		methodTable.put("CLICK_RADIO", "clickRadioButton");
		methodTable.put("CLOSE_BROWSER", "closeBrowser");
		methodTable.put("CREATE_BROWSER", "openBrowser");
		methodTable.put("ELEMENT_PRESENT", "elementPresent");
		methodTable.put("GET_ATTR", "getElementAttribute");
		methodTable.put("GET_TEXT", "getElementText");
		methodTable.put("GOTO_URL", "navigateTo");
		methodTable.put("SELECT_OPTION", "selectDropDown");
		methodTable.put("SET_TEXT", "enterText");
		methodTable.put("SEND_KEYS", "enterText");
		methodTable.put("SWITCH_FRAME", "switchFrame");
		methodTable.put("VERIFY_ATTR", "verifyAttribute");
		methodTable.put("VERIFY_TEXT", "verifyText");
		methodTable.put("CLEAR_TEXT", "clearText");
		methodTable.put("WAIT", "wait");
	}

	private static Configuration testCase = null;
	private static Map<String, HashMap<String, String>> testData = new HashMap<>();
	private static LinkedHashMap<String, Integer> sortedElementSteps = new LinkedHashMap<>();
	private static Map<String, Integer> elementSteps = new HashMap<>();
	private static Map<String, Method> selectorChoiceTable = new HashMap<>();
	private static Map<String, String> elementData = new HashMap<>();
	private static String yamlFilePath = null;

	public static void main(String[] args) {

		yamlFilePath = (args.length == 0)
				? String.format("%s/%s", System.getProperty("user.dir"), "sample.yaml")
				: args[0];

		if (yamlFilePath != null) {
			System.err.println("Loading " + yamlFilePath);
			testCase = YamlHelper.loadConfiguration(yamlFilePath);
			testData = testCase.getElements();
			YamlHelper.printConfiguration(testCase);
		}

		elementSteps = testData.keySet().stream().collect(Collectors.toMap(o -> o,
				o -> Integer.parseInt(testData.get(o).get("ElementStepNumber"))));
		sortedElementSteps = sortByValue(elementSteps);
/* 
// origin: http://www.java2s.com/Code/Java/SWT-JFace-Eclipse/SWTMenuExample.htm 
    menuBar = new Menu(shell, SWT.BAR);
    fileMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
    fileMenuHeader.setText("&File");

    fileMenu = new Menu(shell, SWT.DROP_DOWN);
    fileMenuHeader.setMenu(fileMenu);

    fileSaveItem = new MenuItem(fileMenu, SWT.PUSH);
    fileSaveItem.setText("&Save");

    fileExitItem = new MenuItem(fileMenu, SWT.PUSH);
    fileExitItem.setText("E&xit");

    helpMenuHeader = new MenuItem(menuBar, SWT.CASCADE);
    helpMenuHeader.setText("&Help");

    helpMenu = new Menu(shell, SWT.DROP_DOWN);
    helpMenuHeader.setMenu(helpMenu);

    helpGetHelpItem = new MenuItem(helpMenu, SWT.PUSH);
    helpGetHelpItem.setText("&Get Help");

    fileExitItem.addSelectionListener(new fileExitItemListener());
    fileSaveItem.addSelectionListener(new fileSaveItemListener());
    helpGetHelpItem.addSelectionListener(new helpGetHelpItemListener());

    shell.setMenuBar(menuBar);
*/
		/*
		for (String stepId : sortedElementSteps.keySet()) {
			elementData = testData.get(stepId);
			System.out.println(String.format("Loading step %d(%s) %s %s %s",
					Integer.parseInt(elementData.get("ElementStepNumber")),
					elementData.get("CommandId"), elementData.get("ElementCodeName"),
					elementData.get("ElementSelectedBy"),
					elementData.get(elementData.get("ElementSelectedBy"))));
		}
		*/
		try {
			// NOTE: values of selectorChoiceTable are never used
			selectorChoiceTable.put("cssSelector",
					By.class.getMethod("cssSelector", String.class));
			selectorChoiceTable.put("xpath",
					By.class.getMethod("xpath", String.class));
			selectorChoiceTable.put("id", By.class.getMethod("id", String.class));
			selectorChoiceTable.put("linkText",
					By.class.getMethod("linkText", String.class));
			selectorChoiceTable.put("name", By.class.getMethod("name", String.class));
			// "text" is achieved indirectly.
			selectorChoiceTable.put("text",
					By.class.getMethod("xpath", String.class));
		} catch (NoSuchMethodException e) {
		}

		Display display = new Display();

		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout());
		//
		final Table table = new Table(shell, SWT.CHECK | SWT.BORDER | SWT.MULTI);
		table.setLinesVisible(true);

		table.setHeaderVisible(true);
		String[] titles = { "Element", "Action Keyword", "Selector Choice",
				"Selector Value", "Param 1", "Param 2", "Param 3" };

		for (int titleItem = 0; titleItem < titles.length; titleItem++) {
			TableColumn column = new TableColumn(table, SWT.NULL);
			column.setText(titles[titleItem]);
		}

		for (int i = 0; i < titles.length; i++) {
			TableColumn column = new TableColumn(table, SWT.NONE);
			column.setWidth(100);
		}

		int blankRows = 3;
		int tableSize = sortedElementSteps.keySet().size();
		for (int i = 0; i < tableSize + blankRows; i++) {
			new TableItem(table, SWT.NONE);
		}

		TableItem[] items = table.getItems();

		appendRowToTable(table, sortedElementSteps);

		for (int i = tableSize; i < tableSize + blankRows; i++) {
			TableItem item = items[i];
			appendBlankRowToTable(table, item, i);
		}

		for (int titleItem = 0; titleItem < titles.length; titleItem++) {
			table.getColumn(titleItem).pack();
		}

		// http://help.eclipse.org/mars/index.jsp?topic=%2Forg.eclipse.platform.doc.isv%2Freference%2Fapi%2Forg%2Feclipse%2Fswt%2Fcustom%2FTableEditor.html
		final TableEditor editor = new TableEditor(table);
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = table.getTopIndex();
				while (index < table.getItemCount()) {
					boolean visible = false;
					final TableItem item = table.getItem(index);
					for (int i = 0; i < table.getColumnCount(); i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							final int column = i;
							final Text text = new Text(table, SWT.NONE);
							Listener textListener = new Listener() {
								public void handleEvent(final Event e) {
									switch (e.type) {
									case SWT.FocusOut:
										item.setText(column, text.getText());
										text.dispose();
										break;
									case SWT.Traverse:
										switch (e.detail) {
										case SWT.TRAVERSE_RETURN:
											item.setText(column, text.getText());
											// FALL THROUGH
										case SWT.TRAVERSE_ESCAPE:
											text.dispose();
											e.doit = false;
										}
										break;
									}
								}
							};
							text.addListener(SWT.FocusOut, textListener);
							text.addListener(SWT.Traverse, textListener);
							editor.setEditor(text, item, i);
							text.setText(item.getText(i));
							text.selectAll();
							text.setFocus();
							return;
						}
						if (!visible && rect.intersects(clientArea)) {
							visible = true;
						}
					}
					if (!visible)
						return;
					index++;
				}
			}
		});
		shell.pack();
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	private static void appendRowToTable(Table table,
			LinkedHashMap<String, Integer> steps) {

		TableItem[] items = table.getItems();
		int cnt = 0;
		for (String stepId : sortedElementSteps.keySet()) {

			// Append row into the TableEditor
			TableItem item = items[cnt];
			elementData = testData.get(stepId);
			System.out.println(String.format("Loading step %d(%s) %s %s %s",
					Integer.parseInt(elementData.get("ElementStepNumber")),
					elementData.get("CommandId"), elementData.get("ElementCodeName"),
					elementData.get("ElementSelectedBy"),
					elementData.get(elementData.get("ElementSelectedBy"))));

			item.setText(new String[] { elementData.get("ElementCodeName"),
					String.format("Action %d", cnt), elementData.get("ElementSelectedBy"),
					elementData.get(elementData.get("ElementSelectedBy")) });

			// some columns require combo selects

			TableEditor keywordChoiceEditor = new TableEditor(table);
			CCombo keywordChoiceCombo = new CCombo(table, SWT.NONE);
			for (String keyword : methodTable.values()) {
				keywordChoiceCombo.add(keyword);
				// System.err.println(keyword);
			}
			int keywordChoiceCombo_select = new ArrayList<String>(
					methodTable.values()).indexOf("clickLink");
			// System.err.println("Selecting: " + keywordChoiceCombo_select);
			keywordChoiceCombo.select(keywordChoiceCombo_select);
			keywordChoiceEditor.grabHorizontal = true;
			keywordChoiceEditor.setEditor(keywordChoiceCombo, item, 1);

			TableEditor selectorChoiceEditor = new TableEditor(table);
			CCombo selectorChoiceCombo = new CCombo(table, SWT.NONE);
			for (String locator : selectorChoiceTable.keySet()) {
				selectorChoiceCombo.add(locator);
				// System.err.println(locator);
			}
			int selectorChoiceCombo_select = new ArrayList<String>(
					selectorChoiceTable.keySet())
							.indexOf(elementSelectedByToselectorChoiceTable
									.get(elementData.get("ElementSelectedBy")));
			System.err.println(String.format("Selecting: %d for %s",
					selectorChoiceCombo_select, elementSelectedByToselectorChoiceTable
							.get(elementData.get("ElementSelectedBy"))));
			selectorChoiceCombo.select(selectorChoiceCombo_select);
			selectorChoiceEditor.grabHorizontal = true;
			selectorChoiceEditor.setEditor(selectorChoiceCombo, item, 2);

			cnt = cnt + 1;
		}

		return;

	}

	private static void appendBlankRowToTable(Table table, TableItem item,
			int index) {

		item.setText(new String[] { String.format("Element %d name", index),
				String.format("Action keyword %d", index), "",
				String.format("Selector value", index) });

		TableEditor keywordChoiceEditor = new TableEditor(table);
		CCombo keywordChoiceCombo = new CCombo(table, SWT.NONE);
		keywordChoiceCombo.setText("Choose");
		for (String keyword : methodTable.values()) {
			keywordChoiceCombo.add(keyword);
		}
		keywordChoiceEditor.grabHorizontal = true;
		keywordChoiceEditor.setEditor(keywordChoiceCombo, item, 1);

		TableEditor selectorChoiceEditor = new TableEditor(table);
		CCombo selectorChoiceCombo = new CCombo(table, SWT.NONE);
		selectorChoiceCombo.setText("Choose");
		for (String locator : selectorChoiceTable.keySet()) {
			selectorChoiceCombo.add(locator);
		}
		selectorChoiceEditor.grabHorizontal = true;
		selectorChoiceEditor.setEditor(selectorChoiceCombo, item, 2);
		return;

	}

	// TODO: move to Utils.java
	// sorting example from
	// http://stackoverflow.com/questions/109383/sort-a-mapkey-value-by-values-java
	public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(
			Map<K, V> map) {
		return map.entrySet().stream().sorted(Map.Entry.comparingByValue())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
						(e1, e2) -> e1, LinkedHashMap::new));
	}

}
