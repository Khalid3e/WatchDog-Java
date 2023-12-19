package com.k3e;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class HistoryModel extends AbstractTableModel  {

    private  ArrayList<History> histories = new ArrayList<>();
    String[] titles = {"Status","Timestamp"};
    
    public HistoryModel() {
        reachableIcon = new ImageIcon(("res/reachable.png"));
        unreachableIcon = new ImageIcon(("res/unreachable.png"));
        idleIcon = new ImageIcon(("res/idle.png"));

        reachableIcon = new ImageIcon(Main.resize(reachableIcon.getImage(), 10));
        unreachableIcon = new ImageIcon(Main.resize(unreachableIcon.getImage(), 10));
        idleIcon = new ImageIcon(Main.resize(idleIcon.getImage(), 10));
    }

    private ImageIcon reachableIcon;
    private ImageIcon unreachableIcon;
    private ImageIcon idleIcon;




    @Override
    public String getColumnName(int column) {
        return titles[column];
    }

    public ArrayList<History> getHistories() {
        return histories;
    }



    @Override
    public int getRowCount() {
        return histories.size();
    }

    @Override
    public int getColumnCount() {
        return titles.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        Object value = "??";
        History history = histories.get(rowIndex);
        switch (columnIndex) {
            case 0:
                if(history.state == History.States.reachable){
                    value = reachableIcon;
                }else if(history.state == History.States.unreachable){
                    value = unreachableIcon;
                }else
                    value = idleIcon;
                break;

            case 1:
                value = history.timeStamp;
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



    public void addElement(History history) {
        histories.add(history);
        fireTableDataChanged();
    }
    public void addElements(ArrayList<History> histories) {
        this.histories = histories;
        fireTableDataChanged();
    }

    @Override
    public void fireTableDataChanged() {
        super.fireTableDataChanged();
    }


}
