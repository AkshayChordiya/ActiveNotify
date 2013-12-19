package com.aky.peek.notification.Adapters;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.ImageView;

import com.aky.peek.notification.R;

/** @author Akshay Chordiya
 * @category Class
 * {@code} Adapter for creating customized list view */
public class PackageAdapter extends BaseAdapter{
	
	Context context;
	private List<PackageItem> mApps;
	
	private Filter fRecords;
	
	/** Constructor.
	 * @param context the application context for the layout inflater 
	 * @param data */
	public PackageAdapter(Context context, List<PackageItem> data) {
		this.context = context;
		this.mApps = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		// Declaring View Holder
		AppViewHolder holder;
		
		if(convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// Inflating View
			convertView = mInflater.inflate(R.layout.activity_app_select, null);
			//final CheckedTextView view = (CheckedTextView)getView(position, convertView, parent);
	        //view.setChecked(position == 2);
	         // Creates a ViewHolder and stores a reference to the children view we want to bind data to
	         holder = new AppViewHolder();
	         //holder.mTitle = (TextView) convertView.findViewById(R.id.apptitle);
	         //holder.mVersion = (TextView) convertView.findViewById(R.id.app_version);
	         holder.mIcon = (ImageView) convertView.findViewById(R.id.appicon);
	         holder.mCheck = (CheckedTextView)convertView.findViewById(R.id.app_check_text);
	         //holder.mCheck = (CheckBox) convertView.findViewById(R.id.app_check);
	         convertView.setTag(holder);
	      } else { 
	         // reuse/overwrite the view passed assuming(!) that it is castable!
	         holder = (AppViewHolder) convertView.getTag();
	      }
		// Creating object for App Class
		PackageItem app = mApps.get(position);
		holder.mCheck.setText(app.getTitle());
		holder.mIcon.setImageDrawable(app.getIcon());
		holder.mCheck.setChecked(app.getCheck());
		return convertView; 
	}
	  
    /* A view holder which is used to re/use views inside a list. */
    public class AppViewHolder {
    	
    	private ImageView mIcon;
    	private CheckedTextView mCheck;
    }
	
	@Override
	public int getCount() {
		return mApps.size();
		}
	
	@Override
	public Object getItem(int position) {
		return mApps.get(position);
		}
	
	@Override
	public long getItemId(int position) {
		return position;
		}
	
	public Filter getFilter() {
		if (fRecords == null) 
			fRecords = new RecordFilter();
		return fRecords;
	}
	
	/**
	 * Sets the list of apps to be displayed. 
     * @param list the list of apps to be displayed

	public void setListItems(List<PackageItem> list) {
	    mApps = list;
	  }
	 * @return 
	  */
	
	@SuppressLint("DefaultLocale")
	private class RecordFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            
            //Need Filter
            List<PackageItem> fRecords = new ArrayList<PackageItem>();		

            //Implement filter logic
            if (constraint == null || constraint.length() == 0) {
                //No need for filter
                results.values = mApps;
                results.count = mApps.size();
                mApps.addAll(fRecords);
            } else {

                for (PackageItem s : mApps) {
                    if (s.getTitle().toUpperCase().contains(constraint.toString().toUpperCase())) {
                        fRecords.add(s);
                    }
                }
                notifyDataSetChanged();
                results.values = fRecords;
                results.count = fRecords.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
		@Override
        protected void publishResults(CharSequence constraint,
                FilterResults results) {
            //inform the adapter about the new list
            if (results.count == 0) {
                notifyDataSetInvalidated();
            } else {
                mApps = (List<PackageItem>) results.values;
                notifyDataSetChanged();
            }
        }
    }
	
}
