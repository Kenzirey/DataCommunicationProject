package no.ntnu.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * A UDP server, which handles multiple clients.
 */
public class UdpServer {

  public static final int UDP_PORT = 12345;

  private boolean runningState;
  private DatagramSocket socket;


  public static void main(String[] args) {
    UdpServer server = new UdpServer();
    server.run();
  }

  private void run() {

    //If there is a connection, then the server is running.
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
    //TODO: add a more meaningful message that is our own.
    System.out.println("Server exiting");
  }

  /**
   * DatagramPacket is a form of connectionless packet delivery service.
   * @return
   */
  private DatagramPacket receiveClientDatagram() {
    byte[] dataReceived = new byte[400];
    //TODO: Make this our own.

    //constructor(byte[] buf, int length). (buf for buffer, holding incoming data).
    DatagramPacket clientDatagram = new DatagramPacket(dataReceived, dataReceived.length);
    try {
      socket.receive(clientDatagram);
    } catch (IOException e) {
      System.err.println("Error while receiving client datagram: " + e.getMessage());
      clientDatagram = null;
    }
    return clientDatagram;
  }

  /**
   * Opens up a socket that is listening for incoming connections.
   *
   * DatagramSocket is a socket for sending and receiving datagram packets.
   * Socket is an endpoint of a two-way communication link
   * between two programs running on the network.
   * @return
   */
  private boolean openListeningSocket() {
    //TODO: Make this our own.
    boolean success = false;
    try {
      socket = new DatagramSocket(UDP_PORT);
      System.out.println("Server listening on port number: " + UDP_PORT);
      success = true;
    } catch (IOException e) {
      System.err.println("Error, could not open a listening socket on port " + UDP_PORT
          + ", because: " + e.getMessage());
    }
    return success;
  }


}
