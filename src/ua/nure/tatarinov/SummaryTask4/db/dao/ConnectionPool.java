package ua.nure.tatarinov.SummaryTask4.db.dao;

import org.apache.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * The class provides connection pool.
 *
 * @author S. Tatarinov
 */

public class ConnectionPool {

    private static final Logger LOG = Logger.getLogger(ConnectionPool.class);

    private ConnectionPool(){
    }

    /**
     * Establish connection with Derby
     *
     * @return Connection with Derby
     */
    public static synchronized Connection getDerbyConnetcion() {
        try {
            Context initCtx = new InitialContext();
            DataSource ds = (DataSource) initCtx.lookup("java:comp/env/jdbc/facultaty");
            LOG.trace("Connection is successful");
            return ds.getConnection();
        } catch (NamingException ex) {
            LOG.error("Cannot find the data source");
            return null;
        } catch (SQLException ex) {
            LOG.error("Cannot get connection from data source");
            return null;
        }
    }

    /**
     * Establish connection with MySQL
     *
     * @return Connection with MySQL
     */
    public static synchronized Connection getConnetcion() {
        try {
            Context initCtx = new InitialContext();
            Context envContext = (Context) initCtx.lookup("java:/comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/courses");
            LOG.trace("Connection is successful");
            return ds.getConnection();
        } catch (NamingException ex) {
            LOG.error("Cannot find the data source");
            return null;
        } catch (SQLException ex) {
            LOG.error("Cannot get connection from data source");
            return null;
        }
    }

}
