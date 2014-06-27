package com.laptopfreek0.taskkill.objects;

import java.util.List;
import java.util.Map;

import com.laptopfreek0.taskkill.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CustomSpinnerAdapter extends SimpleAdapter{

    LayoutInflater mInflater;
    private List<? extends Map<String, ?>> dataRecieved;
    Context context1;

    public CustomSpinnerAdapter(Context context, List<? extends Map<String, ?>> data,
            int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        context1 = context;
        dataRecieved =data;
        mInflater=LayoutInflater.from(context);
    }

    @SuppressWarnings("unchecked")
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.spinner_view,
                    null);
        }
    //  HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);
        ((TextView) convertView.findViewById(R.id.textviewSpinner))
                .setText((String) dataRecieved.get(position).get("Name"));
        ((ImageView) convertView.findViewById(R.id.imageviewSpinner)).setImageDrawable((Drawable) dataRecieved.get(position).get("Icon"));
        return convertView;
    }
    
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent){
      if (convertView == null)
      {
        LayoutInflater vi = (LayoutInflater) context1.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //convertView = vi.inflate(android.R.layout.simple_spinner_dropdown_item, null);
        convertView = vi.inflate(R.layout.spinner_dropdown_view, null);
      }

      ((TextView) convertView.findViewById(R.id.textviewSpinner))
      .setText((String) dataRecieved.get(position).get("Name"));
      ((ImageView) convertView.findViewById(R.id.imageviewSpinner)).setImageDrawable((Drawable) dataRecieved.get(position).get("Icon"));

      return convertView;
    }
    
    public int getPosition(String item) {
      for(int i = 0; i < dataRecieved.size(); i++)
        if(dataRecieved.get(i).get("Package").equals(item))
          return i;
      return 0;
    }

}
