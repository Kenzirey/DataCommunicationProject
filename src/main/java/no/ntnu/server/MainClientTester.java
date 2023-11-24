package no.ntnu.server;

import no.ntnu.greenhouse.GreenhouseSimulator;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * A dummy client for testing various commands.
 * Sends a command to the server, and prints the response. Makes sure the server is running correctly.
 */
public class MainClientTester {

  static Server server;
  private static final String SERVER_ADDRESS = "localhost";

  public static void main(String[] args) {

    server = new Server(12346); // Adjust the port and parameters as needed
    server.start();
    System.out.println("Server started.");
    synchronized (server) {
      while (!server.isReady()) {
        try {
          server.wait(); // Wait until the server calls notifyAll()
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

    sendCommandToServer("time");

    sendCommandToServer("name");
  }



  private static void sendCommandToServer(String command) {
    try (DatagramSocket socket = new DatagramSocket()) {
      byte[] buffer;

      buffer = command.getBytes(StandardCharsets.UTF_8);
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(SERVER_ADDRESS), server.getServerPort());
      socket.send(packet);

      // Response
      byte[] receiveBuffer = new byte[1024];
      DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
      socket.receive(receivePacket);

      String received = new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8);
      System.out.println("Received: " + received);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
