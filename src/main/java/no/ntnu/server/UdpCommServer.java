package no.ntnu.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.UdpCommunicationChannel;

/**
 * A UDP server that listens for incoming datagram packets.
 * The server will process the incoming packets,
 * and send a response back to the client.
 */
public class UdpCommServer extends Thread {
  private volatile boolean isReady = false;
  private static int SERVER_PORT = 12346;
  private UdpCommunicationChannel udpChannel;
  private ControlPanelLogic controlPanelLogic;
  private boolean running;
  private final byte[] buffer = new byte[1024];
  private DatagramSocket udpSocket;
  private static final String SERVER_STOPPING = "Server is shutting down..";

  /**
   * Creates an instance of server with a default port and listener.
   *
   * @param args command line arguments.
   */
  public static void main(String[] args) {
    // Create the server instance with the test listener
    UdpCommServer server = new UdpCommServer(SERVER_PORT);
    server.start();
    System.out.println("Server started on port " + SERVER_PORT);

  }

  /**
   * Constructs a new server instance,
   * which opens a DatagramSocket to listen for incoming packets.
   */
  public UdpCommServer(int serverPort) {
    this.controlPanelLogic = new ControlPanelLogic();
    this.udpChannel = new UdpCommunicationChannel(controlPanelLogic, "localhost", serverPort);
    this.controlPanelLogic.setCommunicationChannel(udpChannel);
    this.udpChannel.open();
  }

  /**
   * Starts server loop and processes incoming datagram packets.
   * Keeps running until "running" becomes false, then shuts down the socket.
   */
  @Override
  public void run() {
    running = true;

    while (running && !udpChannel.isSocketClosed()) {
      try {
        DatagramPacket packet = udpChannel.receivePacket();
        if (packet == null) {
          throw new NullPointerException("Packet is null");
        }
        handleRequest(new String(
                packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8), packet);
      } catch (IOException e) {
        if (!running || udpChannel.isSocketClosed()) {
          break;
        }
        System.err.println("Error processing packet: " + e.getMessage());
      }
    }
    //TODO: Document synchronized in protocol.md.
    //Synchronized is used to ensure closing of the UDP socket in a "thread-safe manner".
    //Prevents multiple threads from executing sections of code at the same time,
    //with shared resources (socket).
    synchronized (this) {
      udpChannel.closeSocket();
      notifyAll();
      //Had problems with server trying to receive packets on a closed socket.
      System.out.println("Socket closed.");
    }
  }

  public synchronized  boolean isReady() {
    return isReady;
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
      byte[] responseData = SERVER_STOPPING.getBytes(StandardCharsets.UTF_8);
      DatagramPacket responseDatagram = new DatagramPacket(responseData, responseData.length,
              packet.getAddress(), packet.getPort());

      udpChannel.sendPacket(responseDatagram);
      running = false;
      return;
    }
    //Handles the command received in the packet from client.
    DatagramHandler handler = new DatagramHandler(udpChannel.getSocket(), packet);
    handler.run();
  }

  /**
   * Shuts down the server.
   */
  public void shutdown() {
    this.running = false;
    if (this.udpChannel != null && this.udpChannel.getSocket() != null && !this.udpChannel.isSocketClosed()) {
      this.udpChannel.getSocket().close();
    }
    System.out.println(SERVER_STOPPING);
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
   * Returns true if the server is running, false otherwise.
   *
   * @return true if the server is running, false otherwise.
   */
  public boolean isRunning() {
    return this.running;
  }

  /**
   * Sets the communication channel for the test server.
   *
   * @param testChannel the test channel.
   */
  public void setCommunicationChannel(UdpCommunicationChannel testChannel) {
    this.udpChannel = testChannel;
  }

  public UdpCommServer getServer() {
    return this;
  }


  private boolean openListeningSocket() {
    boolean success = false;
    try {
      udpSocket = new DatagramSocket(SERVER_PORT);
      System.out.println("Server listening to port " + SERVER_PORT);
      success = true;
    } catch (IOException e) {
      System.err.println("Could not open a listening port");
    }
    return success;
  }



}
