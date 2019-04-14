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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.github.advisedtesting.classloader.RestrictiveClassloader;
import com.github.advisedtesting.junit4.Junit4AopClassRunner;
import com.github.javakeyring.gnome.GnomeKeyringBackend;
import com.github.javakeyring.memory.UnencryptedMemoryBackend;
import com.github.javakeyring.osx.OsxKeychainBackend;
import com.github.javakeyring.windows.WindowsDpApiBackend;
import com.sun.jna.Platform;

/**
 * Test of KeyringBackendFactory class.
 */
@RunWith(Junit4AopClassRunner.class)
public class KeyringBackendFactoryTest {

  /**
   * Test of create method, of class KeyringBackendFactory.
   */
  @Test
  @RestrictiveClassloader
  public void testCreateZeroArgs() throws Exception {
    KeyringBackend backend = KeyringBackendFactory.create();
    assertNotNull(backend);
    if (Platform.isMac()) {
      assertTrue(backend instanceof OsxKeychainBackend);
    } else if (Platform.isWindows()) {
      assertTrue(backend instanceof WindowsDpApiBackend);
    } else if (Platform.isLinux()) {
      assertTrue(backend instanceof GnomeKeyringBackend);
    } else {
      fail("Unsupported platform");
    }
  }

  /**
   * Test of create method, of class KeyringBackendFactory by specifying
   * UncryptedMemory.
   */
  @Test
  @RestrictiveClassloader
  public void testCreateStringUnencryptedMemory() throws Exception {
    KeyringBackend backend = KeyringBackendFactory.create(Keyrings.UnencryptedMemory);
    assertNotNull(backend);
    assertTrue(backend instanceof UnencryptedMemoryBackend);
  }

}
