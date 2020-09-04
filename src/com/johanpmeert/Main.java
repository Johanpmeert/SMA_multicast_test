package com.johanpmeert;

import java.io.*;
import java.net.*;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class Main {

    public static void main(String[] args) throws UnknownHostException {

        final String sma_multicastIp = "239.12.255.254";
        final int sma_multicastPort = 9522;
        final String myHostIpAddress = "192.168.2.147";  // change here to your PC ip address

        try {
            InetAddress mcastAddr = InetAddress.getByName(sma_multicastIp);
            InetSocketAddress group = new InetSocketAddress(mcastAddr, sma_multicastPort);
            NetworkInterface netIf = NetworkInterface.getByName(myHostIpAddress);
            MulticastSocket mcSocket = new MulticastSocket(sma_multicastPort);
            mcSocket.joinGroup(group, netIf);

            byte[] txbuf = parseHexBinary("534d4100000402a0ffffffff0000002000000000");  // discovery string to be sent to network, all SMA devices will answer
            System.out.println("Sending out SMA specific discovery code " + printHexBinary(txbuf) + " to multicast address " + sma_multicastIp + "/" + sma_multicastPort);
            DatagramPacket data = new DatagramPacket(txbuf, txbuf.length, mcastAddr, sma_multicastPort);
            mcSocket.send(data);

            byte[] buffer = new byte[10 * 1024];
            data = new DatagramPacket(buffer, buffer.length);
            while (true) {
                mcSocket.receive(data);
                String hexdata = printHexBinary(buffer);
                hexdata = hexdata.substring(0, data.getLength() * 2);
                System.out.println("Received from " + data.getAddress() + ": " + hexdata);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}
