import java.io.*;
import java.util.*;

/*
ClientOutput
thread handling client socket output and user input for SimpleTCPClient
*/

public class ClientOutput extends Thread {
  private SimpleTCPClient client;
  private PrintWriter out;
  private BufferedReader userIn;

  // Constructor
  ClientOutput(SimpleTCPClient client) {
    this.client = client;
    this.out    = client.getOutput();
    this.userIn = new BufferedReader(new InputStreamReader(System.in));

  }

  // Getters
  /**
  * @return the client
  */
  public SimpleTCPClient getClient() {
    return client;
  }

  /**
  * @return the out
  */
  public PrintWriter getOutput() {
    return out;
  }

  /**
  * @return the userIn
  */
  public BufferedReader getUserIn() {
    return userIn;
  }


  @Override
  public void run() {
    String msgSent = "";

    try {
      while ( ! msgSent.equals(".bye") ) {
        // reading from system.in can block the thread
        // Watching for actual input before sending
        // During sleep, thread can be interruped
        // If server disconnect, then client dies too.
        System.out.print("> ");
        while ( ! userIn.ready() ) {
          Thread.sleep(250);
        }
        msgSent = userIn.readLine();
        this.out.println(msgSent);
        this.out.flush();
      }

    } catch (InterruptedException e) {
      //e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      this.client.close();
    }
  }


}
