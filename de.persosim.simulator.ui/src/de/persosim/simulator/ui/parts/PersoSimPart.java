package de.persosim.simulator.ui.parts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.globaltester.logging.BasicLogger;

import de.persosim.simulator.ui.Activator;
import de.persosim.simulator.ui.handlers.SelectPersoFromFileHandler;
import de.persosim.simulator.ui.utils.LinkedListLogListener;
import de.persosim.simulator.ui.utils.PersoSimUILogEntry;
import de.persosim.simulator.ui.utils.PersoSimUILogFormatter;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;

public class PersoSimPart
{
	private static final String[] COLUMN_TITLES = { "Timestamp", "Log Level", "Log Tags", "Log Message" };
	private static final int[] COLUMN_WEIGHTS = { 15, 5, 10, 70 };
	private static final int[] COLUMN_MIN_WIDTHS = { 140, 70, 70, 300 };

	// Remember column width for column visibility toggle
	private final Map<TableColumn, Integer> columnWidths = new HashMap<>();
	private final UISynchronize sync;
	private TableViewer logTableViewer;
	private Table table;

	private Thread updateThread;

	private Font defaultFont;
	private Font boldFont;

	private boolean isLocked = false;
	private boolean wasDragging = false;
	private boolean isProgrammaticSelection = false;
	private int lastLogCount = 0;
	private boolean isMultiLineLogEnabled = false;

	private Composite loggingArea;
	private Composite tableArea;
	private Composite consoleArea;


	@Inject
	public PersoSimPart(UISynchronize sync)
	{
		this.sync = sync;
	}

	@PostConstruct
	public void createComposite(Composite loggingArea)
	{
		this.loggingArea = loggingArea;
		loggingArea.setLayout(new GridLayout(1, false));

		tableArea = new Composite(loggingArea, SWT.NONE);
		tableArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tableArea.setLayout(new GridLayout(1, false));

		createTableViewerAndTable();

		consoleArea = new Composite(loggingArea, SWT.NONE);
		consoleArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		consoleArea.setLayout(new GridLayout(1, false));

		createConsoleIn();
	}


	private void createTableViewerAndTable()
	{
		// Remove table if exists
		for (Control childControl : tableArea.getChildren()) {
			childControl.dispose();
		}

		// Restore initial values
		isLocked = false;
		wasDragging = false;
		isProgrammaticSelection = false;
		lastLogCount = 0;

		logTableViewer = new TableViewer(tableArea, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.VIRTUAL);
		table = logTableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.setHeaderBackground(loggingArea.getBackground());
		table.setBackground(loggingArea.getBackground());
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		createColumns();
		addResizeListenerForLastColumn();
		setLabelProvider();
		addVerticalBarListener();
		addContextMenu();
		addCtrlListener();
		addDynamicTooltip();
		changeSelectionColors();
		addFonts();
		addContentProvider();
		addEntrySelectionChangeListener();

		tableArea.layout(true, true);
		startUpdateThread();

		// Select first entry
		int itemCount = table.getItemCount();
		if (itemCount > 0) {
			table.setSelection(0);
			table.showSelection();
		}
	}

	private void addContentProvider()
	{
		// Use ILazyContentProvider for performance
		logTableViewer.setUseHashlookup(true);
		logTableViewer.setContentProvider(new ILazyContentProvider() {
			@Override
			public void updateElement(int index)
			{
				PersoSimUILogEntry entry = Activator.getListLogListener().getEntry(index);
				logTableViewer.replace(entry, index);
			}

			@Override
			public void dispose()
			{
				// nothing to do
			}

			@Override
			public void inputChanged(org.eclipse.jface.viewers.Viewer viewer, Object oldInput, Object newInput)
			{
				// nothing to do
			}
		});
	}

	private void addFonts()
	{
		Label label = new Label(table, SWT.NONE);
		if (defaultFont == null || defaultFont.isDisposed()) {
			FontDescriptor fontDescriptorDefault = FontDescriptor.createFrom(label.getFont()).setStyle(PersoSimUILogEntry.DEFAULT_FONT_STYLE);
			defaultFont = fontDescriptorDefault.createFont(table.getDisplay());
			loggingArea.addDisposeListener(e -> defaultFont.dispose());
		}
		if (boldFont == null || boldFont.isDisposed()) {
			FontDescriptor fontDescriptorBold = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
			boldFont = fontDescriptorBold.createFont(table.getDisplay());
			loggingArea.addDisposeListener(e -> boldFont.dispose());
		}
	}

