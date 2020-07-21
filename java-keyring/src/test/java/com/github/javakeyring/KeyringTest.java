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
package com.github.javakeyring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.sun.jna.Platform;

/**
 * Test of Keyring class.
 */
public class KeyringTest {

  private static final String SERVICE = "com.github.javakeyring.test";

  private static final String ACCOUNT1 = "username1";

  private static final String PASSWORD1 = "password1";  
  
  private static final String ACCOUNT2 = "username2";

  private static final String PASSWORD2 = "password2";
  
  private static final String ACCOUNT3 = "username3";

  private static final String PASSWORD3 = "password3";
  
  /**
   * Test of create method, of class Keyring.
   */
  @Test
  public void testCreateZeroArgs() throws Exception {
    Keyring keyring = Keyring.create();
    assertNotNull(keyring);
  }

  /**
   * Test of create method, of class Keyring.
   */
  @Test
  public void testCreateString() throws Exception {
    if (Platform.isMac()) {
      assertThat(Keyring.create(KeyringStorageType.OSX_KEYCHAIN)).isNotNull();
      assertThatThrownBy(() -> Keyring.create(KeyringStorageType.WINDOWS_CREDENTIAL_STORE))
         .isInstanceOf(BackendNotSupportedException.class);
      assertThatThrownBy(() -> Keyring.create(KeyringStorageType.GNOME_KEYRING))
          .isInstanceOf(BackendNotSupportedException.class);
    } else if (Platform.isWindows()) {
      assertThat(Keyring.create(KeyringStorageType.WINDOWS_CREDENTIAL_STORE)).isNotNull();
      assertThatThrownBy(() -> Keyring.create(KeyringStorageType.OSX_KEYCHAIN))
          .isInstanceOf(BackendNotSupportedException.class);
      assertThatThrownBy(() -> Keyring.create(KeyringStorageType.GNOME_KEYRING))
          .isInstanceOf(BackendNotSupportedException.class);
    } else if (Platform.isLinux()) {
      //linux may use GNOME_KEYRING or KWALLET
      KeyringStorageType type = Keyring.create().getKeyringStorageType();
      assertThat(type == KeyringStorageType.GNOME_KEYRING || type == KeyringStorageType.KWALLET).isTrue();

      assertThatThrownBy(() -> Keyring.create(KeyringStorageType.OSX_KEYCHAIN))
          .isInstanceOf(BackendNotSupportedException.class);
      assertThatThrownBy(() -> Keyring.create(KeyringStorageType.WINDOWS_CREDENTIAL_STORE))
          .isInstanceOf(BackendNotSupportedException.class);
    }
  }

  /**
   * Test of getPassword method, of class OSXKeychainBackend.
   */
  @Test
  public void testPasswordFlow() throws Exception {
    Keyring keyring = Keyring.create();
    catchThrowable(() -> keyring.deletePassword(SERVICE, ACCOUNT1));
    assertThatThrownBy(() -> keyring.deletePassword(SERVICE, ACCOUNT1)).isInstanceOf(PasswordAccessException.class);
    assertThatThrownBy(() -> keyring.getPassword(SERVICE, ACCOUNT1)).isInstanceOf(PasswordAccessException.class);
    keyring.setPassword(SERVICE, ACCOUNT1, PASSWORD1);
    assertThat(keyring.getPassword(SERVICE, ACCOUNT1)).isEqualTo(PASSWORD1);
    //overwrite password
    keyring.setPassword(SERVICE, ACCOUNT1, PASSWORD1 + "1");
    assertThat(keyring.getPassword(SERVICE, ACCOUNT1)).isEqualTo(PASSWORD1 + "1");
    keyring.deletePassword(SERVICE, ACCOUNT1);
    assertThatThrownBy(() -> keyring.getPassword(SERVICE, ACCOUNT1)).isInstanceOf(PasswordAccessException.class);
  }
  

  /**
   * Test of getPassword method, of class OSXKeychainBackend.
   */
  @Test
  public void testNoCollisions() throws Exception {
    Keyring keyring = Keyring.create();
    
    //ensure empty keychain
    catchThrowable(() -> keyring.deletePassword(SERVICE, ACCOUNT2));
    assertThatThrownBy(() -> keyring.deletePassword(SERVICE, ACCOUNT2)).isInstanceOf(PasswordAccessException.class);
    assertThatThrownBy(() -> keyring.getPassword(SERVICE, ACCOUNT2)).isInstanceOf(PasswordAccessException.class);
    
    catchThrowable(() -> keyring.deletePassword(SERVICE, ACCOUNT3));
    assertThatThrownBy(() -> keyring.deletePassword(SERVICE, ACCOUNT3)).isInstanceOf(PasswordAccessException.class);
    assertThatThrownBy(() -> keyring.getPassword(SERVICE, ACCOUNT3)).isInstanceOf(PasswordAccessException.class);
    
    //create passwords
    keyring.setPassword(SERVICE, ACCOUNT2, PASSWORD2);
    keyring.setPassword(SERVICE, ACCOUNT3, PASSWORD3);
    
    //verify both passwords
    assertThat(keyring.getPassword(SERVICE, ACCOUNT3)).isEqualTo(PASSWORD3);
    assertThat(keyring.getPassword(SERVICE, ACCOUNT2)).isEqualTo(PASSWORD2);

    //delete them both
    keyring.deletePassword(SERVICE, ACCOUNT2);
    keyring.deletePassword(SERVICE, ACCOUNT3);
    assertThatThrownBy(() -> keyring.getPassword(SERVICE, ACCOUNT2)).isInstanceOf(PasswordAccessException.class);
    assertThatThrownBy(() -> keyring.getPassword(SERVICE, ACCOUNT3)).isInstanceOf(PasswordAccessException.class);
  }

}
