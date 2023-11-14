import no.ntnu.controlpanel.UdpCommunicationChannel;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.server.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ServerTest {
  Server server;
  @BeforeEach
    void setUp() {
    this.server = new Server(12346, new GreenhouseSimulator(false));
    }



  //TODO: PACKET IS STILL NULL FFFFFFFFFFFFFFFFFF.
  @Test
  void testServerInitialization() throws InterruptedException {
    this.server.start();
    //Makes sure port is correctly displayed/gathered.
    assertEquals(12346, server.getServerPort());
    //Makes sure the server is actually running.
    synchronized(server) {
      server.wait(20);
    }
    assertTrue(server.isRunning());

    //Shutdown after test.
    server.shutdown();
  }

  @Test
  void testServerRun() throws IOException, InterruptedException {
    UdpCommunicationChannel mockChannel = Mockito.mock(UdpCommunicationChannel.class);

    //Server setup:
    server.setCommunicationChannel(mockChannel);
    server.start();
    synchronized(server) {
      server.wait(20);
    }
    assertTrue(server.isRunning());


    //Mock messages & data.
    String mockMessage = "Test Message";
    byte[] mockData = mockMessage.getBytes(StandardCharsets.UTF_8);
    InetAddress mockAddress = InetAddress.getByName("localhost");
    int mockPort = 12345;

    //DatagramPacket here:
    DatagramPacket mockPacket = new DatagramPacket(mockData, mockData.length, mockAddress, mockPort);

    //Mockito setup:
    when(mockChannel.receivePacket()).thenReturn(mockPacket);

    //Shutdown server after test is done.
    server.shutdown();
    synchronized(server) {
      server.wait(20);
    }
    server.join();
    assertFalse(server.isRunning());
  }

  /**
   * Tests the shutdown method,
   * using synchronized to make sure the server is running before the assertion is made.
   *
   * @throws InterruptedException if the thread is interrupted.
   */
  @Test
  void testShutdown() throws InterruptedException {
    server.start();

    //Synchronized to make sure the server is running before the assertion is made.
    synchronized(server) {
      server.wait(20);
    }
    assertTrue(server.isRunning());

    server.shutdown();
    //To make sure shutdown is run before next assertion.
    synchronized(server) {
      server.wait(20);
    }
    server.join();
    assertFalse(server.isRunning());
  }
}
