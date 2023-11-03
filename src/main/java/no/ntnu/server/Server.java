package no.ntnu.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

/**
 * A UDP server that listens for incoming datagram packets.
 * The server will process the incoming packets,
 * and send a response back to the client.
 */
public class Server extends Thread {
  //TODO: make a shutdown() command or "end"?

  //TODO: implement logic to allow adding multiple clients?
  private static final int SERVER_PORT = 4445;
  private DatagramSocket socket;
  private boolean running;
  private byte[] buffer = new byte[1024];
  private static String serverStopping= "Server is shutting down..";

  public static void main(String[] args) {
    final Server server = new Server(SERVER_PORT);
    //TODO: discuss multithreading.

    //run() is invoked when you call start on a server object.
    //This starts the server loop while(running).
    server.start();
    System.out.println("UDP Server is up and running on port " + SERVER_PORT);

    //Registering a shutdown hook to ensure graceful shutdown.
    //TODO: implement a better shutdown method.
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      System.out.println(serverStopping);
      server.shutdown();
    }));

  }

  /**
   * Constructs a new server instance,
   * which opens a DatagramSocket to listen for incoming packets.
   * @param port the port number to listen on for packets.
   */
  public Server(int port) {
    //This starts up when start() method is called.
    try {
      socket = new DatagramSocket(port);
    } catch (SocketException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Starts server loop and processed incoming datagram packets.
   */
  @Override
  public void run() {
    running = true;

    //This method is called when you use start() on a server object.
    while (running) {
      //Packet to receive data into.
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
      try {
        //Receive packet from client.
        socket.receive(packet);
        //Converts byte data in packet to a String (via default char encoding).
        String received = new String(packet.getData(), 0, packet.getLength());
        //
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
    //TODO: look more into what this does.
    if ("end".equals(message)) {
      //"end" will trigger shutdown for server.
      byte[] responseData = serverStopping.getBytes(StandardCharsets.UTF_8);
      DatagramPacket responseDatagram = new DatagramPacket(responseData, responseData.length,
              packet.getAddress(), packet.getPort());

      //Sends shutdown message back to client.
      socket.send(responseDatagram);
      running = false;
      return;
    }

    //Handles the command received in the packet from client.
    DatagramHandler handler = new DatagramHandler(socket, packet);
    handler.run();
  }



  /**
   * Shuts down the server.
   */
  public void shutdown() {
    running = false;
    socket.close();
    System.out.println(serverStopping);
  }
}
