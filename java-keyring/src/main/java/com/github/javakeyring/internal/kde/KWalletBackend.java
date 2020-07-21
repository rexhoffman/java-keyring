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
package com.github.javakeyring.internal.kde;

import com.github.javakeyring.BackendNotSupportedException;
import com.github.javakeyring.PasswordAccessException;
import com.github.javakeyring.internal.KeyringBackend;
import org.freedesktop.dbus.annotations.DBusInterfaceName;
import org.freedesktop.dbus.connections.impl.DBusConnection;
import org.freedesktop.dbus.interfaces.DBusInterface;

import java.util.List;

public class KWalletBackend implements KeyringBackend {

  private KWallet wallet;
  private int id = -1;

  public KWalletBackend() throws BackendNotSupportedException {
    try {
      DBusConnection connection = DBusConnection.getConnection(DBusConnection.DBusBusType.SESSION);
      wallet = connection.getRemoteObject("org.kde.kwalletd5", "/modules/kwalletd5", KWallet.class, true);
      wallet.localWallet(); //attempt connection to wallet
    } catch (Exception e) {
      throw new BackendNotSupportedException("Cannot connect to KWallet");
    }
  }

  /**
   * Retrieves password from KWallet.
   *
   * @param service
   *         Service name
   * @param account
   *         Account name
   *
   * @return Stored password
   * @throws PasswordAccessException
   *         when the password cannot be found
   */
  @Override
  public synchronized String getPassword(String service, String account) throws PasswordAccessException {
    int id = openWallet(service);

    if (!wallet.hasEntry(id, service, account, service)) {
      throw new PasswordAccessException("Password is not in wallet");
    }

    String pass = wallet.readPassword(id, service, account, service);

    close(service);
    return pass;
  }

  @Override
  public synchronized void setPassword(String service, String account, String password) throws PasswordAccessException {
    int id = openWallet(service);
    wallet.writePassword(id, service, account, password, service);
    close(service);
  }

  /**
   * Removes a password from KWallet.
   * @param service
   *          Service name
   * @param account
   *          Account name
   * @throws PasswordAccessException
   *          when password is not in wallet
   */
  @Override
  public synchronized void deletePassword(String service, String account) throws PasswordAccessException {
    int id = openWallet(service);

    if (!wallet.hasEntry(id, service, account, service)) {
      throw new PasswordAccessException("Password cannot be deleted, it is not in wallet");
    }

    wallet.removeEntry(id, service, account, service);

    //If there are no passwords left in the folder, delete the folder
    if (wallet.entryList(id, service, service).isEmpty()) {
      wallet.removeFolder(id, service, service);
    }

    close(service);
  }

  private int openWallet(String service) {
    if (id >= 0) {
      if (!wallet.isOpen(id)) {
        id = 0;
      }
    } else {
      id = 0;
    }
    return wallet.open(wallet.localWallet(), id, service);
  }

  private void close(String service) {
    wallet.close(id, false, service);
  }

  @DBusInterfaceName("org.kde.KWallet")
  interface KWallet extends DBusInterface {

    String localWallet();

    boolean isOpen(int handleId);

    int open(String wallet, long handleId, String app);

    int close(int handleId, boolean force, String app);

    boolean hasEntry(int handleId, String folder, String key, String app);

    String readPassword(int handleId, String folder, String key, String app);

    int writePassword(int handleId, String folder, String key, String value, String app);

    List<String> entryList(int handleId, String folder, String app);

    int removeEntry(int handleId, String folder, String key, String app);

    boolean removeFolder(int handleId, String folder, String app);
  }
}
