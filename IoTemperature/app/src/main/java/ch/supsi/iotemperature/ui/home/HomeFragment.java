package ch.supsi.iotemperature.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import ch.supsi.iotemperature.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private DeviceListAdapter deviceListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));

        final ListView deviceListView = root.findViewById(R.id.listViewPairedDevice);
        homeViewModel.getIoTDevices().observe(getViewLifecycleOwner(), devices -> {
            if(devices != null) {
                deviceListAdapter = new DeviceListAdapter(this.getContext(), devices);
                deviceListView.setAdapter(deviceListAdapter);
            }
            deviceListAdapter.notifyDataSetChanged();
        });

        final Button refreshButton = root.findViewById(R.id.btnRefresh);
        refreshButton.setOnClickListener(v -> homeViewModel.refreshPairedDevices());

        return root;
    }
}