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
package com.github.javakeyring.internal.windows;

import java.nio.charset.Charset;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.PasswordAccessException;
import com.github.javakeyring.internal.KeyringBackend;
import com.sun.jna.Memory;
import com.sun.jna.Platform;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.ptr.PointerByReference;

/**
 * A Windows "Credential Store" backend.
 *
 */
public class WinCredentialStoreBackend implements KeyringBackend {

  /*
   * Big thanks to this stack overflow for this one.
   * https://stackoverflow.com/questions/38404517/how-to-map-windows-api-credwrite-credread-in-jna
   */

  private final NativeLibraryManager nativeLibraries;

  public WinCredentialStoreBackend() throws BackendNotSupportedException {
    nativeLibraries = new NativeLibraryManager();
  }

  /**
   * Returns true when the backend is supported.
   */
  @Override
  public boolean isSupported() {
    return Platform.isWindows();
  }

  @Override
  public String getPassword(String service, String account) throws PasswordAccessException {
    PointerByReference ref = new PointerByReference();
    DWORD type = new DWORD(1L);
    DWORD unused = new DWORD(0L);
    Boolean success = nativeLibraries.getAdvapi32().CredReadA(service, type, unused, ref);
    if (!success) {
      throw new PasswordAccessException("Error code " + nativeLibraries.getKernel32().GetLastError());
    }
    CREDENTIAL cred = new CREDENTIAL(ref.getValue());
    //TODO: verify username?  or lookup by service & username?
    //String userName = cred.UserName.toString();
    try {
      byte[] passbytes = cred.CredentialBlob.getByteArray(0,cred.CredentialBlobSize);
      return new String(passbytes, Charset.forName("UTF-16LE"));    
    } catch (Exception ex) {
      throw new PasswordAccessException(ex.getMessage());
    } finally {
      nativeLibraries.getAdvapi32().CredFree(ref);
    }
    
  }

  @Override
  public void setPassword(String service, String account, String password) throws PasswordAccessException {
    CREDENTIAL cred = new CREDENTIAL();
    cred.TargetName = service;
    cred.UserName = account;
    cred.Type = 1;
    byte[] bytes = password.getBytes(Charset.forName("UTF-16LE"));
    Memory passwordMemory = new Memory(bytes.length);
    passwordMemory.write(0, bytes, 0, bytes.length);
    cred.CredentialBlob = passwordMemory;
    cred.CredentialBlobSize = bytes.length;
    cred.Persist = 2;
    Boolean success = nativeLibraries.getAdvapi32().CredWriteA(cred, new DWORD(0));
    passwordMemory.clear();
    if (!success) {
      throw new PasswordAccessException("Error code " + nativeLibraries.getKernel32().GetLastError().intValue());
    }
  }

  @Override
  public void deletePassword(String service, String account) throws PasswordAccessException {
    boolean success = nativeLibraries.getAdvapi32().CredDeleteA(service, new DWORD(1), new DWORD(0));
    System.out.println(success);    
  }

}
