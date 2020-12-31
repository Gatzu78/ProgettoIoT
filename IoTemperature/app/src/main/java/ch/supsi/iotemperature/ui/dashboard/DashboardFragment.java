package ch.supsi.iotemperature.ui.dashboard;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import ch.supsi.iotemperature.BluetoothLeService;
import ch.supsi.iotemperature.MainActivity;
import ch.supsi.iotemperature.R;
import ch.supsi.iotemperature.SUPSIGattAttributes;

public class DashboardFragment extends Fragment {
    private final static String TAG = DashboardFragment.class.getSimpleName();

    private DashboardViewModel dashboardViewModel;
    private BLEDataAdapter bleDataAdapter;
    private String mDeviceAddress;
    private String mDeviceName;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "**** onCreateView");

        MainActivity mainActivity = (MainActivity) getActivity();
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        dashboardViewModel.registerReceiver(getContext());
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // DEVICE NAME
        final TextView deviceName = root.findViewById(R.id.device_name);
        dashboardViewModel.getDeviceName().observe(getViewLifecycleOwner(),
                deviceName::setText);

        // DEVICE ADDRESS
        final TextView deviceAddress = root.findViewById(R.id.device_address);
        dashboardViewModel.getDeviceAddress().observe(getViewLifecycleOwner(),
                deviceAddress::setText);

        // REFRESH DATA BUTTON
        final Button btnRefreshData = root.findViewById(R.id.btnRefreshData);
        btnRefreshData.setOnClickListener(view -> {
            mainActivity.asyncReadTemperature();
        });

        // LED BUTTON
        final Switch ledSwitch = root.findViewById(R.id.ledSwitch);
        ledSwitch.setOnClickListener(view -> Objects.requireNonNull(mainActivity).toggleLED1());

        dashboardViewModel.isLED1On().observe(getViewLifecycleOwner(), ledSwitch::setChecked);

        // SAMPLING
        final ImageButton btnSampling = root.findViewById(R.id.btnSampling);
        btnSampling.setOnClickListener(view -> {
            showSamplingDialog();
        });

        final Button btnConnect = root.findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(view -> {
            mainActivity.connect(mDeviceAddress);
        });

        // CONNECTION STATE
        dashboardViewModel.getConnStatus().observe(getViewLifecycleOwner(), status -> {
            btnConnect.setText(status == BluetoothLeService.STATE_CONNECTED ? "CONNECTED" :
                    status == BluetoothLeService.STATE_CONNECTING ? "CONNECTING" : "CONNECT");
            ledSwitch.setEnabled(status == BluetoothLeService.STATE_CONNECTED);
            btnSampling.setEnabled(status == BluetoothLeService.STATE_CONNECTED);
            btnRefreshData.setEnabled(status == BluetoothLeService.STATE_CONNECTED);
            btnConnect.setEnabled(status == BluetoothLeService.STATE_DISCONNECTED);
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

    private void showSamplingDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.sampling_dialog);

        final NumberPicker np = dialog.findViewById(R.id.numSampling);
        Integer value = dashboardViewModel.getSamplingValue().getValue();
        if(value != null)
            np.setValue(value);
        np.setMaxValue(1000);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);

        Button btnConfirm =  dialog.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(view -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.writeSampling(np.getValue());
            dashboardViewModel.setSampling(np.getValue());
            dialog.dismiss();
        });

        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(view -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "**** onActivityCreated");
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            // new device
            mDeviceAddress = bundle.getString(SUPSIGattAttributes.KEY_DEVICE_ADDRESS);
            mDeviceName = bundle.getString(SUPSIGattAttributes.KEY_DEVICE_NAME);
        }

        dashboardViewModel.setDeviceAddress(mDeviceAddress);
        dashboardViewModel.setDeviceName(mDeviceName);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i(TAG, "**** onCreate");
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            // new device
            mDeviceAddress = bundle.getString(SUPSIGattAttributes.KEY_DEVICE_ADDRESS);
            mDeviceName = bundle.getString(SUPSIGattAttributes.KEY_DEVICE_NAME);
        }

        if(savedInstanceState != null) {
            mDeviceAddress = savedInstanceState.getString(SUPSIGattAttributes.KEY_DEVICE_ADDRESS);
            mDeviceName = savedInstanceState.getString(SUPSIGattAttributes.KEY_DEVICE_NAME);
        }
    }

    @Override
    public void onDestroyView() {
        Log.i(TAG, "**** onDestroyView");
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "**** onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "**** onStop");
        super.onStop();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "**** onResume");
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(TAG, "**** onSaveInstanceState");

        outState.putString(SUPSIGattAttributes.KEY_DEVICE_ADDRESS, mDeviceAddress);
        outState.putString(SUPSIGattAttributes.KEY_DEVICE_NAME, mDeviceName);

        super.onSaveInstanceState(outState);
    }
}