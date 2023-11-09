package no.ntnu.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * A different attempt at a client.
 */
public class EchoClient {
  private final DatagramSocket socket;
  private final InetAddress address;

  private byte[] buffer;
  private static final int BUFFER_SIZE = 1024;
  private static final int TIMEOUT_MS = 5000; // 5 seconds.

  public EchoClient(String serverAddress, int serverPort) {
    try {
      socket = new DatagramSocket();
      address = InetAddress.getByName(serverAddress);
      socket.connect(address, serverPort);
      socket.setSoTimeout(TIMEOUT_MS);
    } catch (SocketException | UnknownHostException e) {
      throw new RuntimeException("Failed to initialize the client", e);
    }
  }

  /**
   * Sends a message to the server, and returns the response.
   *
   * @param msg message being sent.
   *
   * @return server response.
   *
   * @throws IOException input/output exception.
   */
  public String sendAndReceive(String msg) {
    this.buffer = msg.getBytes();
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

    try {
      socket.send(packet);
    } catch (IOException e) {
      System.err.println("Error sending packet: " + e.getMessage());
      throw new RuntimeException(e);
    }

    packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);

    try {
      socket.receive(packet);
    } catch (SocketTimeoutException e) {
      System.err.println("Timeout while waiting for response: " + e.getMessage());
      throw new RuntimeException("No response received before timeout.", e);
    } catch (IOException e) {
      System.err.println("Error receiving packet: " + e.getMessage());
      throw new RuntimeException(e);
    }
    return new String(packet.getData(), 0, packet.getLength());
  }


  /**
   * Shuts down the socket.
   */
  public void close() {
    //Systems have finite resources, so closing a socket when done with it is important!
    socket.close();
  }
}
