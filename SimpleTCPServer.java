import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

/*
SimpleTCPServer
A simple TCP server with:
-control of queue;
-limitation of the number of thread (established connections);
-and broadcast messaging abilities.
*/

public class SimpleTCPServer {
  private ServerSocket srvSocket;
  private ArrayList<SimpleService> runningSimpleServices;
  private Semaphore availableThread;

  // Constructor
  public SimpleTCPServer() {
    try {
      // 100 max in queue
      this.srvSocket              = new ServerSocket(2702, 100, InetAddress.getLoopbackAddress());
      this.runningSimpleServices  = new ArrayList<SimpleService>();
      // 50 max established connection
      this.availableThread        = new Semaphore(50);


      while ( true ) {
        this.acceptClient();
      }

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      this.close();
    }
  }


  public SimpleTCPServer(ServerSocket srvSocket) {
    try {
      this.srvSocket              = srvSocket;
      this.runningSimpleServices  = new ArrayList<SimpleService>();
      this.availableThread        = new Semaphore(50);

      while ( true ) {
        this.acceptClient();
      }

    } finally {
      this.close();
    }
  }

  public SimpleTCPServer(ServerSocket srvSocket, int maxClientThreads) {
    try {
      this.srvSocket              = srvSocket;
      this.runningSimpleServices  = new ArrayList<SimpleService>();
      this.availableThread        = new Semaphore(maxClientThreads);

      while ( true ) {
        this.acceptClient();
      }

    } finally {
      this.close();
    }
  }

  // Getters
  /**
  * @return the srvSocket
  */
  public ServerSocket getSrvSocket() {
    return this.srvSocket;
  }

  /**
  * @return the runningSimpleServices
  */
  public ArrayList<SimpleService> getRunningSimpleServices() {
    return runningSimpleServices;
  }

  /**
  * @return the availableThread
  */
  public Semaphore getAvailableThread() {
    return this.availableThread;
  }


  // Method
  public void acceptClient() {
    try {
      // Making sure that not too many client are already connected
      this.availableThread.acquire();

      // TCP State Established
      Socket simpleServiceSocket = this.srvSocket.accept();

      // Making it a Thread
      SimpleService ss = new SimpleService(simpleServiceSocket, this);

      // Adding ch to list
      this.runningSimpleServices.add(ss);

      // Invoking start() method to launch the thread
      ss.start();

      // Not a good idea to mix printing out and handling in the same method
      broadcastMessage(ss.getSocket().getRemoteSocketAddress().toString() + " connected.", ss);
      ss.getOutput().println("enter '.bye' to quit.");
      ss.getOutput().flush();

    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void release(SimpleService ss) {
    // Remove ch from list
    this.runningSimpleServices.remove(ss);

    // Release a permit
    this.availableThread.release();
  }

  public void broadcastMessage(String msg, SimpleService senderService) {
    for (SimpleService ss : this.runningSimpleServices) {
      if ( senderService != ss ) {
        ss.getOutput().println(msg);
        ss.getOutput().flush();
      }
    }
  }

  public void close() {
    try {
      if (this.srvSocket != null) {
        this.srvSocket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  // Main
  public static void main(String[] args) {
    try {
      ServerSocket srvSocketTest = new ServerSocket(2702, 5, InetAddress.getLoopbackAddress());
      new SimpleTCPServer(srvSocketTest, 5);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}
