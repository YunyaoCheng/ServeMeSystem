package com.example.servemesystem;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceDetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int mYear, mMonth, mDay, mHour, mMinute;
    DatabaseAccess db;

    public ServiceDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceDetailFragment newInstance(String param1, String param2) {
        ServiceDetailFragment fragment = new ServiceDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_detail, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseAccess.getInstance(getActivity());
        final String categoryName = getArguments().getString("category_name");
        final EditText categoryText = view.findViewById(R.id.detail_category_name);
        final EditText dateText = view.findViewById(R.id.detail_date_input);
        final EditText timeText = view.findViewById(R.id.detail_time_input);
        Button datePickerButton = view.findViewById(R.id.detail_date_picker);
        Button timePickerButton = view.findViewById(R.id.detail_time_picker);
        final EditText locationText = view.findViewById(R.id.detail_location_input);
        final EditText titleText = view.findViewById(R.id.detail_title_input);
        final EditText descriptionText = view.findViewById(R.id.detail_description_input);
        Button cancelButton = view.findViewById(R.id.detail_cancel_button);
        Button submitButton = view.findViewById(R.id.detail_submit_button);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        dateText.setText(dtf.format(localDate));

        final Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        timeText.setText(sdf.format(cal.getTime()));

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                LocalDate localDate = LocalDate.now();
                                String date = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                Toast toast = Toast.makeText(getContext(), "Date is invalid", Toast.LENGTH_LONG);
                                if (year > localDate.getYear()) {
                                    dateText.setText(date);
                                } else if (year == localDate.getYear()) {
                                    if ((monthOfYear + 1) > localDate.getMonthValue()) {
                                        dateText.setText(date);
                                    } else if ((monthOfYear + 1) == localDate.getMonthValue()) {
                                        if (dayOfMonth >= localDate.getDayOfMonth()) {
                                            dateText.setText(date);
                                        } else {
                                            toast.show();
                                        }
                                    } else {
                                        toast.show();
                                    }
                                } else {
                                    toast.show();
                                }

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();

            }
        });
        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                mHour = calendar.get(Calendar.HOUR_OF_DAY);
                mMinute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String am_pm;
                                if (hourOfDay >= 12) {
                                    am_pm = "PM";
                                    hourOfDay -= 12;
                                } else {
                                    am_pm = "AM";
                                }
                                timeText.setText(hourOfDay + ":" + minute + " " + am_pm);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });
        categoryText.setText(categoryName);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationText.getText().toString().isEmpty()) {
                    locationText.setError("Location is required!");
                }
                if (titleText.getText().toString().isEmpty()) {
                    titleText.setError("Title is required!");
                }
                if (descriptionText.getText().toString().isEmpty()) {
                    descriptionText.setError("Description is required!");
                }
                ServiceRequest serviceRequest = new ServiceRequest();
                serviceRequest.setServiceId(db.getNewServiceId());
                serviceRequest.setCustomerId(1);
                serviceRequest.setVendorId(2);
                serviceRequest.setCategory(categoryText.getText().toString());
                serviceRequest.setServiceTime(dateText.getText().toString() + " " + timeText.getText().toString());
                serviceRequest.setLocation(locationText.getText().toString());
                serviceRequest.setTitle(titleText.getText().toString());
                serviceRequest.setDescription(descriptionText.getText().toString());
                serviceRequest.setStatus("Pending");
                serviceRequest.setReviewed(false);
                if (db.insertServiceRequest(serviceRequest)) {
                    Toast.makeText(getContext(), "Successfully submit the service request", Toast.LENGTH_LONG).show();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    int count = fragmentManager.getBackStackEntryCount();
                    for (int i = 0; i < count; ++i) {
                        fragmentManager.popBackStack();
                    }
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container_customer_home, new CustomerManageServiceRequests())
                            .commit();
                } else {
                    Toast.makeText(getContext(), "False", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
