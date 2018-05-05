package com.biffbangpow.faq.resources;

import com.biffbangpow.faq.db.FaqDAO;
import com.biffbangpow.faq.model.Faq;

import javax.inject.Inject;
import javax.ws.rs.*;

import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

/**
 * JAX-RS resource for frequently asked questions.
 */
@Path("faqs")
@Produces({APPLICATION_XML, APPLICATION_JSON})
@Consumes({APPLICATION_XML, APPLICATION_JSON})
public class FaqsResource {

    @Inject
    private FaqDAO dao;

    /**
     * Returns all the faqs.
     *
     * @return the faq list
     */
    @GET
    public List<Faq> getFaqs() {

        return dao.getFaqs();
    }
}
