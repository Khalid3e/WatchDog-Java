package com.k3e;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class History {
    int state;
    Date timeStamp;

    public @interface States {
        int idle = -1;
        int unreachable = 0;
        int reachable = 1;
    }
    History(int state, Date timeStamp){
        this.state = state;
        this.timeStamp = timeStamp;
    }

    public String getFormattedDate(){
        return new SimpleDateFormat("HHmmss_yyyyMMdd").format(Calendar.getInstance().getTime());
    }
}
