/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.batch.bootstrap;

import org.sonar.api.config.Settings;
import org.sonar.core.persistence.DefaultDatabase;

import java.util.Properties;

/**
 * @since 2.12
 */
public class BatchDatabase extends DefaultDatabase {

  public BatchDatabase(Settings settings,
      // The dependency on JdbcDriverHolder is required to be sure that the JDBC driver
      // has been downloaded and injected into classloader
      JdbcDriverHolder jdbcDriverHolder,

      // The dependency on DryRunDatabase is required to be sure that the dryRun mode
      // changed settings
      DryRunDatabase dryRun) {
    super(settings);
  }

  public BatchDatabase(Settings settings,
      // The dependency on JdbcDriverHolder is required to be sure that the JDBC driver
      // has been downloaded and injected into classloader
      JdbcDriverHolder jdbcDriverHolder) {
    super(settings);
  }

  @Override
  protected void doCompleteProperties(Properties properties) {
    // two connections are required : one for Hibernate and one for MyBatis
    // Note that Hibernate will be removed soon
    properties.setProperty("sonar.jdbc.initialSize", "2");
    properties.setProperty("sonar.jdbc.maxActive", "2");
  }
}
