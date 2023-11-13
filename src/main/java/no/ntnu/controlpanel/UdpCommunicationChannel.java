package no.ntnu.controlpanel;

import no.ntnu.server.Server;

import java.io.IOException;
import java.net.*;

public class UdpCommunicationChannel implements CommunicationChannel {
  private DatagramSocket socket;
  private InetAddress serverAddress;
  private int serverPort;
  private Server server;

  public UdpCommunicationChannel(String serverAddress, int serverPort) throws UnknownHostException {
    this.serverAddress = InetAddress.getByName(serverAddress);
    this.serverPort = server.getServerPort();
  }

  @Override
  public boolean open() {
    try {
      socket = new DatagramSocket();
      return true;
    } catch (SocketException e) {
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
    String message = String.format("CHANGE %d %d %b", nodeId, actuatorId, isOn);
    byte[] buffer = message.getBytes();
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);

    try {
      socket.send(packet);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  // You may also want to add a method to close the socket when done
  public void close() {
    if (socket != null && !socket.isClosed()) {
      socket.close();
    }
  }
}

