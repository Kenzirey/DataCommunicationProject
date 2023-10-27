package no.ntnu.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UdpServer {

  public static final int UDP_PORT = 12345;

  private boolean runningState;
  private DatagramSocket socket;


  public static void main(String[] args) {
    UdpServer server = new UdpServer();
    server.run();
  }

  private void run() {
    if (openListeningSocket()) {
      runningState = true;
      while (runningState) {
        DatagramPacket clientDatagram = receiveClientDatagram();
        if (clientDatagram != null) {
          DatagramHandler datagramHandler = new DatagramHandler(socket, clientDatagram);
          datagramHandler.run();
        }
      }
    }
    System.out.println("Server exiting...");
  }

  private DatagramPacket receiveClientDatagram() {
    byte[] receivedData = new byte[200];
    DatagramPacket clientDatagram = new DatagramPacket(receivedData, receivedData.length);
    try {
      socket.receive(clientDatagram);
    } catch (IOException e) {
      System.err.println("Error while receiving client datagram: " + e.getMessage());
      clientDatagram = null;
    }
    return clientDatagram;
  }

  private boolean openListeningSocket() {
    boolean success = false;
    try {
      socket = new DatagramSocket(UDP_PORT);
      System.out.println("Server listening on port " + UDP_PORT);
      success = true;
    } catch (IOException e) {
      System.err.println("Could not open a listening socket on port " + UDP_PORT
          + ", reason: " + e.getMessage());
    }
    return success;
  }


}
