package ncit.android.voicetasker;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class Activity_Shopping extends Activity implements Observable{

	private static final int RESULT_SPEECH = 1;
	
	protected static boolean speechWhere;
	
	private Button btnSpeak_shop;
	private Button btnReset_shop;
	private Button btnSave_shop;
		
	private ListView lvshop;
	private ListAdapter adapter;
	private ArrayList<ListItem> list;
	
	private File dir;
	private AdapterContextMenuInfo info;
	
	private int pozitie;
	private float budget;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping);
		
		btnSpeak_shop = (Button) findViewById(R.id.btnSpeak_shop);
		btnReset_shop = (Button) findViewById(R.id.btnReset_shop);
		btnSave_shop = (Button) findViewById(R.id.btnSave_shop);
		
		lvshop = (ListView) findViewById(R.id.lvShop);
		
		list = new ArrayList<ListItem>();
		budget = 0;
		
		adapter = new ListAdapter(list, this);
		adapter.setSubject(this);
		lvshop.setAdapter(adapter);
		lvshop.setClickable(true);
		lvshop.setTextFilterEnabled(true);
		registerForContextMenu(lvshop);
		
		dir = getExternalFilesDir(null);
		
		btnSpeak_shop.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
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
							setOkClicked(input);

							return true; // true = close dialog
						}
					};

					dlg.show();
				}
		});

		btnReset_shop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				adapter.notifyDataSetChanged();
				adapter.notifyDataSetInvalidated();
				list.clear();

			}

		});		
	}
	
	public void setOkClicked(String input) {
		try {

			File myOutput = new File(dir + "/shopping lists/" + input);
			if (!myOutput.exists()) {
				myOutput.getParentFile().mkdirs();
				myOutput.createNewFile();
			}

			JSONArray jArray = new JSONArray();
			JSONObject bud = new JSONObject();
			bud.put("price", budget);
			jArray.put(bud);
			for (int i = 0; i < list.size(); i++) {
				JSONObject obj = new JSONObject();
				obj.put("status", list.get(i).isChecked());
				obj.put("name", list.get(i).getItem());
				obj.put("price", list.get(i).getPrice());
				jArray.put(obj);

			}

			FileOutputStream out = new FileOutputStream(myOutput);

			out.write(jArray.toString().getBytes());
			out.close();

		} catch (Exception e) {

			e.printStackTrace();
		}
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
		PromptDialog dlg = new PromptDialog(Activity_Shopping.this, list.get(pos).getItem()) {
			@Override
			public boolean onOkClicked(String input) {

				list.get(position).setItem(input);
				list.get(position).setChecked(false);
				
				adapter.notifyDataSetChanged();
				return true; // true = close dialog
			}
		};

		dlg.show();
	}

	private void addItems(String item) {

		if (item.length() > 0) {
			
			this.list.add(new ListItem(item, "", false));
			this.adapter.notifyDataSetChanged();			
		}
	}
	
	public void addItems2(String price, int pozitie){
		
		list.get(pozitie).setPrice(price);
		adapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void speechFunction(String s){
		
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

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
					this.addItems2(text.get(0), pozitie);
			}
			break;
		}

		}
	}

	@Override

	public void update(int position) {
		this.speechFunction("What is the price, Master?");
		pozitie = position;
	}
	
}
