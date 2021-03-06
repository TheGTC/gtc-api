package uk.org.gtc.api.service;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;

import org.bson.types.ObjectId;
import org.mongojack.DBCursor;
import org.mongojack.DBQuery;
import org.mongojack.DBQuery.Query;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import uk.org.gtc.api.UtilityHelper;
import uk.org.gtc.api.domain.BaseDomainObject;

public abstract class GenericService<T extends BaseDomainObject>
{
    protected final JacksonDBCollection<T, String> collection;
    
    public GenericService(final JacksonDBCollection<T, String> collection)
    {
        this.collection = collection;
    }
    
    public T create(final T item)
    {
        final WriteResult<T, String> result = collection.insert(item);
        return result.getSavedObject();
    }
    
    public Boolean delete(final T item)
    {
        return collection.removeById(item.getId()).getWriteResult().wasAcknowledged();
    }
    
    public List<T> getAll()
    {
        return collection.find().toArray();
    }
    
    /**
     * Find a list of sorted, lightweight items
     *
     * @param sort
     *            - how to sort the returned list
     * @param projection
     *            - what to return out of the retrieved objects
     * @return a sorted, lightweight list of T
     */
    public List<T> getAllLightweightSorted(final DBObject sort, final DBObject projection, final Integer limit)
    {
        return collection.find(new BasicDBObject(), projection).sort(sort).limit(limit).toArray();
    }
    
    public DBCursor<T> getAllSorted(final DBObject sort)
    {
        return collection.find().sort(sort);
    }
    
    public T getById(final String id) throws WebApplicationException
    {
        if (!ObjectId.isValid(id))
        {
            throw new WebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
        }
        
        final T item = collection.findOneById(id);
        
        if (item == null)
        {
            throw new WebApplicationException(HttpServletResponse.SC_NOT_FOUND);
        }
        return item;
    }
    
    /**
     * Find a list of sorted, lightweight items
     *
     * @param sort
     *            - how to sort the returned list
     * @param projection
     *            - what to return out of the retrieved objects
     * @return a sorted, lightweight list of T
     */
    public T getLastBy(final DBObject sort)
    {
        final List<T> sortedObjects = getAllLightweightSorted(sort, new BasicDBObject(), 1);
        if (!UtilityHelper.isNull(sortedObjects) && !sortedObjects.isEmpty())
        {
            return sortedObjects.get(0);
        }
        else
        {
            return null;
        }
    }
    
    Logger logger()
    {
        return LoggerFactory.getLogger(GenericService.class);
    }
    
    public List<T> query(final Query query)
    {
        return collection.find(query).toArray();
    }
    
    /**
     * Find a list of items by named field
     *
     * @param field
     *            - what field to search over
     * @param text
     *            - what text to search for
     * @return a list of matching items
     */
    protected List<T> searchByField(final String field, final String text)
    {
        final String regexPattern = "/.*" + text + ".*/i";
        final Pattern regex = Pattern.compile(regexPattern);
        return collection.find(DBQuery.regex(field, regex)).toArray();
    }
    
    /**
     * Update an existing item
     *
     * @param oldItem
     *            - the item that already exists in the system. This will be
     *            used in the future to generate a difference object.
     * @param newItem
     *            - the item that contains one or more updates to the oldItem.
     * @return the updated item from the database
     */
    public T update(final T oldItem, final T newItem)
    {
        if (UtilityHelper.isNull(oldItem.getCreatedDate()))
        {
            newItem.setCreatedDate(new Date());
        }
        newItem.setLastUpdatedDate(new Date());
        collection.updateById(newItem.getId(), newItem);
        return collection.findOneById(newItem.getId());
    }
}
