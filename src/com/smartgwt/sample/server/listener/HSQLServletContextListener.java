/*
 * Isomorphic SmartGWT web presentation layer
 * Copyright 2000 and beyond Isomorphic Software, Inc.
 *
 * OWNERSHIP NOTICE
 * Isomorphic Software owns and reserves all rights not expressly granted in this source code,
 * including all intellectual property rights to the structure, sequence, and format of this code
 * and to all designs, interfaces, algorithms, schema, protocols, and inventions expressed herein.
 *
 *  If you have any questions, please email <sourcecode@isomorphic.com>.
 *
 *  This entire comment must accompany any portion of Isomorphic Software source code that is
 *  copied or moved from this file.
 */

package com.smartgwt.sample.server.listener;

import org.hsqldb.Server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class HSQLServletContextListener implements ServletContextListener {

    private static final String HSQLDB_DRIVER = "org.hsqldb.jdbcDriver";

    private static final String DEFAULT_CONFIG_FILE = "hsqlserver.properties";

    private static final String URL_PROPERTY = "hsql.url";
    private static final String USER_PROPERTY = "hsql.user";
    private static final String PASSWORD_PROPERTY = "hsql.password";
    private static final String DATA_DIR_PROPERTY = "hsql.data.dir";
    private static final String DATABASE_PROPERTY = "hsql.database";
    private static Properties properties;


    public void contextInitialized(ServletContextEvent sce) {
        properties = new Properties();
        try {
            ServletContext context = sce.getServletContext();

            String param = context.getInitParameter("HSQLDB_CONFIG");
            if (param == null || "".equals(param.trim())) {
                properties.load(this.getClass().getResourceAsStream(DEFAULT_CONFIG_FILE));
            } else {
                properties.load(new FileInputStream(new File(context.getRealPath(param))));
            }

            String databaseDir = properties.getProperty(DATA_DIR_PROPERTY);
            String database = properties.getProperty(DATABASE_PROPERTY);
            Server.main(new String[]{
                    "-remote_open",
	"true",
	"-database.0",
                    context.getRealPath(databaseDir + "/" + database),
                    "-dbname.0",
                    database,
                    "-no_system_exit",
                    "true"
            });
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void contextDestroyed(ServletContextEvent sce) {
        Connection connection = null;
        try {
            Class.forName(HSQLDB_DRIVER);
            connection = DriverManager.getConnection(
                    properties.getProperty(URL_PROPERTY),
                    properties.getProperty(USER_PROPERTY),
                    properties.getProperty(PASSWORD_PROPERTY));

            Statement stmt = connection.createStatement();
            stmt.executeUpdate("SHUTDOWN;");
            stmt.close();
        } catch (RuntimeException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.print("Cannot close database connection " + e.getMessage());
                }
            }
        }
    }
}