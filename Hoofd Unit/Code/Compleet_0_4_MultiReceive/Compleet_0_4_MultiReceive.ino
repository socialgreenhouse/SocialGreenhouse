#include <SoftwareSerial.h>
#include <Streaming.h>
#include <WiFlySerial.h>
#include <Arduino.h>
#include <MsTimer2.h>
#include <HashMap.h>

#define BUFFER_SIZE 100
WiFlySerial wifi(10,11);

char buf[BUFFER_SIZE];
int bufCounter = 0;
//int once = 0;
boolean gate = false, firstData = false;
int mutiplier = 2;
char dataGoed[10];

//define the max size of the hashtable
const byte HASH_SIZE = 50; 
//storage 
HashType<char*,char*> hashRawArray[HASH_SIZE]; 
//handles the storage [search,retrieve,insert]
HashMap<char*,char*> hashMap = HashMap<char*,char*>( hashRawArray , HASH_SIZE ); 
int hashMapCount = 0;

String ids[32];
String vals[32];

void setup()
{
  wifi.begin();
  Serial.begin(9600); // For Debugging
  Serial1.begin(1200); //For 433Mhz receiver
  
  wifi.println("$$$");
  wifi.println("load");
  delay(200);
  wifi.println("join");
  wifi.println("exit");
  
  /*wifi.SendCommandSimple("set wlan auth 0", "AOK");
  wifi.SendCommandSimple("set wlan ssid Stenden", "AOK");
  wifi.SendCommandSimple("set ip dhcp 1", "AOK");
  
  
  wifi.SendCommandSimple("set sys autoconn 0", "AOK");
  wifi.SendCommandSimple("set com idle 0","AOK");
  wifi.SendCommandSimple("set sys sleep 0", "AOK");
  wifi.SendCommandSimple("set sys wake 0", "AOK");
  wifi.SendCommandSimple("set uart mode 2", "AOK");
  
  wifi.SendCommandSimple("set com remote 0", "AOK"); //Set *Hello*  message off
  
  wifi.SendCommandSimple("set ip host 217.122.2.223", "AOK");
  wifi.SendCommandSimple("set ip remote 9999", "AOK");
  
  wifi.SendCommandSimple("save", "AOK");*/
  
  
  delay(1000);
  
  MsTimer2::set(60000,openGate); //Send every Minute
  MsTimer2::start();
  
  Serial.println("Hoofdmodule Gestart");
}

void openGate()
{
  gate = true;    
}

