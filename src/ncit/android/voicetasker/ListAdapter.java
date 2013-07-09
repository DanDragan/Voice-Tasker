package ncit.android.voicetasker;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ListAdapter extends BaseAdapter implements Observer {

	private List<ListItem> objects;
	private Context context;
	private Observable subject;
	
	public ListAdapter(List<ListItem> objects, Context context) {
		this.objects = objects;
		this.context = context;
		this.subject = null;
	}

	@Override
	public int getCount() {
		return objects.size();
	}

	@Override
	public Object getItem(int position) {
		return objects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		convertView = inflater.inflate(R.layout.listitem, parent, false);
		

		TextView item = (TextView) convertView.findViewById(R.id.item);
		
		item.setText(this.objects.get(position).getItem());
	
		TextView price = (TextView) convertView.findViewById(R.id.price);
		price.setText(this.objects.get(position).getPrice());

		if (this.objects.get(position).isChecked()) {
			item.setPaintFlags(item.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			item.setTextColor(Color.rgb(0, 200, 0));
		}
		final int pos = position;
		item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View item) {
				listItemClick(item, pos);
			}
		});

		return convertView;
	}

	private void listItemClick(View item, int position) {

		if (this.objects.get(position).isChecked()) {
			// uncheck it
			Toast.makeText(getApplicationContext(),
					"You unchecked " + objects.get(position).getItem(),
					Toast.LENGTH_SHORT).show();

			TextView row = (TextView) item;
			row.setPaintFlags(row.getPaintFlags()
					& (~Paint.STRIKE_THRU_TEXT_FLAG));
			row.setTextColor(Color.BLACK);
			this.objects.get(position).setChecked(false);	
			
		} else {
			// check it
			Toast.makeText(getApplicationContext(),
					"You checked " + objects.get(position).getItem(),
					Toast.LENGTH_SHORT).show();

			TextView row = (TextView) item;
			row.setPaintFlags(row.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			row.setTextColor(Color.rgb(0, 200, 0));
			this.objects.get(position).setChecked(true);

			Activity_Shopping.speechWhere = true;
			
			this.notifySubject();
			
		}
	}

	private Context getApplicationContext() {
		return this.context;
	}
	

	public void setSubject(Observable observable){
		if (this.subject == null){
			this.subject = observable;
		}
	}
	@Override
	public void notifySubject() {
		if (this.subject != null){
			this.subject.update();
		}
	}

}
