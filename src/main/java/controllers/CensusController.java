
/* CustomerController.java
 *
 * Copyright (C) 2013 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 * 
 */

package controllers;


import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import services.CensusService;
import utilities.RESTClient;
import domain.Census;

@Controller
@RequestMapping("/census")
public class CensusController extends AbstractController {

	
	@Autowired
	private CensusService censusService;
	
	
	// Constructors -----------------------------------------------------------

	public CensusController() {
		super();
	}

	
	
	/****   
	 * Metodos externos
	 ******/
	// Create census ---------------------------------------------------------------		
	//Recibe parametros de votacion y crea un censo por votacion
	
	@RequestMapping(value = "/create", method = RequestMethod.GET, produces="application/json")
	public  @ResponseBody Census create(@RequestParam int idVotacion,@RequestParam String fecha_inicio,@RequestParam String fecha_fin, String tituloVotacion,
			@CookieValue("user") String username) throws ParseException{ 
		Census result = null;
		
		Census  c = censusService.create(idVotacion, username, fecha_inicio, fecha_fin, tituloVotacion);
		try{
			 result = censusService.save(c);
		}catch(Exception oops){
			oops.getCause();
		}
		return result;
	}
	
	//Devuelve JSon a a votaciones para saber si pueden borrar una vaotacion
	//En caso afirmativo, el censo se borrara automaticamente al dar una respuesta positiva
	@RequestMapping(value = "/canDelete", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody String canDelete(@RequestParam int idVotacion, @CookieValue("user") String username){
		return censusService.canDelete(idVotacion, username);
	}	
	
	//Devuelve JSon a cabina para saber si un usuario puede votar
	@RequestMapping(value = "/canVote", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody String canVote(@RequestParam int idVotacion, @CookieValue("user") String username){
		return censusService.canVote(idVotacion, username);
	}
		
	
	// Actualiza el estado de un usuario en una votacion por cabina
	@RequestMapping(value = "/updateUser", method = RequestMethod.GET)
	public @ResponseBody String updateUser(@RequestParam int idVotacion , @CookieValue("user") String username) {
		try{
			if(censusService.updateUser(idVotacion, username)){
				return new String("{\"result\":\"yes\"}");
			}else{
				return new String("{\"result\":\"no\"}");
			}
			
		}catch(Exception oops){
			return new String("{\"result\":\"no\"}");
		}
		
	}
	
	//Devuelve un censo con sus usuarios para deliveraciones al preguntar por una votacion
	@RequestMapping(value = "/findCensusByVote", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody Census findCensusByVote(@RequestParam int idVotacion){
		return censusService.findCensusByVote(idVotacion);
	}
	
	/*****
	  * Métodos internos
	******/
	
	// Metodo para la vista de votaciones por usuario---------------------------------------------------------------	
	 @RequestMapping(value ="/votesByUser", method = RequestMethod.GET)    
	 public ModelAndView getVotesByUser(@CookieValue("user") String username) {  
		ModelAndView result = new ModelAndView("census/votesByUser");
		Collection<Census> cs;
		cs = censusService.findCensusByUser(username);
		result.addObject("misVotaciones",true);
		result.addObject("censues", cs);
		result.addObject("requestURI","census/votesByUser.do");
	
			
			
		 return result;
	 } 


	// Metodo para la vista de censos por creador---------------------------------------------------------------	
	 @RequestMapping(value ="/getAllCensusByCreador", method = RequestMethod.GET)    
	 public ModelAndView getAllCensusByCreador(@CookieValue("user") String username) {  
		 ModelAndView result = new ModelAndView("census/misCensos");;
		 Collection<Census> cs;
		 cs = censusService.findCensusByCreator(username);
		 result.addObject("censues", cs);
		 result.addObject("misVotaciones",false);
		 result.addObject("requestURI","census/getAllCensusByCreador.do");
	
			
			
		 return result;
	 } 
	 
	/*// Lista los censos del sistema  ---------------------------------------------------------------	

	@RequestMapping("/list")
	public ModelAndView list(@CookieValue("language") String cookie){
		String requestURI = "census/list.do";
		ModelAndView result = new ModelAndView("census/list");
		result.addObject("misVotaciones",false);
		result.addObject("censues", censusService.findAll());
		result.addObject("requestURI", requestURI);
		return result;
	}
	 */
	 
	// Add Users ----------------------------------------------------------------
	@RequestMapping(value = "/addUser", method = RequestMethod.GET)
	public ModelAndView addUser(@RequestParam int censusId, @CookieValue("user") String username, @RequestParam String username_add) {
		ModelAndView result = new ModelAndView("census/misVotaciones");;
		try{
			
			censusService.addUserToCensus(censusId,username, username_add);
			result = new ModelAndView("redirect:/census/edit.do?censusId="+censusId);
			
		}catch(Exception oops){
			result = new ModelAndView("redirect:/census/edit.do?censusId="+censusId);
			result.addObject("message", "No se pudo añadir el usuario");
			oops.getStackTrace();
		}
		

		return result;
	}
	 

	// Remove Users ----------------------------------------------------------------
	@RequestMapping(value = "/removeUser", method = RequestMethod.GET)
	public ModelAndView removeUser(@RequestParam int censusId, @CookieValue("user") String username, @RequestParam String username_remove) {
		ModelAndView result = null;
		try{
			
			censusService.removeUserToCensu(censusId, username,username_remove);
			result = new ModelAndView("redirect:/census/edit.do?censusId="+censusId);
			
		}catch(Exception oops){
			result = new ModelAndView("redirect:/census/edit.do?censusId="+censusId);
			result.addObject("message", "No se pudo eliminar el usuario porque ya ha votado");
			oops.getStackTrace();
		}
		

		return result;
	}
	
	
	// Details ----------------------------------------------------------------
	@RequestMapping(value = "/details", method = RequestMethod.GET)
	public ModelAndView details(@RequestParam int censusId) {
		ModelAndView result;
		Census census= censusService.findOne(censusId);
		result=createEditModelAndView(census);

		return result;
	}
	
	// Editar un censo para añadir o quitar usuarios ---------------------------------------------------------------	

	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public ModelAndView edit(@RequestParam int censusId, @CookieValue("user") String username) {
		ModelAndView result = new ModelAndView("census/manage");
		Collection<String> usernames = RESTClient.getListUsernamesByJsonAutentication();//Llamada a todos los usuarios del sistema
		Census census= censusService.findOne(censusId);
		Collection<String> user_list =census.getVoto_por_usuario().keySet();
		result.addObject("usernames", usernames);
		result.addObject("census", census);
		result.addObject("user", user_list);
		result.addObject("requestURI", "census/edit.do");
		

		return result;
	}
	
	/*// Delete ----------------------------------------------------------------
	@RequestMapping(value = "/delete", method = RequestMethod.GET)
	public ModelAndView delete(@RequestParam int censusId, String token) {
		ModelAndView result = null;
		try{
			censusService.delete(censusId, token);
			result = new ModelAndView("redirect:/census/list.do");
			
		}catch(Exception oops){
			result = new ModelAndView("redirect:/census/details.do?censusId="+censusId);
			result.addObject("message", "No se pudo borrar el censo");
			oops.getStackTrace();
		}
		

		return result;
	}
		*/

	//Save
	
	@RequestMapping(value = "/edit", method = RequestMethod.POST, params = "save")
	public ModelAndView save(@Valid Census census, BindingResult binding) {
		ModelAndView result;

		if (binding.hasErrors()) {
			result = createEditModelAndView(census);
		} else {
			try {
				censusService.save(census);
				result = new ModelAndView("redirect:/census/list.do");
				
			}catch (Throwable oops) {
				result = createEditModelAndView(census, "census.commit.error");				
			}
		}
		
		return result;
	}
	
	// Ancillary methods ------------------------------------------------------
	
	protected ModelAndView createEditModelAndView(Census census) {
		ModelAndView result;

		result = createEditModelAndView(census, null);
		
		return result;
	}	
	
	protected ModelAndView createEditModelAndView(Census census, String message) {
		ModelAndView result = new ModelAndView("census/create");
		
		if(census.getId() != 0){
			result = new ModelAndView("census/details");
		}
		HashMap<String, Boolean> mapa = census.getVoto_por_usuario();
		result.addObject("census", census);
		result.addObject("mapa",mapa );
		result.addObject("message", message); //esta definida en el layout, no en la vista edit
		
		return result;

	}
	 
	/* @RequestMapping(value ="/prueba", method = RequestMethod.GET, produces="application/json")    
	 public @ResponseBody  String prueba() {  
		 String res = "";
		 res += "[{\"username\":\"juan22\",\"password\": \"pass1\", \"email\":\"email1\"},";
		 res += "{\"username\":\"name2\",\"password\": \"pass2\", \"email\":\"email2\"},";
		 res += "{\"username\":\"name3\",\"password\": \"pass3\", \"email\":\"email3\"},";
		 res += "{\"username\":\"name4\",\"password\": \"pass4\", \"email\":\"email4\"},";
		 res += "{\"username\":\"name5\",\"password\": \"pass5\", \"email\":\"email5\"},";
		 res += "{\"username\":\"name6\",\"password\": \"pass6\", \"email\":\"email6\"},";
		 res += "{\"username\":\"name7\",\"password\": \"pass7\", \"email\":\"email7\"},";
		 res += "{\"username\":\"name8\",\"password\": \"pass8\", \"email\":\"email8\"}]";

		 return res;
	 }  */
	
}	
	