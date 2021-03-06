#include "platform.h"
#include "jtag.h"
#include "uart.h"

#include <stdint.h>

/// the upper end of the memory
#define LOADER_ADDR 0x2000000

int main ()
{
    // init the jtag interface so we can debug
    jtag_init();

    // init the uart-hardware
    uart_init();
    
    // print the header and some build-info    
    uart_puts("\r\nRaspbootin V2\r\n");
    uart_puts("#############\r\n");
    uart_puts("Built for: ");
    uart_puts(PNAME);
    uart_puts("\r\n#############\r\n");

again:
    // Drain any junk before receiving
    while(uart_check()) {
      uart_recv();
    }

    // send the start-code for the raspbootcom-application
    uart_puts("\x03\x03\x03");
    
    // the size of the kernel has been received
    uint32_t size = 0;
    uint32_t size_tmp = 0;
    while(1) {
      size_tmp = uart_recv_timeout(1000000);
      if (size_tmp == 0xFFFFFFFF) {
        // A timeout occurred...
        // Resend the start-code for the raspbootcom-application
        uart_puts("\x03\x03\x03");
      } else {
        size |= size_tmp;
        break;
      }
    }
    size |= uart_recv() << 8;
    size |= uart_recv() << 16;
    size |= uart_recv() << 24;
    
    // the kernel is too big
    if (0x8000 + size > LOADER_ADDR) 
    {
        uart_puts("SE");
        goto again;
    } 
    else 
    {
        uart_puts("OK");
    }

    // get kernel
    uint8_t *kernel = (uint8_t*)0x8000;
    while(size > 0) 
    {
	    *kernel = uart_recv();
	    kernel++;
	    size--;
    }

    // All good...give user hope!
    uart_puts("boot...\n");

    // Kernel is loaded at 0x8000, call it via function pointer
    BOOTUP(0x8000);
}

