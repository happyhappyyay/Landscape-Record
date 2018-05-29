package com.example.kingdenis.landscaperecord;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

//log actions performed by user
@Entity
public class LogActivity {
    @PrimaryKey(autoGenerate = true)
    private int logID;
    private long time;
    private String username, addInfo;
    private int logActivityAction, logActivityType;

    public LogActivity(String username, String addInfo, int logActivityAction, int logActivityType) {
        time = System.currentTimeMillis();
        this.username = username;
        this.addInfo = addInfo;
        this.logActivityAction = logActivityAction;
        this.logActivityType = logActivityType;
    }

    @Override
    public String toString() {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        String dateMessage = formatter.format(date);
        return dateMessage + ": " + username + " " + convertActivityToString();
    }

    private String convertActivityToString() {
        String activityAction = "";
        List<String> logActivityList = Arrays.asList(LogActivityAction.ADD.toString(),
                LogActivityAction.DELETE.toString(), LogActivityAction.UPDATE.toString());
        List<String> logTypeList = Arrays.asList(LogActivityType.USER.toString(),
                LogActivityType.ACCOUNT.toString(), LogActivityType.PAYMENT.toString());
        activityAction += logActivityList.get(logActivityAction) + " ";
        activityAction += logTypeList.get(logActivityType) + " ";
        activityAction += addInfo;

        return activityAction;
    }

    public int getLogID() {
        return logID;
    }

    public void setLogID(int logID) {
        this.logID = logID;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public int getLogActivityAction() {
        return logActivityAction;
    }

    public int getLogActivityType() {
        return logActivityType;
    }
}
