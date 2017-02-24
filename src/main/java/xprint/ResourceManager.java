package xprint;

import java.io.InputStream;

/**
 * ResourceManager
 * @author Kazuhiko Arase
 */
public abstract class ResourceManager {

  private static ResourceManager rm = new DefaultResourceManager();

  public static void setResourceManager(ResourceManager rm) {
    ResourceManager.rm = rm;
  }

  public static ResourceManager getResourceManager() {
    return rm;
  }

  protected ResourceManager() {
  }

  public abstract InputStream getResourceAsStream(String name);

  protected static class DefaultResourceManager extends ResourceManager {
    @Override
    public InputStream getResourceAsStream(String name) {
      return getClass().getResourceAsStream(name);
    }
  }
}
