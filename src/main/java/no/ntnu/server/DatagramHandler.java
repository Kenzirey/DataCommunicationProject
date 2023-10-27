package no.ntnu.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;

public class DatagramHandler {
  private final DatagramPacket clientDatagram;
  private final DatagramSocket dataSocket;
  public void run() {
    String command = extractClientCommand();
    handleCommand(command);

  }

  private void handleCommand(String command) {
    System.out.println("Command from the client: " + command);

    String response;

    String[] commandParts = command.split(" ", 2);
    if (commandParts.length >= 1) {
      String commandType = commandParts[0];
      switch (commandType) {
        case "version":
          response = getVersionResponse();
          break;
        case "name":
          response = getNameResponse();
          break;
        case "time":
          response = getTimeResponse();
          break;
        case "date":
          response = getDateResponse();
          break;
        case "echo":
          response = getEchoResponse(commandParts);
          break;
        default:
          response = "Unknown command: " + commandType;
      }
    } else {
      response = "No command received.";
    }

    sendResponseToClient(response);
  }

  private String getDateResponse() {
    return "Current date: " + java.time.LocalDate.now();
  }

  private String getTimeResponse() {
    return "Current time: " + java.time.LocalTime.now();
  }

  private String getNameResponse() {
    return "UDP Server";
  }

  private String getVersionResponse() {
    return "Server_V0.1";
  }

  private String getEchoResponse(String[] commandParts) {
    String response;
    if (commandParts.length >= 2) {
      response = commandParts[1];
    } else {
      response = "No text to echo";
    }
    return response;
  }

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

  public DatagramHandler(DatagramSocket udpSocket, DatagramPacket clientDatagram) {
    this.dataSocket = udpSocket;
    this.clientDatagram = clientDatagram;
  }

  private String extractClientCommand() {
    return new String(clientDatagram.getData(), 0, clientDatagram.getLength(),
            StandardCharsets.UTF_8);
  }
}
