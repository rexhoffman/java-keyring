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
package org.keyring.osx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assume.assumeTrue;

import org.junit.Test;
import org.keyring.PasswordRetrievalException;

import com.sun.jna.Platform;

/**
 * Test of OSXKeychainBackend class.
 */
public class OsxKeychainBackendTest {

  private static final String SERVICE = "net.east301.keyring.osx.part2 unit test";

  private static final String ACCOUNT = "testerpart2";

  private static final String PASSWORD = "HogeHoge2012part2";

  /**
   * Test of setup method, of class OSXKeychainBackend.
   */
  @Test
  public void testSetup() throws Exception {
    assumeTrue(Platform.isMac());
    assertThat(catchThrowable(() -> new OsxKeychainBackend().setup())).as("Setup should succeed").doesNotThrowAnyException();
  }

  /**
   * Test of isSupported method, of class OSXKeychainBackend.
   */
  @Test
  public void testIsSupported() {
    assumeTrue(Platform.isMac());
    assertThat(new OsxKeychainBackend().isSupported()).isTrue();
  }

  /**
   * Test of isKeyStorePathRequired method, of class OSXKeychainBackend.
   */
  @Test
  public void testIsKeyStorePathRequired() {
    assumeTrue(Platform.isMac());
    assertThat(new OsxKeychainBackend().isKeyStorePathRequired()).isFalse();
  }

  /**
   * Test of getPassword method, of class OSXKeychainBackend.
   */
  @Test
  public void testPasswordFlow() throws Exception {
    assumeTrue(Platform.isMac());
    OsxKeychainBackend backend = new OsxKeychainBackend();
    backend.setup();
    catchThrowable(() -> backend.deletePassword(SERVICE, ACCOUNT));
    checkExistanceOfPasswordEntry(backend);
    backend.setPassword(SERVICE, ACCOUNT, PASSWORD);
    assertThat(backend.getPassword(SERVICE, ACCOUNT)).isEqualTo(PASSWORD);
    backend.deletePassword(SERVICE, ACCOUNT);
    assertThatThrownBy(() -> backend.getPassword(SERVICE, ACCOUNT)).isInstanceOf(PasswordRetrievalException.class);
  }

  /**
   * Test of getID method, of class OSXKeychainBackend.
   */
  @Test
  public void testGetId() {
    assertThat(new OsxKeychainBackend().getId()).isEqualTo("OSXKeychain");
  }

  private static void checkExistanceOfPasswordEntry(OsxKeychainBackend backend) {
    assertThatThrownBy(() -> backend.getPassword(SERVICE, ACCOUNT))
       .as("Please remove password entry '%s' " + "by using Keychain Access before running the tests", SERVICE)
       .isNotNull();
  }
}
