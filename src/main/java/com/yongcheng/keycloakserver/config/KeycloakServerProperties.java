package com.yongcheng.keycloakserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * An externalized POJO class to access keycloak configuration 
 * properties defined inside application.properties:
 *   - keycloak.server.contextPath
 *   - keycloak.server.realmImportFile
 *   - keycloak.server.adminUser.username
 *   - keycloak.server.adminUser.password
 */
@Configuration
@ConfigurationProperties(prefix = "keycloak.server")
public class KeycloakServerProperties {
  
  String contextPath;
  String realmImportFile;
  AdminUser adminUser = new AdminUser();

  public static class AdminUser {

    String username;
    String password;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }
  }

  public String getContextPath() {
    return contextPath;
  }

  public void setContextPath(String contextPath) {
    this.contextPath = contextPath;
  }

  public String getRealmImportFile() {
    return realmImportFile;
  }

  public void setRealmImportFile(String realmImportFile) {
    this.realmImportFile = realmImportFile;
  }

  public AdminUser getAdminUser() {
    return adminUser;
  }

  public void setAdminUser(AdminUser adminUser) {
    this.adminUser = adminUser;
  }
}