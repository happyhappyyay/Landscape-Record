package com.example.kingdenis.landscaperecord;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LogActivity {

    private long time;
    private String username, addInfo;
    private int logActivityAction, logActivityType;

    public LogActivity(String username, String addInfo, int logActivityAction, int logActivityType){
        time = System.currentTimeMillis();
        this.username = username;
        this.addInfo = addInfo;
        this.logActivityAction = logActivityAction;
        this.logActivityType = logActivityType;
    }

    @Override
    public String toString() {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
        String dateMessage = formatter.format(date);
        return dateMessage + ": " + username + " " + convertActivityToString();
    }

    private String convertActivityToString() {
        String activityAction = "";
        List<String> logActivityList = Arrays.asList(LogActivityAction.ADD.toString(),
                LogActivityAction.DELETE.toString(),LogActivityAction.UPDATE.toString());
        List<String> logTypeList = Arrays.asList(LogActivityType.USER.toString(),
                LogActivityType.ACCOUNT.toString(),LogActivityType.PAYMENT.toString());
        activityAction += logActivityList.get(logActivityAction) + " ";
        activityAction += logTypeList.get(logActivityType) + " ";
        activityAction += addInfo;

        return activityAction;
    }
}
