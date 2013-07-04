package ncit.android.voicetasker;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class Activity_Shopping extends Activity_Voice {

	private Button btnSpeak_shop;
	private Button btnReset_shop;
	private Button btnSave_shop;
	
	private ListView lvshop;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> list;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shopping);
		
		btnSpeak_shop = (Button) findViewById(R.id.btnSpeak_shop);
		btnReset_shop = (Button) findViewById(R.id.btnReset_shop);
		btnSave_shop = (Button) findViewById(R.id.btnSave_shop);

		lvshop = (ListView) findViewById(R.id.lvShop);
		
		list = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
	}
	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
}
