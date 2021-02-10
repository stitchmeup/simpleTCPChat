import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.*;

/*
SimpleTCPClient
A Simple TCP Client
*/

public class SimpleTCPClient {
  private Socket s;
  private PrintWriter out;
  private BufferedReader in;
  private ClientOutput threadOut;
  private ClientInput threadIn;
  private final ReentrantLock lock = new ReentrantLock();

  // Constructor
  SimpleTCPClient(InetAddress addr, int port) {
    try {
      System.out.println("connecting to " + addr + " on port " + port + "...");
      this.s    = new Socket(addr, port);
      if (this.s.isConnected()){
        System.out.println("connected.");
        this.out  = new PrintWriter(this.s.getOutputStream());
        this.in   = new BufferedReader(new InputStreamReader(this.s.getInputStream()));

        this.threadOut = new ClientOutput(this);
        this.threadIn   = new ClientInput(this);

        threadOut.start();
        threadIn.start();
      } else {
        System.out.println("connection failed.\nclosing client...");
        this.close();
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  // Getters
  /**
  * @return the s
  */
  public Socket getSocket() {
    return s;
  }

  /**
  * @return the in
  */
  public BufferedReader getInput() {
    return in;
  }

  /**
  * @return the out
  */
  public PrintWriter getOutput() {
    return out;
  }

  /**
  * @return the threadIn
  */
  public ClientInput getThreadIn() {
    return threadIn;
  }

  /**
  * @return the threadOut
  */
  public ClientOutput getThreadOut() {
    return threadOut;
  }


  // Method
  public void close() {
    try {
      // We don't want both thread to call close().
      this.lock.lockInterruptibly();

      System.out.println("closing client...");
      
      if (this.threadIn != null) {
        if (this.threadIn.getInput() != null) {
          this.threadIn.getInput().close();
        }
        this.threadIn.interrupt();
      }

      if (this.threadOut != null) {
        if (this.threadOut.getUserIn() != null) {
          this.threadOut.getUserIn().close();
        }
        this.threadOut.interrupt();
      }

      if (this != null) {
        this.s.close();
      }

      if (this.in != null) {
        this.in.close();
      }

      if (this.out != null) {
        this.out.close();
      }
      System.out.println("done.");
    } catch (InterruptedException e) {
      //e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  // Main
  public static void main(String[] args) {
    new SimpleTCPClient(InetAddress.getLoopbackAddress(), 2702);
  }


}
