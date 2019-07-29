package com.grobo.notifications.account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;

import java.util.Map;

public class SignUpFragment extends Fragment {

    private static final int SELECT_PICTURE = 2233;
    private OnSignUpInteractionListener callback;
    private ImageView profileImage;

    public SignUpFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);


        Bundle args = getArguments();
        final Map<String, Object> jsonParams = new ArrayMap<>();

        if (args != null) {
            if (args.containsKey("email")) {
                jsonParams.put("email", args.getString("email"));
                Log.e("email", args.getString("email"));
            }
            if (args.containsKey("password")) {
                jsonParams.put("password", args.getString("password"));
                Log.e("passw", args.getString("password"));
            }
        }

        final EditText name = view.findViewById(R.id.signup_input_name);
        final EditText roll = view.findViewById(R.id.signup_input_roll);
        final EditText phone = view.findViewById(R.id.signup_input_phone);

//        CardView signUpImageCard = view.findViewById(R.id.signup_image_cv);
//        signUpImageCard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//                startActivityForResult(intent, SELECT_PICTURE);
//            }
//        });

        Button finish = view.findViewById(R.id.signup_finish_button);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("")) {
                    name.setError("Enter a valid name");
                } else if (roll.getText().toString().equals("")) {
                    roll.setError("Enter a valid roll");
                } else if (phone.getText().toString().equals("") || phone.getText().toString().length() != 10) {
                    roll.setError("Enter a valid phone number");
                } else {
                    jsonParams.put("name", name.getText().toString());
                    jsonParams.put("instituteId", roll.getText().toString());
                    jsonParams.put("phone", phone.getText().toString());
                    callback.onFinishSelected(jsonParams);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
//                if (data != null) {
//                    try {
////                        newProfileImage = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
////                        profileImage.setImageBitmap(newProfileImage);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
            } else if (resultCode == Activity.RESULT_CANCELED)  {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSignUpInteractionListener) {
            callback = (OnSignUpInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callback = null;
    }

    public interface OnSignUpInteractionListener {
        void onFinishSelected(Map<String, Object> jsonParams);
    }
}