	private void addEntrySelectionChangeListener()
	{
		// Selection listener (with programmatic flag)
		logTableViewer.addSelectionChangedListener(event -> {
			if (isProgrammaticSelection)
				return;
			IStructuredSelection selection = (IStructuredSelection) logTableViewer.getSelection();
			PersoSimUILogEntry selectedEntry = (PersoSimUILogEntry) selection.getFirstElement();
			boolean isVerticalScrollbarVisible = table.getVerticalBar().isVisible();
			if (isVerticalScrollbarVisible) {
				if (selectedEntry != null) {
					// Handle the selected log entry
					lockAutoScroll();
				}
				else {
					if (isVerticalBarAtBottom()) {
						unlockAutoScroll();
					}
					else {
						lockAutoScroll();
					}
				}
			}
		});

		table.addListener(SWT.MenuDetect, event -> {
			Point pt = table.toControl(event.x, event.y);
			TableItem selectedItem = table.getItem(pt);
			if (selectedItem != null && table.isSelected(table.indexOf(selectedItem))) {
				// Right-click on selected item
				boolean isVerticalScrollbarVisible = table.getVerticalBar().isVisible();
				if (!isVerticalScrollbarVisible) {
					unlockAutoScroll();
				}
			}
		});

	}


	private boolean isVerticalBarAtBottom()
	{
		boolean isVerticalScrollbarVisible = table.getVerticalBar().isVisible();
		if (!isVerticalScrollbarVisible)
			return false;
		int top = table.getTopIndex();
		int visible = table.getClientArea().height / table.getItemHeight();
		int last = table.getItemCount() - 1;
		return (top + visible) >= last;
	}

