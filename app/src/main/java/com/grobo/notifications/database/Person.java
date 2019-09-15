package com.grobo.notifications.database;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Keep
public class Person {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("user")
    @Expose
    private User user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Keep
    public class User {

        @SerializedName("pors")
        @Expose
        private List<String> pors = null;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("instituteId")
        @Expose
        private String instituteId;
        @SerializedName("batch")
        @Expose
        private String batch;
        @SerializedName("branch")
        @Expose
        private String branch;
        @SerializedName("rollno")
        @Expose
        private String rollno;
        @SerializedName("phone")
        @Expose
        private String phone;
        @SerializedName("_id")
        @Expose
        private String studentMongoId;
        @SerializedName("active")
        @Expose
        private int active;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
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

        public String getBatch() {
            return batch;
        }

        public void setBatch(String batch) {
            this.batch = batch;
        }

        public String getBranch() {
            return branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public String getRollno() {
            return rollno;
        }

        public void setRollno(String rollno) {
            this.rollno = rollno;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPhone() {
            return phone;
        }

        public String getStudentMongoId() {
            return studentMongoId;
        }

        public void setStudentMongoId(String studentMongoId) {
            this.studentMongoId = studentMongoId;
        }

        public int getActive() {
            return active;
        }


        public void setActive(int active) {
            this.active = active;
        }


        public List<String> getPors() {
            return pors;
        }

        public void setPors(List<String> pors) {
            this.pors = pors;
        }
    }
}