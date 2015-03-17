//Simple sketch on arduino to 
//...toggle output value of a digital pin

//NOTE: this pin will connect to Tiva at PD0,
//...where PDO will be the input digital val
//... and read the value that is from pin2 on arduino
void setup(){
  
  //set pin 2 as output
  pinMode(2,OUTPUT);
  Serial.begin(115200);
  while(!Serial){
    ;//wait until serial is initalized
  }
  //digitalWrite(2,HIGH);
  //Serial.println("PIN 2 set to high!");
}

void loop(){
  //loop 
  
  //set pin to HIGH
  digitalWrite(2,HIGH);
  //delay for 1000ms
  Serial.println("PIN 2 set to high!");
  delay(1000);
  //set pin to LOW
  digitalWrite(2,LOW);
  Serial.println("PIN 2 set to low!");
  delay(1000);
}
