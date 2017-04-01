package appSpring.restController;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import appSpring.model.Action;
import appSpring.model.Genre;
import appSpring.model.Resource;
import appSpring.model.ResourceCopy;
import appSpring.model.ResourceType;
import appSpring.service.ActionService;
import appSpring.service.ResourceCopyService;
import appSpring.service.ResourceService;

@RestController
@RequestMapping("/api/resources")
public class ResourceRestController {

	public interface ResourceDetail extends Resource.Basic, Resource.ResoType, Resource.Genr, Resource.ResoCopy, ResourceCopy.Basic, Genre.Basic, ResourceType.Basic {}
	
	@Autowired
	private ResourceService resourceService;
	@Autowired
	private ActionService actionService;
	@Autowired
	private ResourceCopyService resourceCopyService;

	@JsonView(ResourceDetail.class)
	@RequestMapping(value = "/", method = RequestMethod.POST)
	public ResponseEntity<Resource> postResource(@RequestBody Resource resource, HttpSession session) {

		session.setMaxInactiveInterval(-1);
		for (ResourceCopy resourceCopy : resource.getResourceCopies()) {
			if (resourceCopyService.findOne(resourceCopy.getID()) == null) {
				resourceCopy.setResource(resource);
				resourceCopyService.save(resourceCopy);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
			}
		}
		resourceService.save(resource);
		return new ResponseEntity<>(resource, HttpStatus.OK);
	}

	@JsonView(ResourceDetail.class)
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ResponseEntity<List<Resource>> getAllResource(HttpSession session,
			@RequestParam(value = "genre", required = false) String genre,
			@RequestParam(value = "type", required = false) String type) {
		
		session.setMaxInactiveInterval(-1);
		List<Resource> resources = resourceService.findByGenreAndTypeAllIgnoreCase(genre, type);
		if (resources != null) {
			return new ResponseEntity<>(resources, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@JsonView(ResourceDetail.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<Resource> getResource(@PathVariable int id, HttpSession session) {

		session.setMaxInactiveInterval(-1);
		Resource resource = resourceService.findOne(id);
		if (resource != null) {
			return new ResponseEntity<>(resource, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@JsonView(ResourceDetail.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Resource> deleteResource(@PathVariable Integer id, HttpSession session) {

		session.setMaxInactiveInterval(-1);
		Resource resourceSelected = resourceService.findOne(id);
		if (resourceSelected == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		} else {
			List<Action> actions = actionService.findAll();
			for (Action action : actions) {
				if ((action.getDateLoanReturn() == null) && (action.getResource().getResource() == resourceSelected)) {
					return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
				}
			}
			resourceService.delete(resourceSelected);
			return new ResponseEntity<>(resourceSelected, HttpStatus.OK);
		}
	}

	@JsonView(ResourceDetail.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Resource> putResource(@PathVariable Integer id, @RequestBody Resource resourceUpdated,
			HttpSession session) {

		session.setMaxInactiveInterval(-1);
		Resource resource = resourceService.findOne(id);
		if ((resource != null) && (resource.getId() == resourceUpdated.getId())) {
			resourceService.save(resourceUpdated);
			return new ResponseEntity<>(resourceUpdated, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

}