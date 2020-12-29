package ch.supsi.iotemperature.ui.dashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.supsi.iotemperature.R;

public class BLEDataAdapter extends ArrayAdapter<String> {

    public BLEDataAdapter(Context context, List<String> itemList) {
        super(context, 0, itemList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        String item = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_data, parent, false);
        }

        TextView deviceName = convertView.findViewById(R.id.itemValue);
        deviceName.setText(item);

        return convertView;
    }
}