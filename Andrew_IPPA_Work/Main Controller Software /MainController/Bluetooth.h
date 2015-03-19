/*
 * Bluetooth.h
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



#ifndef BLUETOOTH_H_
#define BLUETOOTH_H_

//members

//Gesture gestures[5];
//int bluetooth_tempServoPos[5] = {88,88,88,88,88};
//char bluetooth_servoPos[3];
	//gestures[0].servoPositions[0]=12;




//methods


/*
 * Funcction test initalization of gestures
 */
//Bluetooth_initalizeGesture(){
//
//	int k;
//		for(k=0;k<5;k++){
////			gestures[0].servoPositions[k]=90;
//		gestures[0].servoPositions[k] = bluetooth_tempServoPos[k];
//		}
//
//}

/*
 * receive message function
 */

/* ORIGINALLY TESTED HERE, BUT BELONGS IN MC
 * convert servo pos (a max 3 digit integer) to character array
 */
//void Bluetooth_convertServoPositionToMessage(int num){
//
//	int count=0;
//	int i;
//
//	for(i=2;i>=0;i--){
//		int k = num/pow(10,i); //get digit
//
//		char c = '0'+k;//convert to character
//
//		bluetooth_servoPos[count++]=c;//store in message
//
//		num = num- k*pow(10,i);//decrease number
//	}
//
//}

///*  ORIGINALLY TESTED HERE, BUT BELONGS IN MC
// * send message function
// */
//
//void Bluetooth_TestSendServoPos(){
//	int k;
//	int j;
//
//	ROM_UARTCharPutNonBlocking(UART4_BASE,'S');
//		 SysCtlDelay(g_ui32SysClock / (1000 * 3));//need delay here!
//
//
//
//	//convert servo position to character message
//	for(j=0;j<5;j++){
//		ROM_UARTCharPutNonBlocking(UART4_BASE,'.');
//			 SysCtlDelay(g_ui32SysClock / (1000 * 3));//need delay here!
//	Bluetooth_convertServoPositionToMessage(gestures[0].servoPositions[j]);
//	 for(k=0;k<3;k++)
//	    {
//		//method only gets one character at a time!
//		ROM_UARTCharPutNonBlocking(UART4_BASE,bluetooth_servoPos[k]);
//
//
//		        // Delay for 10 millisecond.  Each SysCtlDelay is about 3 clocks.
//		        SysCtlDelay(g_ui32SysClock / (1000 * 3));//need delay here!
//
//		 //ROM_UARTCharPutNonBlocking(UART0_BASE,'\n');
//	}
//
//	}
//
//}


/*
 * realtime Passing to Servo
 */

/*
 * BluetoothUART handler function
 */
//*****************************************************************************
//Interrupt Code(1)
// The HC-06 (UART_7) interrupt handler for hardware UART.
//
//added by Andrew Mendez: 1/28/15
//*****************************************************************************
void
Bluetooth_HC_06IntHandler(void)
{
    uint32_t ui32Status;

    //
    // Get the interrrupt status.
    //
    ui32Status = ROM_UARTIntStatus(UART7_BASE, true);

    //
    // Clear the asserted interrupts.
    //
    ROM_UARTIntClear(UART7_BASE, ui32Status);



    ROM_UARTCharPutNonBlocking(UART0_BASE,'B');
    ROM_UARTCharPutNonBlocking(UART0_BASE,':');
    ROM_UARTCharPutNonBlocking(UART0_BASE,' ');
    //
    // Loop while there are characters in the receive FIFO.
    //
    while(ROM_UARTCharsAvail(UART7_BASE))
    {
        //
        // Read the next character from the UART and write it back to the UART.
        //
    	char c;
    	c= ROM_UARTCharGetNonBlocking(UART7_BASE);
        ROM_UARTCharPutNonBlocking(UART0_BASE,
                                  c);


    }

    ROM_UARTCharPutNonBlocking(UART0_BASE,'\n');
}

/*
 * Initalize Bluetooth function
 */

void initalizeBluetooth(){

	 //
	    // Enable the peripherals used by this example.(2)
	    //

	ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_UART7);
	ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_GPIOC);

	//configure GPIO C4 and C5 for hardware UART(2)
	 GPIOPinConfigure(GPIO_PC4_U7RX);
	 GPIOPinConfigure(GPIO_PC5_U7TX);
	 ROM_GPIOPinTypeUART(GPIO_PORTC_BASE, GPIO_PIN_4 | GPIO_PIN_5);


	  //configure hardware UART(1)

	    ROM_UARTConfigSetExpClk(UART7_BASE, g_ui32SysClock, 115200,
	                            (UART_CONFIG_WLEN_8 | UART_CONFIG_STOP_ONE |
	                             UART_CONFIG_PAR_NONE));

	 //register the handler function used to handle interrupt

	    UARTIntRegister(UART7_BASE,Bluetooth_HC_06IntHandler);

	    //Interrupt(3)--------------
	        ROM_IntEnable(INT_UART7);//enable the interrupt for UART7
	         ROM_UARTIntEnable(UART7_BASE, UART_INT_RX | UART_INT_RT);//enable the UART interrupt for UART7

}

#endif /* BLUETOOTH_H_ */
