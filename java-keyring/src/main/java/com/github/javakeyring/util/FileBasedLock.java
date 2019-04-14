/*
 * Copyright © 2019, Java Keyring
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.javakeyring.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * File based lock.
 */
public class FileBasedLock {

  /**
   * Path to a file to be used to lock.
   */
  private final String path;

  /**
   * A File instance to be used to lock.
   */
  private File file;

  /**
   * A FileChannel instance obtained from m_file.
   */
  private FileChannel channel;

  /**
   * A FileLock instance obtained from m_channel.
   */
  private FileLock lock;
  
  /**
   * Initializes an instance of FileBasedLock.
   *
   * @param path
   *          Path to a file to be used to lock
   */
  public FileBasedLock(String path) {
    this.path = path;
  }

  /**
   * Lock.
   * 
   * @throws LockException an exception if a file is already set up as a lock.
   */
  public synchronized void lock() throws LockException {
    if (file != null || channel != null || lock != null) {
      throw new LockException("Already locked", null);
    }

    try {
      file = new File(path);
      file.createNewFile();

      channel = new RandomAccessFile(file, "rw").getChannel();
      lock = channel.lock();
    } catch (IOException ex) {
      throw new LockException("Failed to obtain lock", ex);
    }
  }

  /**
   * Release lock.
   * 
   * @throws LockException an exception if a file is already set up as a lock.
   */
  public synchronized void release() throws LockException {
    //
    try {
      if (lock != null && lock.isValid()) {
        lock.release();
      }
    } catch (Exception ex) {
      Logger.getLogger(FileBasedLock.class.getName()).log(Level.SEVERE, null, ex);
    }

    try {
      if (channel != null && channel.isOpen()) {
        channel.close();
      }
    } catch (Exception ex) {
      Logger.getLogger(FileBasedLock.class.getName()).log(Level.SEVERE, null, ex);
    }

    //
    file = null;
    channel = null;
    lock = null;
  }

  /**
   * Returns path to a file to be used to lock.
   * 
   * @return path to a file to be used to lock.
   */
  public String getPath() {
    return path;
  }

}
