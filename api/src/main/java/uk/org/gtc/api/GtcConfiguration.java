package uk.org.gtc.api;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.spinscale.dropwizard.jobs.JobConfiguration;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

public class GtcConfiguration extends Configuration implements JobConfiguration
{
    private static GtcConfiguration configuration;
    
    public static void setInstance(final GtcConfiguration configuration)
    {
        GtcConfiguration.configuration = configuration;
    }
    
    public static GtcConfiguration getInstance()
    {
        return configuration;
    }
    
    @JsonProperty
    public ApplicationMode appMode;
    
    @JsonProperty
    @NotEmpty
    public String mongoHost;
    
    @JsonProperty
    @Min(1)
    @Max(65535)
    public int mongoPort;
    
    @JsonProperty
    public String mongoUser;
    
    @JsonProperty
    public char[] mongoPassword;
    
    @JsonProperty
    @NotEmpty
    public String mongoDatabase;
    
    @JsonProperty
    @NotEmpty
    public String corsOrigins;
    
    @JsonProperty
    @NotEmpty
    public String sendgridApiKey;
    
    @JsonProperty
    @NotEmpty
    public String auth0OfficeApiId;
    
    @JsonProperty
    @NotEmpty
    public String auth0OfficeApiKey;
    
    @JsonProperty
    @NotEmpty
    public String auth0MgmtApiId;
    
    @JsonProperty
    @NotEmpty
    public String auth0MgmtApiKey;
    
    @JsonProperty
    @NotEmpty
    public String auth0UserConnection;
    
    @JsonProperty
    @NotEmpty
    public String auth0Domain;
    
    @JsonProperty
    @NotEmpty
    public String auth0TokenUrl;
    
    @JsonProperty
    @NotEmpty
    public String mailchimpApiKey;
    
    @JsonProperty
    @NotEmpty
    public String mailchimpListId;
    
    @JsonProperty("swagger")
    public SwaggerBundleConfiguration swaggerBundleConfiguration;
}