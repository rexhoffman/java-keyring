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
package com.github.keyring.example;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordRetrievalException;
import com.github.javakeyring.PasswordSaveException;
import com.github.javakeyring.util.LockException;

/**
 * Usage example of java-keyring library.
 */
public class Program {

  /**
   * Sample main method.
   * 
   * @param args
   *          the command line arguments.
   */
  public static void main(String[] args) {

    //
    // setup a Keyring instance
    //
    Keyring keyring;

    // create an instance of Keyring by invoking Keyring.create method
    //
    // Keyring.create method finds appropriate keyring backend, and sets it up
    // for you.
    // On Mac OS X environment, OS X Keychain is used, and On Windows
    // environment,
    // DPAPI is used for encryption of passwords.
    // If no supported backend is found, BackendNotSupportedException is thrown.
    try {
      keyring = Keyring.create();
    } catch (BackendNotSupportedException ex) {
      Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
      return;
    }

    // some backend directory handles a file to store password to disks.
    // in this case, we must set path to key store file by
    // Keyring.setKeyStorePath
    // before using Keyring.getPassword and Keyring.getPassword.
    if (keyring.isKeyStorePathRequired()) {
      try {
        File keyStoreFile = File.createTempFile("keystore", ".keystore");
        keyring.setKeyStorePath(keyStoreFile.getPath());
      } catch (IOException ex) {
        Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
      }
    }

    //
    // store password to key store
    //

    // Password can be stored to key store by using Keyring.setPassword method.
    // PasswordSaveException is thrown when some error happened while saving
    // password.
    // LockException is thrown when keyring backend failed to lock key store
    // file.
    try {
      keyring.setPassword("My service name", "My account name", "My password");
    } catch (LockException ex) {
      Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
      return;
    } catch (PasswordSaveException ex) {
      Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
      return;
    }

    //
    // Retrieve password from key store
    //

    // Password can be retrieved by using Keyring.getPassword method.
    // PasswordRetrievalException is thrown when some error happened while
    // getting password.
    // LockException is thrown when keyring backend failed to lock key store
    // file.
    try {
      String password = keyring.getPassword("My service name", "My account name");
      System.out.println(password);
    } catch (LockException ex) {
      Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
    } catch (PasswordRetrievalException ex) {
      Logger.getLogger(Program.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
} // class Program
