package de.isotoxin.android.contacts;

import android.content.Context;
import android.content.Intent;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.content.Intent;
import android.view.View;



public class ContactListLabel extends android.widget.TextView {

	private final int contact_id;
	public final static String EXTRA_MESSAGE = "com.example.myfirstandroidapp.MESSAGE";
	MainActivity main_activity;

	public ContactListLabel(int contact_id,
			Context context,
			MainActivity main_activity) {
		super(context);

		this.main_activity = main_activity;
		this.contact_id = contact_id;

		this.setClickable(true);
		this.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Do something in response to button
				Intent intent = new Intent(ContactListLabel.this.main_activity,
						DisplayMessageActivity.class);
				intent.putExtra(MainActivity.CONTACT_ID, ContactListLabel.this.contact_id);
				ContactListLabel.this.main_activity.startActivity(intent);
			}
		});
		LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		llp.setMargins(20, 5, 20, 5);
		this.setLayoutParams(llp);
		this.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
		this.setTextColor(0xFF888888);
	}
}
