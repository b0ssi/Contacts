package de.isotoxin.android.contacts;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;


public class MainActivity extends Activity {

	public final static String CONTACT_ID = "de.isotoxin.android.contacts.CONTACT_ID";
	public String[] contacts_names_display;
	private JSONObject[] contacts_tels;
	public int[] contacts_ids;
	public final static String database_path = "/storage/sdcard0/data/de.isotoxin.android.contacts/databases/address_book.sqlite";
	private UpdateResultsListAsyncTask updateResultsListAsyncTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		EditText edit_message = (EditText) findViewById(R.id.edit_message);
		edit_message.addTextChangedListener(
				new TextWatcher() {
					public void afterTextChanged(Editable s) {
						EditText edit_message = (EditText) findViewById(R.id.edit_message);
//						TextView test = (TextView) findViewById(R.id.text_view_input);
//						test.setText(edit_text.getText());
						MainActivity.this.updateResultsList(edit_message);
					}
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
					public void onTextChanged(CharSequence s, int start, int before, int count) {}
				});
		// get contacts data from db
		SQLiteOpenHelper database_helper = new SQLiteOpenHelper(this.getApplicationContext(), database_path, null, 1) {
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			}
			@Override
			public void onCreate(SQLiteDatabase db) {
			}
		};
		Cursor db_cursor = database_helper.getReadableDatabase().rawQuery("SELECT id, name_display, tel FROM contacts", null);
		this.contacts_ids = new int[db_cursor.getCount()];
		this.contacts_names_display = new String[db_cursor.getCount()];
		this.contacts_tels = new JSONObject[db_cursor.getCount()];
		for (int i=0; i<db_cursor.getCount(); i++) {
			db_cursor.moveToPosition(i);
			this.contacts_ids[i] = db_cursor.getInt(0);
			this.contacts_names_display[i] = db_cursor.getString(1);
			try {
				this.contacts_tels[i] = (JSONObject) new JSONTokener(db_cursor.getString(2)).nextValue();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		db_cursor.close();
//		System.out.println(names_display[0]);
//		try {
//			System.out.println(tel[0].getJSONObject("work").getJSONArray("video"));
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void updateResultsList(final EditText edit_message) {
		LinearLayout layout_contacts = (LinearLayout) findViewById(R.id.layout_contacts);
		// empty layout
		layout_contacts.removeAllViews();
		// (re)fill list asynchronously
		UpdateResultsListAsyncTaskData data = new UpdateResultsListAsyncTaskData(this, edit_message);
		if (this.updateResultsListAsyncTask != null) {
			this.updateResultsListAsyncTask.cancel(true);
		}
		this.updateResultsListAsyncTask = new UpdateResultsListAsyncTask();
		this.updateResultsListAsyncTask.execute(data);
	}

}
