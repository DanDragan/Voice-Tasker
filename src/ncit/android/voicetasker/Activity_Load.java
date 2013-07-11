package ncit.android.voicetasker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Activity_Load extends Activity implements Observable {

	protected static final int RESULT_SPEECH = 1;
	protected double total;
	protected double budget;
	protected static boolean speechWhere;
	protected static boolean speechBudget;

	private Button btnSpeak;
	private Button btnReset;
	private Button btnSave;
	private Button btnEdit_shop;
	private ListView lvShop;
	private ListAdapter adapter;
	private ArrayList<ListItem> list;
	private static File dir;
	private static String fileName;
	private AdapterContextMenuInfo info;
	private String subdir;
	private TextView tvTotal;
	private TextView tvBudget;
	private int pozitie;

	private void init(ArrayList<ListItem> list) {

		dir = getExternalFilesDir(null);
		subdir = Activity_ListDir.getSubDirName();
		fileName = Activity_Show.getFileName();

		File myInput = new File(dir + "/" + subdir + "/" + fileName);

		try {
			FileReader in = new FileReader(myInput);

			StringWriter sw = new StringWriter();

			char[] b = new char[1024 * 64];
			while (in.read(b) > 0) {
				sw.write(b);
			}

			String s = sw.toString();
			JSONArray jArray = new JSONArray(s);
			JSONObject bud = jArray.getJSONObject(0);
			String budg = bud.getString("price");
			budget = Double.parseDouble(budg);
			tvBudget.setText("BUDGET : " + budget);

			for (int i = 1; i < jArray.length(); i++) {
				JSONObject obj = jArray.getJSONObject(i);

				boolean status = obj.getBoolean("status");
				String price = obj.getString("price");
				String name = obj.getString("name");
				list.add(new ListItem(name, price, status));
			}

			in.close();

			tvTotal.setText("TOTAL : " + String.valueOf(getTotal()));

			calculateTotal();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping);

		lvShop = (ListView) findViewById(R.id.lvShop);

		btnSpeak = (Button) findViewById(R.id.btnSpeak_shop);
		btnReset = (Button) findViewById(R.id.btnReset_shop);
		btnSave = (Button) findViewById(R.id.btnSave_shop);
		btnEdit_shop = (Button) findViewById(R.id.btnEditBudget);

		tvTotal = (TextView) findViewById(R.id.tvTotal);
		tvBudget = (TextView) findViewById(R.id.tvBudget);

		total = 0;
		speechBudget = false;

		list = new ArrayList<ListItem>();
		this.init(list);

		adapter = new ListAdapter(list, this);
		adapter.setSubject(this);

		lvShop.setAdapter(adapter);
		lvShop.setClickable(true);
		lvShop.setTextFilterEnabled(true);

		registerForContextMenu(lvShop);

		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				speechWhere = false;
				speechFunction("What would you like to add, Master?");

			}
		});

		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				PromptDialog dlg = new PromptDialog(Activity_Load.this,
						fileName) {

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

		btnReset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				adapter.notifyDataSetChanged();
				adapter.notifyDataSetInvalidated();
				total = 0;
				tvTotal.setText("TOTAL : ");
				tvTotal.setTextColor(Color.rgb(0, 0, 0));
				list.clear();

			}

		});

		btnEdit_shop.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				speechBudget = true;
				speechFunction("Please tell me your budget, Master");

			}

		});

		tvBudget.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				PromptDialog dlg = new PromptDialog(Activity_Load.this, String
						.valueOf(budget), "budget") {

					@Override
					public boolean onOkClicked(String input) {

						try {
							String newBudget = input.replaceAll("([^\\d\\.])*",
									"");
							if (!newBudget.equals("")) {
								tvBudget.setText("BUDGET : " + newBudget);
								budget = Double.parseDouble(newBudget);

								calculateTotal();
							}
						} catch (Exception e) {
							e.printStackTrace();
							return false;
						}
						return true; // true = close dialog
					}
				};

				dlg.show();

				return true;
			}
		});

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Item Menu");
		menu.add(0, v.getId(), 0, "Edit Name");
		menu.add(0, v.getId(), 0, "Edit Price");
		menu.add(0, v.getId(), 0, "Delete");
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		info = (AdapterContextMenuInfo) item.getMenuInfo();

		if (item.getTitle().equals("Edit Name")) {
			editItem(info.position);
		} else if (item.getTitle().equals("Edit Price")) {
			editPrice(info.position);
		} else if (item.getTitle() == "Delete") {
			deleteItem(info.position);
		} else {
			return false;
		}
		return true;
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
			bud.put("price", "" + budget);
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

		if (input.length() > 0) {
			Toast.makeText(getApplicationContext(), "List saved!",
					Toast.LENGTH_SHORT).show();
		}

	}

	private void deleteItem(int pos) {

		list.remove(pos);
		adapter.notifyDataSetChanged();

	}

	private void editItem(int pos) {
		final int position = pos;
		PromptDialog dlg = new PromptDialog(Activity_Load.this, list.get(pos)
				.getItem()) {
			@Override
			public boolean onOkClicked(String input) {

				list.get(position).setItem(input);

				adapter.notifyDataSetChanged();
				return true; // true = close dialog
			}
		};

		dlg.show();
	}

	private void editPrice(int pos) {
		final int position = pos;
		PromptDialog dlg = new PromptDialog(Activity_Load.this, list.get(pos)
				.getPrice(), true) {
			@Override
			public boolean onOkClicked(String input) {

				list.get(position).setPrice(input);

				adapter.notifyDataSetChanged();
				total = getTotal();
				tvTotal.setText("TOTAL : " + total);
				calculateTotal();
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

	public void addItems2(String price, int pozitie) {
		Log.i("pret", "" + price);
		try {
			String newPrice = price.replaceAll("([^\\d\\.])*", "");
			price.replaceAll(" million", "000000");
			price.replaceAll(" billion", "000000000");
			list.get(pozitie).setPrice(newPrice);
			adapter.notifyDataSetChanged();
			Log.d("string replacement", newPrice);
			total += Double.parseDouble(newPrice);
			tvTotal.setText("TOTAL : " + String.valueOf(total));

			calculateTotal();
		} catch (Exception e) {
			e.printStackTrace();
			list.get(pozitie).setPrice("");
			list.get(pozitie).setChecked(false);
			adapter.notifyDataSetChanged();
		}
	}

	public void addItems3(String price) {

		try {
			String newBudget = price.replaceAll("([^\\d\\.])*", "");
			newBudget.replaceAll(" million", "000000");
			newBudget.replaceAll(" billion", "000000000");
			if (!newBudget.equals("")) {
				tvBudget.setText("BUDGET : " + newBudget);
				budget = Double.parseDouble(newBudget);
				calculateTotal();
				speechBudget = false;
			}

		} catch (Exception e) {
			e.printStackTrace();
			tvBudget.setText("BUDGET : ");
			budget = 0.1;
			calculateTotal();
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

				if (speechBudget == true) {
					addItems3(text.get(0));
				}

				else {

					if (speechWhere == false)
						this.addItems(text.get(0));
					else
						this.addItems2(text.get(0), pozitie);

				}
			}
			break;
		}

		}
	}

	public void speechFunction(String s) {

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
	public void update(int position) {
		this.speechFunction("What is the price, Master?");
		pozitie = position;
	}

	@Override
	public void update_uncheck(int position) {

		String newPrice = list.get(position).getPrice()
				.replaceAll("([^\\d\\.])*", "");
		newPrice.replaceAll(" milion", "000000");
		newPrice.replaceAll(" bilion", "000000000");

		if (!newPrice.equals("")) {
			total -= Double.parseDouble(newPrice);
		}
		tvTotal.setText("TOTAL: " + String.valueOf(total));
		list.get(position).setPrice("");
		adapter.notifyDataSetChanged();

		calculateTotal();

	}

	public void calculateTotal() {

		if (budget > 0 && total >= 0) {

			if (total < budget) {

				Toast t = Toast.makeText(getApplicationContext(),
						"You are currently at " + (total / budget) * 100
								+ " % of your budget", Toast.LENGTH_SHORT);
				t.show();

				if (total / budget < 0.8)
					tvTotal.setTextColor(Color.rgb(0, 0, 0));

				else
					tvTotal.setTextColor(Color.rgb(255, 140, 0));

			}

			else if (total > budget) {

				Toast t = Toast.makeText(getApplicationContext(),
						"You are over your budget with  "
								+ ((total - budget) / budget) * 100 + " %",
						Toast.LENGTH_SHORT);
				t.show();

				tvTotal.setTextColor(Color.rgb(255, 0, 0));
			}

			else {

				Toast t = Toast.makeText(getApplicationContext(),
						"You reached your budget !", Toast.LENGTH_SHORT);
				t.show();

				tvTotal.setTextColor(Color.rgb(255, 0, 0));
			}

		}

	}

	private double getTotal() {

		total = 0;

		for (int i = 0; i < list.size(); i++) {

			String temp = list.get(i).getPrice();
			if (temp.equals(""))
				temp = "0";
			total += Double.parseDouble(temp);

		}

		return total;
	}
}
