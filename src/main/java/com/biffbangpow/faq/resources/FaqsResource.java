package com.biffbangpow.faq.resources;

import com.biffbangpow.faq.db.FaqDAO;
import com.biffbangpow.faq.model.Faq;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;

/**
 * JAX-RS resource for frequently asked questions.
 */
@Path("faqs")
@Produces({APPLICATION_XML, APPLICATION_JSON})
@Consumes({APPLICATION_JSON, APPLICATION_XML})
@RolesAllowed("admin")
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

    /**
     * creates a faq
     *
     * @return 201 when creation is successful
     */
    @POST
    public Response create(Faq faq, @Context UriInfo uriInfo) {

        dao.create(faq);

        // FIXME returned location...
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        Response.ResponseBuilder builder = Response.created(uriBuilder.build());
        return builder.build();
    }
}
