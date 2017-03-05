package com.example.ana.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by ana on 1/31/2017.
 */

public class TimePickerFragment extends DialogFragment {

    private static final String ARG_TIME = "time";
    private TimePicker mTimePicker;
    public static final String EXTRA_TIME =
            "com.example.android.criminalintent.time";

    public TimePickerInterface timePickerInterface;

    public interface TimePickerInterface {
        void didSelectTime(Date timeOfDay);
    }

    public static TimePickerFragment newInstance(Date timeOfDay) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, timeOfDay);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Date timeOfDay = (Date) getArguments().getSerializable(ARG_TIME);


        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time, null);

        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_time_picker);
        mTimePicker.setHour(hour);
        mTimePicker.setMinute(min);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int hour = mTimePicker.getHour();
                                int min = mTimePicker.getMinute();
                                Date timeOfDay = new GregorianCalendar(0, 0, 0, hour, min).getTime();
                                sendResult(Activity.RESULT_OK, timeOfDay);
                            }
                        })
                .create();
    }

    private void sendResult(int resultCode, Date timeOfDay) {
        if (timePickerInterface != null) {
            timePickerInterface.didSelectTime(timeOfDay);
        }
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, timeOfDay);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
