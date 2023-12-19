package com.k3e;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MACFactory {
    public static String getARPTable(String ip) throws IOException {
        String systemInput = "";
//to renew the system table before querying
        Runtime.getRuntime().exec("arp -a");
        Scanner s = new Scanner(Runtime.getRuntime().exec("arp -a " + ip).getInputStream()).useDelimiter("\\A");
        systemInput = s.next();
        String mac = "";
        Pattern pattern = Pattern.compile("\\s{0,}([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})");
        Matcher matcher = pattern.matcher(systemInput);
        if (matcher.find()) {
            mac = mac + matcher.group().replaceAll("\\s", "");
        } else {
            System.out.println("No string found");
        }
        return mac;
    }

    /*public static void main(String[] args) throws IOException {

        System.out.println(getARPTable("192.168.1.1"));
        // prints 74-d4-35-76-11-ef

    }*/
}
