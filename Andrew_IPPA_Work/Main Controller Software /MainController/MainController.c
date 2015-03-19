//*****************************************************************************
// MainController.c : code will be the driving bed for the Main Controller
//
//		-UART 7 will be communication between Tiva(UART_0 softwareUSB UART) and HC-06
//		-UART 4 will be communication between Tiva(UART_0 softwareUSB UART) and ATMega328p
//		-7 GPIO's : 2 for interrupts, 3 for sensing pressure, 1 RGB LED and one Button for RST
//
//*****************************************************************************

#include "Gesture.h"
#include "MainController.h"
#include "Bluetooth.h"
#include "ServoController.h"
#include "SensorController.h"



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

    initalizeBluetooth();
    initalizeServoController();
    //
    // Enable the peripherals used by this example.
    //
    ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_UART0);


    ROM_SysCtlPeripheralEnable(SYSCTL_PERIPH_GPIOA);


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



//       //sGPIOPadConfigSet(GPIO_PORTC_BASE,GPIO_PIN_4 | GPIO_PIN_5,GPIO_STRENGTH_4MA,GPIO_PIN_TYPE_STD_WPU);

    ROM_GPIOPinTypeUART(GPIO_PORTA_BASE, GPIO_PIN_0 | GPIO_PIN_1 );




    //ROM_GPIOPinTypeUART(GPIO_PORTA_BASE,GPIO_PIN_2 | GPIO_PIN_3);


    // Configure the UART for 115,200, 8-N-1 operation.
    //
    ROM_UARTConfigSetExpClk(UART0_BASE, g_ui32SysClock, 115200,
                            (UART_CONFIG_WLEN_8 | UART_CONFIG_STOP_ONE |
                             UART_CONFIG_PAR_NONE));
   // UARTStdioInit(0);
    //UARTIntRegister(UART0_BASE,UARTIntHandler);




    //UARTStdioInit(7);
    //Interrupt(2)--------------




    //
    // Enable the UART interrupt.
    //
   // SysCtlDelay(100000);
   ROM_IntEnable(INT_UART0);
    ROM_UARTIntEnable(UART0_BASE, UART_INT_RX | UART_INT_RT);


     //ROM_IntEnable(INT_UART4);//(7) enable the interrupt for UART4
       //ROM_UARTIntEnable(UART4_BASE, UART_INT_RX | UART_INT_RT);//enable the UART interrupt for UART4


       //Interrupt(4)--------------
        ROM_IntMasterEnable();
        //


    //UARTEnable(UART7_BASE);

    // Prompt for text to be entered.
    //
   // UARTSend((uint8_t *)"\033[2JEnter text: ", 16);
       // UARTCharPut(UART0_BASE,'0' );

        MC_initalizeGesture();
        MC_initalizeDefaultGesture();
//        MC_sendTaskToServo(gestures[0]);
//        SysCtlDelay(7000000);//need delay here!
//        MC_sendTaskToServo(defaultGest);
        MC_completeTask(gestures[0],defaultGest);


        //MC_TestSendServoPos();

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
     //UARTSendHC_06((uint8_t *)"Talk to me!", 11);
    //UARTSendATMega((uint8_t *)"Talk to me!", 13);
    int i=32;
    unsigned char  c ='A';
    int8_t b;
    //UARTCharPut(UART7_BASE,'A');
    //UARTCharPut(UART7_BASE,'T');
    //UARTCharPut(UART7_BASE,'\n');
   // UARTCharPut(UART7_BASE,'n');

	  uint32_t ui32Status;
	 // unsigned char  count[32]={'2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2','2'};

//	  	while(!ROM_UARTCharsAvail(UART7_BASE)){
//
//	  	}
//	  c = ROM_UARTCharGetNonBlocking(UART7_BASE);


    while(1)
    {


    	}
}
