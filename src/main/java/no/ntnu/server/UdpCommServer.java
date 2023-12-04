package no.ntnu.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.UdpCommunicationChannel;

/**
 * A UDP server that listens for incoming datagram packets.
 * The server will process the incoming packets,
 * and send a response back to the client.
 */
public class UdpCommServer extends Thread {
  private static int SERVER_PORT = 12346;
  private UdpCommunicationChannel udpChannel;
  private ControlPanelLogic controlPanelLogic;
  private boolean running;

  /**
   * Creates an instance of server with a default port and listener.
   *
   * @param args command line arguments.
   */
  public static void main(String[] args) {
    //UdpCommServer server = new UdpCommServer(SERVER_PORT);
    //server.start();
    System.out.println("Server started on port " + SERVER_PORT);

  }

  /**
   * Constructs a new server instance,
   * which opens a DatagramSocket to listen for incoming packets.
   */
  public UdpCommServer(int serverPort) {
    try {
      this.controlPanelLogic = new ControlPanelLogic();
      this.udpChannel = new UdpCommunicationChannel(controlPanelLogic, "localhost", serverPort);
      this.controlPanelLogic.setCommunicationChannel(udpChannel);
      this.udpChannel.open();
    } catch (RuntimeException e) {
      System.err.println("Failed to initialize UdpCommChannel " + e.getMessage());
      setRunning(false);
    }
  }

  /**
   * To ensure the shared variable, running,
   * is handled in a thread-safe manner.
   *
   * @param value true/false.
   */
  private synchronized void setRunning(boolean value) {
    this.running = value;
  }

  public synchronized boolean isRunning() {
    return this.running;
  }

  /**
   * Starts server loop and processes incoming datagram packets.
   * Keeps running until "running" becomes false, then shuts down the socket.
   */
  @Override
  public void run() {
    setRunning(true);
    System.out.println("System is running and listening for packets on " + SERVER_PORT);

    while (isRunning() && udpChannel.isSocketClosed()) {
      try {
        System.out.println("Waiting for packet");
        DatagramPacket packet = udpChannel.receivePacket();
        if (packet == null) {
          System.out.println("Received a null packet, continuing...");
          continue;
        }
        System.out.println("Received a packet, processing...");

        handleRequest(new String(
                packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8), packet);
      } catch (Exception e) {
        System.err.println("Error with Udp Server " + e.getMessage());
        }
    }
  }

  /**
   * Processes incoming requests by checking its content.
   * If it ends with "end", the server will shut down.
   *
   * @param message message content from packet.
   *
   * @param packet the received DatagramPacket from client, containing the message.
   * @throws IOException input/output exception .
   */
  private void handleRequest(String message, DatagramPacket packet) throws IOException {
    if ("end".equals(message.trim())) {
      byte[] responseData = shutdownMessage().getBytes(StandardCharsets.UTF_8);
      DatagramPacket responseDatagram = new DatagramPacket(responseData, responseData.length,
              packet.getAddress(), packet.getPort());

      udpChannel.sendPacket(responseDatagram);
      setRunning(false);
      return;
    }
    //Handles the command received in the packet from client.
    DatagramHandler handler = new DatagramHandler(udpChannel.getSocket(), packet);
    handler.run();
  }

//  private void handleRequest(String message, DatagramPacket packet) throws IOException {
//    // Echo back the received message
//    byte[] responseData = message.getBytes(StandardCharsets.UTF_8);
//    DatagramPacket responseDatagram = new DatagramPacket(
//            responseData, responseData.length, packet.getAddress(), packet.getPort());
//
//    udpChannel.sendPacket(responseDatagram);
//  }
  /**
   * Shuts down the server.
   */
  public synchronized void shutdown() {
    setRunning(false);
    if (this.udpChannel != null && this.udpChannel.getSocket() != null && this.udpChannel.isSocketClosed()) {
      udpChannel.closeSocket();
    }
    System.out.println(shutdownMessage());
  }

  /**
   * Returns the set server port.
   *
   * @return the server port.
   */
  public int getServerPort() {
    return SERVER_PORT;
  }

  /**
   * Sets the communication channel for the test server.
   *
   * @param testChannel the test channel.
   */
  public void setCommunicationChannel(UdpCommunicationChannel testChannel) {
    this.udpChannel = testChannel;
  }

  private String shutdownMessage() {
    return "Server is shutting down..";
  }

  public UdpCommunicationChannel getUdpChannel() {
    return this.udpChannel;
  }
}
