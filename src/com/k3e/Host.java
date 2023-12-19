package com.k3e;

import javax.swing.table.TableModel;
import java.awt.*;
import java.sql.Time;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Host {
    String name;
    String ip;
    String mac;
    HistoryModel historyModel;
    ArrayList<History> histories = new ArrayList<>();

    int state;

    stateListener listener;

    public HistoryModel getHistoryModel() {
        return historyModel;
    }

    interface stateListener {
        void onStateChanged();
    }

    public @interface States {
        int idle = -1;
        int unreachable = 0;
        int reachable = 1;
    }
    public Host(String ip) {
        this.ip = ip;
        this.name = "-";
        this.mac = "";
        historyModel = new HistoryModel();
        idleIt();
    }


    private void addHistory(History history){
        histories.add(history);
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public void setListener(stateListener listener) {
        this.listener = listener;
    }

    @Override
    public String toString() {
        return "Host{" +
                "name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", mac='" + mac + '\'' +
                ", state=" + state +
                '}';
    }

    public void setReachable(boolean reachable) {
        final int previousState = state;
        state = reachable ? States.reachable : States.unreachable;
        if (previousState != state) {
            listener.onStateChanged();
            historyModel.addElement(new History(state, Calendar.getInstance().getTime()));
            if (previousState != States.idle)
                Toolkit.getDefaultToolkit().beep();
        }
    }

    public int getState() {
        return state;
    }

    void idleIt(){
        state = States.idle;
    }



}
