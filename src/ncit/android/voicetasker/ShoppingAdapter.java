package ncit.android.voicetasker;

import java.util.HashMap;
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

public class ShoppingAdapter extends BaseAdapter {

	private List<ShoppingItem> shoppingList;
	private Context context;
	private static HashMap<String, Boolean> boolMap;
	
	public ShoppingAdapter(List<ShoppingItem> shoppingList, Context context){
		this.shoppingList = shoppingList;
		this.context = context;
		this.boolMap = new HashMap<String, Boolean>();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.shoppingList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.shoppingList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView  = inflater.inflate(R.layout.shopping_item_layout, parent, false);
		
		TextView itemName = (TextView) convertView.findViewById(R.id.shopping_item_name);
		itemName.setText(this.shoppingList.get(position).getName());
		
		if (this.shoppingList.get(position).isChecked()){
			itemName.setPaintFlags(itemName.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			itemName.setTextColor(Color.rgb(0, 200, 0));
		}
		final int pos = position;
		itemName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View item) {
				listItemClick(item, pos);
			}
		});
		return convertView;
	}
	
	private void listItemClick(View item, int position) {
		if (this.shoppingList.get(position).isChecked()) {
			//uncheck it
			TextView row = (TextView) item;
			row.setPaintFlags(row.getPaintFlags()
					& (~Paint.STRIKE_THRU_TEXT_FLAG));
			row.setTextColor(Color.BLACK);
			this.shoppingList.get(position).setChecked(false);
			this.boolMap.remove(shoppingList.get(position).getName());
		} else {
			//check it
			TextView row = (TextView) item;
			row.setPaintFlags(row.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			row.setTextColor(Color.rgb(0, 200, 0));
			this.shoppingList.get(position).setChecked(true);
			this.boolMap.put(shoppingList.get(position).getName(), true);
		}
	}
	
	protected HashMap<String, Boolean> getMap() {
		return boolMap;
	}

}
