package ncit.android.voicetasker;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Activity_ListDir extends Activity {

	private ListView lView;
	private ArrayList<String> list;
	private ArrayAdapter<String> adapter;
	private File dir;
	private String[] flist;
	private static String subDir;

	private void init(ArrayList<String> list) {
		list.clear();
		dir = getExternalFilesDir(null);
		flist = dir.list();

		for (String file : flist) {
			list.add(file);
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show);

		lView = (ListView) findViewById(R.id.listview);
		list = new ArrayList<String>();

		this.init(list);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);

		lView.setAdapter(adapter);
		lView.setClickable(true);
		lView.setTextFilterEnabled(true);

		lView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				subDir = list.get(position);

				Intent intent = new Intent(getApplicationContext(),
						Activity_Show.class);
				startActivity(intent);
			}
		});
	}
	
	protected static String getSubDirName() {
		return subDir;
	}
}
