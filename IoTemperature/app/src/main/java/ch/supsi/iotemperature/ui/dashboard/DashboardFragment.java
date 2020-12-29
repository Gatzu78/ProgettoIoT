package ch.supsi.iotemperature.ui.dashboard;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import ch.supsi.iotemperature.R;
import ch.supsi.iotemperature.SUPSIGattAttributes;

public class DashboardFragment extends Fragment {
    private DashboardViewModel dashboardViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        final TextView connState = root.findViewById(R.id.connection_state);
        dashboardViewModel.getConnectionState().observe(getViewLifecycleOwner(), connState::setText);

        final TextView deviceName = root.findViewById(R.id.device_name);
        final TextView deviceAddress = root.findViewById(R.id.device_address);
        dashboardViewModel.getDevice().observe(getViewLifecycleOwner(), device -> {
            deviceAddress.setText(device.getAddress());
            deviceName.setText(device.getName());
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = this.getArguments();
        BluetoothDevice device = bundle.getParcelable(SUPSIGattAttributes.BUNDLE_KEY);
        dashboardViewModel.connect(getContext(), device);
    }
}