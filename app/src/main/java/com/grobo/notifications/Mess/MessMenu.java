package com.grobo.notifications.Mess;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.grobo.notifications.R;
import com.grobo.notifications.utils.ZoomImage;
import com.grobo.notifications.utils.utils;

import java.util.concurrent.ExecutionException;

import static com.grobo.notifications.utils.Constants.MESS_MENU_URL;

public class MessMenu extends AppCompatActivity {

    private FirebaseRemoteConfig remoteConfig;
    private ZoomImage messMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_mess_menu );
        messMenu=findViewById( R.id.messMenu );
        remoteConfig = FirebaseRemoteConfig.getInstance();
        String url=remoteConfig.getString( MESS_MENU_URL );
        utils.ImageDownloader task = new utils.ImageDownloader();
        try {
            Bitmap bitmap = task.execute( remoteConfig.getString( MESS_MENU_URL ) ).get();
            messMenu.setImageBitmap( bitmap );
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
