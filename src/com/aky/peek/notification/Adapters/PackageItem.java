package com.aky.peek.notification.Adapters;

import android.graphics.drawable.Drawable;


/** @author Akshay Chordiya
 * @category Class
 * {@code} For Getter & Setter */
public class PackageItem {
	
	private String title;
	private String packageName;
	private String versionName;
	//private int versionCode;
	//private String description;
	Drawable icon;
	boolean checkStatus;
	
	/*
	public PackageItem(String title, String packageName, String versionName, Drawable icon, boolean checkStatus) {
        this.title = title;
        this.packageName = packageName;
        this.versionName = versionName;
        this.icon = icon;
        this.checkStatus = checkStatus;
}

	
	/**Default getters & setters**/
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getPackageName() {
		return packageName;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
	    this.versionName = versionName;
	}

	/*
	public int getVersionCode() {
	    return versionCode;
	    }
	
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
		}
		*/
	/*
	public String getDescription() {
		return description;
	   }
	
	public void setDescription(String description) {
		this.description = description;
		} */
	
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	
	public Drawable getIcon() {
		return icon;
	}
	
	public void setCheck(boolean status) {
		this.checkStatus = status;
	}
	
	public boolean getCheck() {
	    return checkStatus;
	}
	
}

