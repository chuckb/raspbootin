ARM = arm-none-eabi
CC = $(ARM)-gcc
AS = $(ARM)-as

KRNL=kernel

# Rule to make everything.
all: 
	make rpi
	
rpi: PLAT=PLATFORM_RPI
rpi: MCPU=arm1176jzf-s
rpi: $(KRNL).img

rpi2: PLAT=PLATFORM_RPI2
rpi2: MCPU=cortex-a7
rpi2: $(KRNL).img

rpi3: PLAT=PLATFORM_RPI3
rpi3: MCPU=cortex-a53
rpi3: $(KRNL).img

rpibp: PLAT=PLATFORM_RPIBP 
rpibp: MCPU=arm1176jzf-s
rpibp: $(KRNL).img 

dump: $(KRNL).img

# Rule to remake everything. Does not include clean.
rebuild: all

#ASOPTS=-march=arm -mcpu=arm1176jzf-s
ASOPTS=-mcpu=$(MCPU) -g
LDOPTS=--no-undefined
CFLAGS=-mcpu=$(MCPU) -std=c99 -Wall -pedantic -g


# The names of all object files that must be generated. Deduced from the 
# assembly code files in source.
ASSRCS := $(patsubst $(SOURCE)%.S,$(BUILD)%.o,$(wildcard $(SOURCE)*.S))
CSRCS := $(patsubst $(SOURCE)%.c, $(BUILD)%.o, $(wildcard $(SOURCE)*.c))

OBJECTS = $(ASSRCS) $(CSRCS)

# Rule to make the listing file.
$(KRNL).list : $(KRNL).elf
	$(ARM)-objdump -d $(KRNL).elf > $(LIST)

# Rule to make the image file.
$(KRNL).img : $(KRNL).elf
	$(ARM)-objcopy $(KRNL).elf -O binary $(KRNL).img

# Rule to make the elf file.
$(KRNL).elf : $(OBJECTS) $(LINKER)
	$(ARM)-ld $(LDOPTS) $(OBJECTS) -Map $(KRNL).map -o $(KRNL).elf -T $(KRNL).ld

# Rule to make the object files.
%.o: %.S
	$(CC) -D$(PLAT) -I. $(ASOPTS) $< -c -o $@

%.o: %.c
	$(CC) -D$(PLAT) -I. $(CFLAGS)  $< -c -o $@

# Rule to clean files.
clean : 
	rm -f *.o
	rm -f $(KRNL).img $(KRNL).elf
	rm -f $(KRNL).list $(KRNL).map
	rm -f $(KRNL)
