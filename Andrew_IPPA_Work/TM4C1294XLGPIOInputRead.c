//*****************************************************************************
//
// TM4C1294GPIOInputRead.c - This program is the initial protoype of the Tiva Communication
//				Pin 2 from ATMega328p to PD0
//
//
//
//*****************************************************************************

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

//*****************************************************************************
//
//! \addtogroup example_list
//! <h1>Blinky (blinky)</h1>
//!
//! A very simple example that blinks the on-board LED using direct register
//! access.
//
//*****************************************************************************

//****************************************************************************
//
// System clock rate in Hz.
//
//****************************************************************************
uint32_t g_ui32SysClock;

//*****************************************************************************
//
// The error routine that is called if the driver library encounters an error.
//
//*****************************************************************************
#ifdef DEBUG
void
__error__(char *pcFilename, uint32_t ui32Line)
{
}
#endif

//need handler for software UART

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
            ROM_UARTCharPutNonBlocking(UART0_BASE,
                                      c);
           // ROM_UARTCharPutNonBlocking(UART4_BASE,
              //                                   c);


        //
        // Blink the LED to show a character transfer is occuring.
        //
       // GPIOPinWrite(GPIO_PORTN_BASE, GPIO_PIN_0, GPIO_PIN_0);

        //
        // Delay for 10 millisecond.  Each SysCtlDelay is about 3 clocks.
        //
        SysCtlDelay(g_ui32SysClock / (10000 * 3));

        //
        // Turn off the LED
        //
        //GPIOPinWrite(GPIO_PORTN_BASE, GPIO_PIN_0, 0);
    }
}

//*****************************************************************************
//
// Send a string to the UART.
//
//*****************************************************************************
void
UARTSend(const uint8_t *pui8Buffer, uint32_t ui32Count)
{
    //
    // Loop while there are more characters to send.
    //
    while(ui32Count--)
    {
        //
        // Write the next character to the UART.
        //
        ROM_UARTCharPutNonBlocking(UART0_BASE, *pui8Buffer++);
    }
}

//*****************************************************************************
int
main(void)
{
	//
	    // Set the clocking to run directly from the crystal at 120MHz.
	    //
	    g_ui32SysClock = MAP_SysCtlClockFreqSet((SYSCTL_XTAL_25MHZ |
	                                             SYSCTL_OSC_MAIN |
	                                             SYSCTL_USE_PLL |
	                                             SYSCTL_CFG_VCO_480), 120000000);

	    //
	      // Enable the peripherals used by this example.
	      //



	    //INITALIZE SOFTWARE UART
	    ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_UART0);
	    ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_GPIOA);
	    ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_GPIOD);//NEED TO DO THIS, else error will be thrown

	    int32_t i32val;//value which will store the input values from the input pin

	//set pin 0 and port D to be input and SW controlled
	   // ROM_GPIODirModeSet(GPIO_PORTD_BASE, GPIO_PIN_0, GPIO_DIR_MODE_IN);
	    ROM_GPIOPinTypeGPIOInput(GPIO_PORTD_BASE,GPIO_PIN_0);

	//
	    // Set GPIO A0 and A1 as UART pins.
	    //
	    GPIOPinConfigure(GPIO_PA0_U0RX);
	    GPIOPinConfigure(GPIO_PA1_U0TX);

	    ROM_GPIOPinTypeUART(GPIO_PORTA_BASE, GPIO_PIN_0 | GPIO_PIN_1 );

	    // Configure the UART for 115,200, 8-N-1 operation.
	        //
	        ROM_UARTConfigSetExpClk(UART0_BASE, g_ui32SysClock, 115200,
	                                (UART_CONFIG_WLEN_8 | UART_CONFIG_STOP_ONE |
	                                 UART_CONFIG_PAR_NONE));

	        ROM_IntEnable(INT_UART0);
	           ROM_UARTIntEnable(UART0_BASE, UART_INT_RX | UART_INT_RT);

	           ROM_IntMasterEnable();

	           UARTSend((uint8_t *)"TEST", 5);


	while(1){

		//read input value from PDO

		/*A variable is created to store the return value of GPIOPinRead and then checked if its 0 or not.
		 *
		 * We use a value that is a 32 but value to store the value of the input of the pin. This 32 bit value
		 * enables to read all the 8 pins at the same time, however since we only set one pin of all pins on the port
		 * to one value, we can ignore bits 32-8 from the return packet. Because we set PD0 as input, bit 0 is the bit
		 * we check to see if zero or one, so the only possible valuw we would see is in between 0 and 1
		 *
		 * To look at any pin enabled on a port and see if it is HIGH or low, we can do the following
		 * (i32val & GPIO_PIN_#)==1, then High, else, GPIO_PIN_X is low
		 */
		i32val =GPIOPinRead(GPIO_PORTD_BASE,GPIO_PIN_0);
		//i32val++;
		//display value in console
		//ROM_UARTCharPut(UART0_BASE,c);
//		if(ROM_GPIOPinRead(GPIO_PORTD_BASE,GPIO_PIN_0)==0){
//		ROM_UARTCharPutNonBlocking(UART0_BASE,'0');
//		}
		if((i32val&GPIO_PIN_0)==0){
				ROM_UARTCharPutNonBlocking(UART0_BASE,'0');
				}
//		 if(ROM_GPIOPinRead(GPIO_PORTD_BASE,GPIO_PIN_0)!=0){
//			ROM_UARTCharPutNonBlocking(UART0_BASE,'1');
//		}
		if((i32val&GPIO_PIN_0)!=0){
				ROM_UARTCharPutNonBlocking(UART0_BASE,'1');
				}
		//UARTSend((uint8_t *)"TEST", 5);
		//delay 100ms
		SysCtlDelay(7000000);
		ROM_UARTCharPutNonBlocking(UART0_BASE,'\n');

	}
}
