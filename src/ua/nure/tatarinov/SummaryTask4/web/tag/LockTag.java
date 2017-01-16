package ua.nure.tatarinov.SummaryTask4.web.tag;

import org.apache.log4j.Logger;
import ua.nure.tatarinov.SummaryTask4.db.Query;
import ua.nure.tatarinov.SummaryTask4.db.State;
import ua.nure.tatarinov.SummaryTask4.db.dao.ConnectionPool;
import ua.nure.tatarinov.SummaryTask4.db.dto.StudentDTO;
import ua.nure.tatarinov.SummaryTask4.db.dto.UserDTO;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class LockTag extends TagSupport {

    public static final Logger LOG = Logger.getLogger(LockTag.class);

    @Override
    public int doStartTag() throws JspException {
        LOG.info("Start tracing LockTag");
        int countColumn = 4;
        HttpSession session = pageContext.getSession();
        String language = String.valueOf(session.getAttribute("language"));
        ResourceBundle rb = ResourceBundle.getBundle("resources", new Locale(language));
        List<StudentDTO> students = new ArrayList<>();
        List<UserDTO> users = new ArrayList<>();
        List<State> states = new ArrayList<>();
        StudentDTO student = null;
        UserDTO user = null;
        State state = State.LOCKED;
        try (Connection connection = ConnectionPool.getConnetcion()) {
            if (connection != null) {
                PreparedStatement statement = connection.prepareStatement(Query.SELECT_STUDENTS_AND_STATES);
                connection.setAutoCommit(false);
                statement.execute();
                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()) {
                    student = new StudentDTO();
                    user = new UserDTO();
                    student.setName(resultSet.getString("name"));
                    student.setSurname(resultSet.getString("surname"));
                    student.setPatronymic(resultSet.getString("patronymic"));
                    if ((!student.getPatronymic().isEmpty()) && (student.getPatronymic() != null)) {
                        countColumn = 5;
                    }
                    students.add(student);
                    user.setIdUser(resultSet.getInt("id_user"));
                    user.setLogin(resultSet.getString("login"));
                    user.setPassword(resultSet.getString("password"));
                    users.add(user);
                    String st = resultSet.getString("name_state");
                    if (st.equals("unlocked")) {
                        state = State.UNLOCKED;
                    } else if (st.equals("locked")) {
                        state = State.LOCKED;
                    }
                    states.add(state);
                }
                resultSet.close();
            }
        } catch (SQLException e) {
            LOG.error(e.getLocalizedMessage());
        }
        JspWriter out = pageContext.getOut();
        Iterator studentsIt = students.iterator();
        Iterator usersIt = users.iterator();
        Iterator statesIt = states.iterator();
        StringBuffer table = new StringBuffer();
        table.append("<table id =\"new\">\n").append("<th colspan=\"")
                .append(countColumn).append("\">").append(rb.getString("page.admin.student.list")).append("</th>");
        while (studentsIt.hasNext()) {
            student = (StudentDTO) studentsIt.next();
            user = (UserDTO) usersIt.next();
            state = (State) statesIt.next();
            table.append("<tr>\n<td>")
                    .append(student.getSurname()).append("</td>\n<td>")
                    .append(student.getName()).append("</td>\n<td>");
            if (countColumn == 5) {
                table.append(student.getPatronymic()).append("</td><td>");
            }
            table.append(user.getLogin()).append("</td>\n<td>")
                    .append(user.getPassword()).append("</td>\n<td>");
            if (state == State.UNLOCKED) {
                table.append("<form action=\"controller\">\n" +
                             "    <input type=\"hidden\" name=\"command\" value=\"lockCommand\">\n" +
                             "    <input type=\"hidden\" name=\"id\" value=\"")
                        .append(user.getIdUser())
                        .append("\">\n" +
                             "    <input type=\"hidden\" name=\"lock\" value=\"true\">\n" +
                             "    <button><img src=\"/img/lock.png\" class=\"icon\" alt=\"Заблокировать\" name></button>\n" +
                             "</form>");
            } else if (state == State.LOCKED) {
                table.append("<form action=\"controller\">\n" +
                             "    <input type=\"hidden\" name=\"command\" value=\"lockCommand\">\n" +
                             "    <input type=\"hidden\" name=\"id\" value=\"")
                        .append(user.getIdUser())
                        .append("\">\n" +
                             "    <input type=\"hidden\" name=\"lock\" value=\"false\">\n" +
                             "    <button><img src=\"/img/unlock.png\" class=\"icon\" alt=\"Разблокировать\"></button>\n" +
                             "</form>");
            }
        }
        table.append("</table>");
        try {
            out.println(table);
        } catch (IOException e) {
            LOG.error(e.getLocalizedMessage());
        }


        return EVAL_PAGE;
    }
}