package com.kohls.ibm.ocf.pca.util;
 
import java.io.InputStream;
import java.io.OutputStream;

import com.yantra.yfc.rcp.YRCPlatformUI;
 
/**
* 
 * @author Srikant Telikepalli This class has necessary methods to get weight
*         from the scale
*/
public class KOHLSWtScaleDriver {
 
     /**
     * Define class variables
     */
     InputStream in;
     OutputStream out;
     String weight = "";
     String serialLinkPath = null;
     String portName = null;
     
     /**
     * Get Weight Over the Serial Port
     * 
      * @param None
     * @return Weight as a string
     */
 
     public String getWeight() {/*
           SerialPort serialPort = null;
           try {
//              getConfig();
                serialLinkPath = System.getProperty("weighscaleport");
                YRCPlatformUI.trace("weighscaleport property initialized : " + serialLinkPath);
                // Set linkpath to default in case it is null in config file
                if (serialLinkPath == null) {
                     serialLinkPath = "/dev/lowes/scales";
                     YRCPlatformUI.trace("Setting Default Link Path");
                }
                // Get actual path from the link
                if (serialLinkPath.matches("COM[0-9][0-9]")){
                     YRCPlatformUI.trace("46 Non Windows Port Specified. Creating File...");
                     File serialLinkFile = new File(serialLinkPath);
                     YRCPlatformUI.trace("48 Getting Canonical Path...");
                     portName = serialLinkFile.getCanonicalPath();
                }
                else{
                     YRCPlatformUI.trace("52 Windows Port Specified...");
                     portName = serialLinkPath;
                }
                YRCPlatformUI.trace("portName: " + portName);
//              String portName = serialLinkPath;
                YRCPlatformUI.trace("49 ...");
                CommPortIdentifier portIdentifier = CommPortIdentifier
                           .getPortIdentifier(portName);
                YRCPlatformUI.trace("52 ...");
 
                // Make sure the port is currently not in use
                if (portIdentifier.isCurrentlyOwned()) {
                     YRCPlatformUI.trace("Error: Port is currently in use");
                } else {
                     // Open the serial Port
                     YRCPlatformUI.trace("59 ...");
                     CommPort commPort = portIdentifier.open(this.getClass()
                                .getName(), 2000);
 
                     // Make sure its a serial port
                     if (commPort instanceof SerialPort) {
                           // Set serial port parameters for communication
                           YRCPlatformUI.trace("66 ...");
                           serialPort = (SerialPort) commPort;
                           YRCPlatformUI.trace("67 ...");
                           serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
                                     SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                           YRCPlatformUI.trace("71 ...");
                           serialPort.enableReceiveTimeout(10000);
 
                           // Set the input and output streams
                           YRCPlatformUI.trace("75 ...");
                           if(serialPort == null){
                                YRCPlatformUI.trace("77 ... serialPort is null ...");
                           }
                           in = serialPort.getInputStream();
                           out = serialPort.getOutputStream();
 
                           // Write the weight command to serial port
                           YRCPlatformUI.trace("83 ...");
                           writeToSerialPort(out, "W");
 
                           // Read response
                           YRCPlatformUI.trace("87 ...");
                           readFromSerialPort(in);
                     } else {
                           YRCPlatformUI.trace("Error: Only serial ports are handled by this example.");
                     }
                }
           } catch (Exception e) {
                YRCPlatformUI.trace("94 ...");
                YRCPlatformUI.trace("Error: " + e);
                YRCPlatformUI.trace("Error: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("Serial Comm Error "
                           + e.getClass().toString() + " " + e.getMessage());
           } finally {
                try {
                     // Close all streams and serial ports
                     YRCPlatformUI.trace("101 ...");
                     in.close();
                     out.close();
                     serialPort.close();
                } catch (IOException e) {
                     YRCPlatformUI.trace("Error: " + e);
                     YRCPlatformUI.trace("Error: " + e.getMessage());
                     throw new RuntimeException("Serial Comm Finally Error "
                                + e.getClass().toString() + " " + e.getMessage());
                }
           }
           YRCPlatformUI.trace("110 ...");
           YRCPlatformUI.trace("returning weight :" + weight);
           return weight;
     */
    	 return "0";
    	 }
 
     /**
     * Read weight from serial port
     * 
      * @param in
     *            - InputStream to read from
     * @return None
     */
     public void readFromSerialPort(InputStream in) {
 
           YRCPlatformUI.trace("124 ...");
           byte[] buffer = new byte[1024];
           int len = -1;
           String tmp;
           try {
                while ((len = in.read(buffer)) > -1) {
                     if ((tmp = new String(buffer, 0, len)).endsWith("\r")) {
                           weight = weight + tmp;
                           break;
                     } else {
                           weight = weight + tmp;
                     }
                }
           } catch (Exception e) {
                YRCPlatformUI.trace("138 ...");
                throw new RuntimeException("Serial Comm Read Error "
                           + e.getClass().toString() + " " + e.getMessage());
           }
           if(weight != null){
                YRCPlatformUI.trace("149 ...");
                YRCPlatformUI.trace("weight before : " + weight);
//              weight.replaceAll("\\W", "").trim();
 
                char[] weightChar = weight.toCharArray();
                StringBuffer newstring = new StringBuffer();
                for(int i = 0; i < weightChar.length; i++){
                     YRCPlatformUI.trace("character ... " + weightChar[i]);
                     if (weightChar[i] >= '0' && weightChar[i] <= '9'){
                           newstring.append(weightChar[i]);
                     }else if (weightChar[i] == '.'){
                           newstring.append(weightChar[i]);
                     }else{
                           YRCPlatformUI.trace("ignoring ... " + weightChar[i]);
                     }
                }
                
                weight = newstring.toString();
                YRCPlatformUI.trace("weight after : " + weight);
                YRCPlatformUI.trace("124 ...");
           }
     }
 
     /**
     * Write weight command to serial port
     * 
      * @param out
     *            - Output Stream to write to
     * @param command
     *            - command to be written to serial port
     * @return None
     */
     public void writeToSerialPort(OutputStream out, String command) {
           try {
                YRCPlatformUI.trace("156 ...");
                out.write(command.getBytes());
           } catch (Exception e) {
                throw new RuntimeException("Serial Comm Write Error "
                           + e.getClass().toString() + " " + e.getMessage());
           }
     }
 
     /**
     * Get Config Info from file and populate serial port link path
     * 
      * @param None
     * @return None
     */
//   public void getConfig() {
//         DataInputStream inStr = null;
//         try {
//              // Open the file
//              FileInputStream fileInStr = new FileInputStream(
//                         "config/serialConfig.properties");
//
//              // Get the object of DataInputStream
//              inStr = new DataInputStream(fileInStr);
//              BufferedReader inBufRdr = new BufferedReader(new InputStreamReader(
//                         inStr));
//              String strLine;
//
//              // Read File Line By Line
//              while ((strLine = inBufRdr.readLine()) != null) {
//
//                   // Update the serial link path
//                   if (strLine.contains("SERIALLINKPATH")) {
//                         serialLinkPath = strLine.split("\\=")[1];
//                   }
//
//              }
//
//         } catch (Exception e) {// Catch exception if any
//              throw new RuntimeException("getConfig Error "
//                         + e.getClass().toString() + " " + e.getMessage());
//         } finally {
//              try {
//                   // Close the input stream
//                   inStr.close();
//              } catch (Exception e) {
//                   throw new RuntimeException("getConfig Finally Error "
//                              + e.getClass().toString() + " " + e.getMessage());
//              }
//
//         }
//
//   }
}