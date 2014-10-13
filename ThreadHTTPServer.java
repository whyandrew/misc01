/**
 *   HTTP Server, Multi Threaded
 *   Usage:  java ThreadHTTPServer [port#  [http_root_path]]
 *
 **/

import java.io.*;
import java.net.*;
import java.util.*;

class HTTPThread implements Runnable {

    // constructor to instantiate the HTTPThread object
    public HTTPThread(Socket connectionSocket, String http_root_path) {
    }

    public void run() {
	// invoke processRequest() to process the client request and then generateResponse()
	// to output the response message
    } 

    private void processRequest(Socket connectionSocket) throws Exception {
	// same as in single-threaded (this code is inline in the starter code)
    }

    private void generateResponse(String urlName, Socket connectionSocket) throws Exception {
	// same as in single-threaded
    }

}

public final class ThreadHTTPServer {
    
    public static void main(String args[]) throws Exception  {
	// process command-line options

	// create a listening ServerSocket
	
	while (true) {
	    // accept a connection

	    // Construct an HTTPThread object to process the accepted connection

	    // Wrap the HTTPThread in a Thread object

	    // Start the thread.
	}
	
    } 

}
