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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.ehoffman.classloader.RestrictiveClassloader;
import org.ehoffman.junit.aop.Junit4AopClassRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keyring.osx.OsxKeychainBackend;

import com.sun.jna.Platform;

/**
 * Test of Keyring class.
 */
@RunWith(Junit4AopClassRunner.class)
public class KeyringTest {

  private static final String SERVICE = "net.east301.keyring unit test";

  private static final String ACCOUNT = "tester";

  private static final String PASSWORD = "HogeHoge2012";

  private static final String KEYSTORE_PREFIX = "keystore";

  private static final String KEYSTORE_SUFFIX = ".keystore";
  
  /**
   * Test of create method, of class Keyring.
   */
  @Test
  @RestrictiveClassloader
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
      assertThat(Keyring.create(Keyrings.OSXKeychain)).isNotNull();
    } else if (Platform.isWindows()) {
      assertThat(Keyring.create(Keyrings.WindowsDPAPI)).isNotNull();
    } else if (Platform.isLinux()) {
      assertThat(Keyring.create(Keyrings.GNOMEKeyring)).isNotNull();
    }
    assertThat(Keyring.create(Keyrings.UnencryptedMemory)).isNotNull();
  }

  /**
   * Test of getKeyStorePath method, of class Keyring.
   */
  @Test
  public void testGetKeyStorePath() throws Exception {
    Keyring keyring = Keyring.create();
    assertNull(keyring.getKeyStorePath());
    keyring.setKeyStorePath("/path/to/keystore");
    assertEquals("/path/to/keystore", keyring.getKeyStorePath());
  }

  /**
   * Test of setKeyStorePath method, of class Keyring.
   */
  @Test
  public void testSetKeyStorePath() throws Exception {
    Keyring keyring = Keyring.create();
    keyring.setKeyStorePath("/path/to/keystore");
    assertEquals("/path/to/keystore", keyring.getKeyStorePath());
  }


  /**
   * Test of getPassword method, of class OSXKeychainBackend.
   */
  @Test
  public void testPasswordFlow() throws Exception {
    Keyring keyring = Keyring.create();
    if (keyring.isKeyStorePathRequired()) {
      keyring.setKeyStorePath(File.createTempFile(KEYSTORE_PREFIX, KEYSTORE_SUFFIX).getPath());
    }
    catchThrowable(() -> keyring.deletePassword(SERVICE, ACCOUNT));
    checkExistanceOfPasswordEntry(keyring);
    keyring.setPassword(SERVICE, ACCOUNT, PASSWORD);
    assertThat(keyring.getPassword(SERVICE, ACCOUNT)).isEqualTo(PASSWORD);
    keyring.deletePassword(SERVICE, ACCOUNT);
    assertThatThrownBy(() -> keyring.getPassword(SERVICE, ACCOUNT)).isInstanceOf(PasswordRetrievalException.class);
  }

  /**
   * Test of getID method, of class OSXKeychainBackend.
   */
  @Test
  public void testGetId() throws Exception {
    assertThat(new OsxKeychainBackend().getId()).isEqualTo("OSXKeychain");
  }

  private static void checkExistanceOfPasswordEntry(Keyring keyring) {
    assertThatThrownBy(() -> keyring.getPassword(SERVICE, ACCOUNT))
       .as("Please remove password entry '%s' " + "by using Keychain Access before running the tests", SERVICE)
       .isNotNull();
  }
}
