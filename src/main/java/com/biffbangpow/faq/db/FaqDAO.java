package com.biffbangpow.faq.db;

import com.biffbangpow.faq.config.Configuration;
import com.biffbangpow.faq.model.Faq;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * The frequently asked question dao.
 */
public class FaqDAO {


    private static final String SELECT_ALL_QUERY = "select question, answer, tags from faq";
    private final Configuration config;
    private final ConnectionProvider provider;


    public FaqDAO(Configuration config) {

        this.config = config;
        provider = new ConnectionProvider(config.getDbUrl());
    }

    public List<Faq> getFaqs() {

        List<Faq> faqs = new ArrayList<>();

        // FIXME connection pooling would be nice.
        Connection con = provider.get();
        try (Statement stmt = con.createStatement()) {

            ResultSet rs = stmt.executeQuery(SELECT_ALL_QUERY);
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
}
