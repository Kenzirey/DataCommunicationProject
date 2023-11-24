import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.server.Server;

import java.io.IOException;
import java.util.Scanner;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * A dummy client for testing various commands.
 * Sends a command to the server, and prints the response. Makes sure the server is running correctly.
 */
public class MainClientTester {

  static Server server;
  private static final String SERVER_ADDRESS = "localhost";
  private static final int SERVER_PORT = 12346;

  public static void main(String[] args) {



    // using scanner to get keyboard input
    Scanner scanner = new Scanner(System.in);
    sendCommandToServer("time");

    // loop which is permanently true til exit
    while (true) {
      System.out.print("Enter command (or 'exit' to quit): ");
      String command = scanner.nextLine();

      if ("exit".equalsIgnoreCase(command)) {
        System.out.println("exited the scanner");
        break;
      }
      System.out.println("test");
      sendCommandToServer(command);
      System.out.println(command + " sent");
    }

    scanner.close();
  }
  private static void sendCommandToServer(String command) {
    try (DatagramSocket socket = new DatagramSocket()) {
      byte[] buffer = command.getBytes(StandardCharsets.UTF_8);
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT);
      socket.send(packet);

      // Receive response
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

