package uk.org.gtc.api.resource;

import java.util.List;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.org.gtc.api.domain.BaseDomainObject;
import uk.org.gtc.api.service.GenericService;

public class GenericResource<T extends BaseDomainObject>
{
    protected ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    protected Validator validator = factory.getValidator();

    private GenericService<T> genericService;

    public GenericResource()
    {
        // Empty constructor
    }

    public GenericResource(final GenericService<T> genericService)
    {
        this.genericService = genericService;
    }

    protected T createItem(final T item) throws Exception
    {
        return genericService.create(item);
    }

    protected List<T> getAll()
    {
        return genericService.getAll();
    }

    protected T getItemById(final String id) throws WebApplicationException
    {
        return genericService.getById(id);
    }

    Logger logger()
    {
        return LoggerFactory.getLogger(GenericResource.class);
    }
}