void loop()
{

  if(Serial.available())
  {
     delay(1000);
     bufCounter = 0;
     while(Serial.available())
     {
       char x = Serial.read();
       if(x != 0x24)
       {
         buf[bufCounter] = x;
         bufCounter++;
         Serial.print(x);
       }
       else
       {
         Serial.println();
        parseCommand(buf); 
       }
     }
  }
  
  char data[10];
  int checksum = 0;
  if (Serial1.available())
  {
    //Serial.write(mySerial.read());
   char c = Serial1.read();
   if(c == 'A')
   {
     delay(10 * mutiplier);
     c = Serial1.read();
     int count = 0;
     while(c != 'B' && c < 127 && count < 100)
     {
       c = Serial1.read();
       delay(1 * mutiplier);
       count++;
     }
    if(c == 'B')
    {
      count = 0;
      char c = Serial1.read();
      while(c != 'z' && count < 12)
      {
        if(c != -1)
        {
         Serial.print(c);
         data[count] = c;
         if(count < 10)
         {
           checksum += ((int)c * (count+1));
         }
         count++;
        }
       c = Serial1.read(); 
       delay(4 * mutiplier);
      }
      
      checksum = floor(checksum % 111);
      
      if(data[5] == ':' && data[10] == '-' && count == 12 && checksum == (int)data[11])
      {
        Serial.print(" - Goed");
        String dataString;
        
        for(int i = 0; i<count; i++)
        {
          dataGoed[i] = data[i];
          dataString += String(data[i]); 
        }

        char charId[6];
        String stringId = dataString.substring(0,5);
        dataString.substring(0,5).toCharArray(charId, 6);
        char charVal[5];
        String stringVal = dataString.substring(6,10);
        dataString.substring(6,10).toCharArray(charVal, 5);
         
        if(!firstData)
        {
          ids[0] = stringId;
          vals[0] = stringVal;
          hashMapCount++;
        }
        else
        {
          int num = inArray(stringId);
          if(num == -1)
          {
            ids[hashMapCount] = stringId;
            vals[hashMapCount] = stringVal;
            hashMapCount++;  
          }
          else
          {
            vals[num] = stringVal; 
          }
        }
        
        firstData = true;
        
      }
      else
      {
        Serial.print(" - Fout "); 
      }
      Serial.println();
    }
   }
  }
  
  if(gate)
  {
    if(!wifi.isAssociated())
    {
      Serial.println("No wifi, Reboot Chip!");
      //wifi.SendCommandSimple("reboot", "AOK");
      wifi.println("$$$");
      wifi.println("join");
      wifi.println("exit");
      delay(2000);
      wifi.println("exit");
    }
    //Sending data
    if(firstData)
    {
      Serial.println("Starting Transmit to Server");
      //Open Connection
      parseCommand("openconn$");
      
      for (int i=0; i<hashMapCount; i++){     
        char charBufId[6];
        ids[i].toCharArray(charBufId, 6);           
        	
        char charBufVal[10];
        vals[i].toCharArray(charBufVal, 10);   
       

        Serial.print("Sending: ");
        Serial.print(charBufId[0]); 
        Serial.print(charBufId[1]); 
        Serial.print(charBufId[2]); 
        Serial.print(charBufId[3]); 
        Serial.print(charBufId[4]); 
        Serial.print(","); //
        Serial.print(charBufVal[0]); 
        Serial.print(charBufVal[1]); 
        Serial.print(charBufVal[2]); 
        Serial.println(charBufVal[3]); 
        
        wifi.print("#save,");
        wifi.print(charBufId[0]); 
        wifi.print(charBufId[1]); 
        wifi.print(charBufId[2]); 
        wifi.print(charBufId[3]); 
        wifi.print(charBufId[4]); 
        wifi.print(","); //
        wifi.print(charBufVal[0]); 
        wifi.print(charBufVal[1]); 
        wifi.print(charBufVal[2]); 
        wifi.println(charBufVal[3]); 
        delay(500);
      }

      delay(1000);
      //Close Connection
      parseCommand("closeconn$");
      Serial.println("Transmit finished.");
   }
   gate = false; 
  }
}

int inArray(String zoek)
{
  for(int i = 0; i<hashMapCount; i++)
  {
    if(ids[i] == zoek)
    {
      return i;
    }
  }  
  return -1;
}

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
 
 else if(command.substring(0, 7) == "connect") //connect+<SSID>+<WPA / WEP>+<PASS>$ 
 {
   //int delimeter = command.indexOf('', 0);
   char ssid[33];
   command.substring(8).toCharArray(ssid, 33);
   
   Serial.println(ssid);
   
   connectToNetwork(ssid);
 }
 
 else if(command.substring(0, 8) == "openconn")
 {
   
   wifi.print("$$$");
   delay(400);
   wifi.println("open");
   //wifi.SendCommandSimple("open", "AOK");
   wifi.println("exit");
   delay(5000); 
   
 }
 
 else if(command.substring(0, 9) == "closeconn")
 {
   wifi.print("$$$");
   delay(400);
   wifi.println("close");
   delay(200);
   wifi.println("exit");
   delay(5000); 
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
 wifi.println("$$$");
 wifi.println("save");
 delay(2000);
 wifi.println("exit");
}

void connectToNetwork(char* ssid)
{
 clearBuf();
 strcpy(buf, "set wlan ssid ");
 strcat(buf, ssid);
 Serial.println(buf);
 //wifi.SendCommandSimple(buf, "AOK"); 
 
 wifi.println("$$$");
 wifi.println(buf);
 wifi.println("exit");
 
 wifiSaveAndBoot();
}

void connectToServer(char *ip, char *port)
{
 //wifi.SendCommandSimple("close", "AOK");
 
 clearBuf();
 
 strcpy(buf, "set ip host ");
 strcat(buf, ip);
 //wifi.SendCommandSimple(buf, "AOK");
 
 wifi.println("$$$");
 wifi.println(buf);
 wifi.println("exit");
 
 clearBuf();
 
 strcpy(buf, "set ip remote ");
 strcat(buf, port);
 //wifi.SendCommandSimple(buf, "AOK");
 
 wifi.println("$$$");
 wifi.println(buf);
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

