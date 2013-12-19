package com.aky.peek.notification.Adapters;

public class DrawerModel {
	
	public int title;
	public int iconRes;
	public boolean isHeader;

	public DrawerModel(int title, int iconRes, boolean header) {
		this.title = title;
		this.iconRes = iconRes;
		this.isHeader = header;
    	}

	public DrawerModel(int title, int iconRes) {
		this(title, iconRes, false);
    	}
	
}