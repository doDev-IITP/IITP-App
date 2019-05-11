package com.grobo.notifications.explore.services.maintenance;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.grobo.notifications.R;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class NewMaintenanceFragment extends Fragment implements Spinner.OnItemSelectedListener{


    public NewMaintenanceFragment() {}

    private static final int SELECT_PICTURE = 1;

    private String category;
    private String bodyText;
    private Bitmap bitmap = null;
    private Button imageSelector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_maintenance, container, false);

        final Spinner categorySpinner = view.findViewById(R.id.category_spinner);
        List<String> spinnerItems = Arrays.asList(getResources().getStringArray(R.array.maintenance_list_spinner));
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, spinnerItems);
        categorySpinner.setAdapter(spinnerAdapter);
        categorySpinner.setOnItemSelectedListener(this);

        final EditText body = view.findViewById(R.id.problem_body);
        imageSelector = view.findViewById(R.id.image_selector_button);
        Button submitButton = view.findViewById(R.id.submit_button);

        imageSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bodyText = body.getText().toString();
            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category = position != 0 ? parent.getSelectedItem().toString(): null;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                        imageSelector.setText(data.getData().getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED)  {
                Toast.makeText(getActivity(), "Canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
