package ua.nure.tatarinov.SummaryTask4.db.dao.mysql;

import org.apache.log4j.Logger;
import ua.nure.tatarinov.SummaryTask4.db.Query;
import ua.nure.tatarinov.SummaryTask4.db.dao.ConnectionPool;
import ua.nure.tatarinov.SummaryTask4.db.dao.JournalDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data access object for Journal.
 *
 * @author S. Tatarinov
 *
 */

public class MySQLJournalDAO implements JournalDAO {

    public static final Logger LOG = Logger.getLogger(MySQLJournalDAO.class);

    @Override
    public void updateJournal(int id, int mark) {
        LOG.trace("Starting trace MySQLJournalDAO");
        try (Connection connection = ConnectionPool.getConnetcion()) {
            if (connection != null) {
                try (PreparedStatement statement = connection.prepareStatement(Query.UPDATE_JOURNAL, Statement.RETURN_GENERATED_KEYS)) {
                    connection.setAutoCommit(false);
                    statement.setString(1, String.valueOf(mark));
                    statement.setString(2, String.valueOf(id));
                    statement.executeUpdate();
                    connection.commit();
                } catch (SQLException ex) {
                    LOG.error(ex.getLocalizedMessage());
                    connection.rollback();
                }
            }
        } catch (SQLException e) {
            LOG.error(e.getLocalizedMessage());
        }
    }
}