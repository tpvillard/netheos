package com.biffbangpow.faq.resources;

import com.biffbangpow.faq.db.FaqDAOException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception mapper.
 */
@Provider
public class FaqDAOExceptionMapper implements ExceptionMapper<FaqDAOException>
{
   public Response toResponse(FaqDAOException exception)
   {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
              .entity(exception.getMessage())
              .type("text/plain").build();
   }
}
