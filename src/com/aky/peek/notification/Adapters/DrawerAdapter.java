package com.aky.peek.notification.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aky.peek.notification.R;

public class DrawerAdapter extends ArrayAdapter<DrawerModel> {
	
	public DrawerAdapter(Context context) {
		super(context, 0);
	}

	public void addHeader(int title) { //expects The Title for the header as an Arugment to it
		add(new DrawerModel(title, -1, true));//add the object to the Bottom of the array
	}

	public void addItem(int title, int icon) {
		add(new DrawerModel(title, icon, false));
	}

	public void addItem(DrawerModel itemModel) {
		add(itemModel);
	}

	@Override
	public int getViewTypeCount() {  //Returns the number of types of Views that will be created by getView(int, View, ViewGroup).
		return 2; //we will create 2 types of views
	}

	@Override
	public int getItemViewType(int position) { //framework calls getItemViewType for row n, the row it is about to display.
		//Get the type of View that will be created by getView(int, View, ViewGroup) for the specified item.
		return getItem(position).isHeader ? 0 : 1; // get position passes (n) and accertain  is its a header  or not
	}

	@Override
	public boolean isEnabled(int position) {
		return !getItem(position).isHeader;
	}

	public static class ViewHolder {
		
		public final TextView textHolder;
		public final ImageView imageHolder;
		
		public ViewHolder(TextView text1, ImageView image1) {
			this.textHolder = text1;
			this.imageHolder = image1;
		}
		
		public ViewHolder(TextView text1) {
			this.textHolder = text1;
			imageHolder = null;
		}
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		//Abstract View --> Get a View that displays the data at the specified position in the data set.
		DrawerModel item = getItem(position);
		ViewHolder holder = null;
		View view = convertView;
		int layout;
		if(view == null) {
			layout = R.layout.drawer_list_item;
			if (item.isHeader)
				layout = R.layout.drawer_header;
			
			view = LayoutInflater.from(getContext()).inflate(layout, null);
			if(item.isHeader){
				TextView text1 = (TextView) view.findViewById(R.id.action_headers);
				view.setTag(new ViewHolder(text1));
			}
			else{
				TextView text1 = (TextView) view.findViewById(R.id.action_text);
				ImageView image1 = (ImageView) view.findViewById(R.id.action_icon);
				view.setTag(new ViewHolder(text1, image1));
				}
			}
		if (holder == null && view != null) {
			Object tag = view.getTag();
			if (tag instanceof ViewHolder)
				holder = (ViewHolder) tag;
			}
		if (item != null && holder != null) {
			if (holder.textHolder != null)
				holder.textHolder.setText(item.title);
			if (holder.imageHolder != null) {
				if (item.iconRes > 0) {
					holder.imageHolder.setVisibility(View.VISIBLE);
					holder.imageHolder.setImageResource(item.iconRes);
					}
				else
					holder.imageHolder.setVisibility(View.GONE);
				}
			}
		return view;
		}
	
}
