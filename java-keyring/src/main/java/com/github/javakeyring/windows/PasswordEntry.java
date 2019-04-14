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
package com.github.javakeyring.windows;

import java.io.Serializable;

/**
 * Password Entry.
 */
class PasswordEntry implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Service name.
   */
  private String service;

  /**
   * Account name.
   */
  private String account;

  /**
   * Password.
   */
  private byte[] password;

  
  /**
   * Initializes an instance of PasswordEntry.
   *
   * @param service
   *          Service name
   * @param account
   *          Account name
   * @param password
   *          Password
   */
  public PasswordEntry(String service, String account, byte[] password) {
    this.service = service;
    this.account = account;
    this.password = password;
  }

  /**
   * Returns service name.
   */
  public String getService() {
    return service;
  }

  /**
   * Sets service name.
   */
  public void setService(String service) {
    this.service = service;
  }

  /**
   * Returns account name.
   */
  public String getAccount() {
    return account;
  }

  /**
   * Sets account name.
   */
  public void setAccount(String account) {
    this.account = account;
  }

  /**
   * Returns password.
   */
  public byte[] getPassword() {
    return password;
  }

  /**
   * Sets password.
   */
  public void setPassword(byte[] password) {
    this.password = password;
  }

}
