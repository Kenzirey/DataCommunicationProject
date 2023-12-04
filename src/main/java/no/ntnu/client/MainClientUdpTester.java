package no.ntnu.client;

import no.ntnu.server.UdpCommServer;
import no.ntnu.controlpanel.UdpCommunicationChannel;
import no.ntnu.controlpanel.ControlPanelLogic;
import java.util.Scanner;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class MainClientUdpTester {
  private static final String SERVER_ADDRESS = "localhost";
  private static final int SERVER_PORT = 12346;
  private static UdpCommunicationChannel udpChannel;

  public static void main(String[] args) {
    UdpCommServer server = new UdpCommServer(SERVER_PORT);
    server.start();
    udpChannel = server.getUdpChannel();
    server.setCommunicationChannel(udpChannel);
    udpChannel.open();



    Scanner scanner = new Scanner(System.in);
    sendCommandToServer("time");
    //sendCommandToServer("echo");

    while (true) {
      System.out.print("Enter command (or 'exit' to quit): ");
      String command = scanner.nextLine();

      if ("exit".equalsIgnoreCase(command)) {
        System.out.println("Exiting the scanner...");
        server.shutdown();
        udpChannel.closeSocket();
        server.shutdown();
        break;
      }

      sendCommandToServer(command);
      System.out.println(command + " sent");
    }

    scanner.close();
  }

  private static void sendCommandToServer(String command) {
    try {
      byte[] sendData = command.getBytes(StandardCharsets.UTF_8);
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT);
      udpChannel.sendPacket(sendPacket);

      // Receive response
      DatagramPacket receivePacket = udpChannel.receivePacket();
      String received = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
      System.out.println("Received: " + received);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}


