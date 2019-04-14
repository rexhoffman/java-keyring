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
package com.github.javakeyring.memory;

import java.util.HashMap;
import java.util.Map;

import com.github.javakeyring.KeyringBackend;
import com.github.javakeyring.PasswordRetrievalException;
import com.github.javakeyring.PasswordSaveException;
import com.github.javakeyring.util.LockException;

/**
 * On-memory key store.
 */
public class UnencryptedMemoryBackend extends KeyringBackend {

  /**
   * Password container.
   */
  private Map<String[], String> unencryptedMemoryStore; // { {ServiceName, AccountName} => Password }
  
  /**
   * Initializes an instance of UncryptedMemoryBackend.
   */
  public UnencryptedMemoryBackend() {
    unencryptedMemoryStore = new HashMap<>();
  }

  /**
   * Returns true when the backend is supported.
   */
  @Override
  public boolean isSupported() {
    return true;
  }

  /**
   * Returns true if the backend directory uses some file to store passwords.
   */
  @Override
  public boolean isKeyStorePathRequired() {
    return false;
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
  public String getPassword(String service, String account) throws LockException, PasswordRetrievalException {

    synchronized (unencryptedMemoryStore) {
      //
      for (Map.Entry<String[], String> entries : unencryptedMemoryStore.entrySet()) {
        String[] serviceAndAccount = entries.getKey();

        if (serviceAndAccount[0].equals(service) && serviceAndAccount[1].equals(account)) {
          return entries.getValue();
        }
      }

      //
      throw new PasswordRetrievalException("Password related to the specified service and account is not found");
    } // synchronized
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
  public void setPassword(String service, String account, String password) throws LockException, PasswordSaveException {
    synchronized (unencryptedMemoryStore) {
      String[] targetKey = findEntry(service, account);
      if (targetKey == null) {
        targetKey = new String[] { service, account };
      }
      unencryptedMemoryStore.put(targetKey, password);
    }
  }

  /**
   * Delete password from key store.
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
  public void deletePassword(String service, String account) throws LockException, PasswordSaveException {
    synchronized (unencryptedMemoryStore) {
      String[] targetKey = findEntry(service, account);
      if (targetKey != null) {
        unencryptedMemoryStore.remove(targetKey);
      }
    }
  }

  private String[] findEntry(String service, String account) {
    String[] targetKey = null;

    for (Map.Entry<String[], String> entries : unencryptedMemoryStore.entrySet()) {
      String[] serviceAndAccount = entries.getKey();

      if (serviceAndAccount[0].equals(service) && serviceAndAccount[1].equals(account)) {
        targetKey = serviceAndAccount;
        break;
      }
    }
    return targetKey;
  }
  
  
  /**
   * Gets backend ID.
   */
  @Override
  public String getId() {
    return "UncryptedMemory";
  }
}
