package com.grobo.notifications.feed;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;
import androidx.preference.PreferenceManager;
import androidx.room.Ignore;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.grobo.notifications.utils.Constants.ROLL_NUMBER;
import static com.grobo.notifications.utils.Constants.USER_MONGO_ID;
import static com.grobo.notifications.utils.Constants.USER_NAME;

@Keep
public class DataPoster implements Parcelable {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("instituteId")
    @Expose
    private String instituteId;
    @SerializedName("image")
    @Expose
    private String image;

    @Ignore
    public DataPoster() {}

    public DataPoster(String id, String name, String instituteId) {
        this.id = id;
        this.name = name;
        this.instituteId = instituteId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstituteId() {
        return instituteId;
    }

    public void setInstituteId(String instituteId) {
        this.instituteId = instituteId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    protected DataPoster(Parcel in) {
        id = in.readString();
        name = in.readString();
        instituteId = in.readString();
        image = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(instituteId);
        dest.writeString(image);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DataPoster> CREATOR = new Parcelable.Creator<DataPoster>() {
        @Override
        public DataPoster createFromParcel(Parcel in) {
            return new DataPoster(in);
        }

        @Override
        public DataPoster[] newArray(int size) {
            return new DataPoster[size];
        }
    };

    public static DataPoster getMeAsPoster(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return new DataPoster(preferences.getString(USER_MONGO_ID, ""), preferences.getString(USER_NAME, ""), preferences.getString(ROLL_NUMBER, ""));
    }

}