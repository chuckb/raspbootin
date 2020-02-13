# raspbootin

Simple boot-over-serial bootloader for the Raspberry Pi

The Raspbootin repository contains 2 components: Raspbootin2 and Raspbootcom.

## Architecture

The Raspbootin2 runs on the Raspberry Pi as Operating System Kernel. Raspbootcom runs on a PC, reads __your__ Kernel and sends it over the Serial connction to the Raspberry Pi. Raspbootin2 saves the kernel to memory (0x8000) and starts running it. 

You could use the Adafruit TTL-Cable for the serial connection or another USB-TTL-Adapter based on PC-PL2303 (no guarantees).

## Raspbootin2

Raspbootin is the actual bootloader that you install on the SD Card for your Raspberry Pi. Copy the raspbootin/kernel.img in place of the kernel.img on the SD Card and you are ready for use.

Be sure to load this kernel to 0x02000000 (edit the config.txt-file in the boot-partition and add a line ```kernel_address=0x02000000```).

This is a rewrite of the Raspbootin loader. It will use UART1 instead of UART0 of the Raspberry Pi. The code has been tested on:
- Raspberry Pi Model A
- Raspberry Pi Zero

Raspbootin2 is compatible to Raspbootcom. Special thanks to David Welch (dwelch@dwelch.com) for the uart05-demo.

## Raspbootcom

Raspbootcom is a simple boot server and terminal program for the other side of the serial connection. You need to run this on another computer, the one the serial port of your Raspberry Pi is conneted to. On start Raspbootcom is in terminal mode. Any input on stdin is passed to the Raspberry Pi and any reply from the Raspberry Pi is printed to stdout. The Raspbootin2 bootloader will send 3 breaks (0x03) over the serial connection when it wants to boot a kernel and Raspbootcom then switches into kernel sending mode, reads the kernel from disk and sends it to the Raspberry Pi. After that it goes back into terminal mode so you can see the output from the Raspberry Pi and interact with it.

The kernel is read fresh every time it is send so you do not need to restart Raspbootcom every time the kernel image changes. My Raspberry Pi gets its power over the serial connection so unplugging and repluging the USB serial converter is how it reboots. Raspbootcom also survives unplugging and replugging of an USB serial converter and will automatically reopen the device when you replug it. 

## Compiling

The build system is very simple and the only thing configurable is the location of the arm cross compiler. By default the raspbootin/Makefile assumes you build your cross-compiler with PREFIX = /usr/local/cross and TARGET = arm-none-eabi. If that is not the case then you can override by setting PREFIX or ARMGNU in your environment:

```
export PREFIX=/usr/local/cross
export ARMGNU=${PREFIX}/bin/arm-none-eabi
```

Where ARMGNU is the prefix for the compiler to use, ${ARMGNU}-gcc and friends must exist.

Other than that simply type
```
make
```
and it will build both Raspbootin and Raspbootcom.

Gradle compatible builds have been added to this project. Gradle defers to the makefile, but one can include
this project as a source dependency so that another project can consume these artifacts as built on the host.
To build:
```
./gradlew assemble
```

To include as a source dependency, see [this example](https://github.com/chuckb/sourcedep/blob/master/build.gradle)

## Usage:

- Copy the raspbootin/kernel.img to the SD Card for the Raspberry Pi.
- make sure to put the Kernel to address 0x02000000 (in config.txt add ```kernel_address=0x02000000```)
- Run ```raspbootcom /dev/ttyUSB0 /where/you/have/your/kernel.img```.
- Turn on the Raspberry Pi.
