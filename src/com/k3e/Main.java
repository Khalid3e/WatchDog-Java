package com.k3e;

import com.formdev.flatlaf.FlatIntelliJLaf;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Thread.sleep;

@SuppressWarnings("BusyWait")
public class Main {


    private JPanel panelMain;
    private JButton bStart;
    private int selectedRow = -1;

    //private final HostListModel<Host> model = new HostListModel<>();
    private final HostModel hostModel = new HostModel();
    private ImageIcon reachableIcon;
    private ImageIcon unreachableIcon;
    private ImageIcon idleIcon;
    private JButton bAdd;
    private JButton bStop;
    private JTextField tfBit4;
    private JTextField tfBit1;
    private JTextField tfBit2;
    private JTextField tfBit3;
    private JTable hostJTable;
    private JTable historyJTable;
    private boolean running = false;


    public static void main(String[] args) {
        FlatIntelliJLaf.setup();
        SwingUtilities.invokeLater(() -> {
            Main mainGUI = new Main();
            mainGUI.gui();
        });


    }


    private void gui() {



        reachableIcon = new ImageIcon(("res/reachable.png"));
        unreachableIcon = new ImageIcon(("res/unreachable.png"));
        idleIcon = new ImageIcon(("res/idle.png"));

        reachableIcon = new ImageIcon(resize(reachableIcon.getImage(), 10));
        unreachableIcon = new ImageIcon(resize(unreachableIcon.getImage(), 10));
        idleIcon = new ImageIcon(resize(idleIcon.getImage(), 10));


        Thread DNSResolverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!running){
                    System.out.println("Waiting to run");
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                for (Host host : hostModel.getAliveHosts()) {
                    System.out.println("DNSResolver = " + host);
                    InetAddress address = null;

                    try {
                        address = InetAddress.getByName(host.ip);
                        if (!address.getHostName().equals(host.ip)) host.setName(address.getHostName());
                        else host.setName("-");
                    } catch (IOException ignored) {
                    }
                }
                while (hostModel.getHosts().isEmpty()){
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
                try {
                    sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                run();
            }

            public boolean initiated = false;
        });

        Thread PingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!running){
                    System.out.println("Waiting to run");
                    try {
                        sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                for (Host host : hostModel.getHosts()) {
                    System.out.println("host = " + host);
                    InetAddress address = null;
                    try { address = InetAddress.getByName(host.ip); } catch (IOException ignored) { }

                    try {
                        host.setListener(() -> {
                            SwingUtilities.invokeLater(() -> {
                                hostModel.fireTableCellUpdated(hostModel.getHosts().indexOf(host), 0);
                            });
                        });

                        host.setReachable(address != null && address.isReachable(1000));
                        historyJTable.setModel(host.getHistoryModel());
                        if (address != null) {
                            host.setMac(MACFactory.getARPTable(host.ip));
                        }
                        SwingUtilities.invokeLater(hostModel::fireTableDataChanged);
                        sleep(1000);
                    } catch (IOException | InterruptedException ignored) {
                    }


                }

                while (hostModel.getHosts().isEmpty()){
                    try {
                        sleep(300);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                }
                run();
            }
        });


        DNSResolverThread.start();
        PingThread.start();



        bStart.addActionListener(e -> {



            running = true;

            bStart.setEnabled(false);
            bStop.setEnabled(true);
            bAdd.setEnabled(false);

            for (JTextField jTextField : Arrays.asList(tfBit1, tfBit2, tfBit3, tfBit4)) {
                jTextField.setEnabled(false);
            }

            JRootPane rootPane = SwingUtilities.getRootPane(bStop);
            rootPane.setDefaultButton(bStop);

        });

        bAdd.addActionListener(e -> {

            if (tfBit1.getText().length() == 0) {
                Toolkit.getDefaultToolkit().beep();
                tfBit1.grabFocus();
                return;
            }
            if (tfBit2.getText().length() == 0) {
                Toolkit.getDefaultToolkit().beep();
                tfBit2.grabFocus();
                return;
            }
            if (tfBit3.getText().length() == 0) {
                Toolkit.getDefaultToolkit().beep();
                tfBit3.grabFocus();
                return;
            }
            if (tfBit4.getText().length() == 0) {
                Toolkit.getDefaultToolkit().beep();
                tfBit4.grabFocus();
                return;
            }


            String ip = tfBit1.getText() + "." + tfBit2.getText() + "." + tfBit3.getText() + "." + tfBit4.getText();
            if (!hostModel.contains(ip))
                hostModel.addElement(new Host(ip));
            hostModel.setIcons(reachableIcon, unreachableIcon, idleIcon);
            hostJTable.setModel(hostModel);
            hostJTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    selectedRow = e.getFirstIndex();

                }
            });

