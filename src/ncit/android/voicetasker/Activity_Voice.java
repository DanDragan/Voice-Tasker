package ncit.android.voicetasker;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Voice extends Activity {

	protected static final int RESULT_SPEECH = 1;

	private Button btnSpeak;
	private Button btnReset;
	private Button btnExit;
	private ListView lView;
	ArrayAdapter<String> adapter;
	ArrayList<String> list;
	HashMap<View, Boolean> hmap; 

	private void init(ArrayList<String> list) {
		list.add("apple");
		list.add("bananas");
		list.add("cucumbers");
		list.add("elephant");
		list.add("Fanta");
		list.add("juice");
		list.add("mango");
		list.add("vegetables");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice);

		lView = (ListView) findViewById(R.id.listview);

		btnSpeak = (Button) findViewById(R.id.btnSpeak);
		btnReset = (Button) findViewById(R.id.btnReset);
		btnExit = (Button) findViewById(R.id.btnExit);

		hmap = new HashMap<View, Boolean>();
		
		list = new ArrayList<String>();
		this.init(list);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		lView.setAdapter(adapter);
		lView.setClickable(true);
		lView.setTextFilterEnabled(true);

		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
						"What shall I do, Master?");
				intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);

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

		btnExit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
				System.exit(0);

			}

		});

		btnReset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				adapter.clear();
				adapter.notifyDataSetInvalidated();
				list.clear();

			}

		});

		lView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				if (hmap.get(view) == null) {

					Toast.makeText(getBaseContext(), "You checked " + list.get(position), Toast.LENGTH_SHORT).show();

					TextView row = (TextView) view;
					row.setPaintFlags(row.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
					hmap.put(view, true);
				}
				
				else{
					Toast.makeText(getBaseContext(), "You unchecked " + list.get(position), Toast.LENGTH_SHORT).show();

					TextView row = (TextView) view;
					row.setPaintFlags(row.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
					hmap.remove(view);
				}

			}

		});

		lView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked
				list.remove(position);
				adapter.notifyDataSetChanged();
				return true;
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
