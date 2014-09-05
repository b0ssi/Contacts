package de.isotoxin.android.contacts;

import android.widget.EditText;

public class UpdateResultsListAsyncTaskData {
	public MainActivity main_activity = null;
	public EditText edit_text = null;

	public UpdateResultsListAsyncTaskData(final MainActivity main_activity,
			final EditText edit_text) {
		this.main_activity = main_activity;
		this.edit_text = edit_text;
	}
}
