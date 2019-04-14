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

/**
 * Factory of KeyringBackend.
 */
class KeyringBackendFactory {

  /**
   * Creates an instance of KeyringBackend.
   */
  public static KeyringBackend create() throws BackendNotSupportedException {
    for (Keyrings keyRing : Keyrings.values()) {
      KeyringBackend backend = tryToCreateBackend(keyRing);
      if (backend != null) {
        return backend;
      }
    }
    throw new BackendNotSupportedException("No available keyring backend found");
  }

  /**
   * Creates an instance of KeyringBackend.
   *
   * @param preferred
   *          Preferred backend name
   */
  public static KeyringBackend create(Keyrings preferred) throws BackendNotSupportedException {
    KeyringBackend backend = tryToCreateBackend(preferred);
    if (backend == null) {
      throw new BackendNotSupportedException(String.format("The backend '%s' is not supported", preferred));
    }
    return backend;
  }

  /**
   * Try to create keyring backend instance from Class.
   *
   * @param backendClass
   *          Target backend class
   */
  private static KeyringBackend tryToCreateBackend(Keyrings keyring) {
    KeyringBackend backend;
    try {
      backend = (KeyringBackend) keyring
              .getSupportingClass()
              .getConstructor(new Class[] {})
              .newInstance(new Object[]{});
    } catch (Exception ex) {
      return null;
    }
    if (!backend.isSupported()) {
      return null;
    }
    return backend;
  }
}
