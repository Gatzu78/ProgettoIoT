package ch.supsi.iotemperature.ui.home;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import ch.supsi.iotemperature.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private DeviceListAdapter deviceListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        final ListView deviceListView = root.findViewById(R.id.listViewPairedDevice);
        homeViewModel.getIoTDevices().observe(getViewLifecycleOwner(), devices -> {
            if(devices != null) {
                deviceListAdapter = new DeviceListAdapter(this.getContext(), devices);
                deviceListView.setAdapter(deviceListAdapter);
            }
            deviceListAdapter.notifyDataSetChanged();
        });

        deviceListView.setOnItemClickListener((parent, view, position, id) -> {
            BluetoothDevice device = (BluetoothDevice)deviceListView.getItemAtPosition(position);
            Snackbar.make(view, "Start pairing:" + device.getName(), Snackbar.LENGTH_LONG)
                    .setAction("", action -> homeViewModel.pair(view.getContext(), device)
                    ).show();
        });

        final Button refreshButton = root.findViewById(R.id.btnRefresh);
        homeViewModel.getRefreshEnabled()
                .observe(getViewLifecycleOwner(), refreshButton::setEnabled);
        refreshButton.setOnClickListener(view -> homeViewModel.refreshDevices(view.getContext()));

        return root;
    }
}