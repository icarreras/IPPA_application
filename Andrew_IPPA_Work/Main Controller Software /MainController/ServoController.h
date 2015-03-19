/*
 * ServoController.h
 *
 *  Created on: Mar 18, 2015
 *      Author: Andrew Mendez
 */

#include <stdint.h>
#include <stdbool.h>
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

#ifndef SERVOCONTROLLER_H_
#define SERVOCONTROLLER_H_

//methods

//NOTE: here we will refer a Task as either a gesture or a grasp



/*
 * startTask method
 */

/*
 * halt Task method
 */

/*
 * release Task method
 */

/*
 * Servo Controller Interrupt Handler
 */
//*****************************************************************************
//Interrupt Code(1)
// The ATMega328p (UART_4) interrupt handler for hardware UART.
//
//added by Andrew Mendez: 1/28/15
//*****************************************************************************
void
SERVO_ATMegaIntHandler(void)
{
    uint32_t ui32Status;

    //
    // Get the interrrupt status.
    //
    ui32Status = ROM_UARTIntStatus(UART4_BASE, true);

    //
    // Clear the asserted interrupts.
    //
    ROM_UARTIntClear(UART4_BASE, ui32Status);

    //case when no characters are available,

    	 ROM_UARTCharPutNonBlocking(UART0_BASE,'A');
    	        ROM_UARTCharPutNonBlocking(UART0_BASE,':');
    	        ROM_UARTCharPutNonBlocking(UART0_BASE,' ');


    	        //
    	             // Delay for 1 millisecond.  Each SysCtlDelay is about 3 clocks.
    	             //
    	            // SysCtlDelay(g_ui32SysClock / (1000 * 3));


    //
    // Loop while there are characters in the receive FIFO.
    //

    while(ROM_UARTCharsAvail(UART4_BASE))
    {
        //
        // Read the next character from the UART and write it back to the UART.
        //
    	char c;
    	c= ROM_UARTCharGetNonBlocking(UART4_BASE);
        ROM_UARTCharPutNonBlocking(UART0_BASE,
                                  c);


    }
    ROM_UARTCharPutNonBlocking(UART0_BASE,'\n');
}


/*
 * Initalize Servo Controller function
 * -This function initalizes the  UART communication between ATMega328p and Tiva
 */
void initalizeServoController(){

	 //
	    // Enable the peripherals used by this example.
	    //
	ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_UART4);
	 ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_GPIOK);//(2)



	 //configure GPIO K0 and K1 for hardware UART //(3)
	 GPIOPinConfigure(GPIO_PK0_U4RX);
	  GPIOPinConfigure(GPIO_PK1_U4TX);
	  ROM_GPIOPinTypeUART(GPIO_PORTK_BASE, GPIO_PIN_0 | GPIO_PIN_1);//(4)

	  // configure hardware UART_4 for ATMega //(5)
	          ROM_UARTConfigSetExpClk(UART4_BASE, g_ui32SysClock, 115200,
	                                  (UART_CONFIG_WLEN_8 | UART_CONFIG_STOP_ONE |
	                                   UART_CONFIG_PAR_NONE));

	          //register the handler function used to handle interrupt
	                UARTIntRegister(UART4_BASE,SERVO_ATMegaIntHandler);//(6)
	                //
	                   // Enable the UART interrupt.
	                   //

	                ROM_IntEnable(INT_UART4);//(7) enable the interrupt for UART4
	                       ROM_UARTIntEnable(UART4_BASE, UART_INT_RX | UART_INT_RT);//enable the UART interrupt for UART4

}

#endif /* SERVOCONTROLLER_H_ */
