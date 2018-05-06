package com.biffbangpow.faq.db;

import com.biffbangpow.faq.config.Configuration;
import com.biffbangpow.faq.model.Faq;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * The frequently asked question dao.
 */
public class FaqDAO {


    private static final String SELECT_ALL_QUERY = "SELECT question, answer, tags FROM faq";
    private static final MessageFormat INSERT_MSG =
            new MessageFormat("INSERT INTO faq VALUES ({0}, {1}, {2})");

    private final ConnectionProvider provider;

    public FaqDAO(Configuration config) {

        provider = new ConnectionProvider(config.getDbUrl());
    }

    /**
     * Returns all the frequently asked questions
     *
     * @return the faq list
     */
    public List<Faq> getFaqs() {
        return getFaqs(SELECT_ALL_QUERY);
    }

    public List<Faq> searchFaqs(String queriedString) {

        if (queriedString == null) {
            return getFaqs();
        } else {
            return getFaqs(buildSearchQuery(queriedString));
        }
    }

    private String buildSearchQuery(String query) {

        // Single quote in the queried string should be escaped.
        String escapedQuery = query.replaceAll("'", "''");
        return SELECT_ALL_QUERY +
                " WHERE question LIKE '%" + escapedQuery + "%'" +
                "UNION " +
                SELECT_ALL_QUERY +
                " WHERE answer LIKE '%" + escapedQuery + "%'";
    }

    private List<Faq> getFaqs(String query) {

        List<Faq> faqs = new ArrayList<>();

        // FIXME connection pooling would be nice.
        Connection con = provider.get();
        try (Statement stmt = con.createStatement()) {

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                String question = rs.getString("question");
                String answer = rs.getString("answer");
                String tags = rs.getString("tags");
                faqs.add(Faq.of(question, answer, tags));
            }
        } catch (SQLException ex) {
            throw new FaqDAOException(ex.getMessage(), ex.getCause());
        }
        return faqs;
    }

    public void create(Faq faq) {

        Connection con = provider.get();
        try (Statement stmt = con.createStatement()) {

            stmt.executeUpdate(buildInsertString(faq));
        } catch (SQLException ex) {
            throw new FaqDAOException(ex.getMessage(), ex.getCause());
        }
    }

    String buildInsertString(Faq faq) {

        Object[] args = {appendSingleQuote(faq.getQuestion()),
                appendSingleQuote(faq.getAnswer()),
                appendSingleQuote(faq.getTags())};
        return INSERT_MSG.format(args);
    }

    String appendSingleQuote(String input) {
        return "'" + input.replace("'", "''") + "'";
    }

}
