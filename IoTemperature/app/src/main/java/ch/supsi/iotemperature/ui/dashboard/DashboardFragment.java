package ch.supsi.iotemperature.ui.dashboard;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ch.supsi.iotemperature.BluetoothLeService;
import ch.supsi.iotemperature.MainActivity;
import ch.supsi.iotemperature.R;
import ch.supsi.iotemperature.SUPSIGattAttributes;

public class DashboardFragment extends Fragment {
    private final static String TAG = DashboardFragment.class.getSimpleName();

    private DashboardViewModel dashboardViewModel;
    private LogDataAdapter logDataAdapter;
    private String mDeviceAddress;
    private String mDeviceName;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainActivity mainActivity = (MainActivity) getActivity();
        ActionBar actionBar = mainActivity.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        Toolbar toolbar = mainActivity.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.getMenu().findItem(R.id.action_sampling).setOnMenuItemClickListener(i -> {
            showSamplingDialog(mainActivity);
            return true;
        });
        toolbar.getMenu().findItem(R.id.action_led).setOnMenuItemClickListener(i -> {
            mainActivity.toggleLED1();
            return true;
        });
        toolbar.getMenu().findItem(R.id.action_refresh).setOnMenuItemClickListener(i -> {
            mainActivity.asyncReadTemperature();
            return true;
        });

        toolbar.setNavigationOnClickListener(v -> {
            mainActivity.disconnect();
            // back button pressed
            NavHostFragment.findNavController(DashboardFragment.this)
                    .navigate(R.id.action_DashboardFragment_to_HomeFragment);
        });
    }

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

        // LED BUTTON
        final SwitchCompat ledSwitch = root.findViewById(R.id.ledSwitch);
        ledSwitch.setOnClickListener(view -> mainActivity.toggleLED1());

        dashboardViewModel.isLED1On().observe(getViewLifecycleOwner(), ledSwitch::setChecked);

        final Button btnConnect = root.findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(view -> {
            mainActivity.connect(mDeviceAddress);
        });

        // CONNECTION STATE
        dashboardViewModel.getConnStatus().observe(getViewLifecycleOwner(), status -> {
            btnConnect.setText(status == BluetoothLeService.STATE_CONNECTED ? "CONNECTED" :
                    status == BluetoothLeService.STATE_CONNECTING ? "CONNECTING" : "CONNECT");
            ledSwitch.setEnabled(status == BluetoothLeService.STATE_CONNECTED);
            btnConnect.setEnabled(status == BluetoothLeService.STATE_DISCONNECTED);
        });

        // LOG
        final ListView dataListView = root.findViewById(R.id.listViewBLEData);
        dashboardViewModel.getLog().observe(getViewLifecycleOwner(), itemList -> {
            if(itemList != null) {
                logDataAdapter = new LogDataAdapter(this.getContext(), itemList);
                dataListView.setAdapter(logDataAdapter);
            }
            logDataAdapter.notifyDataSetChanged();
        });

        // CHART
        // Sostituito con LineChart
        //final BarChart chart = root.findViewById(R.id.chart);
        final LineChart chart = root.findViewById(R.id.chart);
        setChartSettings(chart);
        dashboardViewModel.getTemperatures().observe(getViewLifecycleOwner(), temperatures -> {
            //BarData data = transformData(temperatures);
            LineData data = transformData(temperatures);
            chart.setData(data);
            chart.invalidate();
        });

        return root;
    }
    //Sostituita da LineChart
    /*
    private void setChartSettings(BarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        // Set Y Axis boundaries
        chart.getAxisLeft().setAxisMaximum(30);
        chart.getAxisLeft().setAxisMinimum(0);
        chart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            private final DecimalFormat mFormat= new DecimalFormat("0.0");
            @Override
            public String getFormattedValue(float value) {
                return mFormat.format(value);
            }
        });
        // Hide Right Axis
        chart.getAxisRight().setEnabled(false);
        // Hide X Axis
        chart.getXAxis().setEnabled(false);
    }
    */
    private void setChartSettings(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        // Set Y Axis boundaries
        chart.getAxisLeft().setAxisMaximum(26);
        chart.getAxisLeft().setAxisMinimum(14);
        chart.getAxisLeft().setValueFormatter(new ValueFormatter() {
            private final DecimalFormat mFormat= new DecimalFormat("0.0");
            @Override
            public String getFormattedValue(float value) {
                return mFormat.format(value);
            }
        });
        // Hide Right Axis
        chart.getAxisRight().setEnabled(false);
        // Hide X Axis
        chart.getXAxis().setEnabled(false);
    }
    //Sostituita da transformLineData
    /*
    private BarData transformData(List<Integer> itemList) {
        ArrayList<BarEntry> data = new ArrayList<>();
        for (int i=0; i<itemList.size(); i++)
            data.add(new BarEntry(i, itemList.get(i)));

        BarDataSet dataSet = new BarDataSet(data, "Temperature");
        dataSet.setColor(Color.BLUE);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        return new BarData(dataSets);
    }*/

    private LineData transformData(List<Float> itemList){
        ArrayList<Entry> data = new ArrayList<>();
        for (int i=0; i<itemList.size(); i++)
            data.add(new Entry(i, itemList.get(i)));

        LineDataSet dataSet = new LineDataSet(data, "Temperature");
        dataSet.setColor(Color.BLUE);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        return new LineData(dataSets);
    }

    private void showSamplingDialog(MainActivity mainActivity) {
        mainActivity.asyncReadSampling();
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.sampling_dialog);

        final NumberPicker np = dialog.findViewById(R.id.numSampling);
        dashboardViewModel.getSamplingValue().observe(getViewLifecycleOwner(), value -> {
            if(value != null)
                np.setValue(value);
        });

        np.setMaxValue(5000);
        np.setMinValue(100);
        np.setWrapSelectorWheel(false);

        Button btnConfirm =  dialog.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(view -> {
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
        dashboardViewModel.unregisterReceiver(getContext());
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        Log.i(TAG, "**** onResume");
        super.onResume();

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.connect(mDeviceAddress);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(TAG, "**** onSaveInstanceState");

        outState.putString(SUPSIGattAttributes.KEY_DEVICE_ADDRESS, mDeviceAddress);
        outState.putString(SUPSIGattAttributes.KEY_DEVICE_NAME, mDeviceName);

        super.onSaveInstanceState(outState);
    }
}