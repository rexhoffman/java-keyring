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
package com.github.javakeyring.gnome;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.KeyringBackend;
import com.github.javakeyring.PasswordRetrievalException;
import com.github.javakeyring.PasswordSaveException;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * Keyring backend which uses GNOME Keyring.
 */
public class GnomeKeyringBackend extends KeyringBackend {

  private final NativeLibraryManager libraries;
  
  public GnomeKeyringBackend() throws BackendNotSupportedException {
    libraries = new NativeLibraryManager();
    int result = libraries.getGklib().gnome_keyring_unlock_sync(null, null);
    if (result != 0) {
      throw new BackendNotSupportedException(libraries.getGklib().gnome_keyring_result_to_message(result));
    }
  }

  /**
   * Returns true when the backend is supported.
   */
  @Override
  public boolean isSupported() {
    return Platform.isLinux();
  }

  /**
   * Returns true if the backend directory uses some file to store passwords.
   */
  @Override
  public boolean isKeyStorePathRequired() {
    return true;
  }

  /**
   * Gets password from key store.
   *
   * @param service
   *          Service name
   * @param account
   *          Account name
   *
   * @return Password related to specified service and account
   *
   * @throws PasswordRetrievalException
   *           Thrown when an error happened while getting password
   */
  @Override
  public String getPassword(String service, String account) throws PasswordRetrievalException {

    PointerByReference ptr = new PointerByReference();
    Pointer item = null;
    Map<String, Integer> map = loadMap();
    Integer id = map.get(service + "/" + account);
    if (id == null) {
      throw new PasswordRetrievalException("No password stored for this service and account.");
    }
    try {
      int result = libraries.getGklib().gnome_keyring_item_get_info_full_sync(null, id, 1, ptr);
      if (result == 0) {
        return libraries.getGklib().gnome_keyring_item_info_get_secret(ptr.getValue());
      } else {
        throw new PasswordRetrievalException(libraries.getGklib().gnome_keyring_result_to_message(result));
      }
    } finally {
      if (item != null) {
        libraries.getGklib().gnome_keyring_item_info_free(item);
      }
    }
  }

  /**
   * Sets password to key store.
   *
   * @param service
   *          Service name
   * @param account
   *          Account name
   * @param password
   *          Password
   *
   * @throws PasswordSaveException
   *           Thrown when an error happened while saving the password
   */
  @Override
  public void setPassword(String service, String account, String password) throws PasswordSaveException {
    IntByReference ref = new IntByReference();
    int result = libraries.getGklib().gnome_keyring_set_network_password_sync(null, account, null, service, null,
        null, null, 0, password, ref);
    if (result != 0) {
      throw new PasswordSaveException(libraries.getGklib().gnome_keyring_result_to_message(result));
    }
    Map<String, Integer> map = loadMap();
    map.put(service + "/" + account, ref.getValue());
    saveMap(map);
  }
  
  /**
   * Delete password to key store.
   *
   * @param service
   *          Service name
   * @param account
   *          Account name
   *
   * @throws PasswordSaveException
   *           Thrown when an error happened while saving the password
   */
  @Override
  public void deletePassword(String service, String account) throws PasswordSaveException {
    Map<String, Integer> map = loadMap();
    map.remove(service + "/" + account);
    saveMap(map);
  }
  

  /**
   * Gets backend ID.
   */
  @Override
  public String getId() {
    return "GNOMEKeyring";
  }

  /**
   * Loads map from a file. This method is not thread/process safe.
   */
  @SuppressWarnings("unchecked")
  private Map<String, Integer> loadMap() {
    try {
      File keyStoreFile = new File(keyStorePath);
      if (keyStoreFile.exists() && keyStoreFile.length() > 0) {
        ObjectInputStream fin = new ObjectInputStream(new FileInputStream(keyStoreFile));
        try {
          return (Map<String, Integer>) fin.readObject();
        } finally {
          fin.close();
        }
      }
    } catch (Exception ex) {
      Logger.getLogger(GnomeKeyringBackend.class.getName()).log(Level.SEVERE, null, ex);
    }
    return new HashMap<>();
  }

  /**
   * Saves account/save to ID map to a file This method is not thread/process
   * safe.
   *
   * @param entries
   *          Map to be saved
   *
   * @throws PasswordSaveException
   *           Thrown when an error happened while writing to a file
   */
  private void saveMap(Map<String, Integer> map) throws PasswordSaveException {

    try {
      ObjectOutputStream fout = new ObjectOutputStream(new FileOutputStream(keyStorePath));
      try {
        fout.writeObject(map);
        fout.flush();
      } finally {
        fout.close();
      }
    } catch (Exception ex) {
      Logger.getLogger(GnomeKeyringBackend.class.getName()).log(Level.SEVERE, null, ex);
      throw new PasswordSaveException("Failed to save password entries to a file");
    }
  }
} // class GNOMEKeyringBackend
