/*
 * Copyright © 2017, Rex Hoffman
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
package org.keyring;

import org.keyring.util.LockException;

/**
 * java-keyring backend interface.
 */
public abstract class KeyringBackend {

  /**
   * Path to key store.
   */
  protected String keyStorePath;
  
  /**
   * Gets backend Id.
   * 
   * @return the identifier for the backend.
   */
  public abstract String getId();

  /**
   * Setup actual key store.
   * 
   * @throws BackendNotSupportedException if setup fails.
   */
  public abstract void setup() throws BackendNotSupportedException;

  /**
   * Gets path to key store.
   */
  public String getKeyStorePath() {
    return keyStorePath;
  }

  /**
   * Sets path to key store.
   *
   * @param path
   *          Path to key store
   */
  public void setKeyStorePath(String path) {
    keyStorePath = path;
  }

  /**
   * Returns true when the backend is supported.
   */
  public abstract boolean isSupported();

  /**
   * Returns true if the backend directory uses some file to store passwords.
   */
  public abstract boolean isKeyStorePathRequired();

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
  public abstract String getPassword(String service, String account) throws LockException, PasswordRetrievalException;

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
  public abstract void setPassword(String service, String account, String password)
      throws LockException, PasswordSaveException;

  public abstract void deletePassword(String service, String account) throws LockException, PasswordSaveException;
}
