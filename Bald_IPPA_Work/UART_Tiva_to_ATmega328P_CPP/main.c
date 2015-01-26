#include <stdint.h>
#include <stdbool.h>
#include "inc/hw_memmap.h"
#include "inc/hw_types.h"
#include "driverlib/gpio.h"
#include "driverlib/pin_map.h"
#include "driverlib/sysctl.h"
#include "driverlib/uart.h"

uint32_t ui32SysClkFreq;

int main(void)
{
	ui32SysClkFreq = SysCtlClockFreqSet((SYSCTL_XTAL_25MHZ |
			SYSCTL_OSC_MAIN | SYSCTL_USE_PLL |
			SYSCTL_CFG_VCO_480), 120000000);

	SysCtlPeripheralEnable(SYSCTL_PERIPH_UART4);
	SysCtlPeripheralEnable(SYSCTL_PERIPH_GPIOA);

	GPIOPinConfigure(GPIO_PA2_U4RX);
	GPIOPinConfigure(GPIO_PA3_U4TX);
	GPIOPinTypeUART(GPIO_PORTA_BASE, GPIO_PIN_2 | GPIO_PIN_3);


	UARTConfigSetExpClk(UART4_BASE, ui32SysClkFreq, 4800,
			(UART_CONFIG_WLEN_8 | UART_CONFIG_STOP_ONE | UART_CONFIG_PAR_NONE));

	UARTCharPut(UART4_BASE, 4);
	UARTCharPut(UART4_BASE, 255);
	UARTCharPut(UART4_BASE, 255);
	UARTCharPut(UART4_BASE, 255);
	UARTCharPut(UART4_BASE, 255);
	UARTCharPut(UART4_BASE, 255);
	/*
	UARTCharPut(UART4_BASE, 'n');
	UARTCharPut(UART4_BASE, 't');
	UARTCharPut(UART4_BASE, 'e');
	UARTCharPut(UART4_BASE, 'r');
	UARTCharPut(UART4_BASE, ' ');
	UARTCharPut(UART4_BASE, 'T');
	UARTCharPut(UART4_BASE, 'e');
	UARTCharPut(UART4_BASE, 'x');
	UARTCharPut(UART4_BASE, 't');
	UARTCharPut(UART4_BASE, ':');
	UARTCharPut(UART4_BASE, ' ');
	*/
	while (1)
	{
		//if (UARTCharsAvail(UART4_BASE)) UARTCharPut(UART4_BASE, UARTCharGet(UART4_BASE));
	}
}
