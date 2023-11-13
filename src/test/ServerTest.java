import no.ntnu.controlpanel.UdpCommunicationChannel;
import no.ntnu.server.Server;
import no.ntnu.server.ServerMessageListener;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServerTest {

  //TODO: PACKET IS STILL NULL, WHY? Fix this.
  @Test
  void testServerInitialization() {
    UdpCommunicationChannel mockChannel = Mockito.mock(UdpCommunicationChannel.class);
    ServerMessageListener mockListener = Mockito.mock(ServerMessageListener.class);

    Server server = new Server(12346, mockListener);
    assertEquals(12346, server.getServerPort());
    // Additional assertions as needed
  }


  @Test
  void testServerRun() throws IOException {
    UdpCommunicationChannel mockChannel = Mockito.mock(UdpCommunicationChannel.class);
    ServerMessageListener mockListener = Mockito.mock(ServerMessageListener.class);

    //Server setup:
    Server server = new Server(12346, mockListener);


    String mockMessage = "Test Message";
    byte[] mockData = mockMessage.getBytes(StandardCharsets.UTF_8);
    InetAddress mockAddress = InetAddress.getByName("localhost");
    int mockPort = 12345;

    //DatagramPacket here:
    DatagramPacket mockPacket = new DatagramPacket(mockData, mockData.length, mockAddress, mockPort);

    //Mockito setup:
    when(mockChannel.receivePacket()).thenReturn(mockPacket);

    // Start the server in a separate thread
    new Thread(server).start();

    // Additional logic to simulate a client sending a packet and verifying the server's response

    // Shut down the server for clean-up
    server.shutdown();
  }

  @Test
  public void testShutdown() throws InterruptedException {
    UdpCommunicationChannel mockChannel = Mockito.mock(UdpCommunicationChannel.class);
    ServerMessageListener mockListener = Mockito.mock(ServerMessageListener.class);

    Server server = new Server(12346, mockListener);
    server.setCommunicationChannel(mockChannel);
    server.start();
    server.shutdown();
    server.join();

    // Verify that the channel's closeSocket method was called
    verify(mockChannel).closeSocket();
  }

  /**
   * Checks to see if the server shuts down as it is supposed to.
   */
  @Test
  void testServerShutdown() {
    ServerMessageListener mockListener = Mockito.mock(ServerMessageListener.class);
    Server server = new Server(12346, mockListener);
    server.shutdown();
    assertFalse(server.isRunning());
  }
}
