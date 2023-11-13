package no.ntnu.server;

import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.UdpCommunicationChannel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

/**
 * A UDP server that listens for incoming datagram packets.
 * The server will process the incoming packets,
 * and send a response back to the client.
 */
public class Server extends Thread implements ServerMessageListener {

  //TODO: implement logic to allow adding multiple clients?
  private static final int SERVER_PORT = 12346;
  private UdpCommunicationChannel udpChannel;
  private ControlPanelLogic controlPanelLogic;
  private boolean running;
  private static final String serverStopping = "Server is shutting down..";
  private ServerMessageListener messageListener;
  public static void main(String[] args) {
    // Simple implementation of ServerMessageListener for testing
    ServerMessageListener messageListener =
            message -> System.out.println("Received message: " + message);

    // Create the server instance with the test listener
    Server server = new Server(SERVER_PORT, messageListener);
    server.start();

  }

  /**
   * Constructs a new server instance,
   * which opens a DatagramSocket to listen for incoming packets.
   */
  public Server(int SERVER_PORT, ServerMessageListener messageListener) {
    this.controlPanelLogic = new ControlPanelLogic();
    this.messageListener = messageListener;
    this.udpChannel = new UdpCommunicationChannel(controlPanelLogic, "localhost", SERVER_PORT);
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

        handleRequest(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8), packet);
      } catch (IOException e) {
        if (!running || udpChannel.isSocketClosed()) {
          break;
        }
        System.err.println("Error processing packet: " + e.getMessage());
      }
    }
    //TODO: Document synchronized.
    synchronized (this) {
      udpChannel.closeSocket();
      notifyAll();
      //Had problems with server trying to receive packets on a closed socket.
      System.out.println("Socket closed.");
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
      byte[] responseData = serverStopping.getBytes(StandardCharsets.UTF_8);
      DatagramPacket responseDatagram = new DatagramPacket(responseData, responseData.length,
              packet.getAddress(), packet.getPort());

      udpChannel.sendPacket(responseDatagram);
      running = false;
      return;
    }
    //Handles the command received in the packet from client.
    DatagramHandler handler = new DatagramHandler(udpChannel.getSocket(), packet);
    handler.run();

    if (messageListener!= null) {
      messageListener.onMessageReceived(message);
    }
  }

  /**
   * Shuts down the server.
   */
  public void shutdown() {
    this.running = false;
    this.udpChannel.closeSocket();
    System.out.println(serverStopping);
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
   * ServerMessageListener interface method.
   * To update the GreenhouseSimulator with the received message.
   * @param message the received message.
   */
  @Override
  public void onMessageReceived(String message) {
   //Empty on purpose - Server does not process messages itself.
  }

  /**
   * Returns true if the server is running, false otherwise.
   * @return true if the server is running, false otherwise.
   */
  public boolean isRunning() {
    if (this.running == true) {
      return true;
    } else {
      return false;
    }
  }

  public void setCommunicationChannel(UdpCommunicationChannel testChannel) {
    this.udpChannel = testChannel;
  }
}