// Restore selected raw table
            hostModel.addTableModelListener(new TableModelListener() {
                @Override
                public void tableChanged(TableModelEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (selectedRow >= 0) {
                                hostJTable.addRowSelectionInterval(selectedRow, selectedRow);
                            }
                        }
                    });
                }
            });

            for (JTextField jTextField : Arrays.asList(tfBit1, tfBit2, tfBit3, tfBit4)) {
                jTextField.setText("");
            }


        });

        bStop.addActionListener(e -> {
            bStop.setEnabled(false);


            running = false;

            for (Host host : hostModel.getHosts()) {
                host.idleIt();
            }

            bStart.setEnabled(true);
            bAdd.setEnabled(true);
            tfBit1.setEnabled(true);
            tfBit2.setEnabled(true);
            tfBit3.setEnabled(true);
            tfBit4.setEnabled(true);
            JRootPane rootPane = SwingUtilities.getRootPane(bAdd);
            rootPane.setDefaultButton(bAdd);
        });


        JFrame frame = new JFrame();
        frame.setTitle("k3e Watch-Dog");
        frame.setIconImage(resize(new ImageIcon("res/icon.png").getImage(),32));
        frame.setName("k3e Watch-Dog");
        frame.setContentPane(panelMain);
        frame.pack();
        frame.setMinimumSize(new Dimension(500, 400));
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    DNSResolverThread.interrupt();
                    PingThread.interrupt();

                }catch (Exception ignored){

                }
                running = false;
                e.getWindow().dispose();
            }
        });
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JRootPane rootPane = SwingUtilities.getRootPane(bAdd);
        rootPane.setDefaultButton(bAdd);

        for (JTextField field : Arrays.asList(tfBit1, tfBit2, tfBit3, tfBit4)) {
            setUpFields(field);
        }



    }


    static Image resize(Image image, int d) {
        return image.getScaledInstance(d,d, Image.SCALE_SMOOTH);

    }


    public void setUpFields(JTextField field) {

        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                SwingUtilities.invokeLater(() -> {
                    JTextField tf = (JTextField) e.getSource();
                    tf.selectAll();
                });
            }

            @Override
            public void focusLost(FocusEvent e) {
                try {
                    if (Integer.parseInt(field.getText().trim()) < 0) {
                        field.setText("0");
                        Toolkit.getDefaultToolkit().beep();
                    }
                } catch (Exception ignored) {
                }


                try {
                    if (Integer.parseInt(field.getText().trim()) > 255) {
                        field.setText("255");
                        Toolkit.getDefaultToolkit().beep();

                    }
                } catch (Exception ignored) {
                }

            }
        });
        field.setHorizontalAlignment(SwingConstants.CENTER);

        ((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
            final Pattern regEx = Pattern.compile("\\d*");


            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                Matcher matcher = regEx.matcher(text);
                boolean space = text.equals(" ");
                boolean oldBigNumber = false;
                try {
                    int i = Integer.parseInt(field.getText() + text);
                    if (i > 255) {
                        oldBigNumber = true;
                    }
                } catch (Exception ignored) {
                }

                StringBuilder bigText = new StringBuilder(field.getText());

                if (!matcher.matches() && !space) {
                    return;
                }
                if (space) text = "";


                bigText.append(text);


                try {
                    if (Integer.parseInt(bigText.toString()) >= 100 || space) {
                        if (!oldBigNumber)
                            KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
                        bigText.delete(0, bigText.length() - 1);
                    }
                }catch (Exception ignored){

                }

                super.replace(fb, offset, length, text, attrs);
            }
        });


    }

    private InputStream accessFile(String resource) {

        // this is the path within the jar file
        InputStream input = Main.class.getResourceAsStream("/res/" + resource);
        if (input == null) {
            // this is how we load file within editor (eg eclipse)
            input = Main.class.getClassLoader().getResourceAsStream(resource);
        }

        return input;
    }

}
