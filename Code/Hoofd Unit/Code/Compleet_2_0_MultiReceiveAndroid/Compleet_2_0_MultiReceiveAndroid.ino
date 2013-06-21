/*
Code for Arduino 2560 Mega
Project Social GreenHouse
*/
#include <SoftwareSerial.h>
#include <Streaming.h>
#include <WiFlySerial.h>
#include <Arduino.h>
#include <MsTimer2.h>
#include <AndroidAccessory.h>

//Default buffer
#define BUFFER_SIZE 100
//Define reading buffer
#define DATASTRINGLENGTH 15

char buf[BUFFER_SIZE];
int bufCounter = 0;

WiFlySerial wifi(10,11);

AndroidAccessory accessory("Google, Inc.", "SocialGreenhouse");

boolean gate = false, firstData = false, adkGate = true;
//Multiplier for delay between receives
int multiplier = 2;
//HashMapCounter
int hashMapCount = 0;

//Reading 433 Receiver
char data[DATASTRINGLENGTH];
int checksum = 0;

//Storing Keys and Values
String ids[32];
String vals[32];

//Reading ADK
char readADK;
bool reading = false;
String adkRead = "";

//Pin for softwareReset
int resetPin = 12;

void setup()
{
  digitalWrite(resetPin, HIGH);//Set Pin high on default
  
  Serial.begin(9600); // For Debugging
  Serial.println("Starting");
  wifi.begin();
  
  Serial1.begin(1200); //For 433Mhz receiver
  
  wifi.println("$$$");
  wifi.println("load");
  delay(200);
  //wifi.println("join");
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
  
  if (!accessory.begin()) {
    Serial.println("OSCOKIRQ failed to assert");
  }  
  
  MsTimer2::set(60000,openGate); //Send every Minute
  MsTimer2::start();
  
  Serial.println("Hoofdmodule Gestart");
  
  pinMode(resetPin, OUTPUT); 
}

//Opens the gate every minute for sending to server
void openGate()
{
  gate = true;    
}

void loop()
{

  //ReadLine in serial monitor
  if(Serial.available())
  {
    readSerialLine();
  }
  
  //Read data from 433 Receiver
  if (Serial1.available())
  {
    read433Receiver();
  }
  
  //Send data to server when te gate is open.
  if((gate) && (adkGate))
  {
    sendDataToServer();
  }
  
  accessory.refresh();
  
  if(accessory.isConnected()) {
    adkGate = false;
    readAndroidADK();
  }
  else
  {
    adkGate = true;
  }
}

void readAndroidADK()
{
  if(accessory.available())
  {
    readADK = accessory.read();
    //Begin en eind teken
    //35 == #
    //36 == $
    //Eind Teken gegeven stop lezen.
    if((int)readADK == 36)
    {
      reading = false; 
      Serial.println(adkRead);
      parseCommand(adkRead);
    }
    
    if(reading)
    {
      adkRead += (char)readADK;  
    }
    //Begin teken gegeven.
    if((int)readADK == 35)
    {
      reading = true;
      adkRead = "";
    }
  }
}

void sendDataToServer()
{
  //Rejoin wifi connection
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
  if((firstData) && (hashMapCount > 0))
  {
    Serial.println("Starting Transmit to Server");
    //Open Connection
    parseCommand("openconn$");
    
    //Create String to send data
    String sendToWifi = "#save";
    
    for (int i=0; i<hashMapCount; i++){     
      sendToWifi += "|";
      sendToWifi += ids[i];
      sendToWifi += ",";
      sendToWifi += vals[i];
      
      ids[i] = "";
      vals[i] = "";
    }
    
    hashMapCount = 0;
    
    sendToWifi += "$";
    
    //Sending data
    wifi.print(sendToWifi);
    Serial.println(sendToWifi);

    delay(1000);
    //Close Connection
    parseCommand("closeconn$");
    Serial.println("Transmit finished.");
  }
  gate = false;   
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







