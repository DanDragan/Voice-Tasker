package ncit.android.voicetasker;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class Activity_Voice extends Activity {

	protected static final int RESULT_SPEECH = 1;

	private Button btnSpeak;
	private Button btnReset;
	private Button btnExit;
	private ListView lView;
	ArrayAdapter<String> adapter;
	ArrayList<String> list;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice);

		lView = (ListView) findViewById(R.id.listview);
		
		btnSpeak = (Button) findViewById(R.id.btnSpeak);
		btnReset = (Button) findViewById(R.id.btnReset);
		btnExit = (Button) findViewById(R.id.btnExit);

		list = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		lView.setAdapter(adapter);

		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

				try {
					startActivityForResult(intent, RESULT_SPEECH);
				} catch (ActivityNotFoundException a) {
					Toast t = Toast.makeText(getApplicationContext(),
							"Opps! Your device doesn't support Speech to Text",
							Toast.LENGTH_SHORT);
					t.show();
				}
			}
		});

	}

	private void addItems(String item) {

		if (item.length() > 0) {
			this.list.add(item);
			this.adapter.notifyDataSetChanged();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SPEECH: {
			if (resultCode == RESULT_OK && data != null) {

				ArrayList<String> text = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				this.addItems(text.get(0));				
			}
			break;
		}

		}
	}
}