	private void addVerticalBarListener()
	{
		ScrollBar verticalBar = table.getVerticalBar();
		verticalBar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (e.detail == SWT.DRAG) {
					wasDragging = true;
				}
				else if (e.detail == SWT.NONE && wasDragging) {
					wasDragging = false;
					if (isVerticalBarAtBottom()) {
						unlockAutoScroll();
					}
					else {
						lockAutoScroll();
					}
				}
			}
		});
	}

	private Text createConsoleIn()
	{
		final Text txtIn = new Text(consoleArea, SWT.BORDER);
		txtIn.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		txtIn.setMessage("<Enter command here>");
		txtIn.setToolTipText("Enter command here. Use array keys for command history.");

		createConsoleInCommandHistory(txtIn);

		txtIn.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e)
			{
				if ((e.character == SWT.CR) || (e.character == SWT.LF)) {
					String line = txtIn.getText();
					Activator.executeUserCommands(line, false); // No overlay
					txtIn.setText("");
				}
			}
		});

		return txtIn;
	}

	private void createConsoleInCommandHistory(final Text txtIn)
	{
		// Command history buffer
		java.util.List<String> commandHistory = new java.util.ArrayList<>();
		int[] historyIndex = { -1 };

		// Handle key events for history navigation
		txtIn.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.keyCode == SWT.ARROW_UP) {
					if (commandHistory.isEmpty())
						return;
					if (historyIndex[0] < 0)
						historyIndex[0] = commandHistory.size() - 1;
					else if (historyIndex[0] > 0)
						historyIndex[0]--;
					txtIn.setText(commandHistory.get(historyIndex[0]));
					txtIn.setSelection(txtIn.getText().length());
				}
				else if (e.keyCode == SWT.ARROW_DOWN) {
					if (commandHistory.isEmpty())
						return;
					if (historyIndex[0] < commandHistory.size() - 1) {
						historyIndex[0]++;
						txtIn.setText(commandHistory.get(historyIndex[0]));
						txtIn.setSelection(txtIn.getText().length());
					}
					else {
						txtIn.setText("");
						historyIndex[0] = -1;
					}
				}
				else if (e.keyCode == SWT.CR) { // Enter pressed
					String cmd = txtIn.getText().trim();
					if (!cmd.isEmpty()) {
						int commandCount = commandHistory.size();
						if (commandCount == 0 || !cmd.equals(commandHistory.get(commandCount - 1)))
							commandHistory.add(cmd);
						historyIndex[0] = -1;
					}
				}
			}
		});
	}

	private void lockAutoScroll()
	{
		if (!isLocked) {
			isLocked = true;
		}
	}

	private void unlockAutoScroll()
	{
		if (isLocked) {
			isLocked = false;
		}
	}

	private void addDynamicTooltip()
	{
		DefaultToolTip toolTip = new DefaultToolTip(table, org.eclipse.jface.window.ToolTip.RECREATE, false);
		toolTip.setShift(new Point(10, 10));
		toolTip.setHideOnMouseDown(false);
		toolTip.setBackgroundColor(table.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		table.setToolTipText("");
		int delayDefault = 300;
		int delayMax = Integer.MAX_VALUE;
		toolTip.setPopupDelay(delayDefault);
		table.addListener(SWT.MouseMove, event -> {
			TableItem item = table.getItem(new Point(event.x, event.y));
			if (item != null) {
				int x = event.x;
				int col = -1;
				int cumWidth = 0;
				for (int i = 0; i < table.getColumnCount(); i++) {
					cumWidth += table.getColumn(i).getWidth();
					if (x < cumWidth) {
						col = i;
						break;
					}
				}
				if (col >= 0) {
					Object data = item.getData();
					if (data instanceof PersoSimUILogEntry entry) {
						String text = switch (col) {
							case 0 -> entry.getTimeStamp();
							case 1 -> entry.getLogLevel().name();
							case 2 -> PersoSimUILogFormatter.getFormattedLogTags(entry, PersoSimUILogFormatter.NO_TAGS_AVAILABLE_INFO);
							case 3 -> entry.getLogContent();
							default -> "";
						};
						toolTip.setText(text);
						toolTip.setForegroundColor(table.getDisplay().getSystemColor(entry.getColorId()));
						toolTip.setPopupDelay(delayDefault);
						if (entry.getFontStyle() == SWT.BOLD)
							toolTip.setFont(boldFont);
						else
							toolTip.setFont(defaultFont);
						return;
					}
				}
			}
			toolTip.setText("");
			toolTip.setPopupDelay(delayMax); // hide() does not work properly
		});

		table.addListener(SWT.MouseExit, event -> {
			toolTip.setText("");
			toolTip.setPopupDelay(delayMax);
		});
	}

	private void changeSelectionColors()
	{
		Display display = table.getDisplay();
		Color selectionBackground = display.getSystemColor(SWT.COLOR_GRAY);
		Color selectionForeground = display.getSystemColor(SWT.COLOR_WHITE);

		table.addListener(SWT.EraseItem, event -> {
			TableItem item = (TableItem) event.item;
			boolean isSelected = false;
			for (TableItem selected : table.getSelection()) {
				if (selected == item) {
					isSelected = true;
					break;
				}
			}
			if (isSelected) {
				event.gc.setBackground(selectionBackground);
				event.gc.setForeground(selectionForeground);
				event.gc.fillRectangle(event.getBounds());
				event.detail &= ~SWT.SELECTED;
			}
		});
	}

	private void createColumns()
	{
		TableLayout tableLayout = new TableLayout();
		table.setLayout(tableLayout);

		for (int i = 0; i < COLUMN_TITLES.length; i++) {
			tableLayout.addColumnData(new ColumnWeightData(COLUMN_WEIGHTS[i], COLUMN_MIN_WIDTHS[i], true));
			createTableViewerColumn(COLUMN_TITLES[i], SWT.LEFT);
		}
	}

	private TableViewerColumn createTableViewerColumn(String title, int alignment)
	{
		TableViewerColumn viewerColumn = new TableViewerColumn(logTableViewer, alignment);
		TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	private void addResizeListenerForLastColumn()
	{
		table.addListener(SWT.Resize, event -> {
			TableColumn[] columns = table.getColumns();
			if (columns.length == 0)
				return;

			int totalWidth = table.getClientArea().width;
			int fixedWidth = 0;
			for (int i = 0; i < columns.length - 1; i++) {
				fixedWidth += columns[i].getWidth();
			}
			int lastColWidth = Math.max(100, totalWidth - fixedWidth);
			columns[columns.length - 1].setWidth(lastColWidth);
		});
	}

	private static final int MIN_ROW_HEIGHT = 20; // Minimal cell height in table

	private void setLabelProvider()
	{
		Display display = table.getDisplay();
		logTableViewer.setLabelProvider(new OwnerDrawLabelProvider() {
			@Override
			protected void measure(Event event, Object element)
			{
				if (isMultiLineLogEnabled) {
					PersoSimUILogEntry entry = (PersoSimUILogEntry) element;
					int column = event.index;
					int height = MIN_ROW_HEIGHT;
					if (column == 3) { // MultiLine only for column with log content
						String colContent = entry.getLogContent();
						String[] lines = colContent.split("\\R");
						int totalHeight = 0;
						for (String line : lines) {
							Point extent = event.gc.textExtent(line);
							totalHeight += extent.y;
						}

						// Add padding and set row height
						int padding = 4;
						height = Math.max(totalHeight + padding, MIN_ROW_HEIGHT);
					}
					event.height = Math.max(event.height, height);
				}
				else
					event.height = MIN_ROW_HEIGHT;
			}

			@Override
			protected void paint(Event event, Object element)
			{
				PersoSimUILogEntry entry = (PersoSimUILogEntry) element;
				Color color = display.getSystemColor(entry.getColorId());
				event.gc.setForeground(color != null ? color : display.getSystemColor(SWT.COLOR_BLACK));
				if (entry.getFontStyle() == SWT.BOLD) {
					event.gc.setFont(boldFont);
				}
				else {
					event.gc.setFont(defaultFont);
				}

				int column = event.index;
				String colContent = "";
				switch (column) {
					case 0:
						colContent = entry.getTimeStamp();
						break;
					case 1:
						colContent = entry.getLogLevel().name();
						break;
					case 2:
						colContent = PersoSimUILogFormatter.getFormattedLogTags(entry, null);
						break;
					case 3:
						colContent = entry.getLogContent();
						break;
					default:
						// not possible; internal error
						break;
				}

				if (!isMultiLineLogEnabled && column == 3) {
					String[] colContentParts = colContent.split("\\R");
					int noParts = colContentParts.length;
					if (noParts > 1) {
						StringBuilder colContentSingleLine = new StringBuilder();
						for (int i = 0; i < noParts; i++) {
							String part = colContentParts[i];
							colContentSingleLine.append(part);
							if (i < colContentParts.length - 1)
								colContentSingleLine.append(" | ");
						}
						colContent = colContentSingleLine.toString();
					}
					event.gc.drawText(colContent, event.x + 3, event.y + 3, true);
				}
				else if (isMultiLineLogEnabled && column == 3) {
					String[] lines = colContent.split("\\R");
					int x = event.x + 3;
					int y = event.y + 3;
					for (String line : lines) {
						event.gc.drawText(line, x, y, true);
						y += event.gc.textExtent(line).y;
					}
				}
				else {
					event.gc.drawText(colContent, event.x + 3, event.y + 3, true);
				}
			}
		});
	}

	private void addContextMenu()
	{
		Menu contextMenu = new Menu(table);
		table.setMenu(contextMenu);

		addContextMenuCopy(contextMenu);
		new MenuItem(contextMenu, SWT.SEPARATOR);
		addContextMenuLogLevel(contextMenu);
		addContextMenuLoadPersonalization(contextMenu);
		addContextMenuSaveLogToFile(contextMenu);
		new MenuItem(contextMenu, SWT.SEPARATOR);
		addContextMenuLogMultiLineLog(contextMenu);

		MenuItem submenuItem = new MenuItem(contextMenu, SWT.CASCADE);
		submenuItem.setText("Columns visibility");
		Menu submenu = new Menu(contextMenu);
		submenuItem.setMenu(submenu);

		for (TableColumn tableColumn : table.getColumns()) {
			createColumnVisibilityItem(submenu, tableColumn);
		}
	}

	private void addContextMenuCopy(Menu contextMenu)
	{
		MenuItem copyItem = new MenuItem(contextMenu, SWT.CASCADE);
		copyItem.setText("&Copy selected entries");
		copyItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				copySelectedRowsToClipboard();
			}
		});
	}

	public void copySelectedRowsToClipboard()
	{
		IStructuredSelection selection = (IStructuredSelection) logTableViewer.getSelection();
		if (!selection.isEmpty()) {
			StringBuilder selectedText = new StringBuilder();
			for (Object obj : selection.toArray()) {
				selectedText.append(PersoSimUILogFormatter.format((PersoSimUILogEntry) obj)).append('\n');
			}
			Clipboard clipboard = new Clipboard(table.getDisplay());
			TextTransfer textTransfer = TextTransfer.getInstance();
			clipboard.setContents(new Object[] { selectedText.toString() }, new Transfer[] { textTransfer });
			clipboard.dispose();
		}
	}

	public void addCtrlListener()
	{
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e)
			{
				if ((e.stateMask & SWT.CTRL) != 0 && (e.keyCode == 'c' || e.keyCode == 'C')) {
					copySelectedRowsToClipboard();
				}
				else if ((e.stateMask & SWT.CTRL) != 0 && (e.keyCode == 'a' || e.keyCode == 'A')) {
					table.selectAll();
				}
			}
		});
	}

	private void addContextMenuLogLevel(Menu contextMenu)
	{
		MenuItem changeLogLevelItem = new MenuItem(contextMenu, SWT.CASCADE);
		changeLogLevelItem.setText("Configure Log Levels && Tags");
		changeLogLevelItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				new LogLevelDialog(table.getShell()).open();
			}
		});
	}

	private void addContextMenuLoadPersonalization(Menu contextMenu)
	{
		MenuItem selectPersonalization = new MenuItem(contextMenu, SWT.CASCADE);
		selectPersonalization.setText("Load Personalization");
		selectPersonalization.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				new SelectPersoFromFileHandler().execute(table.getShell());
			}
		});
	}


	private void addContextMenuSaveLogToFile(Menu contextMenu)
	{
		MenuItem saveLogItem = new MenuItem(contextMenu, SWT.CASCADE);
		saveLogItem.setText("Save log to file");
		saveLogItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				try {
					String logFileName = "PersoSim_" + new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime()) + ".log";
					File file = new File(logFileName);
					LinkedListLogListener listener = Activator.getListLogListener();
					int count = listener.getNumberOfCachedEntries();
					try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
						for (int i = 0; i < count; i++) {
							writer.write(PersoSimUILogFormatter.format(listener.getEntry(i)));
							writer.write('\n');
						}
					}
					MessageDialog.openInformation(table.getShell(), "Info", "Logfile written to " + file.getAbsolutePath());
				}
				catch (IOException ioe) {
					BasicLogger.logException(getClass(), ioe);
				}
			}
		});
	}


	private void addContextMenuLogMultiLineLog(Menu contextMenu)
	{
		MenuItem menuItem = new MenuItem(contextMenu, SWT.CHECK);
		menuItem.setText("MultiLine log");
		menuItem.setSelection(isMultiLineLogEnabled);
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				isMultiLineLogEnabled = menuItem.getSelection();
				createTableViewerAndTable();
			}
		});
	}

	private void createColumnVisibilityItem(Menu parent, final TableColumn column)
	{
		final MenuItem menuItem = new MenuItem(parent, SWT.CHECK);
		menuItem.setText(column.getText());
		menuItem.setSelection(column.getWidth() > 0);
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				if (menuItem.getSelection()) {
					int width = columnWidths.getOrDefault(column, 150);
					column.setWidth(width);
					column.setResizable(true);
				}
				else {
					columnWidths.put(column, column.getWidth());
					column.setWidth(0);
					column.setResizable(false);
				}
			}
		});
	}

	private void refreshLogTable()
	{
		LinkedListLogListener listener = Activator.getListLogListener();
		if (listener != null && !isLocked) {
			int count = listener.getNumberOfCachedEntries();
			if (count == 0)
				return;
			if (count != lastLogCount) {
				logTableViewer.setItemCount(count);
				lastLogCount = count;
			}
			logTableViewer.refresh(false); // Only refresh visible rows

			boolean isVerticalScrollbarVisible = table.getVerticalBar().isVisible();
			if (isVerticalScrollbarVisible) {
				scrollToEnd();
			}
		}
	}


	private void scrollToEnd()
	{
		int itemCount = table.getItemCount();
		if (itemCount > 0) {
			isProgrammaticSelection = true;
			table.setSelection(itemCount - 1);
			table.showSelection();
			isProgrammaticSelection = false;
		}
	}

	/**
	 * Update Thread for refreshing log view
	 */
	private void startUpdateThread()
	{
		if (updateThread != null)
			return;
		updateThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				sync.syncExec(this::refreshLogTable);
				try {
					Thread.sleep(500); // Update interval for log view
				}
				catch (InterruptedException e) {
					System.out.println("ERROR: Logging interrupted: " + e.getMessage()); // NOSONAR
					BasicLogger.logException(getClass(), "ERROR: Logging interrupted", e);
					Thread.currentThread().interrupt();
					break;
				}
			}
		});
		updateThread.setDaemon(true);
		updateThread.start();
	}

	@PreDestroy
	public void onDestroy()
	{
		if (updateThread != null && updateThread.isAlive()) {
			updateThread.interrupt();
		}
	}

	@Focus
	public void setFocus()
	{
		logTableViewer.getControl().setFocus();
	}
}
