package ncit.android.voicetasker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button btnNewList;
	private Button btnExit;
	private Button btnShowLists;
	private AlertDialog levelDialog;

	private void insertDialog(){
		
		final CharSequence[] items = { "Simple List", "Shopping List" };

		// Creating and Building the Dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Please select list type");
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {

						switch (item) {
						case 0:
							Intent intent = new Intent(getApplicationContext(), Activity_Voice.class); 
							startActivity(intent);
							break;
						case 1:
							Intent intent2 = new Intent(getApplicationContext(), Activity_Shopping.class); 
							startActivity(intent2);
							break;

						}
						levelDialog.dismiss();
					}
				});
		levelDialog = builder.create();
		levelDialog.show();
		
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnNewList = (Button) findViewById(R.id.btnNewList);
		btnExit = (Button) findViewById(R.id.btnExit);
		btnShowLists = (Button) findViewById(R.id.btnShowLists);
	
		
		btnNewList.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				/*
				 * Intent intent = new Intent(getApplicationContext(),
				 * Activity_Voice.class); startActivity(intent);
				 */
				insertDialog();
				

			}
		});

		btnExit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				finish();
				System.exit(0);
			}
		});

		btnShowLists.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(),
						Activity_Show.class);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
