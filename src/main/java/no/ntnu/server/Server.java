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
  private final UdpCommunicationChannel udpChannel;
  private ControlPanelLogic controlPanelLogic;
  private boolean running;
  private final byte[] buffer = new byte[1024];
  private static final String serverStopping = "Server is shutting down..";
  private ServerMessageListener messageListener;
  public static void main(String[] args) {
    // Simple implementation of ServerMessageListener for testing
    ServerMessageListener messageListener = new ServerMessageListener() {
      @Override
      public void onMessageReceived(String message) {
        System.out.println("Received message: " + message);
      }
    };

    // Create the server instance with the test listener
    Server server = new Server(12346, messageListener);
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
   * Starts server loop and processed incoming datagram packets.
   */
  @Override
  public void run() {
    running = true;

    while (running) {
      try {
        DatagramPacket packet = udpChannel.receivePacket();

        handleRequest(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8), packet);
      } catch (IOException e) {
        if (!running) {
          break;
        }
        System.err.println("Error processing packet: " + e.getMessage());
      }
    }

    udpChannel.closeSocket();
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
    running = false;
    udpChannel.closeSocket();
    System.out.println(serverStopping);
  }

  public int getServerPort() {
    return SERVER_PORT;
  }

  @Override
  public void onMessageReceived(String message) {
   //TODO: what to do here
  }
}
