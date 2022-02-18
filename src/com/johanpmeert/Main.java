package com.johanpmeert;

import java.io.*;
import java.net.*;

public class Main {

    public static void main(String[] args) {

        final String sma_multicastIp = "239.12.255.254";
        final int sma_multicastPort = 9522;
        final String myHostIpAddress = "192.168.2.147";  // change here to your PC ip address

        try {
            InetAddress mcastAddr = InetAddress.getByName(sma_multicastIp);
            InetSocketAddress group = new InetSocketAddress(mcastAddr, sma_multicastPort);
            NetworkInterface netIf = NetworkInterface.getByName(myHostIpAddress);
            MulticastSocket mcSocket = new MulticastSocket(sma_multicastPort);
            mcSocket.joinGroup(group, netIf);

            byte[] txbuf = hexStringToByteArray("534d4100000402a0ffffffff0000002000000000"); // discovery string to be sent to network, all SMA devices will answer
            System.out.println("Sending out SMA specific discovery code " + byteArrayToHexString(txbuf) + " to multicast address " + sma_multicastIp + "/" + sma_multicastPort);
            DatagramPacket data = new DatagramPacket(txbuf, txbuf.length, mcastAddr, sma_multicastPort);
            mcSocket.send(data);

            byte[] buffer = new byte[1024];
            data = new DatagramPacket(buffer, buffer.length);
            while (true) {
                mcSocket.receive(data);
                String hexdata = byteArrayToHexString(buffer);
                hexdata = hexdata.substring(0, data.getLength() * 2);
                System.out.println("Received from " + data.getAddress() + ": " + hexdata);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public static byte[] hexStringToByteArray(String hex) {
        hex = hex.length() % 2 != 0 ? "0" + hex : hex;
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(hex.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    public static String byteArrayToHexString(byte[] bytes) {
        final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

}
