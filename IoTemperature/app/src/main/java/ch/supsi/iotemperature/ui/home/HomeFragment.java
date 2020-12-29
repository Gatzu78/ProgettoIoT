package ch.supsi.iotemperature.ui.home;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import ch.supsi.iotemperature.R;
import ch.supsi.iotemperature.SUPSIGattAttributes;

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

            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            Bundle bundle = new Bundle();
            bundle.putParcelable(SUPSIGattAttributes.BUNDLE_KEY, device);
            navController.navigate(R.id.navigation_dashboard, bundle);

//            if (device == null) return;
//            final Intent intent = new Intent(getContext(), DeviceControlActivity.class);
//            intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
//            intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
//            if (homeViewModel.getIsScanning().getValue()) {
//                homeViewModel.stopScan();
//            }
//            startActivity(intent);
        });

        final Button scanButton = root.findViewById(R.id.btnScan);
        homeViewModel.getIsScanning()
                .observe(getViewLifecycleOwner(), isScanning -> {
                    String text = isScanning ? "STOP" : "SCAN";
                    scanButton.setText(text);
                });
        scanButton.setOnClickListener(view -> homeViewModel.refreshDevices(view.getContext()));

        return root;
    }
}