package com.kendohamster;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PracticeTimeFragment extends Fragment {
    public PracticeTimeFragment() {
        // Required empty public constructor
    }

    EditText editTextInputPracticeTime;
    Button btnCancel, btnConfirm;
    String motionName;
    int practiceTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_practice_time, container, false);

        Bundle bundle = getArguments();
        motionName = bundle.getString("motionName");

        editTextInputPracticeTime = view.findViewById(R.id.editTextInputPracticeTime);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnConfirm = view.findViewById(R.id.btnConfirm);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MotionVideo motionVideo =(MotionVideo)getActivity();
                motionVideo.closeFragment(R.id.framePracticeTime);
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(editTextInputPracticeTime.getText().toString().length() == 0){
                    Toast.makeText(getActivity().getApplicationContext()
                            , "Please enter positive integer!",Toast.LENGTH_SHORT).show();
                }
                else{
                    Double input = Double.valueOf(editTextInputPracticeTime.getText().toString());
                    practiceTime = (int) Math.round(input);

                    if(practiceTime >0){
                        MotionVideo motionVideo = (MotionVideo) getActivity();
                        motionVideo.startPracticing(motionName, practiceTime);

                    }
                    else{
                        editTextInputPracticeTime.setText("");
                        Toast.makeText(getActivity().getApplicationContext()
                                , "Please enter positive integer!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }
}