/**
 *   HTTP Server, Multi Threaded
 *   Usage:  java ThreadHTTPServer [port#  [http_root_path]]
 *
 **/

import java.io.*;
import java.net.*;
import java.util.*;

class HTTPThread implements Runnable {
    //instance variables
    private String http_root_path;
    private Socket connectionSocket;
    
    // constructor to instantiate the HTTPThread object
    public HTTPThread(Socket connectionSocket, String http_root_path) {
        this.http_root_path = http_root_path;
        this.connectionSocket = connectionSocket;
    }

    @Override
    public void run() {
	// invoke processRequest() to process the client request and then generateResponse()
	// to output the response message
        try
        {
            Thread.sleep(10);
            processRequest(connectionSocket);
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
        }
    } 

    private void processRequest(Socket connectionSocket) throws Exception {
	// same as in single-threaded (this code is inline in the starter code)
        // create buffered reader for client input
        boolean isOK = true;
        String log = "Log for " + connectionSocket.getInetAddress() + ":" +
                connectionSocket.getPort() + '\n';
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
            log += "Empty request or header lines\n";
        }

        /* Read the HTTP request line and display it on Server stdout.
         * We will handle the request line below, but first, read and
         * print to stdout any request headers (which we will ignore).
         */
        log += "Request:\n\t" + requestLine + "\n";
        // Loop through all header lines
        log += "Headers:\n";
        do 
        {
            log += "\t" + requestHeader + "\n";
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

                    log += generateResponse(urlName, connectionSocket);
                }
                else
                    log += "No URL in request\n";
            } 
            else 
                log += "Bad Request Message\n";
        }
        log += "Connection closed\n";
        log += "________________________________________\n";
        System.out.println(log);
    }

    private String generateResponse(String urlName, Socket connectionSocket) throws Exception {
	// same as in single-threaded
        // create an output stream  
        String log = "";
        DataOutputStream outToClient =
                new DataOutputStream(connectionSocket.getOutputStream());

	String fileLoc = http_root_path + urlName;//map urlName to rooted path  
	log += "\nRequest Line: GET " + fileLoc + "\n";

	File file = new File( fileLoc );
	if (!file.isFile())
	{
	    // generate 404 File Not Found response header
	    outToClient.writeBytes("HTTP/1.0 404 File Not Found\r\n");
            outToClient.writeBytes("\r\n404 File Not Found!");
            
	    // and output a copy to server's stdout
	    log += "HTTP/1.0 404 File Not Found\r\n";
	} else {
	    // get the requested file content
	    int numOfBytes = (int) file.length();
	    
	    FileInputStream inFile  = new FileInputStream (fileLoc);
	
	    byte[] fileInBytes = new byte[numOfBytes];
	    inFile.read(fileInBytes);

	    //generate HTTP response line; output to stdout
            outToClient.writeBytes("HTTP/1.0 200 OK\r\n");
            log += "HTTP/1.0 200 OK\r\n";
	
	    // generate HTTP Content-Type response header; output to stdout
            // generate HTTP Content-Length response header; output to stdout
            String contentType = "text/plain";
            if (fileLoc.contains("."))
            {
                String fileExt = fileLoc.substring(fileLoc.lastIndexOf('.') + 1);
                
                switch (fileExt.toLowerCase())
                {
                    case "html":
                        contentType = "text/html";
                        break;
                    case "css":
                        contentType = "text/css";
                        break;
                    case "js":
                        contentType = "text/javascript";
                        break;
                    case "jpg":
                    case "jpeg":
                        contentType = "image/jpeg";
                        break;
                    case "txt":
                        contentType = "text/plain";
                        break;
                    default: // all unsupported type set to text
                        contentType = "text/plain";
                        break;
                }
            }
            
            outToClient.writeBytes("Content-Length: " + numOfBytes + "\r\n");
            log += "Content-Length: " + numOfBytes + "\r\n";
            outToClient.writeBytes("Content-Type: " + contentType + "\r\n");
	    log += "Content-Type: " + contentType + "\r\n";
            //end of header
            outToClient.writeBytes("\r\n");

	    // send file content
	    outToClient.write(fileInBytes, 0, numOfBytes);
	}  // end else (file found case)

	// close connectionSocket
	connectionSocket.close();
        return log;
    }
}

public final class ThreadHTTPServer {
    
    public static int serverPort = 35350;    // default port 35350-35359
    public static String http_root_path = "C:\\Users\\Andrew\\Documents\\GitHub\\misc01";    // rooted default path in your mathlab area

    private static ServerSocket welcomeSocket; //Listening
    private static Socket connectionSocket; //Connect to client
    
    public static void main(String args[]) throws Exception  {
	// process command-line options
        boolean isOK = true;
        String errMsg;
        String msg;
        
        // Get arguments
	errMsg = processArgs(args);
        if (!errMsg.equals(""))
        {
            isOK = false;
        }
        
        if (!isOK) {
            System.err.println(errMsg);
	    System.out.println("usage: java HTTPServer [port_# [http_root_path]]");
	    System.exit(0);
	}
	// create a listening ServerSocket
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
        
	while (true) {
	    // accept a connection
            try
            {
                System.out.println("________________________________________");
                connectionSocket = welcomeSocket.accept();
                // display on server stdout the request origin  
                msg = "Connection accepted, IP:" + 
                        connectionSocket.getInetAddress() +
                        " Port:" + connectionSocket.getPort();
                System.out.println(msg);
                                    
                // Construct an HTTPThread object to process the accepted connection
                HTTPThread obj = new HTTPThread(connectionSocket, http_root_path);
                // Wrap the HTTPThread in a Thread object
                new Thread(obj).start();
                // Start the thread.
            }
            catch (Exception e)
            {
                System.err.println(e.getMessage());
            }
	}	
    } 

    private static String processArgs(String args[]) 
    {
        // allow the user to choose a different port, as arg[0]  
	// allow the user to choose a different http_root_path, as arg[1] 
	// display error on server stdout if usage is incorrect
        String errMsg = "";
        boolean isOK = true;
        
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
                }
            }
            //Add a "/" to end of path if needs
            if (!http_root_path.endsWith("/"))
            {
                http_root_path += "/";
            }
        }
        return errMsg;
    }
    
}
