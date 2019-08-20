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

import com.sun.jna.Library;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.ptr.PointerByReference;

/**
 * OS X CoreFoundation library.
 */
@SuppressWarnings({"AbbreviationAsWordInName","ParameterName", "MethodName"})
interface Advapi32 extends Library {

  /**
   * Advapi32.lib
   * @param TargetName name of credential in store
   * @param Type cred type 
   * @param Flags always zero
   * @param Credential credential pointer
   * @return success or failure.
   */  
  public boolean CredReadA(
      String             TargetName,
      DWORD              Type,
      DWORD              Flags,
      PointerByReference Credential
      );
  
  /**
   * Advapi32.lib
   * @param Credential credential pointer
   * @param Flags always zero
   * @return success or failure.
   */  
  public boolean CredWriteA(
      CREDENTIAL         Credential,
      DWORD              Flags
      );
  
  /**
   * Advapi32.lib
   * @param Credential who's memory we'll free.
   * @param Flags has one value, set to use existing credential memory or not.
   * @return success or failure.
   */
  public boolean CredFree(
      PointerByReference Credential
      );
    
  /**
   * Advapi32.lib
   * @param TargetName name of credential in store
   * @param Type cred type 
   * @param Flags always zero
   * @return success or failure.
   */
  public boolean CredDeleteA(
      String TargetName,
      DWORD  type,
      DWORD  flags
      );
  
}
