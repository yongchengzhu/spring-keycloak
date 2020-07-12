package com.yongcheng.keycloakserver.config;

import java.io.InputStream;
import java.util.NoSuchElementException;

import com.yongcheng.keycloakserver.config.KeycloakServerProperties.AdminUser;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.services.util.JsonConfigProviderFactory;
import org.keycloak.util.JsonSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

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
    createAuthRealm();
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

  private void createAuthRealm() {
    KeycloakSession session = getSessionFactory().create();

    try {
      session.getTransactionManager().begin();

      RealmManager manager = new RealmManager(session);
      
      String path = keycloakServerProperties.getRealmImportFile();
      Resource realmImportFile = new ClassPathResource(path);
      
      InputStream bytes = realmImportFile.getInputStream();
      Class<RealmRepresentation> type = RealmRepresentation.class;
      RealmRepresentation rep = JsonSerialization.readValue(bytes, type);
      
      manager.importRealm(rep);
      
      session.getTransactionManager().commit();
    } catch (Exception ex) {
      LOG.warn("Failed to import Realm json file: {}", ex.getMessage());
      session.getTransactionManager().rollback();
    }

    session.close();
  }

}