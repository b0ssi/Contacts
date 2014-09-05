package de.isotoxin.android.contacts;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView.BufferType;

public class UpdateResultsListAsyncTask extends AsyncTask <UpdateResultsListAsyncTaskData, Integer, UpdateResultsListAsyncTaskData> {

	List<ContactListLabel> contactLabels = new ArrayList<ContactListLabel>();

	protected UpdateResultsListAsyncTaskData doInBackground(UpdateResultsListAsyncTaskData... data) {
		MainActivity main_activity = data[0].main_activity;
		EditText edit_message = data[0].edit_text;

		int contacts_count = main_activity.contacts_names_display.length;
		int j = 0;
		String patternStr = "(?i)(.*)(" + edit_message.getText() + ")(.*)";
		Pattern p = Pattern.compile(patternStr);
		for (int i=0; i<contacts_count; i++) {
			Matcher m = p.matcher(main_activity.contacts_names_display[i]);

			if (m.find()) {
				// create view
				ContactListLabel text_view = new ContactListLabel(main_activity.contacts_ids[i], main_activity.getApplicationContext(), main_activity);
				// set text
				SpannableString text = new SpannableString(m.group(1) + m.group(2) + m.group(3));
				text.setSpan(new ForegroundColorSpan(0xffffffff), m.group(1).length(), m.group(1).length() + m.group(2).length(), 0);
				text_view.setText(text, BufferType.SPANNABLE);
//				text_view.setText(Html.fromHtml(m.group(1) + "<b>" +m.group(2) + "</b>" + m.group(3)));
				this.contactLabels.add(text_view);
				if (j==9) {
					break;
				}
				j ++;
			}
		}

		return data[0];
	}

	protected void onPreExecute() {
	}

	protected void onProgressUpdate(Integer... progress) {
	}

	protected void onPostExecute(UpdateResultsListAsyncTaskData data) {
		MainActivity main_activity = data.main_activity;

		LinearLayout layout_contacts = (LinearLayout) main_activity.findViewById(R.id.layout_contacts);
		for (int i=0; i<this.contactLabels.size(); i++) {
			ContactListLabel contactLabel = this.contactLabels.get(i);
			layout_contacts.addView(contactLabel);
		}
	}
}