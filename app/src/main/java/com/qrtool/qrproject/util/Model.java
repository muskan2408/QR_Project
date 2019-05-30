package com.qrtool.qrproject.util;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Model implements Parcelable {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("user")
    @Expose
    private User user;

    @SerializedName("qrId")
    @Expose
    private String qrId;

    @SerializedName("usertype")
    @Expose
    private String userType;

    @SerializedName("data")
    @Expose
    private ArrayList<History> data;

    @SerializedName("status")
    @Expose
    private ArrayList<Status> status;

    public ArrayList<Status> getStatus() {
        return status;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setStatus(ArrayList<Status> status) {
        this.status = status;
    }

    public ArrayList<History> getData() {
        return data;
    }

    public void setData(ArrayList<History> data) {
        this.data = data;
    }

    public String getQrId() {
        return qrId;
    }

    public void setQrId(String qrId) {
        this.qrId = qrId;
    }

    protected Model(Parcel in) {
        byte tmpSuccess = in.readByte();
        success = tmpSuccess == 0 ? null : tmpSuccess == 1;
        token = in.readString();
    }

    public static final Creator<Model> CREATOR = new Creator<Model>() {
        @Override
        public Model createFromParcel(Parcel in) {
            return new Model(in);
        }

        @Override
        public Model[] newArray(int size) {
            return new Model[size];
        }
    };

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
//

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success == null ? 0 : success ? 1 : 2));
        dest.writeString(token);
    }
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
