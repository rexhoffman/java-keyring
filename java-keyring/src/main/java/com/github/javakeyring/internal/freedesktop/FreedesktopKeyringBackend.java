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
package com.github.javakeyring.internal.freedesktop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.PasswordAccessException;
import com.github.javakeyring.internal.KeyringBackend;

/**
 * <p>
 * Keyring backend which uses Freedesktop Secret service.  Seems that concurrent reads/writes are unsafe.
 * Not sure if a problem with the java libraries or the secret-service in dbus.
 * </p>
 * Maybe replace with https://specifications.freedesktop.org/secret-service/?
 */
public class FreedesktopKeyringBackend implements KeyringBackend {
  
  //private static final String LOCK_STRING = "globalLock";

  private final SimpleCollection collection;

  public FreedesktopKeyringBackend() throws BackendNotSupportedException {
    try {
      collection = new SimpleCollection();
    } catch (IOException ex) {
      throw new BackendNotSupportedException("Error connecting to dbus", ex);
    }
  }

  private Map<String, String> getMap(String service, String account) {
    Map<String, String> output = new HashMap<>();
    output.put("service", service);
    output.put("account", account);
    return output;
  }
  
  private String getLabel(String service, String account) {
    return service + "|" + account;
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
   * @throws PasswordAccessException
   *           Thrown when an error happened while getting password
   */
  @Override
  public String getPassword(String service, String account) throws PasswordAccessException {
    List<String> objectPaths = getObjectPaths(getMap(service, account));
    if (objectPaths.size() > 1) {
      throwTooManyCredentialsException(service, account);
    }
    if (objectPaths.size() == 0) {
      throwNoExistingCredentialException(service, account);
    }
    return new String(collection.getSecret(objectPaths.get(0)));
  }

  private void throwTooManyCredentialsException(String service, String account) throws PasswordAccessException {
    throw new PasswordAccessException("Too many stored credentials match " + service + " account: " + account);
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
   * @throws PasswordAccessException
   *           Thrown when an error happened while saving the password
   */
  @Override
  public void setPassword(String service, String account, String password) throws PasswordAccessException {
    Map<String, String> attributes = getMap(service, account);
    String label = getLabel(service, account);
    List<String> objectPaths = getObjectPaths(attributes);
    if (objectPaths.size() > 1) {
      throwTooManyCredentialsException(service, account);
    }
    if (objectPaths.size() == 1) {
      collection.updateItem(objectPaths.get(0), label, password, attributes);
    } else {
      collection.createItem(label, password, attributes);
    }
  }

  private List<String> getObjectPaths(Map<String, String> attributes) {
    List<String> objectPaths = collection.getItems(attributes);
    if (objectPaths == null) {
      objectPaths = new ArrayList<String>();
    }
    return objectPaths;
  }
  
  /**
   * Delete password to key store.
   *
   * @param service
   *          Service name
   * @param account
   *          Account name
   * @throws PasswordAccessException
   *           Thrown when an error happened while deleting the password
   */
  @Override
  public void deletePassword(String service, String account) throws PasswordAccessException {
    List<String> objectPaths = getObjectPaths(getMap(service, account));
    if (objectPaths.size() > 1) {
      throwTooManyCredentialsException(service, account);
    }
    if (objectPaths.size() == 1) {
      collection.deleteItem(objectPaths.get(0));
    }
    if (objectPaths.size() == 0) {
      throwNoExistingCredentialException(service, account);
    }
  }

  private void throwNoExistingCredentialException(String service, String account) throws PasswordAccessException {
    throw new PasswordAccessException("No stored credentials match " + service + " account: " + account);
  }
}
