package uk.org.gtc.api;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.Auth0User;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;

public class GtcAuthenticator implements Authenticator<String, Auth0User>
{
    private final Logger logger;
    
    private final JWTVerifier jwtVerifier;
    
    private final GtcConfiguration configuration;
    
    public GtcAuthenticator(final Logger logger, final GtcConfiguration configuration)
    {
        this.logger = logger;
        this.configuration = configuration;
        this.jwtVerifier = new JWTVerifier(configuration.auth0OfficeApiKey, configuration.auth0OfficeApiId);
    }
    
    @Override
    public Optional<Auth0User> authenticate(final String token) throws AuthenticationException
    {
        JSONObject userJson = null;
        Auth0User user = null;
        try
        {
            jwtVerifier.verify(token);
            userJson = new JSONObject(Unirest.post(configuration.auth0TokenUrl).field("id_token", token).asString().getBody());
            user = new Auth0User(userJson);
        }
        catch (InvalidKeyException | NoSuchAlgorithmException | IllegalStateException | SignatureException | IOException
                | JWTVerifyException | UnirestException | JSONException e)
        {
            throw new AuthenticationException("Could not authenticate", e);
        }
        
        if (UtilityHelper.isNull(user))
        {
            logger.debug("Failed to authenticate with token {}", token);
        }
        else
        {
            logger.debug("Authenticated " + user.getEmail() + " (" + user.getUserId() + ")");
        }
        
        return Optional.of(user);
    }
    
    Logger logger()
    {
        return LoggerFactory.getLogger(GtcAuthenticator.class);
    }
}
