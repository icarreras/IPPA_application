//*****************************************************************************
// Bluetooth_ATMega328p.c : code to communicate with HC-06 and ATMega328p simultaneously
//
//		-UART 7 will be communication between Tiva(UART_0 softwareUSB UART) and HC-06
//		-UART 4 will be communication between Tiva(UART_0 softwareUSB UART) and ATMega328p
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
//! <h1>UART Echo (uart_echo)</h1>
//!
//! This example application utilizes the UART to echo text.  The first UART
//! (connected to the USB debug virtual serial port on the evaluation board)
//! will be configured in 115,200 baud, 8-n-1 mode.  All characters received on
//! the UART are transmitted back to the UART.
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


//*****************************************************************************
//
// Send a string to the UART of Tiva.
//
//*****************************************************************************
void
UARTSendTiva(const uint8_t *pui8Buffer, uint32_t ui32Count)
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
//
// Send a string to the UART of HC-06.
//
//*****************************************************************************
void
UARTSendHC_06(const uint8_t *pui8Buffer, uint32_t ui32Count)
{
    //
    // Loop while there are more characters to send.
    //
    while(ui32Count--)
    {
        //
        // Write the next character to the UART.
        //
        ROM_UARTCharPutNonBlocking(UART7_BASE, *pui8Buffer++);
    }
}

//*****************************************************************************
//
// Send a string to the UART to ATMega.
//
//*****************************************************************************
void
UARTSendATMega(const uint8_t *pui8Buffer, uint32_t ui32Count)
{
    //
    // Loop while there are more characters to send.
    //
    while(ui32Count--)
    {
        //
        // Write the next character to the UART.
        //
        ROM_UARTCharPutNonBlocking(UART4_BASE, *pui8Buffer++);
    }
}
//*****************************************************************************
//Interrupt Code(1)
// The HC-06 (UART_7) interrupt handler for hardware UART.
//
//added by Andrew Mendez: 1/28/15
//*****************************************************************************
void
HC_06IntHandler(void)
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

//*****************************************************************************
//Interrupt Code(1)
// The ATMega328p (UART_4) interrupt handler for hardware UART.
//
//added by Andrew Mendez: 1/28/15
//*****************************************************************************
void
ATMegaIntHandler(void)
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
        GPIOPinWrite(GPIO_PORTN_BASE, GPIO_PIN_0, GPIO_PIN_0);

        //
        // Delay for 10 millisecond.  Each SysCtlDelay is about 3 clocks.
        //
        SysCtlDelay(g_ui32SysClock / (10000 * 3));

        //
        // Turn off the LED
        //
        GPIOPinWrite(GPIO_PORTN_BASE, GPIO_PIN_0, 0);
    }
}

//*****************************************************************************
// Send a string to the Hardware UART.
//
//added by Andrew Mendez: 1/28/15
//*****************************************************************************
void
HardwareUARTSend(const uint8_t *pui8Buffer, uint32_t ui32Count)
{
    //
    // Loop while there are more characters to send.
    //
    while(ui32Count--)
    {
        //
        // Write the next character to the UART.
        //
        ROM_UARTCharPutNonBlocking(UART7_BASE, *pui8Buffer++);
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
//
// This example demonstrates how to send a string of data to the UART.
//
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
    // Enable the GPIO port that is used for the on-board LED.
    //
    ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_GPION);

    //
    // Enable the GPIO pins for the LED (PN0).
    //
    ROM_GPIOPinTypeGPIOOutput(GPIO_PORTN_BASE, GPIO_PIN_0);

    //
    // Enable the peripherals used by this example.
    //
    ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_UART0);
    ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_UART7);
    ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_UART4);//(1)
    ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_GPIOA);
    ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_GPIOC);
    ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_GPIOK);//(2)
    //need to initalize hardware UART
    //SysCtlPeripheralEnable(SYSCTL_PERIPH_GPIOA);


    //
    // Enable processor interrupts.
    //

    //
    // Set GPIO A0 and A1 as UART pins.
    //
    GPIOPinConfigure(GPIO_PA0_U0RX);
    GPIOPinConfigure(GPIO_PA1_U0TX);
    //configure GPIO A2 and A3 for hardware UART
       GPIOPinConfigure(GPIO_PC4_U7RX);
       GPIOPinConfigure(GPIO_PC5_U7TX);

       //configure GPIO K0 and K1 for hardware UART //(3)
              GPIOPinConfigure(GPIO_PK0_U4RX);
              GPIOPinConfigure(GPIO_PK1_U4TX);
