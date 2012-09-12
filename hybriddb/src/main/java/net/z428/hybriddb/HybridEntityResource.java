package net.z428.hybriddb;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.svenson.JSON;
import org.svenson.JSONParser;

@Stateless
@Path("entities")
public class HybridEntityResource {

	@PersistenceContext(name = "hybriddb")
	private EntityManager em;

	public EntityManager getEntityManager() {
		return em;
	}

	@GET
	@Produces("application/json")
	public Response showAll() {
		final CriteriaQuery<SimpleHybridEntity> cq = this.getEntityManager().getCriteriaBuilder().createQuery(SimpleHybridEntity.class);
		cq.select(cq.from(SimpleHybridEntity.class));
		final List<SimpleHybridEntity> resultList = this.getEntityManager().createQuery(cq).getResultList();
		return Response.ok(JSON.defaultJSON().forValue(resultList)).build();
	}

	@POST
	@Consumes("application/json")
	public Response postOne(String jsonString) {
		final Map inbound = JSONParser.defaultJSONParser().parse(Map.class, jsonString);
		final SimpleHybridEntity entity = new SimpleHybridEntity();
		entity.setName("" + inbound.get("name"));
		entity.setDescription("" + inbound.get("description"));
		entity.setSeparatedTags("" + inbound.get("tags"));
		entity.setTimestamp("" + (new Date().getTime()));
		this.em.persist(entity);
		return Response.ok().build();
	}

	@PUT
	@Path("{id}")
	public Response update(@PathParam("id") Integer id, String jsonContent) {
		final Map inbound = JSONParser.defaultJSONParser().parse(Map.class, jsonContent);
		final SimpleHybridEntity entity = this.getEntityManager().find(SimpleHybridEntity.class, id);

		entity.setName("" + inbound.get("name"));
		entity.setDescription("" + inbound.get("description"));
		entity.setSeparatedTags("" + inbound.get("tags"));

		this.getEntityManager().persist(entity);
		return Response.ok().build();
	}

	@DELETE
	@Path("{id}")
	public Response delete(@PathParam("id") Integer id) {
		final SimpleHybridEntity e = this.getEntityManager().find(SimpleHybridEntity.class, id);
		this.getEntityManager().remove(e);
		return Response.ok().build();
	}
}
