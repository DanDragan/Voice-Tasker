package ncit.android.voicetasker;

public class ListItem {

	private String item;
	private String price;
	private boolean checked;

	public ListItem(String item, String price, boolean checked) {
		this.item = item;
		this.price = price;
		this.checked = checked;
	}

	public String getItem() {
		return item;
	}

	public String getPrice() {
		return price;
	}
	
	public void setItem(String item) {
		this.item = item;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}
}
