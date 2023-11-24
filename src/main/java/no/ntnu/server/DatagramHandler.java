package no.ntnu.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import no.ntnu.command.*;


/**
 * Handles incoming datagrams from the client.
 */
public class DatagramHandler {
  private final DatagramPacket clientDatagram;
  private final DatagramSocket dataSocket;
  private final Map<String, Command> commandRegistry;

  /**
   * Processes the command(s) from the client's datagram.
   */
  public void run() {
    String command = extractClientCommand();
    handleCommand(command);
  }

  /**
   * Initializes a new instance of the DatagramHandler class,
   * with UDP socket and client.
   *
   * @param udpSocket the UDP socket used for sending and receiving data.
   *
   * @param clientDatagram the incoming datagram from the client.
   */
  public DatagramHandler(DatagramSocket udpSocket, DatagramPacket clientDatagram) {
    this.dataSocket = udpSocket;
    this.clientDatagram = clientDatagram;

    this.commandRegistry = new HashMap<>();
    //Add commands to the command registry here.
    commandRegistry.put("version", new VersionCommand());
    commandRegistry.put("echo", new EchoCommand());
    commandRegistry.put("name", new NameCommand());
    commandRegistry.put("date", new DateCommand());
    commandRegistry.put("time", new TimeCommand());
    commandRegistry.put("change", new ChangeStateCommand());
  }

  /**
   * Processes the command string, identifying the type of command.
   *
   * @param command the command string from the client.
   */
  private void handleCommand(String command) {
    System.out.println("Command from the client: " + command);

    String[] commandParts = command.split(" ", 2);
    if (commandParts.length >= 1) {
      String commandType = commandParts[0].toLowerCase();
      Command cmd = commandRegistry.get(commandType);
      if (cmd != null) {
        String response = cmd.execute(Arrays.copyOfRange(commandParts, 1, commandParts.length));
        sendResponseToClient(response);
      } else {
        sendResponseToClient("Unknown command: " + commandType);
      }
    } else {
      sendResponseToClient("No command received.");
    }
  }

  /**
   * Sends a response to the client.
   *
   * @param response the response sent to the client.
   */
  private void sendResponseToClient(String response) {
    byte[] responseData = response.getBytes(StandardCharsets.UTF_8);
    DatagramPacket responseDatagram = new DatagramPacket(responseData, responseData.length,
            clientDatagram.getAddress(), clientDatagram.getPort());
    try {
      dataSocket.send(responseDatagram);
    } catch (Exception e) {
      System.err.println("Error while sending response to client: " + e.getMessage());
    }
  }


  /**
   * Extracts the command from the client datagram.
   *
   * @return the extracted command string from the client datagram.
   */
  private String extractClientCommand() {
    return new String(clientDatagram.getData(), 0, clientDatagram.getLength(),
            StandardCharsets.UTF_8);
  }
}
