package ncit.android.voicetasker;

import java.io.File;
import java.io.FileOutputStream;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_Shopping extends Activity {

	private static final int RESULT_SPEECH = 1;
	
	private boolean speechWhere;
	
	private Button btnSpeak_shop;
	private Button btnReset_shop;
	private Button btnSave_shop;
	private TextView tvBudget;
	private TextView tvTotal;
	
	private ListView lvshop;
	private ArrayList<String> list;
	private ArrayAdapter<String> adapter;
	
	private File dir;
	private AdapterContextMenuInfo info;
	private HashMap<View, Boolean> hmap;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping);
		
		btnSpeak_shop = (Button) findViewById(R.id.btnSpeak_shop);
		btnReset_shop = (Button) findViewById(R.id.btnReset_shop);
		btnSave_shop = (Button) findViewById(R.id.btnSave_shop);
		tvTotal = (TextView) findViewById(R.id.tvTotal);
		tvBudget = (TextView) findViewById(R.id.tvBudget);
		lvshop = (ListView) findViewById(R.id.lvShop);
		
		list = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		lvshop.setAdapter(adapter);
		lvshop.setClickable(true);
		lvshop.setTextFilterEnabled(true);
		registerForContextMenu(lvshop);
		
		hmap = new HashMap<View, Boolean>();
		
		dir = getExternalFilesDir(null);
		
		btnSpeak_shop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				/*Intent intent = new Intent(
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
				}*/
				
				speechWhere = false;
				speechFunction("What would you like to add, Master?");
				
			}
		});

		btnSave_shop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				PromptDialog dlg = new PromptDialog(Activity_Shopping.this,
						R.string.title, R.string.enter_comment) {
					@Override
					public boolean onOkClicked(String input) {
						// do something
						try {

							File myOutput = new File(dir + "/shopping lists" + "/" + input);
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

						if (input.length() > 0)
							Toast.makeText(getApplicationContext(),
									"List saved!", Toast.LENGTH_SHORT).show();
						return true; // true = close dialog
					}
				};

				dlg.show();

			}

		});

		btnReset_shop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				adapter.clear();
				adapter.notifyDataSetInvalidated();
				list.clear();

			}

		});

		lvshop.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked
				if (hmap.get(view) == null) {

					Toast.makeText(getApplicationContext(),
							"You checked " + list.get(position),
							Toast.LENGTH_SHORT).show();

					TextView row = (TextView) view;
					row.setPaintFlags(row.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);
					row.setTextColor(Color.rgb(0, 200, 0));
					hmap.put(view, true);
					
					speechWhere = true;
					speechFunction("What is the price, Master?");	
				}

				else {
					Toast.makeText(getApplicationContext(),
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

		if (item.getTitle() == "Edit")
			editItem(info.position);
		else if (item.getTitle() == "Delete")
			deleteItem(info.position);
		else
			return false;

		return true;
	}

	private void deleteItem(int pos) {

		list.remove(pos);
		adapter.notifyDataSetChanged();

	}

	private void editItem(int pos) {
		final int position = pos;
		PromptDialog dlg = new PromptDialog(Activity_Shopping.this, list.get(pos)) {
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
	
	public void addItems2(String item){
		
		list.set(0, list.get(0).toString() + " " + item);
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void speechFunction(String s){
		
		Intent intent = new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, s);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SPEECH: {
			if (resultCode == RESULT_OK && data != null) {

				ArrayList<String> text = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				
				if(speechWhere == false)
					this.addItems(text.get(0));
				else
					this.addItems2(text.get(0));
			}
			break;
		}

		}
	}
	
}
