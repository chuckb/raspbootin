ARM = arm-none-eabi
CC = $(ARM)-gcc
AS = $(ARM)-as

#ASOPTS=-march=arm -mcpu=arm1176jzf-s
ASOPTS=-march=armv6 -mcpu=arm1176jzf-s -g
LDOPTS=
CFLAGS=-std=c99 -Wall -pedantic -g -O0

KRNL=kernel

# The names of all object files that must be generated. Deduced from the 
# assembly code files in source.
ASSRCS := $(patsubst $(SOURCE)%.s,$(BUILD)%.o,$(wildcard $(SOURCE)*.s))
CSRCS := $(patsubst $(SOURCE)%.c, $(BUILD)%.o, $(wildcard $(SOURCE)*.c))

OBJECTS = $(ASSRCS) $(CSRCS)

# Rule to make everything.
all: 
	make rpi
	
rpi: PLAT=PLATFORM_RPI 
rpi: $(KRNL).img

rpi2: PLAT=PLATFORM_RPI2 
rpi2: $(KRNL).img

rpibp: PLAT=PLATFORM_RPIBP 
rpibp: $(KRNL).img 

dump: $(KRNL).img

# Rule to remake everything. Does not include clean.
rebuild: all

# Rule to make the listing file.
$(KRNL).list : $(KRNL).elf
	$(ARM)-objdump -d $(KRNL).elf > $(LIST)

# Rule to make the image file.
$(KRNL).img : $(KRNL).elf
	$(ARM)-objcopy $(KRNL).elf -O binary $(KRNL).img

# Rule to make the elf file.
$(KRNL).elf : $(OBJECTS) $(LINKER)
	$(ARM)-ld --no-undefined $(OBJECTS) $(LDOPTS) -Map $(KRNL).map -o $(KRNL).elf -T $(KRNL).ld

# Rule to make the object files.
%.o: %.s
	$(AS) -I. $(ASOPTS) $< -o $@

%.o: %.c
	$(CC) -D$(PLAT) -I. $(CFLAGS)  $< -c -o $@

# Rule to clean files.
clean : 
	rm -f *.o
	rm -f $(KRNL).img $(KRNL).elf
	rm -f $(KRNL).list $(KRNL).map
	rm -f $(KRNL)
