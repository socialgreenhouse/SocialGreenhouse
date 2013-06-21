/*
Functions for reading serial lines
Project Social GreenHouse
*/


void read433Receiver()
{
 char c = Serial1.read();
 if(c == 'A')
 {
   delay(10 * multiplier);
   c = Serial1.read();
   int count = 0;
   while(c != 'B' && c < 127 && count < 100)
   {
     c = Serial1.read();
     delay(1 * multiplier);
     count++;
   }
  if(c == 'B')
  {
    count = 0;
    checksum = 0;
    char c = Serial1.read();
    while(c != 'z' && count < DATASTRINGLENGTH)
    {
      if(c != -1)
      {
       Serial.print(c);
       data[count] = c;
       if(count < DATASTRINGLENGTH-2)
       {
         checksum += ((int)c * (count+1));
       }
       count++;
      }
     c = Serial1.read(); 
     delay(4 * multiplier);
    }
    
    checksum = floor(checksum % 111);
    
    if(data[8] == ':' && data[DATASTRINGLENGTH-2] == '-' && count == DATASTRINGLENGTH && checksum == (int)data[DATASTRINGLENGTH-1])
    {
      Serial.print(" - Goed");
      String dataString;
      
      for(int i = 0; i<count; i++)
      {
        dataString += String(data[i]); 
      }
      
      String stringId = dataString.substring(0,8);
      String stringVal = dataString.substring(9,13);
       
      //Firstdata always add value
      if(hashMapCount == 0)
      {
        ids[0] = stringId;
        vals[0] = stringVal;
        hashMapCount++;
      }
      else
      {
        //Check if moduleId excists in array
        //If not add oterwise update value
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

void readSerialLine()
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
