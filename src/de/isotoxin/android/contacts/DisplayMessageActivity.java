package de.isotoxin.android.contacts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import java.util.Iterator;

public class DisplayMessageActivity extends Activity {

	private int contact_id;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_message);
		// Show the Up button in the action bar.
		setupActionBar();

		// Get the message from the intent
		Intent intent = getIntent();
		this.contact_id = intent.getIntExtra(MainActivity.CONTACT_ID, -1);

		// Get data from db
		SQLiteOpenHelper database_helper = new SQLiteOpenHelper(this.getApplicationContext(), MainActivity.database_path, null, 1) {
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			}
			@Override
			public void onCreate(SQLiteDatabase db) {
			}
		};
		Cursor db_cursor = database_helper.getReadableDatabase().rawQuery("SELECT name_display, tel, note, email FROM contacts WHERE id = ?",
				new String[] { Integer.toString(this.contact_id) });
		db_cursor.moveToFirst();
		String name_display = db_cursor.getString(0);
		String tel_serialized = db_cursor.getString(1);
		String notes = db_cursor.getString(2);
		String emails_serialized = db_cursor.getString(3);
		if (notes == null) notes = "";
		notes = notes.replace("\\n", "\n").
					  replace("\\:", ":").
					  replace("\\,", ",");

		// Create layout
		ScrollView scrollView = new ScrollView(this);
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setPadding(30, 30, 30, 30);
		scrollView.addView(layout);
		setContentView(scrollView);

		// Add name
		TextView textView = new TextView(this);
		textView.setTextSize(30);
		textView.setTextColor(0xff888888);
		textView.setText(name_display);
		layout.addView(textView);

		// Add tel numbers
		Space telNumbersTitleSpace = new Space(this);
		telNumbersTitleSpace.setMinimumHeight(10);
		layout.addView(telNumbersTitleSpace);
		TextView telNumbersTitle = new TextView(this);
		telNumbersTitle.setTextSize(20);
		telNumbersTitle.setText("Contact Numbers");
		telNumbersTitle.setTextColor(0xffaaaaff);
		layout.addView(telNumbersTitle);
		try {
			JSONObject tel = (JSONObject) new JSONTokener(tel_serialized).nextValue();
			Iterator<String> iter_location = tel.keys();
			while (iter_location.hasNext()) {
				String location = iter_location.next();
				Iterator<String> iter_device_type = tel.getJSONObject(location).keys();
				while (iter_device_type.hasNext()) {
					String device_type = iter_device_type.next();
					JSONArray tel_nums_for_device_type = tel.getJSONObject(location).getJSONArray(device_type);
					for (int i=0; i<tel_nums_for_device_type.length(); i++) {
						String tel_number_formatted = location.toUpperCase() + " " + device_type.toUpperCase() + " #" + (i+1) + ": " + tel.getJSONObject(location).getJSONArray(device_type).getJSONArray(i).getString(0) + " " + tel.getJSONObject(location).getJSONArray(device_type).getJSONArray(i).getString(1);
						TextView tel_number = new TextView(this);
						tel_number.setText(tel_number_formatted);
						tel_number.setTextIsSelectable(true);
						layout.addView(tel_number);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// add email addresses
		Space emailsTitleSpace = new Space(this);
		emailsTitleSpace.setMinimumHeight(10);
		layout.addView(emailsTitleSpace);
		TextView emailsTitle = new TextView(this);
		emailsTitle.setTextSize(20);
		emailsTitle.setText("Email Addresses");
		emailsTitle.setTextColor(0xffaaaaff);
		layout.addView(emailsTitle);
		try {
			JSONObject emails = (JSONObject) new JSONTokener(emails_serialized).nextValue();
			Iterator<String> emails_loc_iter = emails.keys();
			while (emails_loc_iter.hasNext()) {
				String emails_loc_str = emails_loc_iter.next();
				String emails_loc_formatted_str = emails_loc_str.substring(0, 1).toUpperCase() + emails_loc_str.substring(1);
				JSONArray emails_addresses = emails.getJSONArray(emails_loc_str);
				// email location label
				TextView email_loc_view = new TextView(this);
				email_loc_view.setText(emails_loc_formatted_str);
				email_loc_view.setTextColor(0xff888888);;
				layout.addView(email_loc_view);
				for (int i=0; i<emails_addresses.length(); i++) {
					// email address
					TextView email_view = new TextView(this);
					email_view.setText(emails_addresses.getString(i));
					email_view.setTextIsSelectable(true);
					layout.addView(email_view);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// Add note
		Space noteTitleSpace = new Space(this);
		noteTitleSpace.setMinimumHeight(10);
		layout.addView(noteTitleSpace);
		TextView noteTitle = new TextView(this);
		noteTitle.setTextSize(20);
		noteTitle.setText("Notes");
		noteTitle.setTextColor(0xffaaaaff);
		layout.addView(noteTitle);
		TextView noteBody = new TextView(this);
		noteBody.setText(notes);
		noteBody.setTextIsSelectable(true);
		layout.addView(noteBody);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
