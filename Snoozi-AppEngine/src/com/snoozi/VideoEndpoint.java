package com.snoozi;

import com.snoozi.EMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "videoendpoint",version="v2", namespace = @ApiNamespace(ownerDomain = "snoozi.com", ownerName = "snoozi.com", packagePath = ""))
public class VideoEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listVideo")
	public CollectionResponse<Video> listVideo(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		
		
		
		EntityManager mgr = null;
		Cursor cursor = null;
		List<Video> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr.createQuery("select from Video as Video");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<Video>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Video obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Video> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getVideo")
	public Video getVideo(@Named("id") String id) {
		EntityManager mgr = getEntityManager();
		Video video = null;
		try {
			video = mgr.find(Video.class, id);
		} finally {
			mgr.close();
		}
		return video;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param video the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertVideo")
	public Video insertVideo(Video video) {
		EntityManager mgr = getEntityManager();
		try {
			if (containsVideo(video)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.persist(video);
		} finally {
			mgr.close();
		}
		return video;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param video the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updateVideo")
	public Video updateVideo(Video video) {
		EntityManager mgr = getEntityManager();
		try {
			if (!containsVideo(video)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.persist(video);
		} finally {
			mgr.close();
		}
		return video;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removeVideo")
	public void removeVideo(@Named("id") String id) {
		EntityManager mgr = getEntityManager();
		try {
			Video video = mgr.find(Video.class, id);
			mgr.remove(video);
		} finally {
			mgr.close();
		}
	}

	private boolean containsVideo(Video video) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			Video item = mgr.find(Video.class, video.getId());
			if (item == null) {
				contains = false;
			}
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
