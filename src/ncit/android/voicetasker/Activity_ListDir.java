package ncit.android.voicetasker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Activity_ListDir extends Activity {

	private Button btnSimple;
	private Button btnShopping;
	private static String pressed;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu_lists);

		btnSimple = (Button) findViewById(R.id.btnSimple);
		btnShopping = (Button) findViewById(R.id.btnShopping);
		
		btnSimple.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				
				pressed="simple lists";
				Intent intent = new Intent(getApplicationContext(), Activity_Show.class); 
				startActivity(intent);

			}
		});
		
		
		btnShopping.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				
				pressed = "shopping lists";
				Intent intent = new Intent(getApplicationContext(), Activity_Show.class); 
				startActivity(intent);

			}
		});
		
	}
	
	protected static String getSubDirName(){
		
		return pressed;
		
	}
	
}