//       //sGPIOPadConfigSet(GPIO_PORTC_BASE,GPIO_PIN_4 | GPIO_PIN_5,GPIO_STRENGTH_4MA,GPIO_PIN_TYPE_STD_WPU);

    ROM_GPIOPinTypeUART(GPIO_PORTA_BASE, GPIO_PIN_0 | GPIO_PIN_1 );
    ROM_GPIOPinTypeUART(GPIO_PORTC_BASE, GPIO_PIN_4 | GPIO_PIN_5);
    ROM_GPIOPinTypeUART(GPIO_PORTK_BASE, GPIO_PIN_0 | GPIO_PIN_1);//(4)


    //ROM_GPIOPinTypeUART(GPIO_PORTA_BASE,GPIO_PIN_2 | GPIO_PIN_3);


    // Configure the UART for 115,200, 8-N-1 operation.
    //
    ROM_UARTConfigSetExpClk(UART0_BASE, g_ui32SysClock, 115200,
                            (UART_CONFIG_WLEN_8 | UART_CONFIG_STOP_ONE |
                             UART_CONFIG_PAR_NONE));
   // UARTStdioInit(0);
    //UARTIntRegister(UART0_BASE,UARTIntHandler);
    //configure hardware UART
    ROM_UARTConfigSetExpClk(UART7_BASE, g_ui32SysClock, 115200,
                            (UART_CONFIG_WLEN_8 | UART_CONFIG_STOP_ONE |
                             UART_CONFIG_PAR_NONE));

   // configure hardware UART_4 for ATMega //(5)
        ROM_UARTConfigSetExpClk(UART4_BASE, g_ui32SysClock, 115200,
                                (UART_CONFIG_WLEN_8 | UART_CONFIG_STOP_ONE |
                                 UART_CONFIG_PAR_NONE));
    //UARTStdioInit(7);
    //Interrupt(2)--------------
    //register the handler function used to handle interrupt
   UARTIntRegister(UART7_BASE,HC_06IntHandler);

   //register the handler function used to handle interrupt
      UARTIntRegister(UART4_BASE,ATMegaIntHandler);//(6)

    //
    // Enable the UART interrupt.
    //
   // SysCtlDelay(100000);
   ROM_IntEnable(INT_UART0);
    ROM_UARTIntEnable(UART0_BASE, UART_INT_RX | UART_INT_RT);

    //Interrupt(3)--------------
    ROM_IntEnable(INT_UART7);//enable the interrupt for UART7
     ROM_UARTIntEnable(UART7_BASE, UART_INT_RX | UART_INT_RT);//enable the UART interrupt for UART7

     ROM_IntEnable(INT_UART4);//(7) enable the interrupt for UART4
       ROM_UARTIntEnable(UART4_BASE, UART_INT_RX | UART_INT_RT);//enable the UART interrupt for UART4
     //Interrupt(4)--------------
        ROM_IntMasterEnable();
        //


    //UARTEnable(UART7_BASE);

    // Prompt for text to be entered.
    //
    //UARTSend((uint8_t *)"\033[2JEnter text: ", 16);

    /*----------
     * Test: how to pass a single character through a hardware UART to the the USB UART

    int test = 255;
    UARTCharPut(UART7_BASE,test);
    unsigned char test2 = UARTCharGet(UART7_BASE);
    UARTCharPut(UART0_BASE,test2 );
    -*/
    //
    // Loop forever echoing data through the hardware UART.
    //
     UARTSendHC_06((uint8_t *)"Talk to me!", 11);
    UARTSendATMega((uint8_t *)"Talk to me!", 13);
    int i=32;
    unsigned char  c ='A';
    int8_t b;
    //UARTCharPut(UART7_BASE,'A');
    //UARTCharPut(UART7_BASE,'T');
    //UARTCharPut(UART7_BASE,'\n');
   // UARTCharPut(UART7_BASE,'n');

	  uint32_t ui32Status;
	  unsigned char  count[32]={'2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2'};

//	  	while(!ROM_UARTCharsAvail(UART7_BASE)){
//
//	  	}
//	  c = ROM_UARTCharGetNonBlocking(UART7_BASE);
//      ROM_UARTCharPut(UART0_BASE,c);

    while(1)
    {



    }
}
