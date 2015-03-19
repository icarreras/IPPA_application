//Plan:

//make software UART control servo motor:
//Will send message: S.###, where ### will be a number
//...between 0 and 180. This number will control a servo

//NOTE: can only get it to 18

//recieve message from serial UART and let servo move to that
//...position quickly!->done by using small delay

#include <SoftwareSerial.h>
#include <Servo.h>
#include <stdlib.h>     /* atoi */

Servo servo[5];
// software serial #1: TX = digital pin 10, RX = digital pin 11
// on the Mega, use other pins instead, since 8 and 9 don't work on the Mega
SoftwareSerial portOne(10,11);

void parseAngle_and_moveServo();
 void readOneIntatTime();
  void printMessage();
   void parseMessage();
    void writeToServos();//This function passes the parsed Message to the servos
  
   char inByte;
   char mess[20];
   int servoPos[5];
   
   boolean isStart=false;
   boolean isHalt=false;
   boolean isRelease=false;
   boolean determineMessage=true;
  // boolean printM=false;
  int count=0;

void setup()
{
 // Open serial communications and wait for port to open:
  Serial.begin(115200);
   while (!Serial) {
    ; // wait for serial port to connect. Needed for Leonardo only
  }
  
  servo[0].attach(A0);
  servo[1].attach(A1);//need to implement
  servo[2].attach(A2);//need to implement
  servo[3].attach(A3);//need to implement
  servo[4].attach(A4);//need to implement
  
   // Start each software serial port
  portOne.begin(115200);
  
}
  void loop(){
    
    // By default, the last intialized port is listening.
  // when you want to listen on a port, explicitly select it:
  portOne.listen();
  
  //GOOD FOR READING REAL TIME VALUES
//   while (portOne.available() > 0) {
//    //DO NOT READ ONE BYTE AT A 
//    //TIME, READ WHOLE BUFFER
//     char inByte=portOne.read();
//    //Data.concat(inByte); // Add the received character to the receive buffer
//   Serial.write(inByte);
//   //Serial.print(Data);
//  }
   
   //store message 
   while(portOne.available()>0){//need to handle null character
     
     inByte = portOne.read();
     //Serial.write(inByte);
     if(determineMessage&&inByte=='S'){
       isStart=true;
       determineMessage=false;
       //inByte = Serial.read();
     }
     else{
     //determine what message it is
     if(isStart){
      mess[count] = inByte;
      
      if(count==19){//here, entire S message is done, print result
        //print message
        //printMessage();
        parseMessage();
          writeToServos();//Here we will write to the servos!
        count=0;
        isStart=false;//reset
        determineMessage=true;//go back to try to determine message
      }
      else{
       count++; 
      }
    }
    else if(isHalt){//THIS FUNCTION SHOULD SET AN INTERRUPT
   //...TO HALT SERVOS 
     Serial.print(inByte); 
    }
    else if(isRelease){
     Serial.print(inByte); 
    }
     
     }//end of else
  
    
     }
     
   
  } 
  
  void parseAngle_and_moveServo(){//updated to 
    char msg[3];
    
    //need to store three characters in character array
    msg[0]=Serial.read();
    msg[1]=Serial.read();
    msg[2]=Serial.read();
    
    //then convert to integer
    int angle = atoi(msg);
    //Serial.println("msg: "+angle);
     //int angle = Serial.parseInt();//api function in arduino
    //max value is 100,000 roughly
    
    
    if(angle>0 && angle <181){//make sure servo angle is in range of 1 to 180
    //NOTE: zero always returns, probably from null character
    Serial.println("0: "+angle);
    //servo[0].write(angle);
    delay(1);
    
    }
  }
  
  void readOneIntatTime(){
   char inByte = Serial.read();
     //Best way to convert single char to int
     int angle = inByte - 48;//actual ASCII value of zero
     
     if(angle>0){//handle cases of recieving null character and cariage return!
    Serial.println(angle);//use, not write!

     }
  }
  
  void printMessage(){
   int i;
    for(i=0;i<20;i++){
     Serial.print(mess[i]); 
    }
      
  }
  
   void parseMessage(){
   int i;
   int servoPosCount=0;//count of which position in servo array to add too
   char message[4];
    for(i=0;i<20;i+=4){
     
      //get char array of three numbers and save as servo position
      //Serial.write(mess[i+1]);
      //Serial.write(mess[i+2]);
      //Serial.write(mess[i+3]);
      //Serial.write('\n');
      message[0]=mess[i+1];
      message[1]=mess[i+2];
      message[2]=mess[i+3];
      //message[3] = '/0';
      
      //convert to integer
      int angle = atoi(message);
      //Serial.println(angle);//HAVE TO USE PRINTLN
      //NOT SERIAL.WRITE, WILL ONLY PRINT A BYTE
      servoPos[servoPosCount]=angle;
      //count++;
      servoPosCount++;
      
    }
    
    //print servo positions
    for(i=0;i<5;i++){
       //Serial.print("Servo"+i); 
     Serial.println(servoPos[i]); 
    }
      
  }
  
  void writeToServos(){//This function passes the parsed Message to the servos
    servo[0].write(servoPos[0]);
    servo[1].write(servoPos[1]);
    servo[2].write(servoPos[2]);
    servo[3].write(servoPos[3]);
    servo[4].write(servoPos[4]);
  }

    
    

  
 
