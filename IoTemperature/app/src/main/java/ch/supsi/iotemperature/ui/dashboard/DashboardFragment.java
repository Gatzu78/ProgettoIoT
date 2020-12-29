package ch.supsi.iotemperature.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ch.supsi.iotemperature.R;
import ch.supsi.iotemperature.SUPSIGattAttributes;

public class DashboardFragment extends Fragment {
    private DashboardViewModel dashboardViewModel;
    private BLEDataAdapter bleDataAdapter;
    private String mDeviceAddress;
    private String mDeviceName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // CONNECTION STATE
        final TextView connState = root.findViewById(R.id.connection_state);
        dashboardViewModel.getConnectionState().observe(getViewLifecycleOwner(), connState::setText);

        // DEVICE NAME AND ADDRESS
        final TextView deviceName = root.findViewById(R.id.device_name);
        final TextView deviceAddress = root.findViewById(R.id.device_address);
        dashboardViewModel.getDeviceName().observe(getViewLifecycleOwner(), name -> {
            deviceName.setText(name);
        });

        dashboardViewModel.getDeviceAddress().observe(getViewLifecycleOwner(), addr -> {
            deviceAddress.setText(addr);
        });

        // DEVICE DATA
        final ListView dataListView = root.findViewById(R.id.listViewBLEData);
        dashboardViewModel.getData().observe(getViewLifecycleOwner(), itemList -> {
            if(itemList != null) {
                bleDataAdapter = new BLEDataAdapter(this.getContext(), itemList);
                dataListView.setAdapter(bleDataAdapter);
            }
            bleDataAdapter.notifyDataSetChanged();
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            // new device
            mDeviceAddress = bundle.getString(SUPSIGattAttributes.KEY_DEVICE_ADDRESS);
            mDeviceName = bundle.getString(SUPSIGattAttributes.KEY_DEVICE_NAME);
        }

        dashboardViewModel.connect(getContext(), mDeviceAddress, mDeviceName);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            mDeviceAddress = savedInstanceState.getString(SUPSIGattAttributes.KEY_DEVICE_ADDRESS);
            mDeviceName = savedInstanceState.getString(SUPSIGattAttributes.KEY_DEVICE_NAME);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(SUPSIGattAttributes.KEY_DEVICE_ADDRESS, mDeviceAddress);
        outState.putString(SUPSIGattAttributes.KEY_DEVICE_NAME, mDeviceName);

        super.onSaveInstanceState(outState);
    }
}