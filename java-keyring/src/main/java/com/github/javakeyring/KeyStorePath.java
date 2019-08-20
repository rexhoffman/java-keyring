package com.github.javakeyring;

/**
 * If a {@link KeyringBackend} supports changing target files, it will implement this interface as well.
 */
public interface KeyStorePath {

  /**
   * Gets path to key store.
   * 
   * @return a keystore path (if required by backend)
   */
  public String getKeyStorePath();

  /**
   * Sets path to key store.
   *
   * @param path Path to key store
   */
  public void setKeyStorePath(String path);
}
