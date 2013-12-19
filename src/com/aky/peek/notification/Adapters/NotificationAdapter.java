package com.aky.peek.notification.Adapters;

import android.app.PendingIntent;

/** @author Akshay Chordiya
 * {@code} For Notification Getter & Setter */
public class NotificationAdapter {
	
	private String mTitle = "";
	//private String mSummary = "";
	private String mPackage = "";
	private int mIconID = 0;
	private Long mPostTime = 0L;
	private PendingIntent mPendingIntent;
	private String mTag = "";
	private int mStatID = 0;
	
	/** Default Setters **/
	
	protected void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}
	
	protected void setPackage(String mPackage) {
		this.mPackage = mPackage;
	}
	
	protected void setIcon(int mIconID) {
		this.mIconID = mIconID;
	}
	
	protected void setTime(Long mPostTime) {
		this.mPostTime = mPostTime;
	}
	
	protected void setIntent(PendingIntent mPendingIntent) {
		this.mPendingIntent = mPendingIntent;
	}
	
	protected void setNotificationTAG(String mTag) {
		this.mTag = mTag;
	}
	
	protected void setNotificationID(int mStatID) {
		this.mStatID = mStatID;
	}
	
	
	/** Default Getters **/
	protected String getTitle() {
		return mTitle;
	}
	
	protected String getPackage() {
		return mPackage;
	}
	
	protected int getIcon() {
		return mIconID;
	}
	
	protected Long getTime() {
		return mPostTime;
	}
	
	protected PendingIntent getIntent() {
		return mPendingIntent;
	}
	
	protected String getNotificationTAG() {
		return mTag;
	}
	
	protected int getNotificationID() {
		return mStatID;
	}
	

}
