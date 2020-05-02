package com.github.chuckb.raspbootin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Logic to boot load a remote target by listening for start sequence, and then sending
 * a binary image file. Also act as a serial terminal by forwarding keystrokes from the
 * target and printing any received data, after booting, from the target.
 */
public class BootLoaderDriver {
  private static final int NOBYTE = 0xFFFFFFFF;
  private final InputStream imageInputStream;
  private final OutputStream targetOutputStream;
  private final InputStream targetInputStream;
  private final OutputStream forwardingStream;
  private final InputStream auxillaryInputStream;
  private final PrintStream messageStream;
  private final int imageSize;
  private final String imageFileName;

  /**
   * Construct a boot loader driver object.
   * 
   * @param imageInputStream        Input stream of the boot file image to be loaded to the target.
   * @param targetOutputStream      Output stream to target for sending boot image and forwarded user data.
   * @param targetInputStream       Input stream from target for receiving boot image start sequence and other communication.
   * @param forwardingStream        Output stream to forward information received from target.
   * @param auxillaryInputStream    Input stream, usually from console, to receive data that will be forwarded to the target.
   * @param messageStream           Output stream to receive messages from driver.
   */
  public BootLoaderDriver(int imageSize,
      String imageFileName,
      InputStream imageInputStream, 
      OutputStream targetOutputStream,
      InputStream targetInputStream,
      OutputStream forwardingStream,
      InputStream auxillaryInputStream,
      PrintStream messageStream) {
    // Wire up local instance vars
    this.imageSize = imageSize;
    this.imageInputStream = imageInputStream;
    this.targetOutputStream = targetOutputStream;
    this.targetInputStream = targetInputStream;
    this.forwardingStream = forwardingStream;
    this.auxillaryInputStream = auxillaryInputStream;
    this.messageStream = messageStream;
    this.imageFileName = imageFileName;
  }

  /**
   * Execute the boot loader driver process. It listens for three break characters (0x03, 0x03, 0x03)
   * from the target. Once received, it sends the size of the image file, and then the file itself.
   * It also acts as a serial terminal of sorts passing characters back and forth between the system
   * console and the target.
   * @throws IOException
   */
  public void run() throws IOException {
    int breaks = 0;                     // The number of 0x03 breaks received from the target
    boolean sent = false;
    int targetInputByte = NOBYTE;       // This means no byte
    int auxillaryInputByte = NOBYTE;    // This means no byte

    while(true) {
      // Scan input sources and update state variables
      if (targetInputStream.available() > 0) {
        targetInputByte = targetInputStream.read();
        if (targetInputByte == 0x03 && !sent) {
          breaks++;
          targetInputByte = NOBYTE;
        }
      }

      if (auxillaryInputStream.available() > 0) {
        auxillaryInputByte = auxillaryInputStream.read();
      }

      // If we have 3 breaks, start the boot image sending process
      if (breaks == 3) {
        sendImage();
        breaks = 0;
        sent = true;
        if (auxillaryInputByte != NOBYTE) {
          messageStream.println("Discarding input after tripple break");
          auxillaryInputByte = NOBYTE;
        }
      }

      // If we have an aux byte from user, send it to target
      if (auxillaryInputByte != NOBYTE) {
        targetOutputStream.write(auxillaryInputByte);
        auxillaryInputByte = NOBYTE;
      }

      // If we have a byte from the target, forward it on
      if (targetInputByte != NOBYTE) {
        forwardingStream.write(targetInputByte);
        targetInputByte = NOBYTE;
      }
    }
  }

  /**
   * Sends the image file to the target.
   * 
   * @throws IOException
   */
  private void sendImage() throws IOException {
    messageStream.println(String.format("### sending kernel %s [%d byte]", imageFileName, imageSize));
    sendImageSize();
    if (receiveImageSizeReply()) {
      while(imageInputStream.available() > 0) {
        targetOutputStream.write(imageInputStream.read());
      }
      messageStream.println("### finished sending");
    }
  }

  /**
   * Send the size of the image (assumes to be a 32-bit integer) to the target
   * 
   * @throws IOException
   */
  private void sendImageSize() throws IOException {
    targetOutputStream.write(imageSize & 0xFF);
    targetOutputStream.write(imageSize >> 8 & 0xFF);
    targetOutputStream.write(imageSize >> 16 & 0xFF);
    targetOutputStream.write(imageSize >> 24 & 0xFF);
  }

  /**
   * Waits for 2 characters from the target. Expecting "OK" or "SE".
   * "OK" means size ok. "SE" means size error, which means it is too big.
   * Anything else is unexpected and an error. But, any 0x03 received will be
   * dropped. 
   * 
   * @return    true if ok, otherwise false
   * 
   * @throws IOException
   */
  private boolean receiveImageSizeReply() throws IOException {
    char char1 = 0;
    char char2 = 0;
    while (true) {
      if (targetInputStream.available() > 0) {
        // Read the first character in reply if not already read
        if (char1 == 0) {
          char1 = (char)targetInputStream.read();
          if (char1 == 0x03) {      // Ignore extraneous break characters if sent
            char1 = 0;
          }
        } else {
          // Read the second character in reply
          char2 = (char)targetInputStream.read();
          if (char2 == 0x03) {      // Ignore extraneous break characters if sent
            char2 = 0;
          } else {
            // We got expected two characters...bail out
            break;
          }
        }
      }
    }
    if (char1 == 'O' && char2 == 'K') {
      return true;
    } else if (char1 == 'S' && char2 == 'E') {
      messageStream.println("### received size error");
      return false;
    } else {
      messageStream.println(String.format("error after sending size; got [%x, %x]", char1, char2));
      return false;
    }
  }
}