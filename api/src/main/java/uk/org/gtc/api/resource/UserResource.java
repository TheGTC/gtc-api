package uk.org.gtc.api.resource;

import java.io.IOException;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.Auth0User;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import uk.org.gtc.api.domain.ApplicationRole;
import uk.org.gtc.api.domain.UserAppMetadata;

@SuppressWarnings("rawtypes")
@Path("user")
@Api("user")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource extends GenericResource
{
    @GET
    @ApiOperation("Returns the current user's metadata")
    @Path("metadata/app")
    @PermitAll
    public String getUserAppMetadata(final @Context SecurityContext context)
    {
        final Auth0User prin = (Auth0User) context.getUserPrincipal();
        return prin.getAppMetadata().toString();
    }
    
    @GET
    @ApiOperation("Returns the current user's roleset")
    @Path("roles")
    @PermitAll
    public List<ApplicationRole> getUserRoles(final @Context SecurityContext context)
    {
        final Auth0User prin = (Auth0User) context.getUserPrincipal();
        final String userAppMetadataString = prin.getAppMetadata().toString();
        final ObjectMapper om = new ObjectMapper();
        try
        {
            return om.readValue(userAppMetadataString, UserAppMetadata.class).getRoles();
        }
        catch (final IOException e)
        {
            throw new WebApplicationException("Invalid roleset");
        }
    }
    
    @Override
    Logger logger()
    {
        return LoggerFactory.getLogger(UserResource.class);
    }
}