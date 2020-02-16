package com.grobo.notifications.survey;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.grobo.notifications.R;
import com.grobo.notifications.network.RetrofitClientInstance;
import com.grobo.notifications.network.SurveyRoutes;
import com.grobo.notifications.survey.models.Question;
import com.grobo.notifications.survey.models.Survey;
import com.grobo.notifications.utils.utils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.grobo.notifications.utils.Constants.USER_TOKEN;

public class SurveyDetailsActivity extends FragmentActivity {

    private ProgressDialog progressDialog;
    private Survey currentSurvey;
    private List<Question> allQuestions;
    private ProgressBar questionProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey_details);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        questionProgressBar = findViewById(R.id.progress_load_question);

        if (getIntent() != null && getIntent().hasExtra("survey"))
            currentSurvey = getIntent().getParcelableExtra("survey");

        if (currentSurvey != null) {
            showSurveyData();
            loadSurveyQuestions(currentSurvey.getId());
        } else utils.showSimpleAlertDialog(this, "Alert!!!", "Error in Survey!");
    }

    private void showSurveyData() {
        TextView surveyTitle = findViewById(R.id.survey_title);
        TextView surveyDescription = findViewById(R.id.survey_description);
        surveyTitle.setText(currentSurvey.getTitle());
        surveyDescription.setText(currentSurvey.getDescription());

    }

    private void loadSurveyQuestions(String surveyId) {
        questionProgressBar.setVisibility(View.VISIBLE);

        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_TOKEN, "0");

        SurveyRoutes service = RetrofitClientInstance.getRetrofitInstance().create(SurveyRoutes.class);

        Call<List<Question>> call = service.getSurveyQuestions(token, surveyId);
        call.enqueue(new Callback<List<Question>>() {
            @Override
            public void onResponse(@NonNull Call<List<Question>> call, @NonNull Response<List<Question>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    allQuestions = response.body();
                    showSurveyQuestions();
                } else {
                    Toast.makeText(SurveyDetailsActivity.this, "Load failed, error: " + response.code(), Toast.LENGTH_LONG).show();
                }
                questionProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<List<Question>> call, @NonNull Throwable t) {
                questionProgressBar.setVisibility(View.GONE);
                if (t.getMessage() != null) Log.e("failure", t.getMessage());
                Toast.makeText(SurveyDetailsActivity.this, "Update failed!! Please check internet connection!", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void showSurveyQuestions() {
        ViewGroup insertPoint = findViewById(R.id.survey_insert_point);
        int viewIndex = 0;

        for (Question question : allQuestions) {
            switch (question.getQuestionType()) {
                case 1:
                    insertPoint.addView(getParagraphType(question), viewIndex++, new ViewGroup.LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    break;
                case 2:
                    insertPoint.addView(getChoiceType(question), viewIndex++, new ViewGroup.LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    break;
                case 3:
                    insertPoint.addView(getCheckboxType(question), viewIndex++, new ViewGroup.LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    break;
                case 4:
                    insertPoint.addView(getDateType(question), viewIndex++, new ViewGroup.LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    break;
                case 5:
                    insertPoint.addView(getTimeType(question), viewIndex++, new ViewGroup.LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    break;
            }
        }

        MaterialButton button = findViewById(R.id.button_submit);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(v -> {
            validateInputData();
        });
    }

    private void validateInputData() {

        Map<String, Object> jsonParams = new HashMap<>();
        List<Map<String, Object>> answers = new ArrayList<>();

        boolean valid = true;
        ViewGroup insertPoint = findViewById(R.id.survey_insert_point);

        for (int i = 0; i < allQuestions.size(); i++) {

            Question question = allQuestions.get(i);

            switch (question.getQuestionType()) {
                case 1:
                    View v1 = insertPoint.getChildAt(i);
                    EditText editText1 = v1.findViewById(R.id.answer_paragraph);

                    String answerText1 = editText1.getText().toString();

                    if (question.getRequired() && answerText1.isEmpty()) {
                        editText1.setError("Enter a valid answer!");
                        MaterialCardView cv1 = v1.findViewById(R.id.card_root);
                        cv1.setStrokeColor(Color.RED);
                        valid = false;
                    }

                    if (!answerText1.isEmpty()) {
                        Map<String, Object> answer1 = new HashMap<>();
                        answer1.put("question_id", question.getId());
                        answer1.put("answer_text", answerText1);
                        answers.add(answer1);
                    }

                    break;

                case 2:
                    View v2 = insertPoint.getChildAt(i);
                    ViewGroup insertion2 = v2.findViewById(R.id.ll_options);
                    RadioGroup radioGroup2 = (RadioGroup) insertion2.getChildAt(0);

                    int id = radioGroup2.getCheckedRadioButtonId();
                    RadioButton radioButton2 = insertion2.findViewById(id);
                    if (radioButton2 != null) {
                        String answerText2 = radioButton2.getText().toString();
                        Map<String, Object> answer2 = new HashMap<>();
                        answer2.put("question_id", question.getId());
                        answer2.put("answer_text", answerText2);
                        answers.add(answer2);
                    } else {
                        if (question.getRequired()) {
                            MaterialCardView cv2 = v2.findViewById(R.id.card_root);
                            cv2.setStrokeColor(Color.RED);
                            valid = false;
                        }
                    }

                    break;

                case 3:
                    View v3 = insertPoint.getChildAt(i);
                    ViewGroup insertion3 = v3.findViewById(R.id.ll_options);

                    StringBuilder answerText3 = new StringBuilder();

                    for (int j = 0; j < insertion3.getChildCount(); j++) {
                        CheckBox checkBox = (CheckBox) insertion3.getChildAt(j);
                        if (checkBox != null && checkBox.isChecked()) {
                            if (answerText3.length() > 0) answerText3.append(",");
                            answerText3.append(j);
                        }
                    }

                    if (question.getRequired() && answerText3.length() == 0) {
                        MaterialCardView cv3 = v3.findViewById(R.id.card_root);
                        cv3.setStrokeColor(Color.RED);
                        valid = false;
                    }

                    if (answerText3.length() > 0) {
                        Map<String, Object> answer3 = new HashMap<>();
                        answer3.put("question_id", question.getId());
                        answer3.put("answer_text", answerText3.toString());
                        answers.add(answer3);
                    }

                    break;

                case 4:
                    View v4 = insertPoint.getChildAt(i);
                    TextView textView4 = v4.findViewById(R.id.answer_date);

                    String answerText4 = textView4.getText().toString();

                    if (question.getRequired() && answerText4.isEmpty()) {
                        MaterialCardView cv4 = v4.findViewById(R.id.card_root);
                        cv4.setStrokeColor(Color.RED);
                        valid = false;
                    }

                    if (!answerText4.isEmpty()) {
                        Map<String, Object> answer4 = new HashMap<>();
                        answer4.put("question_id", question.getId());
                        answer4.put("answer_text", answerText4);
                        answers.add(answer4);
                    }

                    break;

                case 5:
                    View v5 = insertPoint.getChildAt(i);
                    TextView textView5 = v5.findViewById(R.id.answer_time);

                    String answerText5 = textView5.getText().toString();

                    if (question.getRequired() && answerText5.isEmpty()) {
                        MaterialCardView cv5 = v5.findViewById(R.id.card_root);
                        cv5.setStrokeColor(Color.RED);
                        valid = false;
                    }

                    if (!answerText5.isEmpty()) {
                        Map<String, Object> answer5 = new HashMap<>();
                        answer5.put("question_id", question.getId());
                        answer5.put("answer_text", answerText5);
                        answers.add(answer5);
                    }

                    break;
            }
        }

        if (!valid) {
            Toast.makeText(this, "Please check for errors!", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("answer", answers.toString());
            jsonParams.put("response_details", answers);
            showPostDialog(jsonParams);
        }
    }

    private void showPostDialog(Map<String, Object> jsonParams) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation Dialog")
                .setMessage("Submitting answer... Please confirm!!")
                .setPositiveButton("Confirm", (dialog, which) -> postResponse(jsonParams))
                .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss())
                .show();
    }

    private void postResponse(Map<String, Object> jsonParams) {

        progressDialog.setMessage("Processing...");
        progressDialog.show();

        RequestBody body = RequestBody.create((new JSONObject(jsonParams)).toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
        String token = PreferenceManager.getDefaultSharedPreferences(this).getString(USER_TOKEN, "0");

        SurveyRoutes service = RetrofitClientInstance.getRetrofitInstance().create(SurveyRoutes.class);
        Call<ResponseBody> call = service.fillSurvey(token, currentSurvey.getId(), body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (progressDialog != null) progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(SurveyDetailsActivity.this, "Your response has been recorded.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    try {
                        if (response.errorBody() != null)
                            Log.e("failure", String.valueOf(response.code()) + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    utils.showSimpleAlertDialog(SurveyDetailsActivity.this, "Alert!!!", "Save failed! Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                if (t.getMessage() != null) Log.e("failure", t.getMessage());
                if (progressDialog != null) progressDialog.dismiss();
                Toast.makeText(SurveyDetailsActivity.this, "Save failed, please check internet connection", Toast.LENGTH_LONG).show();
            }
        });


    }


    private View getParagraphType(Question question) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (vi != null) {
            View v = vi.inflate(R.layout.survey_question_paragraph, new LinearLayout(this));
            TextView q = v.findViewById(R.id.question);
            q.setText(question.getText());
            if (question.getRequired()) q.append(" *");
            return v;
        }

        return null;
    }

    private View getChoiceType(Question question) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (vi != null) {
            View v = vi.inflate(R.layout.survey_question_choice, new LinearLayout(this));

            TextView q = v.findViewById(R.id.question);
            q.setText(question.getText());
            if (question.getRequired()) q.append(" *");

            RadioGroup radioGroup = new RadioGroup(this);
            for (String option : question.getOptions()) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(option);
                radioGroup.addView(radioButton, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

            ViewGroup insertion = v.findViewById(R.id.ll_options);
            insertion.addView(radioGroup, 0, new ViewGroup.LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            return v;
        }

        return null;
    }

    private View getCheckboxType(Question question) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (vi != null) {
            View v = vi.inflate(R.layout.survey_question_choice, new LinearLayout(this));

            TextView q = v.findViewById(R.id.question);
            q.setText(question.getText());
            if (question.getRequired()) q.append(" *");

            ViewGroup insertion = v.findViewById(R.id.ll_options);

            for (String option : question.getOptions()) {
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(option);
                insertion.addView(checkBox, new ViewGroup.LayoutParams(MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }


            return v;
        }

        return null;
    }

    private View getDateType(Question question) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (vi != null) {
            View v = vi.inflate(R.layout.survey_question_date, new LinearLayout(this));
            TextView q = v.findViewById(R.id.question);
            q.setText(question.getText());
            if (question.getRequired()) q.append(" *");

            ImageView dateSelect = v.findViewById(R.id.answer_date_select);
            dateSelect.setOnClickListener(v1 -> {
                setDate(v.findViewById(R.id.answer_date));
            });

            return v;
        }

        return null;
    }

    private void setDate(TextView textView) {
        Calendar c = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dateValue = dayOfMonth + "/" + month + "/" + year;
            textView.setText(dateValue);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
        datePickerDialog.show();
    }

    private View getTimeType(Question question) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (vi != null) {
            View v = vi.inflate(R.layout.survey_question_time, new LinearLayout(this));
            TextView q = v.findViewById(R.id.question);
            q.setText(question.getText());
            if (question.getRequired()) q.append(" *");

            ImageView timeSelect = v.findViewById(R.id.answer_time_select);
            timeSelect.setOnClickListener(v1 -> {
                setTime(v.findViewById(R.id.answer_time));
            });

            return v;
        }

        return null;
    }

    private void setTime(TextView textView) {
        Calendar c = Calendar.getInstance();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String timeValue = hourOfDay + ":" + minute;
            textView.setText(timeValue);
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), false);

        timePickerDialog.show();
    }


}
