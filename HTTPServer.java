/**
 * HTTP Server, Single Threaded,  starter code.  
 * Usage:  java HTTPServer [port#  [http_root_path]]
 **/

import java.io.*;
import java.net.*;
import java.util.*;

public final class HTTPServer {
    public static int serverPort = 35350;    // default port 35350-35359
    public static String http_root_path = "./";    // rooted default path in your mathlab area

    private static ServerSocket welcomeSocket; //Listening
    private static Socket connectionSocket; //Connect to client
    private static boolean isOK;
    private static String errMsg;
    
    public static void main(String args[]) throws Exception  
    {
        isOK = true;
        errMsg = "";
        String msg;
        
        // Get arguments
	processArgs(args);
        
        if (!isOK) {
            System.err.println(errMsg);
	    System.out.println("usage: java HTTPServer [port_# [http_root_path]]");
	    System.exit(0);
	}

	// create server socket       
        try {
            welcomeSocket = new ServerSocket(serverPort);
        }
        catch (Exception e) {
            isOK = false;
            errMsg = "Error creating server socket: " + e.getMessage();
        }
	// display server stdout message indicating listening
	// on port # with server root path ... 
        if (isOK) 
        {
            msg = "HTTP Server started listening on port " + serverPort +
                    " with server root path at " + http_root_path;
            System.out.println(msg);
        } 
        else 
        {
            System.err.println(errMsg);
            System.exit(1);
        }
	// server runs continuously
	while (true) 
        {
	    try 
            {
		// take a waiting connection from the accepted queue 
                System.out.println("\nListening for connection request...");
                connectionSocket = welcomeSocket.accept();
		// display on server stdout the request origin  
                msg = "Connection accepted, IP:" + 
                        connectionSocket.getInetAddress() +
                        " Port:" + connectionSocket.getPort();
                System.out.println(msg);	

		// create buffered reader for client input
		BufferedReader inFromClient = 
                    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

                // First line = request line
                // Second line = header line(s)
		String requestLine = inFromClient.readLine();
		String requestHeader = inFromClient.readLine();	
                if (requestLine == null || requestLine.equals("") ||
                        requestHeader == null || requestHeader.equals(""))
                {
                    isOK = false;
                    System.out.println("Empty request or header lines");
                }

		/* Read the HTTP request line and display it on Server stdout.
		 * We will handle the request line below, but first, read and
		 * print to stdout any request headers (which we will ignore).
		 */
                System.out.println("Request:\n\t" + requestLine);
                // Loop through all header lines
                System.out.println("Headers:");
                do 
                {
                    System.out.println("\t" + requestHeader);
                    requestHeader = inFromClient.readLine();
                } while (requestHeader != null && !requestHeader.equals(""));
                
                if (isOK) 
                {
                    // now back to the request line; tokenize the request
                    StringTokenizer tokenizedLine = new StringTokenizer(requestLine);
                    // process the request
                    if (tokenizedLine.nextToken().equals("GET")) 
                    {    
                        // parse URL to retrieve file name
                        if (tokenizedLine.hasMoreTokens()) 
                        {
                            String urlName = tokenizedLine.nextToken();

                            if (urlName.startsWith("/") == true )
                                urlName  = urlName.substring(1);

                            generateResponse(urlName, connectionSocket);
                        }
                        else
                            System.out.println("No URL in request");
                    } 
                    else 
                        System.out.println("Bad Request Message");
                }
            } 
            catch (Exception e) 
            {
                System.err.println(e.getMessage());
            }
        }  // end while true 
	
    } // end main

    private static void generateResponse(String urlName, Socket connectionSocket) throws Exception
    {
	// ADD_CODE: create an output stream  

	String fileLoc = "TODO";// ADD_CODE: map urlName to rooted path  
	System.out.println ("Request Line: GET " + fileLoc);

	File file = new File( fileLoc );
	if (!file.isFile())
	{
	    // generate 404 File Not Found response header
	    //outToClient.writeBytes("HTTP/1.0 404 File Not Found\r\n");
            
	    // and output a copy to server's stdout
	    System.out.println ("HTTP/1.0 404 File Not Found\r\n");
	} else {
	    // get the requested file content
	    int numOfBytes = (int) file.length();
	    
	    FileInputStream inFile  = new FileInputStream (fileLoc);
	
	    byte[] fileInBytes = new byte[numOfBytes];
	    inFile.read(fileInBytes);

	    // ADD_CODE: generate HTTP response line; output to stdout
	
	    // ADD_CODE: generate HTTP Content-Type response header; output to stdout

	    // ADD_CODE: generate HTTP Content-Length response header; output to stdout

	    // send file content
	    //outToClient.write(fileInBytes, 0, numOfBytes);
            
	}  // end else (file found case)

	// close connectionSocket
	connectionSocket.close();
    } // end of generateResponse
    
    private static void processArgs(String args[]) 
    {
        // allow the user to choose a different port, as arg[0]  
	// allow the user to choose a different http_root_path, as arg[1] 
	// display error on server stdout if usage is incorrect
        if (args.length > 2) {
            isOK = false;
            errMsg = "Too many arguments.";
        }
        
        if (isOK) {
            if (args.length >= 1) {
                // if at least 1 argument, must have port#
                try {
                    serverPort = Integer.parseInt(args[0]);
                }
                catch (Exception e) {
                    isOK = false;
                    errMsg = "Invalid port number: " + e.getMessage();
                }
                if (2 == args.length) {
                    // 2 args, port and path, no sanity check for path
                    http_root_path = args[1];
                    //Add a "/" to end of path if needs
                    if (!http_root_path.endsWith("/"))
                    {
                        http_root_path += "/";
                    }
                }
            }
        }
    }
    
} // end of class HTTPServer
