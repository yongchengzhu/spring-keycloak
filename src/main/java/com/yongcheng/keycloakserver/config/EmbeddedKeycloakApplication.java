package com.yongcheng.keycloakserver.config;

import java.util.NoSuchElementException;

import com.yongcheng.keycloakserver.config.KeycloakServerProperties.AdminUser;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.services.util.JsonConfigProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedKeycloakApplication extends KeycloakApplication {

  private static final Logger LOG = LoggerFactory.getLogger(EmbeddedKeycloakApplication.class);

  static KeycloakServerProperties keycloakServerProperties;

  protected void loadConfig() {
    JsonConfigProviderFactory factory = new RegularJsonConfigProviderFactory();
    Config.init(factory.create().orElseThrow(() -> new NoSuchElementException("No value present")));
  }

  public EmbeddedKeycloakApplication() {
    super();
    createMasterRealmAdminUser();
  }

  private void createMasterRealmAdminUser() {
    // getSessionFactory is an inherited method.
    KeycloakSession session = getSessionFactory().create();
    ApplianceBootstrap applianceBootstrap = new ApplianceBootstrap(session);
    AdminUser admin = keycloakServerProperties.getAdminUser();

    try {
      session.getTransactionManager().begin();
      applianceBootstrap.createMasterRealmUser(admin.getUsername(), admin.getPassword());
      session.getTransactionManager().commit();
    } catch (Exception ex) {
      LOG.warn("Couldn't create keycloak master admin user: {}", ex.getMessage());
      session.getTransactionManager().rollback();
    }

    session.close();
  }
}