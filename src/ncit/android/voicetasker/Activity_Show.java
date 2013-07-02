package ncit.android.voicetasker;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import org.json.JSONArray;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class Activity_Show extends Activity {

	private ListView lView;
	private ArrayList<String> list;
	private ArrayAdapter<String> adapter;
	private File dir;
	private String[] flist;
	private static String fileName;
	private AdapterContextMenuInfo info;

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
				fileName = list.get(position);

				Intent intent = new Intent(getApplicationContext(),
						Activity_List.class);
				startActivity(intent);
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

		if (item.getTitle() == "Rename") {
			renameList(info.position);
		} else if (item.getTitle() == "Delete") {
			deleteList(info.position);
		} else {
			return false;
		}
		return true;
	}

	private void renameList(int pos) {
		final int position = pos;
		PromptDialog dlg = new PromptDialog(Activity_Show.this, list.get(pos)) {
			@Override
			public boolean onOkClicked(String input) {

				list.add(position, input);
				
				try {

					File myOutput = new File(dir + "/" + input);
					if (!myOutput.exists()) {
						myOutput.getParentFile().mkdirs();
						myOutput.createNewFile();
					}

					JSONArray jArray = new JSONArray(list);
					FileOutputStream out = new FileOutputStream(
							myOutput);

					out.write(jArray.toString().getBytes());
					out.close();

				} catch (Exception e) {
					e.printStackTrace();
				}

				if (input.length() > 0)
					Toast.makeText(getApplicationContext(),
							"List saved!", Toast.LENGTH_SHORT).show();
				
				list.remove(position + 1);
				
				File file = new File(dir + "/" + list.get(position + 1));

				boolean deleted = file.delete();

				if (!deleted) {
					Toast.makeText(getApplicationContext(), "Could not delete file ",
							Toast.LENGTH_SHORT).show();
				}
				
				adapter.notifyDataSetChanged();
				return true; // true = close dialog
			}
		};

		dlg.show();
	}

	private void deleteList(int pos) {

		File file = new File(dir + "/" + list.get(pos));

		boolean deleted = file.delete();

		if (!deleted) {
			Toast.makeText(getApplicationContext(), "Could not delete file ",
					Toast.LENGTH_SHORT).show();
		}

		else {
			list.remove(pos);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		this.init(list);
		lView.invalidateViews();
	}

	protected static String getFileName() {
		return fileName;
	}
}
