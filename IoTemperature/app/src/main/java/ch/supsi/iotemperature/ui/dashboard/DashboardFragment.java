package ch.supsi.iotemperature.ui.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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

        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // DEVICE NAME
        final TextView deviceName = root.findViewById(R.id.device_name);
        dashboardViewModel.getDeviceName().observe(getViewLifecycleOwner(), name -> {
            deviceName.setText(name);
        });

        // DEVICE ADDRESS
        final TextView deviceAddress = root.findViewById(R.id.device_address);
        dashboardViewModel.getDeviceAddress().observe(getViewLifecycleOwner(), addr -> {
            deviceAddress.setText(addr);
        });

        // REFRESH DATA BUTTON
        final Button btnRefreshData = root.findViewById(R.id.btnRefreshData);
        btnRefreshData.setOnClickListener(view -> {
            MainActivity main = (MainActivity) getActivity();
            main.asyncReadTime();
            main.asyncReadTemperature();
            main.asyncReadSampling();
        });

        final EditText numSampling = root.findViewById(R.id.numSampling);
        dashboardViewModel.getSamplingValue().observe(getViewLifecycleOwner(), value -> {
            numSampling.setText(String.valueOf(value));
        });

        // LED BUTTON
        final Button btnLED = root.findViewById(R.id.btnLED);
        btnLED.setOnClickListener(view -> {
            MainActivity main = (MainActivity) getActivity();
            main.toggleLED();
        });

        dashboardViewModel.isLED1On().observe(getViewLifecycleOwner(), isOn -> {
            btnLED.setText(isOn ? "TURN OFF LED" : "TURN ON LED");
        });

        // CONNECTION STATE
        final TextView connState = root.findViewById(R.id.connection_state);
        dashboardViewModel.isConnected().observe(getViewLifecycleOwner(), isConnected -> {
            connState.setText(isConnected ? "CONNECTED" : "DISCONNECTED");
            btnLED.setEnabled(isConnected);
            btnRefreshData.setEnabled(isConnected);
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
        Log.i(TAG, "**** onActivityCreated");

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