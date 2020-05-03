package com.github.chuckb.raspbootin;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BootLoaderDriverUnitTest {
  @Mock OutputStream targetOutputStream;
  @Mock InputStream targetInputStream;
  @Mock OutputStream forwardingStream;
  @Mock InputStream auxillaryInputStream;
  @Mock PrintStream messageStream;
  InputStream imageInputStream;
  File imgFile;

  @BeforeEach
  public void init() throws IOException {
    // Set up fake target serial communication
    when(targetInputStream.read())
      .thenReturn(0x03)
      .thenReturn(0x03)
      .thenReturn(0x03)
      .thenReturn((int)'O')
      .thenReturn((int)'K');
    when(targetInputStream.available())
      .thenReturn(1)
      .thenReturn(1)
      .thenReturn(1)
      .thenReturn(1)
      .thenReturn(1)
      .thenThrow(IOException.class);
    // Set up fake input image file to transmit to target
    imgFile = File.createTempFile("kernel-", ".img");
    imgFile.deleteOnExit();
    PrintStream stream = new PrintStream(imgFile);
    stream.print("data");
    stream.close();
    imageInputStream = new FileInputStream(imgFile.getAbsolutePath());
  }

  @Test
  public void itSendsImageFileWhenSignaled() throws IOException {
    // Assemble
    InOrder inOrder = inOrder(targetOutputStream);
    BootLoaderDriver driver = new BootLoaderDriver(
      (int)imgFile.length(), 
      imgFile.getAbsolutePath(), 
      imageInputStream, 
      targetOutputStream, 
      targetInputStream, 
      forwardingStream, 
      auxillaryInputStream, 
      messageStream);

    // Act
    try {
      driver.run();
    } catch (IOException e) {
      // expected...forces break out of loop
    }

    // Assert
    // Int length of 4 written as 4 bytes, then the data in the file
    inOrder.verify(targetOutputStream).write(4);
    inOrder.verify(targetOutputStream, times(3)).write(0);
    inOrder.verify(targetOutputStream).write((int)'d');
    inOrder.verify(targetOutputStream).write((int)'a');
    inOrder.verify(targetOutputStream).write((int)'t');
    inOrder.verify(targetOutputStream).write((int)'a');
  }

  @Test
  public void itForwardsBytesFromConsoleToTarget() throws IOException {
    // Assemble
    when(auxillaryInputStream.available()).thenReturn(1).thenReturn(0).thenReturn(0);
    when(auxillaryInputStream.read()).thenReturn(32);
    BootLoaderDriver driver = new BootLoaderDriver(
      (int)imgFile.length(), 
      imgFile.getAbsolutePath(), 
      imageInputStream, 
      targetOutputStream, 
      targetInputStream, 
      forwardingStream, 
      auxillaryInputStream, 
      messageStream);

    // Act
    try {
      driver.run();
    } catch (IOException e) {
      // expected...forces break out of loop
    }

    // Assert
    verify(targetOutputStream).write(32);
  }

  @Test
  public void itForwardsBytesFromTargetToConsole() throws IOException {
    // Assemble
    when(targetInputStream.available()).thenReturn(1).thenThrow(IOException.class);
    when(targetInputStream.read()).thenReturn(32);
    BootLoaderDriver driver = new BootLoaderDriver(
      (int)imgFile.length(), 
      imgFile.getAbsolutePath(), 
      imageInputStream, 
      targetOutputStream, 
      targetInputStream, 
      forwardingStream, 
      auxillaryInputStream, 
      messageStream);

    // Act
    try {
      driver.run();
    } catch (IOException e) {
      // expected...forces break out of loop
    }

    // Assert
    verify(forwardingStream).write(32);
  }
}