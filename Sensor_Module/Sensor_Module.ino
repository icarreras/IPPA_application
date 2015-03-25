/*
 * Sensor_Module.ino
 *
 * Created: 2/27/2015 12:18:38 PM
 * Author: Matthew
 */

/*	
 *  Rfsr = [(Vcc * Rm) / Vdet] - Rm
 *  Force = 462.95*(Vout) - 153.86
 */
#include <SoftwareSerial.h>

#define VCC 5.0 // conversion constants
#define ADC 1024.0
//#define ADC 5.0/1024.0

#define emgPin 0
#define pirPin 1
#define p1Pin 2
#define p2Pin 3
#define p3Pin 4

int pinArray[5] = {
  1, 2, 3, 4, 5};	// defines the analog pins to use as inputs

double sensorValue[5] = {
  0, 0, 0, 0, 0}; // storage for the sensor readings

double emgBuffer[10] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // emg data memory 
double pirBuffer[10] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // pir data memory

int emgpos = 0, pirpos = 0; // points to the oldest item in the buffer

double emgThresh = 1.0; // emg cutoff (volts)
double pirThresh = 1.0; // pir cutoff (volts)
double fsrThresh = 500.0; // pressure sens cutoff (grams)

int interrupt1 = 0; // Outgoing data pins
int interrupt2 = 0;
int emgOut = 0;
int pirOut = 0;
int fsr1 = 0;
int fsr2 = 0;
int fsr3 = 0;

SoftwareSerial mySerial(10, 11); // RX, TX

void setup()
{
  // Initialize comm with tiva
  initComm();
  
  // Setup GPIOs as inputs/outputs
  pinMode(A1, INPUT);
  pinMode(A2, INPUT);
  pinMode(A3, INPUT);
  pinMode(A4, INPUT);
  pinMode(A5, INPUT);
  
  pinMode(0, OUTPUT); // fsr1 out
  pinMode(1, OUTPUT); // fsr2 out
  pinMode(2, OUTPUT); // pir out
  pinMode(3, OUTPUT); // emg out
  
  //debug outputs
  pinMode(10, OUTPUT); // emg out
  pinMode(11, OUTPUT); // pir out
  pinMode(12, OUTPUT); // fsr1 out
  pinMode(13, OUTPUT); // fsr2 out
  
  delay(100);
  
  // Read some values from emg sensor to determine base value
  //calibrateEMG();
  
  // Repeat above for pir 
  calibratePIR();
  
  
}

void loop()
{
  readSensorValues();

  // Process FSR
  double force = sensorValue[p1Pin]*462.95 - 153.86;
  
  proc_FSR(force);
    
  // Process EMG
  /*
  int emgOut = proc_EMG(sensorValue[emgPin]); 
  
  if (emgOut == 1) digitalWrite(13, HIGH);
  else digitalWrite(13, LOW);
  */
  
  
  // Process PIR
  int pirOut = proc_PIR(sensorValue[pirPin]);
  
  if (pirOut == 1) {
    //digitalWrite(12, HIGH);
    digitalWrite(2, HIGH);
  }
  
  else {
    //digitalWrite(12, LOW);
    digitalWrite(2, LOW);
  }
  
  //printSensorValues(); 
}

int proc_EMG(double data) {
  
  emgBuffer[emgpos] = data;
  
  // Compute the average of the new buffer
  double avg = 0;
  
  for (int i = 0; i < 10; i++) {
     avg += emgBuffer[i];
  }

  avg /= 10;
  
  // Check if the average is above the threshold
  if (avg >= emgThresh)
    return 1;
  
  return 0;
}

void proc_FSR(double data) {
  
  if (data > 800) { // Medium force detected, set fsrpin 1 to HIGH
    //digitalWrite(12, HIGH); // debug
    digitalWrite(0, HIGH); // <-- actual comm. port to tiva
  }
  else {
    //digitalWrite(12, LOW); // debug
    digitalWrite(0, LOW); // <-- actual comm. port to tiva
  }
  
  if (data > 1000) { // High force detected, set fsr pin 2 to HIGH
    //digitalWrite(13, HIGH);
    digitalWrite(1, HIGH);
  }
  else {
    //digitalWrite(13, LOW);
    digitalWrite(1, LOW);
  }
  
}

int proc_PIR(double data) {
  
  pirBuffer[pirpos] = data;
  
  pirpos = (pirpos+1)%10; // advance to the next buffer pos
  
  // Compute the average of the new buffer
  double avg = 0;
  
  for (int i = 0; i < 10; i++) {
     avg += pirBuffer[i];
  }

  // THIS IS WHERE TO MODIFY THE DETECTION DISTANCE
  avg /= 1.5; 
  
  // return true if the data has been reduced to half the threshold
  if (avg < pirThresh)
    return 1;
 
  return 0; 
}

void readSensorValues() {

  for (int i = 0; i <= 4; i++) {
    // Store the analog read value & convert into a voltage between 0 & 5V
    sensorValue[i] = analogRead(pinArray[i])*(VCC/ADC);
  }
  
  //Serial.println(analogRead(pinArray[p1Pin]));
  //printSensorValues();
}

void printSensorValues() {
  
  //double force = sensorValue[2]*462.95 - 153.86;
  
  //Serial.print(force);

  for (int i = 0; i <= 4; i++) {
      Serial.print(i+1);
      Serial.print(": ");
      Serial.print(sensorValue[i]);
      Serial.print(" ");
  }
	
  Serial.println();
  delay(500); 
  
}

void initComm() {
  Serial.begin(9600);
}

void calibrateEMG() {
  // fill the emg buffer
  for (int i = 0; i < 10; i++) {
     emgBuffer[i] = analogRead(pinArray[emgPin])*ADC;
  }
  
  // Take average of buffer & use as the "relaxed" value
  // TO DO
    // TO DO
      // TO DO
        // TO DO
          // TO DO
            // TO DO
}

void calibratePIR() {
  // Fill the pir buffer & compute avg
  double avg = 0;

  for (int i = 0; i < 10; i++) {
     pirBuffer[i] = analogRead(pinArray[pirPin])*VCC/ADC;
     avg = avg + pirBuffer[i];
  }
  
  // use average of buffer for baseline
  avg = avg / 10.0; 
  
  pirThresh = avg;
}

