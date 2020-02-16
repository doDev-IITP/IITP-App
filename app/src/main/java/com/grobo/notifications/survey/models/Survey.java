package com.grobo.notifications.survey.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.grobo.notifications.feed.DataPoster;

@Keep
public class Survey implements Parcelable {

    @SerializedName("anonymous")
    @Expose
    private Boolean anonymous;
    @SerializedName("limit_responses")
    @Expose
    private Boolean limitResponses;
    @SerializedName("accepting_responses")
    @Expose
    private Boolean acceptingResponses;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("poster")
    @Expose
    private DataPoster poster;

    public Boolean getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }

    public Boolean getLimitResponses() {
        return limitResponses;
    }

    public void setLimitResponses(Boolean limitResponses) {
        this.limitResponses = limitResponses;
    }

    public Boolean getAcceptingResponses() {
        return acceptingResponses;
    }

    public void setAcceptingResponses(Boolean acceptingResponses) {
        this.acceptingResponses = acceptingResponses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DataPoster getPoster() {
        return poster;
    }

    public void setPoster(DataPoster poster) {
        this.poster = poster;
    }

    private Survey(Parcel in) {
        byte anonymousVal = in.readByte();
        anonymous = anonymousVal == 0x02 ? null : anonymousVal != 0x00;
        byte limitResponsesVal = in.readByte();
        limitResponses = limitResponsesVal == 0x02 ? null : limitResponsesVal != 0x00;
        byte acceptingResponsesVal = in.readByte();
        acceptingResponses = acceptingResponsesVal == 0x02 ? null : acceptingResponsesVal != 0x00;
        id = in.readString();
        title = in.readString();
        description = in.readString();
        poster = (DataPoster) in.readValue(DataPoster.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (anonymous == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (anonymous ? 0x01 : 0x00));
        }
        if (limitResponses == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (limitResponses ? 0x01 : 0x00));
        }
        if (acceptingResponses == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (acceptingResponses ? 0x01 : 0x00));
        }
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeValue(poster);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Survey> CREATOR = new Parcelable.Creator<Survey>() {
        @Override
        public Survey createFromParcel(Parcel in) {
            return new Survey(in);
        }

        @Override
        public Survey[] newArray(int size) {
            return new Survey[size];
        }
    };
}