package no.ntnu.controlpanel;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import no.ntnu.tools.Logger;

/**
 * Manages a communication channel using UDP protocol.
 * Handles sending and receiving DatagramPackets to/from a server.
 */
public class UdpCommunicationChannel implements CommunicationChannel {

  private final ControlPanelLogic logic;
  private DatagramSocket socket;
  private InetAddress serverAddress;
  private int serverPort;
  //TODO: Document why socketClosed is used.
  private volatile boolean socketClosed = false;

  /**
   * Create a new UDP communication channel.
   *
   * @param logic central logic instance of a control panel node.
   *
   * @param serverIp the IP address of the server.
   *
   * @param serverPort the port number for the UDP server.
   */
  public UdpCommunicationChannel(ControlPanelLogic logic, String serverIp, int serverPort) {
    this.logic = logic;
    this.serverPort = serverPort;
    try {
      this.serverAddress = InetAddress.getByName(serverIp);

      //System chooses available client port, so not specified here.
      this.socket = new DatagramSocket();
    } catch (IOException e) {
      throw new RuntimeException("Error setting up UDP socket", e);
    }
  }

  /**
   * Receive a datagram packet.
   *
   * @return packet the received datagram packet.
   *
   * @throws IOException input/output exception.
   */
  public DatagramPacket receivePacket() throws IOException {
    //TODO: use ByteBuffer instead.
    byte[] buffer = new byte[1024];
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    socket.receive(packet);
    return packet;
  }

  /**
   * Sends a command to change the state of an actuator via UDP.
   *
   * @param nodeId     ID of the node to which the actuator is attached
   * @param actuatorId Node-wide unique ID of the actuator
   * @param isOn       When true, actuator must be turned on; off when false.
   */
  @Override
  public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
    String message =
            "Node: " + nodeId + ", Actuator: " + actuatorId + ", State: " + (isOn ? "ON" : "OFF");
    byte[] sendData = message.getBytes();
    DatagramPacket sendPacket =
            new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);

    try {
      socket.send(sendPacket);
      Logger.info("Sent command to server: " + message);
    } catch (IOException e) {
      System.err.println("Error sending UDP packet" + e.getMessage());
    }
  }

  /**
   * Opens the UDP socket if not already open.
   *
   * @return true if socket is opened, false otherwise.
   */
  @Override
  public boolean open() {
    if (socket == null || socket.isClosed()) {
      try {
        socket = new DatagramSocket();
        return true;
      } catch (SocketException e) {
        System.err.println("Error opening UDP socket" + e.getMessage());
        return false;
      }
    }
    return true;
  }

  /**
   * Closes the UDP socket.
   */
  public void closeSocket() {
    if (socket != null && !socket.isClosed()) {
      socket.close();
      socketClosed = true;
    }
  }

  /**
   * Returns the UDP socket.
   *
   * @return socket returns the UDP socket.
   */
  public DatagramSocket getSocket() {
    return this.socket;
  }

  /**
   * Sends a DatagramPacket.
   *
   * @param packet the DatagramPacket being sent.
   *
   * @throws IOException input/output exception.
   */
  public void sendPacket(DatagramPacket packet) throws IOException {
    if (socket != null && !socket.isClosed() && packet!= null) {
      socket.send(packet);
    } else {
      throw new SocketException("Socket is closed or not initialized");
    }
  }

  public boolean isSocketClosed() {
    return this.socketClosed;
  }
  //TODO: separate thread or listener for the sending and receiving?
}
