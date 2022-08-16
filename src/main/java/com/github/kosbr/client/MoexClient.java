package com.github.kosbr.client;

import com.github.kosbr.client.dto.MoexResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/engines/currency/markets/selt/")
@RegisterRestClient
public interface MoexClient {

    @GET
    @Path("securities.json")
    @Consumes("application/xml")
    MoexResponse getData(@QueryParam("securities") String security, @QueryParam("iss.only") String issOnly);

}
