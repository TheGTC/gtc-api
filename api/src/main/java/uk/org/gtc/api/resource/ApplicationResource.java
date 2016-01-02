package uk.org.gtc.api.resource;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.Api;
import uk.org.gtc.api.domain.ApplicationDO;
import uk.org.gtc.api.domain.MemberDO;
import uk.org.gtc.api.service.ApplicationService;
import uk.org.gtc.api.service.MemberService;

@Path("application")
@Api("application")
@Produces(MediaType.APPLICATION_JSON)
public class ApplicationResource extends GenericResource<ApplicationDO>
{
	private ApplicationService applicationService;
	private MemberService memberService;
	
	public ApplicationResource(ApplicationService applicationService, MemberService memberService)
	{
		super(applicationService);
		this.applicationService = applicationService;
		this.memberService = memberService;
	}
	
	@POST
	@Path("{id}/accept")
	public MemberDO acceptMembership(@PathParam("id") String id, ApplicationDO application)
	{
		final MemberDO memberToCreate = new MemberDO(memberService.getNextMemberNumber(), application);
		final MemberDO createdMember = memberService.create(memberToCreate);
		
		final Boolean deletedApplication = applicationService.delete(application);
		if (!deletedApplication)
		{
			logger().error("Couldn't delete application with ID " + application.getId());
		}
		return createdMember;
	}
	
	@Override
	@GET
	@Path("all")
	public List<ApplicationDO> getAll()
	{
		return applicationService.getAll();
	}
	
	@Override
	Logger logger()
	{
		return LoggerFactory.getLogger(ApplicationResource.class);
	}
}