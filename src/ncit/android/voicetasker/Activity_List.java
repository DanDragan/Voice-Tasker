package ncit.android.voicetasker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_List extends Activity {

	protected static final int RESULT_SPEECH = 1;

	private Button btnSpeak;
	private Button btnReset;
	private Button btnSave;
	private ListView lView;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> list;
	private HashMap<View, Boolean> hmap;
	private File dir;
	private String fileName;
	private AdapterContextMenuInfo info;

	private void init(ArrayList<String> list) {

		dir = getExternalFilesDir(null);
		fileName = Activity_Show.getFileName();

		File myInput = new File(dir + "/" + fileName);

		try {
			FileReader in = new FileReader(myInput);

			StringWriter sw = new StringWriter();

			char[] b = new char[1024 * 64];
			while (in.read(b) > 0) {
				sw.write(b);
			}

			String s = sw.toString();
			JSONArray jArray = new JSONArray(s);

			for (int i = 0; i < jArray.length(); i++) {
				list.add(jArray.getString(i));
			}
			
			in.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_voice);

		lView = (ListView) findViewById(R.id.listview);

		btnSpeak = (Button) findViewById(R.id.btnSpeak);
		btnReset = (Button) findViewById(R.id.btnReset);
		btnSave = (Button) findViewById(R.id.btnSave);

		hmap = new HashMap<View, Boolean>();

		list = new ArrayList<String>();
		this.init(list);

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		lView.setAdapter(adapter);
		lView.setClickable(true);
		lView.setTextFilterEnabled(true);
		registerForContextMenu(lView);

		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
				intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "What shall I do, Master?");
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

		btnSave.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				
				PromptDialog dlg = new PromptDialog(Activity_List.this, fileName) {  
					 @Override  
					 public boolean onOkClicked(String input) {  
						 // do something
						 try {
								
								File myOutput = new File(dir + "/" + input);
								if (!myOutput.exists()) {
									myOutput.getParentFile().mkdirs();
									myOutput.createNewFile();
								}
								
								JSONArray jArray = new JSONArray(list);
								FileOutputStream out = new FileOutputStream(myOutput);
									
								out.write(jArray.toString().getBytes());
								out.close();
								
								
						 } catch (Exception e) {

								e.printStackTrace();
						 }
						 
						 if(input.length()>0)
								Toast.makeText(getApplicationContext(), "List saved!", Toast.LENGTH_SHORT).show();
							return true; // true = close dialog
					 }  
				};  
					
				dlg.show();
			
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
				// When clicked
				if (hmap.get(view) == null) {

					Toast.makeText(getBaseContext(),
							"You checked " + list.get(position),
							Toast.LENGTH_SHORT).show();

					TextView row = (TextView) view;
					row.setPaintFlags(row.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);
					row.setTextColor(Color.rgb(0, 200, 0));
					hmap.put(view, true);
				}

				else {
					Toast.makeText(getBaseContext(),
							"You unchecked " + list.get(position),
							Toast.LENGTH_SHORT).show();

					TextView row = (TextView) view;
					row.setPaintFlags(row.getPaintFlags()
							& (~Paint.STRIKE_THRU_TEXT_FLAG));
					row.setTextColor(Color.BLACK);
					hmap.remove(view);
				}

			}

		});

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Item Menu");
		menu.add(0, v.getId(), 0, "Edit");
		menu.add(0, v.getId(), 0, "Delete");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		info = (AdapterContextMenuInfo) item.getMenuInfo();

		if (item.getTitle() == "Edit") {
			editItem(info.position);
		} else if (item.getTitle() == "Delete") {
			deleteItem(info.position);
		} else {
			return false;
		}
		return true;
	}

	private void deleteItem(int pos) {

		list.remove(pos);
		adapter.notifyDataSetChanged();

	}
	
	private void editItem(int pos) {
		final int position = pos;
		PromptDialog dlg = new PromptDialog(Activity_List.this, list.get(pos)) {
			@Override
			public boolean onOkClicked(String input) {
				
				list.add(position, input);
				list.remove(position + 1);
				adapter.notifyDataSetChanged();
				
				return true; // true = close dialog
			}
		};

		dlg.show();
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
