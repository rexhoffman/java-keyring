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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assume.assumeTrue;

import java.io.File;

import org.junit.Test;

import com.github.javakeyring.KeyStorePath;
import com.github.javakeyring.PasswordAccessException;
import com.github.javakeyring.internal.gnome.GnomeKeyringBackend;
import com.sun.jna.Platform;

/**
 * Test of GnomeKeyringBackend class.
 */
public class GnomeKeyringBackedTest {

  private static final String SERVICE = "net.east301.keyring.osx.part2 unit test";

  private static final String ACCOUNT = "testerpart2";

  private static final String PASSWORD = "HogeHoge2012part2";
  
  private static final String KEYSTORE_PREFIX = "keystore";

  private static final String KEYSTORE_SUFFIX = ".keystore";

  /**
   * Test of setup method, of class GnomeKeyringBackend.
   */
  @Test
  public void testSetup() throws Exception {
    assumeTrue(Platform.isLinux());
    assertThat(catchThrowable(() -> new GnomeKeyringBackend())).as("Setup should succeed").doesNotThrowAnyException();
  }

  /**
   * Test of isSupported method, of class GnomeKeyringBackend.
   */
  @Test
  public void testIsSupported() throws Exception {
    assumeTrue(Platform.isLinux());
    assertThat(new GnomeKeyringBackend().isSupported()).isTrue();
  }

  /**
   * Test of isKeyStorePathRequired method, of class GnomeKeyringBackend.
   */
  @Test
  public void testIsKeyStorePathRequired() throws Exception {
    assumeTrue(Platform.isLinux());
    assertThat(new GnomeKeyringBackend()).isInstanceOf(KeyStorePath.class);
  }

  /**
   * Test of getPassword method, of class GnomeKeyringBackend.
   */
  @Test
  public void testPasswordFlow() throws Exception {
    assumeTrue(Platform.isLinux());
    GnomeKeyringBackend backend = new GnomeKeyringBackend();
    backend.setKeyStorePath(File.createTempFile(KEYSTORE_PREFIX, KEYSTORE_SUFFIX).getPath());
    catchThrowable(() -> backend.deletePassword(SERVICE, ACCOUNT));
    checkExistanceOfPasswordEntry(backend);
    backend.setPassword(SERVICE, ACCOUNT, PASSWORD);
    assertThat(backend.getPassword(SERVICE, ACCOUNT)).isEqualTo(PASSWORD);
    backend.deletePassword(SERVICE, ACCOUNT);
    assertThatThrownBy(() -> backend.getPassword(SERVICE, ACCOUNT)).isInstanceOf(PasswordAccessException.class);
  }

  private static void checkExistanceOfPasswordEntry(GnomeKeyringBackend backend) {
    assertThatThrownBy(() -> backend.getPassword(SERVICE, ACCOUNT))
       .as("Please remove password entry '%s' " + "by using Keychain Access before running the tests", SERVICE)
       .isNotNull();
  }
}