package com.biffbangpow.faq.resources;

import com.biffbangpow.faq.db.FaqDAO;
import com.biffbangpow.faq.model.Faq;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

/**
 * JAX-RS resource for searching frequently asked questions.
 */
@Path("search")
@Produces({APPLICATION_XML, APPLICATION_JSON})
@RolesAllowed("user")
public class SearchResource {

    @Inject
    private FaqDAO dao;

    /**
     * Returns the faqs containing the queried string either in the question
     * or in the answer.
     * @param query the queried string
     * @return the faq list
     */
    @GET
    public List<Faq> searchFaqs(@QueryParam("query") String query) {

        return dao.searchFaqs(query);
    }
}
