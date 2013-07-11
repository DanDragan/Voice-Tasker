package ncit.android.voicetasker;

import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Activity_Load extends Activity_Shopping implements Observable {

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
}