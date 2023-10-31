package no.ntnu.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class CommandLineTestClient {

  private static final int SERVER_PORT = 4445; // Change this to your server's port
  private static final String SERVER_ADDRESS = "localhost"; // Or your server's IP

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Please provide a command to send. Example: 'echo Hello Server!'");
      return;
    }

    String command = String.join(" ", args);
    sendCommandToServer(command);
  }

  private static void sendCommandToServer(String command) {
    try (DatagramSocket socket = new DatagramSocket()) {
      byte[] buffer;

      buffer = command.getBytes(StandardCharsets.UTF_8);
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT);
      socket.send(packet);

      // Now receive the response
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
