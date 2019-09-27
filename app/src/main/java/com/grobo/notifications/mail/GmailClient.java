package com.grobo.notifications.mail;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class GmailClient {

    private String userName;
    private String password;
    private String sendingHost;
    private int sendingPort;
    private String from;
    private String to;
    private String subject;
    private String text;
    private String receivingHost;
//    private int receivingPort;

    private Context context;

    public GmailClient(Context context) {
        this.context = context;
    }

    public void setAccountDetails(String userName, String password) {

        this.userName = userName;//sender's email can also use as User Name
        this.password = password;

    }

    public void sendGmail(String from, String to, String subject, String text) {

        // This will send mail from -->sender@gmail.com to -->receiver@gmail.com

        this.from = from;
        this.to = to;
        this.subject = subject;
        this.text = text;

        // For a Gmail account--sending mails-- host and port shold be as follows

        this.sendingHost = "smtp.gmail.com";
        this.sendingPort = 465;

        Properties props = new Properties();

        props.put("mail.smtp.host", this.sendingHost);
        props.put("mail.smtp.port", String.valueOf(this.sendingPort));
        props.put("mail.smtp.user", this.userName);
        props.put("mail.smtp.password", this.password);

        props.put("mail.smtp.auth", "true");

        Session session1 = Session.getDefaultInstance(props);

        Message simpleMessage = new MimeMessage(session1);

        //MIME stands for Multipurpose Internet Mail Extensions

        InternetAddress fromAddress = null;
        InternetAddress toAddress = null;

        try {

            fromAddress = new InternetAddress(this.from);
            toAddress = new InternetAddress(this.to);

        } catch (AddressException e) {

            e.printStackTrace();

//            Toast.makeText(context, "Mail Send failed!!!", Toast.LENGTH_SHORT).show();

        }

        try {

            simpleMessage.setFrom(fromAddress);

            simpleMessage.setRecipient(MimeMessage.RecipientType.TO, toAddress);

            // to add CC or BCC use
            // simpleMessage.setRecipient(RecipientType.CC, new InternetAddress("CC_Recipient@any_mail.com"));
            // simpleMessage.setRecipient(RecipientType.BCC, new InternetAddress("CBC_Recipient@any_mail.com"));

            simpleMessage.setSubject(this.subject);

            simpleMessage.setText(this.text);

            //sometimes Transport.send(simpleMessage); is used, but for gmail it's different

            Transport transport = session1.getTransport("smtps");

            transport.connect(this.sendingHost, sendingPort, this.userName, this.password);

            transport.sendMessage(simpleMessage, simpleMessage.getAllRecipients());

            transport.close();

//            Toast.makeText(context, "Mail Sent", Toast.LENGTH_SHORT).show();

        } catch (MessagingException e) {

            e.printStackTrace();

//            Toast.makeText(context, "Mail send failed!!!", Toast.LENGTH_SHORT).show();

        }

    }

    public void readGmail() {

        /*this will print subject of all messages in the inbox of sender@gmail.com*/

        this.receivingHost = "mail.iitp.ac.in";//for imap protocol

        Properties props2 = System.getProperties();

        props2.setProperty("mail.store.protocol", "imaps");
        // I used imaps protocol here

        Session session2 = Session.getDefaultInstance(props2, null);

        try {

            Store store = session2.getStore("imaps");

            store.connect(this.receivingHost, this.userName, this.password);

            Folder folder = store.getFolder("INBOX");//get inbox

            folder.open(Folder.READ_ONLY);//open folder only to read

            Message[] messages = folder.getMessages();

            Log.e("maillen", "message length  " + messages.length);

            for (int i = 0, n = messages.length; i < n; i++) {
                try {
                    Message message = messages[i];
                    Log.e("mail","---------------------------------");
                    Log.e("mail","Email Number " + (i + 1));
                    Log.e("mail","Subject: " + message.getSubject());
                    Log.e("mail","From: " + message.getFrom()[0]);
                    Log.e("mail","Text: " + message.getContent().toString());
                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }
            }
            //close connections

            folder.close(true);

            store.close();

        } catch (Exception e) {

            System.out.println(e.toString());

        }
    }

    public void receiveMail() {

        String host = "pop.gmail.com";// change accordingly
        String mailStoreType = "pop3";

        try {

            //create properties field
            Properties properties = new Properties();

            properties.put("mail.pop3.host", host);
            properties.put("mail.pop3.port", "995");
            properties.put("mail.pop3.starttls.enable", "true");
            Session emailSession = Session.getDefaultInstance(properties);

            //create the POP3 store object and connect with the pop server
            Store store = emailSession.getStore("pop3s");

            store.connect(host, userName, password);

            //create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();

            for (int i = 0, n = messages.length; i < n; i++) {
                try {
                    Message message = messages[i];
                    Log.e("mail","---------------------------------");
                    Log.e("mail","Email Number " + (i + 1));
                    Log.e("mail","Subject: " + message.getSubject());
                    Log.e("mail","From: " + message.getFrom()[0]);
                    Log.e("mail","Text: " + message.getContent().toString());
                } catch (MessagingException | IOException e) {
                    e.printStackTrace();
                }
            }

            //close the store and folder objects
            emailFolder.close(true);
            store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}