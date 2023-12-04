import no.ntnu.client.EchoClient;
import no.ntnu.controlpanel.UdpCommunicationChannel;
import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.server.Server;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * Tests the old Server class (before UDPCommChannel implementation).
 */
class ServerTest {
  Server server;
  EchoClient client = new EchoClient("localhost", 12348);
  @BeforeEach
    void setUp() {
    this.server = new Server(12348);
    }

  @AfterEach
    void tearDown() throws InterruptedException {
    if (server.isRunning()) {
      server.shutdown();
      server.join();
    }
    }


  //TODO: PACKET IS STILL NULL FFFFFFFFFFFFFFFFFF.
  @Test
  void testServerInitialization() throws InterruptedException {
    if (!server.isRunning()) {
      this.server.start();
    }
    //Makes sure port is correctly displayed/gathered.
    // assertEquals(12348, server.getServerPort());
    //Makes sure the server is actually running.
    synchronized(server) {
      server.wait(20);
    }
    assertTrue(server.isRunning());

    //Shutdown after test.z
    server.shutdown();
  }

  @Test
  void sendAndReceivePackets() {
    this.server.start();
    String echo = client.sendAndReceive("name");
    assertEquals("UDP Server", echo);
    echo = client.sendAndReceive("server is working");
    assertNotEquals("test", echo);
  }

  /**
   * Tests the shutdown method,
   * using synchronized to make sure the server is running before the assertion is made.
   *
   * @throws InterruptedException if the thread is interrupted.
   */
  @Test
  void testShutdown() throws InterruptedException {
    if (!server.isRunning()) {
      server.start();
    }

    synchronized (server) {
      server.wait(20);
    }
    assertTrue(server.isRunning());

    server.shutdown();

    synchronized (server) {
      server.wait(20);
    }
    assertFalse(server.isRunning());
  }

}
