/*
Functions for networking
Project Social GreenHouse
*/

void parseCommand(String command)
{
 if(command.substring(0, 6) == "server") //server 10.210.5.250:9999$
 {
   int delimeter = command.indexOf(':', 0);
   char ip[17];
   command.substring(7, delimeter).toCharArray(ip, 17);
   char port[5];
   command.substring(delimeter+1).toCharArray(port, 5);
   
   Serial.println(ip);
   Serial.println(port);
   
   connectToServer(ip, port);
 }
 
 else if(command.substring(0, 7) == "connect") //connect <SSID>+<WPA/OPEN>+<PASS>$ 
 {
   int delimeter = command.indexOf('+', 0);
   char ssid[33];
   command.substring(8,delimeter).toCharArray(ssid, 33);
   
   int delimeter1 = command.lastIndexOf('+');
   String connection = command.substring(delimeter+1, delimeter1);
   
   char pass[64];
   command.substring(delimeter1+1).toCharArray(pass, 64);
   
   Serial.println(ssid);
   Serial.println(connection);
   Serial.println(pass);
   
   if(connection == "OPEN")
   {
     connectToNetworkOpen(ssid);
   }
   else if(connection == "WPA")
   {
     connectToNetworkWPA(ssid,pass);
   }
 }
 
 else if(command.substring(0, 8) == "openconn")
 {
   wifi.SendCommandSimple("open", "AOK");
   wifi.SendCommandSimple("exit", "AOK");
   delay(2000); 
 }
 
 else if(command.substring(0, 9) == "closeconn")
 {
   wifi.SendCommandSimple("close", "AOK");
   wifi.SendCommandSimple("exit", "AOK");
   delay(2000); 
 }
 
 else if(command.substring(0, 5) == "debug")
 {
   int REQUEST_BUFFER_SIZE = 180;
   char bufRequest[REQUEST_BUFFER_SIZE];
   Serial << F("IP: ") << wifi.getIP(bufRequest, REQUEST_BUFFER_SIZE) << endl
    << F("Netmask: ") << wifi.getNetMask(bufRequest, REQUEST_BUFFER_SIZE) << endl
    << F("Gateway: ") << wifi.getGateway(bufRequest, REQUEST_BUFFER_SIZE) << endl
    << F("DNS: ") << wifi.getDNS(bufRequest, REQUEST_BUFFER_SIZE) << endl 
    << F("RSSI: ") << wifi.getRSSI(bufRequest, REQUEST_BUFFER_SIZE) << endl
    << F("SSID: ") <<  wifi.getSSID(bufRequest, REQUEST_BUFFER_SIZE) << endl
    << F("battery: ") <<  wifi.getBattery(bufRequest, REQUEST_BUFFER_SIZE) << endl;
    
    wifi.println("exit");
 }
 
 else
 {
   clearBuf();
   command.toCharArray(buf, BUFFER_SIZE);
   Serial.print("Sending: ");
   Serial.println(buf);
   wifi.print("#");
   wifi.println(buf); 
 }
 
 clearBuf();
}

void wifiSaveAndBoot()
{
 wifi.SendCommandSimple("save", "AOK");
 delay(100);
 wifi.SendCommandSimple("reboot", "AOK");
 delay(100);
 wifi.SendCommandSimple("load", "AOK");
 delay(100);
 wifi.SendCommandSimple("exit", "AOK");
}

void connectToNetworkOpen(char* ssid)
{
 wifi.SendCommandSimple("leave","AOK");
  
 clearBuf();
 
 strcpy(buf, "set wlan ssid ");
 strcat(buf, ssid);

 wifi.SendCommandSimple(buf, "AOK");  
 delay(100);
 wifi.SendCommandSimple("set wlan auth 0", "AOK");
 delay(100);
 wifiSaveAndBoot();
 delay(100);
 wifi.SendCommandSimple("join", "AOK");
 delay(100);
 wifi.SendCommandSimple("exit","AOK");
 
 Serial.println("done");
}

void connectToNetworkWPA(char* ssid, char* pass)
{
 wifi.SendCommandSimple("leave","AOK");
  
 clearBuf();
 
 strcpy(buf, "set wlan ssid ");
 strcat(buf, ssid);

 wifi.SendCommandSimple(buf, "AOK");  
 delay(100);
 wifi.SendCommandSimple("set wlan auth 4", "AOK");
 delay(100);
 
 clearBuf();
 
 strcpy(buf, "set wlan phrase ");
 strcat(buf, pass);

 wifi.SendCommandSimple(buf, "AOK");  
 
 delay(100);
 wifiSaveAndBoot();
 delay(100);
 wifi.SendCommandSimple("join", "AOK");
 delay(100);
 wifi.SendCommandSimple("exit","AOK");

 Serial.println("done");
}

void connectToServer(char *ip, char *port)
{

 clearBuf();
 
 strcpy(buf, "set ip host ");
 strcat(buf, ip);
 
 delay(30);
 wifi.SendCommandSimple(buf, "AOK");
 delay(30);
 clearBuf();
 
 strcpy(buf, "set ip remote ");
 strcat(buf, port);
 
 wifi.SendCommandSimple(buf, "AOK");
 delay(30);
 wifi.println("exit");
 
 clearBuf();
 
 wifiSaveAndBoot();
}

void clearBuf()
{
 bufCounter = 0;
 for(int i = 0; i < BUFFER_SIZE; i ++)
 {
  buf[i] = 0x00; 
 } 
}
