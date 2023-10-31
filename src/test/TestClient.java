import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class TestClient {

  private static final int SERVER_PORT = 4445;
  private static final String SERVER_ADDRESS = "localhost"; // Or your server's IP

  public static void main(String[] args) {
    try (DatagramSocket socket = new DatagramSocket()) {
      byte[] buffer;

      //Tests commands.
      String command = "echo Hello Server!";
      buffer = command.getBytes(StandardCharsets.UTF_8);
      DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(SERVER_ADDRESS), SERVER_PORT);
      socket.send(packet);

      //Response.
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

