package uk.org.gtc.api;

import java.net.UnknownHostException;
import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;

import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.mongojack.JacksonDBCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microtripit.mandrillapp.lutung.MandrillApi;
import com.mongodb.DB;
import com.mongodb.MongoClient;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import uk.org.gtc.api.domain.ApplicationDO;
import uk.org.gtc.api.domain.AuthDO;
import uk.org.gtc.api.domain.BookDO;
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.domain.User;
import uk.org.gtc.api.health.BasicHealthCheck;
import uk.org.gtc.api.health.MandrillHealthCheck;
import uk.org.gtc.api.health.MongoHealthCheck;
import uk.org.gtc.api.resource.ApiResource;
import uk.org.gtc.api.resource.ApplicationResource;
import uk.org.gtc.api.resource.AuthResource;
import uk.org.gtc.api.resource.BookResource;
import uk.org.gtc.api.resource.MemberResource;
import uk.org.gtc.api.resource.UserResource;
import uk.org.gtc.api.service.ApplicationService;
import uk.org.gtc.api.service.AuthService;
import uk.org.gtc.api.service.BookService;
import uk.org.gtc.api.service.MemberService;
import uk.org.gtc.api.service.UserService;

public class GtcApplication extends Application<GtcConfiguration>
{
	
	public static void main(String[] args) throws Exception
	{
		new GtcApplication().run(args);
	}
	
	@Override
	public String getName()
	{
		return "gtc-api";
	}
	
	Logger logger()
	{
		return LoggerFactory.getLogger(GtcApplication.class);
	}
	
	@Override
	public void initialize(Bootstrap<GtcConfiguration> bootstrap)
	{
		bootstrap.addBundle(new SwaggerBundle<GtcConfiguration>()
		{
			@Override
			protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(GtcConfiguration configuration)
			{
				return configuration.swaggerBundleConfiguration;
			}
		});
	}
	
	@Override
	public void run(GtcConfiguration configuration, Environment environment) throws UnknownHostException
	{
		// Managed resources
		final MongoClient mongo = new MongoClient(configuration.mongoHost, configuration.mongoPort);
		environment.lifecycle().manage(new MongoManaged(mongo));
		
		// Servlet configuration
		FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
		filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, configuration.corsOrigins);
		filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
		filter.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM,
				"Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
		filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
		
		// Integrations
		final MandrillApi mandrill = new MandrillApi(configuration.mandrillApiKey);
		
		// Health checks
		environment.healthChecks().register("basic", new BasicHealthCheck());
		environment.healthChecks().register("mongo", new MongoHealthCheck(mongo));
		environment.healthChecks().register("mandrill", new MandrillHealthCheck(mandrill));
		
		// Database and Jackson mappings
		final DB db = mongo.getDB("gtc-dev");
		
		final JacksonDBCollection<User, String> users = JacksonDBCollection.wrap(db.getCollection("users"), User.class, String.class);
		final JacksonDBCollection<AuthDO, String> authUsers = JacksonDBCollection.wrap(db.getCollection("users"), AuthDO.class,
				String.class);
		final JacksonDBCollection<MemberDO, String> members = JacksonDBCollection.wrap(db.getCollection("members"), MemberDO.class,
				String.class);
		final JacksonDBCollection<ApplicationDO, String> applications = JacksonDBCollection.wrap(db.getCollection("applications"),
				ApplicationDO.class, String.class);
		final JacksonDBCollection<BookDO, String> books = JacksonDBCollection.wrap(db.getCollection("books"), BookDO.class, String.class);
		
		// Services
		final UserService userService = new UserService(users);
		final AuthService authService = new AuthService(authUsers);
		final MemberService memberService = new MemberService(members, mandrill);
		final ApplicationService applicationService = new ApplicationService(applications);
		final BookService bookService = new BookService(books);
		
		// Resource registration
		environment.jersey().register(new ApiResource());
		environment.jersey().register(new AuthResource(authService, userService));
		environment.jersey().register(new UserResource(userService));
		environment.jersey().register(new MemberResource(memberService));
		environment.jersey().register(new ApplicationResource(applicationService, memberService));
		environment.jersey().register(new BookResource(bookService));
		
		// Authentication
		environment.jersey()
				.register(new AuthDynamicFeature(
						new BasicCredentialAuthFilter.Builder<AuthDO>().setAuthenticator(new GtcAuthenticator(authService, logger()))
								.setAuthorizer(new GtcAuthoriser(userService)).setRealm("GTC API").buildAuthFilter()));
		environment.jersey().register(RolesAllowedDynamicFeature.class);
		environment.jersey().register(new AuthValueFactoryProvider.Binder<>(AuthDO.class));
	}
}