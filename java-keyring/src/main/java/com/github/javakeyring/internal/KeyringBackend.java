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
package com.github.javakeyring.internal;

import com.github.javakeyring.KeyStorePath;
import com.github.javakeyring.PasswordAccessException;

/**
 * <p>
 * java-keyring backend interface.
 * </p>
 * May also implement {@link KeyStorePath} if there is support for backing files on they file system.
 */
public interface KeyringBackend {

  /**
   * Returns true when the back end is supported.
   *
   * @return true if the back end will function on the machine.
   */
  public boolean isSupported();

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
   * @throws PasswordAccessException
   *           Thrown when an error happened while getting password
   */
  public String getPassword(String service, String account) throws PasswordAccessException;

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
   * @throws PasswordAccessException
   *           Thrown when an error happened while saving the password
   */
  public void setPassword(String service, String account, String password)
      throws PasswordAccessException;

  /**
   * Deletes password from keystore.
   *
   * @param service
   *          Service name
   * @param account
   *          Account name
   *
   * @throws PasswordAccessException
   *           Thrown when an error happened while saving the password
   */
  public void deletePassword(String service, String account) throws PasswordAccessException;
}
