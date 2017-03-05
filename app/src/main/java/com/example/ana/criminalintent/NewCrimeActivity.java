package com.example.ana.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by ana on 2/22/2017.
 */

public class NewCrimeActivity extends AppCompatActivity implements DatePickerFragment.DatePickerInterface, TimePickerFragment.TimePickerInterface
{
    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mCancelButton;
    private Button mCreateButton;
    private Button mReportButton;
    private Button mSuspectButton;

    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_CONTACT = 1;
    private static final int REQUEST_PHOTO= 2;


    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, NewCrimeActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_new_crime);
//        mPhotoFile = CrimeLab.get(this).getPhotoFile(mCrime);

        mCrime = new Crime();

        mTitleField = (EditText) findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mDateButton = (Button) findViewById(R.id.crime_date);
        updateDate();

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                DatePickerFragment datePickerFragment = DatePickerFragment
                        .newInstance(mCrime.getDate());
                datePickerFragment.datePickerInterface = NewCrimeActivity.this;
                datePickerFragment.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) findViewById(R.id.crime_time);
        updateTime();

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getSupportFragmentManager();
                TimePickerFragment timePickerFragment = TimePickerFragment
                        .newInstance(mCrime.getTime());
                timePickerFragment.timePickerInterface = NewCrimeActivity.this;
                timePickerFragment.show(manager, DIALOG_TIME);

            }
        });

        mSolvedCheckBox = (CheckBox) findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mCrime.setSolved(isChecked);
            }
        });

        mCancelButton = (Button) findViewById(R.id.cancel);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mCreateButton = (Button) findViewById(R.id.create_crime);

        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCrime.getTitle()==null) {
                Toast.makeText(NewCrimeActivity.this, R.string.toast_write_title, Toast.LENGTH_SHORT).show();}

                else {
                    CrimeLab.get(NewCrimeActivity.this).addCrime(mCrime);
                    finish();
                }
            }
        });

        mReportButton = (Button) findViewById(R.id.send_report);

        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);

        mSuspectButton = (Button) findViewById(R.id.choose_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager packageManager = this.getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) findViewById(R.id.crime_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);
        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });

        mPhotoView = (ImageView) findViewById(R.id.crime_photo);
    }


    private void updateDate() {
        mDateButton.setText(mCrime.getCrimeDate());
    }

    private void updateTime() {
        mTimeButton.setText(mCrime.getCrimeTime());
    }

    @Override
    public void didSelectDate(Date date) {
        mCrime.setDate(date);
        updateDate();
    }

    @Override
    public void didSelectTime(Date timeOfDay) {
        mCrime.setTime(timeOfDay);
        updateTime();
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);
        return report;
    }
}


