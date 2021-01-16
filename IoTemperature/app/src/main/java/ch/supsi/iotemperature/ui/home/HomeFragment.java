package ch.supsi.iotemperature.ui.home;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.ArrayList;
import ch.supsi.iotemperature.MainActivity;
import ch.supsi.iotemperature.R;
import ch.supsi.iotemperature.SUPSIGattAttributes;

import static ch.supsi.iotemperature.ui.home.HomeViewModel.SCAN_PERIOD;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private DeviceListAdapter deviceListAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mainActivity.getSupportActionBar().setDisplayShowHomeEnabled(false);
        mainActivity.invalidateOptionsMenu();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final ProgressBar progressBar = root.findViewById(R.id.progBar);

        // TITLE
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // DEVICE LIST
        final ListView deviceListView = root.findViewById(R.id.listViewPairedDevice);
        homeViewModel.getIoTDevices().observe(getViewLifecycleOwner(), devices -> {
            if(devices != null) {
                deviceListAdapter = new DeviceListAdapter(this.getContext(),
                        new ArrayList<>(devices));
                deviceListView.setAdapter(deviceListAdapter);
            }
            deviceListAdapter.notifyDataSetChanged();
        });

        // DEVICE SELECTED
        deviceListView.setOnItemClickListener((parent, view, position, id) -> {
            BluetoothDevice device = (BluetoothDevice)deviceListView.getItemAtPosition(position);

            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            Bundle bundle = new Bundle();
            bundle.putString(SUPSIGattAttributes.KEY_DEVICE_ADDRESS, device.getAddress());
            bundle.putString(SUPSIGattAttributes.KEY_DEVICE_NAME, device.getName());
            navController.navigate(R.id.navigation_dashboard, bundle);
        });

        // SCAN BUTTON
        final Button scanButton = root.findViewById(R.id.btnScan);
        homeViewModel.getIsScanning()
                .observe(getViewLifecycleOwner(), isScanning -> {
                    progressBar.setVisibility(isScanning ? View.VISIBLE : View.INVISIBLE);
                    String text = isScanning ? "STOP" : "SCAN";
                    scanButton.setText(text);
                });
        scanButton.setOnClickListener(view -> homeViewModel.scanBleDevice());

        homeViewModel.scanBleDevice();
        return root;
    }
}