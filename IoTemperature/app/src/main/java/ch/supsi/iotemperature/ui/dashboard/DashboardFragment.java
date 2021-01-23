package ch.supsi.iotemperature.ui.dashboard;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import ch.supsi.iotemperature.BluetoothLeService;
import ch.supsi.iotemperature.MainActivity;
import ch.supsi.iotemperature.R;
import ch.supsi.iotemperature.SUPSIGattAttributes;

public class DashboardFragment extends Fragment {
    private final static String TAG = DashboardFragment.class.getSimpleName();

    private static final int MIN_TEMPERATURE = 14;
    private static final int MAX_TEMPERATURE = 26;
    private final static int SAMPLING_MAX_VALUE = 2000;
    private final static int SAMPLING_MIN_VALUE = 100;
    private final static int SAMPLING_STEP = 10;

    private Dialog mDialog;
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

        initToolbar(mainActivity);
        initDialog(mainActivity);
    }

    private void initToolbar(MainActivity mainActivity) {
        Toolbar toolbar = mainActivity.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.getMenu().findItem(R.id.action_sampling).setOnMenuItemClickListener(i -> {
            mDialog.show();
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
        toolbar.getMenu().findItem(R.id.disconnect).setOnMenuItemClickListener(i -> {
            mainActivity.disconnect();
            return true;
        });

        toolbar.setNavigationOnClickListener(v -> {
            mainActivity.disconnect();
            // back button pressed
            NavHostFragment.findNavController(DashboardFragment.this)
                    .navigate(R.id.action_DashboardFragment_to_HomeFragment);
        });
    }

    private void initDialog(MainActivity mainActivity) {
        mDialog = new Dialog(getContext());
        mDialog.setContentView(R.layout.sampling_dialog);

        final NumberPicker np = mDialog.findViewById(R.id.numSampling);
        dashboardViewModel.getSamplingValue().observe(getViewLifecycleOwner(), value -> {
            if(value != null) {
                int index = (value - SAMPLING_MIN_VALUE) / SAMPLING_STEP + 1;
                np.setValue(index);
            }
        });
        String[] values = getArrayWithSteps(SAMPLING_MIN_VALUE, SAMPLING_MAX_VALUE, SAMPLING_STEP);
        np.setDisplayedValues(values);
        np.setMinValue(1);
        np.setMaxValue(values.length);

        Button btnConfirm =  mDialog.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(view -> {
            int value = SAMPLING_MIN_VALUE + ((np.getValue()-1) * SAMPLING_STEP);
            mainActivity.writeSampling(value);
            dashboardViewModel.setSampling(value);
            mDialog.dismiss();
        });

        Button btnCancel = mDialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(view -> mDialog.dismiss());
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
        btnConnect.setOnClickListener(view -> mainActivity.connect(mDeviceAddress));

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
        final LineChart chart = root.findViewById(R.id.chart);
        setChartSettings(chart);
        dashboardViewModel.getTemperatures()
                .observe(getViewLifecycleOwner(), temperatures -> updateChart(chart, temperatures));

        return root;
    }

    private void setChartSettings(LineChart chart) {
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);

        // Y Axis (temperature)
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(MIN_TEMPERATURE);
        yAxis.setAxisMaximum(MAX_TEMPERATURE);
        yAxis.setValueFormatter(new ValueFormatter() {
            private final DecimalFormat mFormat= new DecimalFormat("0");
            @Override
            public String getFormattedValue(float value) {
                return mFormat.format(value);
            }
        });

        // X Axis (time)
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ss.SS");

            @Override
            public String getFormattedValue(float value) {
                LocalTime time = LocalTime.ofNanoOfDay((long)value);
                return formatter.format(time);
            }
        });

        // Hide Right Axis
        chart.getAxisRight().setEnabled(false);
    }

    private static int mLineColor = Color.rgb(0x1a, 0x50, 0x8b);
    private static int mCircleColor = Color.rgb(0x0d, 0x33, 0x5d);
    private void updateChart(LineChart chart, List<Pair<LocalTime, Float>> temperatures) {
        ArrayList<Entry> data = new ArrayList<>();
        for (Pair<LocalTime, Float> entry : temperatures) {
            long x = entry.first.toNanoOfDay();
            data.add(new Entry(x, entry.second));
        }

        LineDataSet dataSet = new LineDataSet(data, "Temperature");
        dataSet.setColor(mLineColor);
        dataSet.setCircleHoleColor(mLineColor);
        dataSet.setCircleColor(mCircleColor);
        dataSet.setCircleRadius(1.3f);
        dataSet.setDrawValues(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);
        chart.invalidate();
    }

    public String[] getArrayWithSteps (int iMinValue, int iMaxValue, int iStep)
    {
        int arraySize = (iMaxValue - iMinValue) / iStep + 1;
        String[] arrayValues = new String[arraySize];
        for(int i = 0; i < arraySize; i++)
            arrayValues[i] = String.valueOf(iMinValue + (i * iStep));
        return arrayValues;
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
            // new device selected
            mDeviceAddress = bundle.getString(SUPSIGattAttributes.KEY_DEVICE_ADDRESS);
            mDeviceName = bundle.getString(SUPSIGattAttributes.KEY_DEVICE_NAME);
        }

        if(savedInstanceState != null) {
            // latest device saved
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

        // try reconnect to the latest address
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.connect(mDeviceAddress);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.i(TAG, "**** onSaveInstanceState");

        // store the last device name and address to try reconnect later
        outState.putString(SUPSIGattAttributes.KEY_DEVICE_ADDRESS, mDeviceAddress);
        outState.putString(SUPSIGattAttributes.KEY_DEVICE_NAME, mDeviceName);

        super.onSaveInstanceState(outState);
    }
}