package com.k3e;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class HostModel extends AbstractTableModel  {

    private final ArrayList<Host> hosts = new ArrayList<>();
    String[] titles = {"Hostname","IP Address","MAC","Status"};

    private ImageIcon reachableIcon;
    private ImageIcon unreachableIcon;
    private ImageIcon idleIcon;

    public HostModel() {
    }

    void setIcons(ImageIcon reachable, ImageIcon unreachable,  ImageIcon idle){
        reachableIcon = reachable;
        unreachableIcon = unreachable;
        idleIcon = idle;

    }



    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    public ArrayList<Host> getHosts() {
        return hosts;
    }

    public ArrayList<Host> getAliveHosts() {
        ArrayList<Host> alive = new ArrayList<>();

        for (Host host : hosts) {
            if (host.getState() == Host.States.reachable)
                alive.add(host);
        }

        return alive;
    }

    @Override
    public int getRowCount() {
        return hosts.size();
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Object value = "??";
        Host host = hosts.get(rowIndex);
        switch (columnIndex) {
            case 3:
                if(host.state == Host.States.reachable){
                    value = reachableIcon;
                }else if(host.state == Host.States.unreachable){
                    value = unreachableIcon;
                }else
                    value = idleIcon;
                break;
            case 1:
                value = host.ip;
                break;

            case 2:
                value = host.mac;
                break;
            case 0:
                value = host.name;
                break;
        }

        return value;

    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        super.setValueAt(aValue, rowIndex, columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0,columnIndex).getClass();
    }

    public boolean contains(String ip) {
        boolean found;
        found = false;
        for (Host host : hosts) {
            found = host.ip.equals(ip);
            if (found) break;
        }

        return found;
    }

    public void addElement(Host host) {
        hosts.add(host);
        fireTableDataChanged();
    }


    @Override
    public void fireTableDataChanged() {
        super.fireTableDataChanged();
    }


}
