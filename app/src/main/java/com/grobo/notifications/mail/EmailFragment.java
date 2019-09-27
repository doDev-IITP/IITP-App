package com.grobo.notifications.mail;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.grobo.notifications.R;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

public class EmailFragment extends Fragment {

    private EmailRecyclerAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SharedPreferences preferences;

    public EmailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        RecyclerView recyclerView = view.findViewById(R.id.rv_email);
        swipeRefreshLayout = view.findViewById(R.id.sr_email);
        swipeRefreshLayout.setOnRefreshListener(this::updateData);

        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new EmailRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setRefreshing(true);
        updateData();

        super.onViewCreated(view, savedInstanceState);
    }

    private void updateData() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                readMail();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }.execute();
    }

    private void readMail() {

        String receivingHost = "mail.iitp.ac.in";
        String userName = "1801ee03@iitp.ac.in";
        String password = "Iamironman!1055";

        Properties props2 = System.getProperties();
        props2.setProperty("mail.store.protocol", "imaps");
        Session session2 = Session.getDefaultInstance(props2, null);

        try {
            Store store = session2.getStore("imaps");
            store.connect(receivingHost, userName, password);

            Folder folder = store.getFolder("INBOX");
            folder.open(Folder.READ_ONLY);

            int count = folder.getMessageCount();
            Message[] messages = folder.getMessages(count - 50, count);
            Log.e("maillen", "message length  " + messages.length);

            for (int i = messages.length - 1; i >= 0; i--) {
                try {
                    Message message = messages[i];
                    EmailItem email = new EmailItem();
                    email.setId(message.getMessageNumber());
                    email.setContent(message.getSubject());
                    email.setDetails(message.getContent().toString());

                    if (getActivity() != null)
                        getActivity().runOnUiThread(() -> {
                            adapter.addEmail(email);
                        });
                    else break;

                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }
            }

            folder.close(true);
            store.close();

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}


//                try {
//                    Message message = messages[i];
//                    Log.e("mail", "---------------------------------");
//                    Log.e("mail", "Email Number " + (i + 1));
//                    Log.e("mail", "Subject: " + message.getSubject());
//                    Log.e("mail", "From: " + message.getFrom()[0]);
//                    Log.e("mail", "Text: " + message.getContent().toString());
//                } catch (MessagingException | IOException e) {
//                    e.printStackTrace();
//                }