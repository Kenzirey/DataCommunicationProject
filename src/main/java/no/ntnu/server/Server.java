package no.ntnu.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/**
 * A different attempt at UDP server.
 */
public class Server extends Thread {
  //TODO: Make sure threads work as intended.

  private static final int SERVER_PORT = 4445;
  private DatagramSocket socket;
  private boolean running;
  private byte[] buffer = new byte[1024];
  private static String serverStopping= "Server is shutting down..";

  public static void main(String[] args) {
    final Server server = new Server(SERVER_PORT);
    Thread serverThread = new Thread(server);
    serverThread.start();
    System.out.println("UDP Server is up and running on port " + SERVER_PORT);

    //Registering a shutdown hook to ensure graceful shutdown.
    //TODO: implement a better shutdown method.
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println(serverStopping);
      server.shutdown();
    }));

  }

  public Server(int port) {
    try {
      socket = new DatagramSocket(port);
    } catch (SocketException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    running = true;

    //TODO: look into who sends what.
    while (running) {
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
      try {
        socket.receive(packet);
        String received = new String(packet.getData(), 0, packet.getLength());
        handleRequest(received, packet);
      } catch (IOException e) {
        // If socket is closed and server is no longer running, breaks the while(running) loop.
        if (socket.isClosed() && !running) {
          break;
        }
        System.err.println("Error processing packet: " + e.getMessage());
      }
    }
    socket.close();
    System.out.println("Server has been shut down.");
  }



  private void handleRequest(String message, DatagramPacket packet) throws IOException {
    if ("end".equals(message)) {
      byte[] responseData = serverStopping.getBytes(StandardCharsets.UTF_8);
      DatagramPacket responseDatagram = new DatagramPacket(responseData, responseData.length,
              packet.getAddress(), packet.getPort());
      socket.send(responseDatagram);
      running = false;
      return;
    }

    // For all other commands, use DatagramHandler.
    DatagramHandler handler = new DatagramHandler(socket, packet);
    handler.run();
  }



  /**
   * Graceful shutdown.
   * Will stop accepting new requests, and wait for the current ones to finish.
   */
  public void shutdown() {
    running = false;
    socket.close();
    System.out.println(serverStopping);
  }
}
