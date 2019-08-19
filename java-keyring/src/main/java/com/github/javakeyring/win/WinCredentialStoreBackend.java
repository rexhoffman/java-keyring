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
package com.github.javakeyring.win;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.KeyringBackend;
import com.github.javakeyring.PasswordRetrievalException;
import com.github.javakeyring.PasswordSaveException;
import com.sun.jna.Memory;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinBase.FILETIME;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.ptr.PointerByReference;

/**
 * A Windows "Credential Store" backend.
 *
 */
public class WinCredentialStoreBackend extends KeyringBackend {

  /*
   * Big thanks to this stack overflow for this one.
   * https://stackoverflow.com/questions/38404517/how-to-map-windows-api-credwrite-credread-in-jna
   */
  
  /**
   * represents a credential.
   */
  @SuppressWarnings({"AbbreviationAsWordInName","ParameterName","MemberName"})
  public static class CREDENTIAL extends Structure {
    public int Flags;
    public int Type;
    public String TargetName;
    public String Comment;
    public FILETIME LastWritten;
    public int CredentialBlobSize;
    public Pointer CredentialBlob;
    public int Persist;
    public int AttributeCount;
    public Pointer Attributes;
    public WString TargetAlias;
    public String UserName;

    public CREDENTIAL() { }

    public CREDENTIAL(Pointer ptr) {
      // initialize ourself from the raw memory block returned to us by ADVAPI32
      super(ptr);
      read();
    }

    @Override
    protected List<String> getFieldOrder() {
      return Arrays.asList("Flags", "Type", "TargetName", "Comment", "LastWritten", "CredentialBlobSize",
          "CredentialBlob", "Persist", "AttributeCount", "Attributes", "TargetAlias", "UserName");
    }
  }
  


  NativeLibraryManager nativeLibraries = new NativeLibraryManager();

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
  public String getId() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean isKeyStorePathRequired() {
    return false;
  }

  @Override
  public String getPassword(String service, String account) throws PasswordRetrievalException {
    PointerByReference ref = new PointerByReference();
    DWORD type = new DWORD(1L);
    DWORD unused = new DWORD(0L);
    Boolean success = nativeLibraries.getAdvapi32().CredReadA(service, type, unused, ref);
    if (!success) {
      throw new PasswordRetrievalException("");
    }
    CREDENTIAL cred = new CREDENTIAL(ref.getValue());
    //TODO: verify username?  or lookup by service & username?
    //String userName = cred.UserName.toString();
    try {
      byte[] passbytes = cred.CredentialBlob.getByteArray(0,cred.CredentialBlobSize);
      return new String(passbytes, Charset.forName("UTF-16LE"));    
    } catch (Exception ex) {
      throw new PasswordRetrievalException(ex.getMessage());
    } finally {
      nativeLibraries.getAdvapi32().CredFree(ref);
    }
    
  }

  @Override
  public void setPassword(String service, String account, String password) throws PasswordSaveException {
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
    	throw new PasswordSaveException("");
    }
  }

  @Override
  public void deletePassword(String service, String account) throws PasswordSaveException {
    boolean success = nativeLibraries.getAdvapi32().CredDeleteA(service, new DWORD(1), new DWORD(0));
    System.out.println(success);    
  }

}