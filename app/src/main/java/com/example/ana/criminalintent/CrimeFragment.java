package com.example.ana.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;



/**
 * Created by ana on 1/23/2017.
 */

public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mDeleteButton;
    private Button mUpdateButton;
    private Button mReportButton;
    private Button mSuspectButton;

    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private ImageView mExpandedPhotoView;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_PHOTO="DialogPhoto";
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_PHOTO = 3;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
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

        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment date_dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                date_dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                date_dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        updateTime();

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment time_dialog = TimePickerFragment
                        .newInstance(mCrime.getTime());
                time_dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                time_dialog.show(manager, DIALOG_TIME);
            }
        });

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                mCrime.setSolved(isChecked);
            }
        });

        mDeleteButton = (Button) v.findViewById(R.id.delete_crime);

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
                CrimeLab.get(getActivity()).deleteCrime(crimeId);
                deleteInstance();
            }
        });

        mUpdateButton = (Button) v.findViewById(R.id.update_crime);

        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteInstance();
            }
        });

        mReportButton = (Button) v.findViewById(R.id.send_report);

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

        mSuspectButton = (Button) v.findViewById(R.id.choose_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        final PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }


        final boolean canTakePhoto = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        mPhotoButton.setEnabled(canTakePhoto);
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri image_uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider",mPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
                if (takePictureIntent.resolveActivity(packageManager) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_PHOTO);
                }
            }
        });

        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        mPhotoView.setClickable(false);


        mExpandedPhotoView = (ImageView) v.findViewById(R.id.expanded_image);
        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = BitmapFactory.decodeFile(mPhotoFile.getPath());
                FragmentManager manager = getFragmentManager();
                ZoomFragment photo_dialog = ZoomFragment
                        .newInstance(bitmap);
                photo_dialog.show(manager, DIALOG_PHOTO);
//                mExpandedPhotoView.setImageBitmap(bitmap);
            }
        });

        ViewTreeObserver observer = mPhotoView.getViewTreeObserver();

        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                updatePhotoView();
            }
        });
//        updatePhotoView();

        return v;
    }


    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void deleteInstance() {
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_DATE:
                Date date = (Date) data
                        .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
                mCrime.setDate(date);
                updateDate();
                break;
            case REQUEST_TIME:
                Date timeOfDay = (Date) data
                        .getSerializableExtra(TimePickerFragment.EXTRA_TIME);
                mCrime.setTime(timeOfDay);
                updateTime();
                break;
            case REQUEST_CONTACT:
                if (data != null) {
                    Uri contactUri = data.getData();
                    // Specify which fields you want your query to return values for.
                    String[] queryFields = new String[]{
                            ContactsContract.Contacts.DISPLAY_NAME
                    };
                    // Perform your query - the contactUri is like a "where" clause here

                    Cursor c = getActivity().getContentResolver()
                            .query(contactUri, queryFields, null, null, null);
                    try {
                        // Double-check that you actually got results
                        if (c.getCount() == 0) {
                            return;
                        }
                        // Pull out the first column of the first row of data - that is your suspect's name.
                        c.moveToFirst();
                        String suspect = c.getString(0);
                        mCrime.setSuspect(suspect);
                        mSuspectButton.setText(suspect);
                    } finally {
                        c.close();
                    }
                }
                break;

            case REQUEST_PHOTO:

                //for api<24
//                Bundle extras = data.getExtras();
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                mPhotoView.setImageBitmap(imageBitmap);

                //for api>24
                updatePhotoView();
                break;

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_crime, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Intent intent = NewCrimeActivity
                        .newIntent(getActivity());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void updateDate() {
        mDateButton.setText(mCrime.getCrimeDate());
    }

    private void updateTime() {
        mTimeButton.setText(mCrime.getCrimeTime());
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

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
           Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
            mPhotoView.setClickable(true);

        }
    }

    private void expandPhotoView() {

    }

}
