/*
 * MainController.h
 *
 *  Created on: Mar 18, 2015
 *      Author: Andrew Mendez
 */
#include "Gesture.h"
#include <stdint.h>
#include <stdbool.h>
#include <stdio.h>
#include <math.h>
#include "inc/hw_ints.h"
#include "inc/hw_memmap.h"
#include "driverlib/debug.h"
#include "driverlib/gpio.h"
#include "driverlib/interrupt.h"
#include "driverlib/pin_map.h"
#include "driverlib/rom.h"
#include "driverlib/rom_map.h"
#include "driverlib/sysctl.h"
#include "driverlib/uart.h"
#include "utils/uartstdio.h"//library to have nice function to printf

Gesture gestures[5];
Gesture defaultGest;
int MC_tempServoPos[5] = {179,179,179,179,179};
//int MC_defaultServoPos[5] = {90,90,90,90,90};
int MC_defaultServoPos[5] = {9,9,9,9,9};
char MC_servoPos[3];


//****************************************************************************
//
// System clock rate in Hz.
//
//****************************************************************************
uint32_t g_ui32SysClock;


#ifndef MAINCONTROLLER_H_
#define MAINCONTROLLER_H_

//TASK will define either a grasp or a gesture

//methods

/*
 * Store task method
 */

/*
 * removeTaskMethod
 */







MC_initalizeGesture(){

	int k;
		for(k=0;k<5;k++){
//			gestures[0].servoPositions[k]=90;
		gestures[0].servoPositions[k] = MC_tempServoPos[k];
		}

}

/*
 * initalize default gesture function
 */

MC_initalizeDefaultGesture(){

	int k;
			for(k=0;k<5;k++){
	//			gestures[0].servoPositions[k]=90;
			defaultGest.servoPositions[k] = MC_defaultServoPos[k];
			}
}

/*
 * convert servo pos (a max 3 digit integer) to character array
 */
void MC_convertServoPositionToMessage(int num){

	int count2=0;
	int l;

	for(l=2;l>=0;l--){
		int k = num/pow(10,l); //get digit

		char c = '0'+k;//convert to character

		MC_servoPos[count2++]=c;//store in message

		num = num- k*pow(10,l);//decrease number
	}

}




/*
 * send message function
 */

void MC_SendServoPos(int pos[]){
	int k;
	int j;

	ROM_UARTCharPutNonBlocking(UART4_BASE,'S');
		 SysCtlDelay(g_ui32SysClock / (1000 * 3));//need delay here!



	//convert servo position to character message
	for(j=0;j<5;j++){
		ROM_UARTCharPutNonBlocking(UART4_BASE,'.');
			 SysCtlDelay(g_ui32SysClock / (1000 * 3));//need delay here!
	MC_convertServoPositionToMessage(pos[j]);
	 for(k=0;k<3;k++)
	    {
		//method only gets one character at a time!
		ROM_UARTCharPutNonBlocking(UART4_BASE,MC_servoPos[k]);


		        // Delay for 10 millisecond.  Each SysCtlDelay is about 3 clocks.
		        SysCtlDelay(g_ui32SysClock / (1000 * 3));//need delay here!

		 //ROM_UARTCharPutNonBlocking(UART0_BASE,'\n');
	}

	}

}

/*
 * SendTaskToServo method
 *
 * Method comprised of
 *
 * ->Converting the Servo Position to a character array,
 * ->Sending the message to the servo controller
 *
 *  Parameters: Gesture
 */
void MC_sendTaskToServo(Gesture g){

	//call MC_
	MC_SendServoPos(g.servoPositions);
}

/*
 * Complete Task Method
 *
 * This method will be the task of completing either a gesture or a grasp
 *
 * This will be done by:
 *
 * -> Send Servo Positions for the Servo Controller to Move to
 *
 * -> Receive message back from Servo Controller that it is done
 *
 * -> add delay for 4 second (REALLY SHOULD BE WAIT FOR EMG SIGNAL)
 *
 * -> Send Servo Default Position for the Servo Controller to move to
 * 		*simulate a releasing of the task
 */
void MC_completeTask(Gesture startGesture, Gesture endGesture){

	 MC_sendTaskToServo(startGesture);
	        SysCtlDelay(70000000);//delay to show servo's holding
	        MC_sendTaskToServo(endGesture);

}

/*
 * DefaultUARTIntHandler
 */

//*****************************************************************************
//
// The UART interrupt handler.
//
//*****************************************************************************
void
UARTIntHandler(void)
{
    uint32_t ui32Status;

    //
    // Get the interrrupt status.
    //
    ui32Status = ROM_UARTIntStatus(UART0_BASE, true);

    //
    // Clear the asserted interrupts.
    //
    ROM_UARTIntClear(UART0_BASE, ui32Status);

    //
    // Loop while there are characters in the receive FIFO.
    //
    while(ROM_UARTCharsAvail(UART0_BASE))
    {
        //
        // Read the next character from the UART and write it back to the UART.
        //
    	char c;
        	c= ROM_UARTCharGetNonBlocking(UART0_BASE);

        	//this thing will do a broadcast
            ROM_UARTCharPutNonBlocking(UART7_BASE,
                                      c);
            ROM_UARTCharPutNonBlocking(UART4_BASE,
                                                 c);


        //
        // Blink the LED to show a character transfer is occuring.
        //
        //GPIOPinWrite(GPIO_PORTN_BASE, GPIO_PIN_0, GPIO_PIN_0);

        //
        // Delay for 10 millisecond.  Each SysCtlDelay is about 3 clocks.
        //
        SysCtlDelay(g_ui32SysClock / (1000 * 3));

        //
        // Turn off the LED
        //
      //  GPIOPinWrite(GPIO_PORTN_BASE, GPIO_PIN_0, 0);
    }
}

#endif /* MAINCONTROLLER_H_ */
