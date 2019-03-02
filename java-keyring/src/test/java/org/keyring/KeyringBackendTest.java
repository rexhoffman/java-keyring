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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.keyring.util.LockException;

/**
 * Test of KeyringBackend class.
 */
public class KeyringBackendTest {

  /**
   * Test of getKeyStorePath method, of class KeyringBackend.
   */
  @Test
  public void testGetKeyStorePath() {
    KeyringBackend instance = new KeyringBackendImpl();
    assertNull(instance.getKeyStorePath());
    instance.setKeyStorePath("/path/to/keystore");
    assertEquals("/path/to/keystore", instance.getKeyStorePath());
  }

  /**
   * Test of setKeyStorePath method, of class KeyringBackend.
   */
  @Test
  public void testSetKeyStorePath() {
    KeyringBackend instance = new KeyringBackendImpl();
    instance.setKeyStorePath("/path/to/keystore");
    assertEquals("/path/to/keystore", instance.getKeyStorePath());
  }

  public class KeyringBackendImpl extends KeyringBackend {

    @Override
    public boolean isSupported() {
      return false;
    }

    @Override
    public boolean isKeyStorePathRequired() {
      return false;
    }

    @Override
    public String getPassword(String service, String account) throws LockException, PasswordRetrievalException {
      return "";
    }

    @Override
    public void setPassword(String service, String account, String password)
        throws LockException, PasswordSaveException {
      //no op.
    }
    
    @Override
    public void deletePassword(String service, String account)
        throws LockException, PasswordSaveException {
      //no op.
    }

    @Override
    public String getId() {
      return "";
    }

    @Override
    public void setup() throws BackendNotSupportedException {
      //no op.
    }
  }
}
