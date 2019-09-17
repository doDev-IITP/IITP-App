package com.grobo.notifications.Mess;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

@Keep
public class MessModel {

    @SerializedName("mess")
    @Expose
    private Mess mess;

    public Mess getMess() {
        return mess;
    }

    public void setMess(Mess mess) {
        this.mess = mess;
    }

    @Keep
    public class Mess implements Serializable {
        @SerializedName("fullday")
        @Expose
        private List<Long> fullday = null;
        @SerializedName("breakfast")
        @Expose
        private List<Long> breakfast = null;
        @SerializedName("lunch")
        @Expose
        private List<Long> lunch = null;
        @SerializedName("snacks")
        @Expose
        private List<Long> snacks = null;
        @SerializedName("dinner")
        @Expose
        private List<Long> dinner = null;
        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("student")
        @Expose
        private Student student;
        @SerializedName("messChoice")
        @Expose
        private Integer messChoice;

        public List<Long> getBreakfast() {
            return breakfast;
        }

        public List<Long> getDinner() {
            return dinner;
        }

        public Integer getMessChoice() {
            return messChoice;
        }

        public List<Long> getFullday() {
            return fullday;
        }

        public List<Long> getLunch() {
            return lunch;
        }

        public List<Long> getSnacks() {
            return snacks;
        }

        @NonNull
        public String getId() {
            return id;
        }

        public Student getStudent() {
            return student;
        }

        public void setBreakfast(List<Long> breakfast) {
            this.breakfast = breakfast;
        }

        public void setDinner(List<Long> dinner) {
            this.dinner = dinner;
        }

        public void setFullday(List<Long> fullday) {
            this.fullday = fullday;
        }

        public void setId(@NonNull String id) {
            this.id = id;
        }

        public void setLunch(List<Long> lunch) {
            this.lunch = lunch;
        }

        public void setMessChoice(Integer messChoice) {
            this.messChoice = messChoice;
        }

        public void setSnacks(List<Long> snacks) {
            this.snacks = snacks;
        }

        public void setStudent(Student student) {
            this.student = student;
        }

        @Keep
        public class Student {

            @SerializedName("_id")
            @Expose
            private String id;
            @SerializedName("name")
            @Expose
            private String name;
            @SerializedName("instituteId")
            @Expose
            private String instituteId;

            public void setId(@NonNull String id) {
                this.id = id;
            }

            @NonNull
            public String getId() {
                return id;
            }

            public String getInstituteId() {
                return instituteId;
            }

            public String getName() {
                return name;
            }

            public void setInstituteId(String instituteId) {
                this.instituteId = instituteId;
            }

            public void setName(String name) {
                this.name = name;
            }

        }

    }
}
