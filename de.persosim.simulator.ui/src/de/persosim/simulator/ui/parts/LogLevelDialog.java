package de.persosim.simulator.ui.parts;

import java.util.List;
import java.util.StringJoiner;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.globaltester.logging.tags.LogLevel;

import de.persosim.simulator.PersoSimLogTags;
import de.persosim.simulator.preferences.PersoSimPreferenceManager;
import de.persosim.simulator.ui.Activator;
import de.persosim.simulator.ui.LogHelper;
import de.persosim.simulator.ui.utils.PersoSimUILogFormatter;

public class LogLevelDialog extends Dialog
{
	private Button[] btnLogLevels;
	private Button[] btnLogTags;
	private List<String> allTags;

	public LogLevelDialog(Shell parentShell)
	{
		super(parentShell);
		allTags = PersoSimLogTags.getAllTags();
	}

	@Override
	protected Control createDialogArea(Composite parent)
	{
		Composite mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new GridLayout(2, true));

		// --- LogLevel Panel ---
		Group logLevelGroup = new Group(mainComposite, SWT.NONE);
		logLevelGroup.setText("Log Levels");
		logLevelGroup.setLayout(new GridLayout(1, false));
		logLevelGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		logLevelGroup.setToolTipText("Show all selected log levels");

		Label lblInfo = new Label(logLevelGroup, SWT.NONE);
		lblInfo.setText("Select the log levels to view:");

		String levels = PersoSimPreferenceManager.getPreference(LogHelper.PREF_LOG_LEVELS);
		btnLogLevels = new Button[LogLevel.values().length];
		for (int i = 0; i < LogLevel.values().length; i++) {
			LogLevel logLevel = LogLevel.values()[i];
			btnLogLevels[i] = new Button(logLevelGroup, SWT.CHECK);
			btnLogLevels[i].setText(logLevel.toString());
			btnLogLevels[i].setToolTipText("Show log entries with level " + logLevel.name());
			boolean selected = (levels == null) || levels.contains(logLevel.name());
			btnLogLevels[i].setSelection(selected);
		}

		// --- LogTag Panel ---
		Group logTagGroup = new Group(mainComposite, SWT.NONE);
		logTagGroup.setText("Log Tags");
		logTagGroup.setLayout(new GridLayout(1, false));
		logTagGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		logTagGroup.setToolTipText("Show all selected log tags");

		Label lblTagInfo = new Label(logTagGroup, SWT.NONE);
		lblTagInfo.setText("Select the log tags to view:");
		btnLogTags = new Button[allTags.size() + 1];
		String tagsPref = PersoSimPreferenceManager.getPreference(LogHelper.PREF_LOG_TAGS);

		for (int i = 0; i < allTags.size(); i++) {
			String tag = allTags.get(i);
			btnLogTags[i] = new Button(logTagGroup, SWT.CHECK);
			btnLogTags[i].setText(tag);
			btnLogTags[i].setToolTipText("Show log entries with tag " + tag);
			boolean selected = (tagsPref == null) || tagsPref.contains(tag);
			btnLogTags[i].setSelection(selected);
		}

		// Handle NO_TAGS_AVAILABLE_INFO
		int noTagsEntryIndex = allTags.size();
		btnLogTags[noTagsEntryIndex] = new Button(logTagGroup, SWT.CHECK);
		btnLogTags[noTagsEntryIndex].setText(PersoSimUILogFormatter.NO_TAGS_AVAILABLE_INFO);
		btnLogTags[noTagsEntryIndex].setToolTipText("Show log entries without any tag");
		boolean selected = (tagsPref == null) || tagsPref.contains(PersoSimUILogFormatter.NO_TAGS_AVAILABLE_INFO);
		btnLogTags[noTagsEntryIndex].setSelection(selected);

		return mainComposite;
	}

	@Override
	protected void configureShell(Shell newShell)
	{
		super.configureShell(newShell);
		newShell.setText("Configure visible Log Levels & Log Tags");
	}

	@Override
	protected void okPressed()
	{
		// Save LogLevel
		StringJoiner levelJoiner = new StringJoiner(LogHelper.PREF_DELIMITER);
		for (int i = 0; i < btnLogLevels.length; i++) {
			if (btnLogLevels[i].getSelection()) {
				levelJoiner.add(LogLevel.values()[i].name());
			}
		}
		PersoSimPreferenceManager.storePreference(LogHelper.PREF_LOG_LEVELS, levelJoiner.toString());

		// Save LogTags
		StringJoiner tagJoiner = new StringJoiner(LogHelper.PREF_DELIMITER);
		for (int i = 0; i < btnLogTags.length; i++) {
			if (btnLogTags[i].getSelection()) {
				// Handle NO_TAGS_AVAILABLE_INFO
				if (i != btnLogTags.length - 1)
					tagJoiner.add(allTags.get(i));
				else
					tagJoiner.add(PersoSimUILogFormatter.NO_TAGS_AVAILABLE_INFO);
			}
		}
		PersoSimPreferenceManager.storePreference(LogHelper.PREF_LOG_TAGS, tagJoiner.toString());

		Activator.getListLogListener().setRefreshState(true);
		Activator.getListLogListener().updateConfig();

		super.okPressed();
	}
}
