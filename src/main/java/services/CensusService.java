package services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.CensusRepository;
import domain.Census;


@Service
@Transactional
public class CensusService {
	
	// Managed repository -----------------------------------------------------

	@Autowired
	private CensusRepository censusRepository;
	
	// Supporting services ----------------------------------------------------

	
	// Constructors -----------------------------------------------------------
	
	public CensusService(){
		super();
	}	
	
	// Others Methods
	
	/****
	 *Método que crea un censo creando el mapa y recibiendo el username del creador
	 *y la id de la votación
	 *
	 *	
	 * @param int idVotacion
	 * @return census
	 * @throws ParseException 
	 * 
	 ****/
	public Census create(int idVotacion, String username,String fecha_inicio,String fecha_fin, String tituloVotacion ) throws ParseException{ 
		Assert.isTrue(!username.equals(""));
		Census c = new Census();
		long start_date = Long.parseLong(fecha_inicio);
		long finish_date = Long.parseLong(fecha_fin);

		
		Date fecha_comienzo = new Date(start_date);
		Date fecha_final = new Date(finish_date);
		
		Assert.isTrue(fecha_comienzo.before(fecha_final));
		
		c.setFechaFinVotacion(fecha_final);
		c.setFechaInicioVotacion(fecha_comienzo);
		
		c.setIdVotacion(idVotacion);
		c.setTituloVotacion(tituloVotacion);
		c.setUsername(username);
		HashMap<String, Boolean> vpo = new HashMap<String, Boolean>();
		c.setVoto_por_usuario(vpo);		
					
		return c;
	}
	
	//Método para crear un censo e indicar si la votación es abierta o cerrada
	public Census create(int idVotacion, String username,String fecha_inicio,String fecha_fin, String tituloVotacion, boolean open ) throws ParseException{ 
		Assert.isTrue(!username.equals(""));
		Census c = new Census();
		long start_date = Long.parseLong(fecha_inicio);
		long finish_date = Long.parseLong(fecha_fin);

		
		Date fecha_comienzo = new Date(start_date);
		Date fecha_final = new Date(finish_date);
		
		Assert.isTrue(fecha_comienzo.before(fecha_final));
		
		c.setFechaFinVotacion(fecha_final);
		c.setFechaInicioVotacion(fecha_comienzo);
		
		c.setIdVotacion(idVotacion);
		c.setTituloVotacion(tituloVotacion);
		c.setUsername(username);
		HashMap<String, Boolean> vpo = new HashMap<String, Boolean>();
		c.setVoto_por_usuario(vpo);		
		
		c.setOpen(open);
					
		return c;
	}
	
	
	/***
	 * Metodo utilizado por cabina para actualizar el estado de voto de un usuario
	 * 
	 * @param censusId
	 * @param token
	 */
	public boolean updateUser(int idVotacion, String username) {
		boolean res = false;
		Assert.isTrue(!username.equals(""));
		Census c = findCensusByVote(idVotacion);
		HashMap<String, Boolean> vpo = c.getVoto_por_usuario();
		
		if (vpo.containsKey(username) && !vpo.get(username)){
			
			vpo.remove(username);
			vpo.put(username, true);
			res = true;
		}

		c.setVoto_por_usuario(vpo);
		save(c);
		
		return res;
	}
	
	/****
	 * Metodo para devolver un json para saber si puede borrar o no puede borrar una votacion
	 * 
	 * 
	 * @param idVotacion
	 * @return string format json
	 */
	public String canDelete(int idVotacion, String username){
		Assert.isTrue(!username.equals(""));
		String res = "";
		
		Census c = findCensusByVote(idVotacion);
		
		if(c.getVoto_por_usuario().isEmpty()){
			res = "[{\"result\":\"yes\"}]";
			delete(c.getId(), username);
		}else{
			res = "[{\"result\":\"no\"}]";
		}
		
		return res;
	}
	
	
	public String canVote(int idVotacion, String username) {
		Assert.isTrue(!username.equals(""));
		String res = "";
		
		Census c = findCensusByVote(idVotacion);
		
		if(c != null && c.getVoto_por_usuario().containsKey(username)){
			//Modificacion del codigo del año pasado, añadiendo la comprobación de que si el censo es abierto, se puede volver a votar.
			if(!c.getVoto_por_usuario().get(username) || c.getOpen()){
				res = "{\"result\":\"yes\"}";
				
			}else{
				
				res = "{\"result\":\"no\"}";
			}
			
		}else{
			res = "{\"result\":\"no\"}";
		}
		
		return res;
	}
	
	
	/****
	 * Metodo que devuelve todas las votaciones que un usuario no a votado y esta activa
	 * @param username
	 * @return Collection<census>
	 *
	 *
	 */
	public Collection<Census> findCensusByUser(String username) {
		Assert.isTrue(!username.equals(""));
		Collection<Census> cs = new ArrayList<Census>();
		Collection<Census> aux = findAll(); //Obtengo todos los censos
		
		for(Census c: aux){
			HashMap<String,Boolean> vpo = c.getVoto_por_usuario();
			if(vpo.containsKey(username)){
				boolean votado = vpo.get(username);
				boolean activa = votacionActiva(c.getFechaInicioVotacion(),c.getFechaFinVotacion());
				//Si el usuario esta en el censo, la votación esta activa y no ha votado tengo que mostrar su censo.
				if(!votado && activa){
					cs.add(c);
				}
			}
			
		}
		
		return cs;
	}
	
	/****
	 * Metodo que devuelve todas los censos que ha creado
	 * @param username
	 * @return Collection<census>
	 *
	 *
	 */
	public Collection<Census> findCensusByCreator(String username) {
		Assert.isTrue(!username.equals(""));
		Collection<Census> cs = censusRepository.findCensusByCreator(username);		
		return cs;
	}
	
	/****
	 * 
	 * Añade un usuario con un username determidado a un censo
	 * @param censusId identificador del censo a añadir el usuario
	 * @param username es el creador del censo
	 * @param username_add es el nick del usuario a añadir
	 */
	public void addUserToCensus(int censusId, String username, String username_add) {
		Census c = findOne(censusId);
		Assert.isTrue(votacionActiva(c.getFechaInicioVotacion(), c.getFechaFinVotacion()));
		Assert.isTrue(c.getUsername().equals(username));
		HashMap<String, Boolean> vpo = c.getVoto_por_usuario();
		Assert.isTrue(!vpo.containsKey(username_add));
		vpo.put(username_add, false);
		c.setVoto_por_usuario(vpo);
		save(c);
		
		
	}

	/****
	 * 
	 * Elimina un usuario con un token determidado a un censo,
	 * cumpliendo la condicion de que el usuario no tenga voto en ese censo
	 * @param censusId identificador del censo
	 * @param username creador del censo
	 * @param username_remove usuario a eliminar
	 */
	public void removeUserToCensus(int censusId, String username, String username_remove) {
		Census c = findOne(censusId);
		Assert.isTrue(votacionActiva(c.getFechaInicioVotacion(), c.getFechaFinVotacion()));
		HashMap<String, Boolean> vpo = c.getVoto_por_usuario();
		Assert.isTrue(c.getUsername().equals(username));
		Assert.isTrue(vpo.containsKey(username_remove) && !vpo.get(username_remove));
		vpo.remove(username_remove);
		c.setVoto_por_usuario(vpo);
		save(c);
		
		
	}
	
	/****
	 * 
	 * Persiste un censo 
	 * Tambien actualizara este para añadir usuarios y cambiar su valores de voto
	 * 
	 * @param census
	 * @return census
	 */
	public Census save(Census census){
		Census c = censusRepository.save(census);
		return c;
	}
	
	
	
	/****
	 * 
	 * Borra un censo determinado cumpliendo la condicion que el mapa de votos por usuarios debe estar vacia
	 * 
	 * @param censusId
	 * @param token
	 */
	public void delete(int censusId, String username){
		Census c = findOne(censusId);
		Assert.isTrue(c.getVoto_por_usuario().isEmpty());//Puedo borrarlo siempre y cuando no haya añadido usuario
		Assert.isTrue(c.getUsername().equals(username));
		censusRepository.delete(censusId);
	}
	

	/******
	 * 
	 * Encuentra un censo determidado por su id
	 * 
	 * @param censusId
	 * @return census
	 */
	public Census findOne(int censusId){
		Census c = censusRepository.findOne(censusId);
		Assert.notNull(c);
		return c;
	}
	
	
	/*****
	 * 
	 * Encuentra un censo por la votacion creada
	 * 
	 * @param idVotacion
	 * @return census
	 */
	public Census findOneByVote(int idVotacion){
		Census c = censusRepository.findCensusByVote(idVotacion);
		Assert.notNull(c);
		return c;
	}
	
	/******
	 * 
	 * Metodo que devuelve un json informando sbre un determinado usuario y su estado en el boto
	 * 
	 * @param idVotacion
	 * @param token
	 * @return String
	 */
	public String createResponseJson(int idVotacion, String username){
		String response = "";
		Census c = findOneByVote(idVotacion);
		//formato: idVotacion, token usuario, true/false
		if(c.getVoto_por_usuario().get(username)){
			response = response +  "{\"idVotacion\":" + idVotacion + ",\"username\":\"" + username + "\",\"result\":" + c.getVoto_por_usuario().get(username) + "}";
		} else {
			response = response +  "{\"result\":" + "user dont exist}";

		}
		return  response;
	}
	
	
	/*****
	 * 
	 * Encuentra todos los censos del sistema
	 * 
	 * @return Collection<Census>
	 */
	public Collection<Census> findAll(){
		return censusRepository.findAll();
	}

	
	
	/****
	 * 
	 * Añade un usuario con un token determidado a un censo
	 * @param censusId
	 * @param username
	 * @param username_add
	 */
	public void addUserToCensu(int censusId, String username, String username_add) {
		Census c = findOne(censusId);
		Assert.isTrue(c.getUsername().equals(username));
		HashMap<String, Boolean> vpo = c.getVoto_por_usuario();
		Assert.isTrue(!vpo.get(username_add));
		vpo.put(username_add, false);

		c.setVoto_por_usuario(vpo);
		save(c);
		
	}
	
	
	/****
	 * 
	 * Elimina un usuario con un token determidado a un censo,
	 * cumpliendo la condicion de que el usuario no tenga voto en ese censo
	 * @param censusId
	 * @param username
	 * @param username_remove
	 */
	public void removeUserToCensu(int censusId, String username, String username_remove) {
		Census c = findOne(censusId);
		Assert.isTrue(c.getUsername().equals(username));
		HashMap<String, Boolean> vpo = c.getVoto_por_usuario();
		Assert.isTrue(vpo.containsKey(username_remove));//contiene usuario
		Assert.isTrue(!vpo.get(username_remove));//Miro si ha votado
		vpo.remove(username_remove);
		c.setVoto_por_usuario(vpo);
		save(c);
		
	}
	
	/****
	 * Metodo para buscar un censo pur su votacion
	 * @param idVotacion
	 * @return census
	 */
	public Census findCensusByVote(int idVotacion){
		Census c = censusRepository.findCensusByVote(idVotacion);
		Assert.notNull(c);
		return c;
	}

	/***
	 * Metodo creado para saber si un censo tien 
	 * 
	 * @param fecha_inicio fecha inicio de la votacion
	 * @param fecha_fin fecha fin de la votacion
	 * @param c censo a comprobar
	 * @return true si esta activa
	 */
	private boolean votacionActiva(Date fecha_inicio,Date fecha_fin){
		Boolean res = false;
		Date fecha_actual = new Date();
		Long fecha_actual_long = fecha_actual.getTime();
		Long fecha_inicio_long = fecha_inicio.getTime();
		Long fecha_fin_long = fecha_fin.getTime();
		if(fecha_inicio_long < fecha_actual_long && fecha_fin_long > fecha_actual_long){
			res = true;
		}
		
		return res;
		
	}
	
	//METODOS NUEVOS
	
	//Censos que un usuario puede borrar
	public Collection<Census> canDelete(String username){
		Collection<Census> result;
		Collection<Census> aux;
		
		result = new ArrayList<Census>();
		
		aux = censusRepository.findCensusByCreator(username);
		
		for(Census c: aux){
			if(c.getVoto_por_usuario().isEmpty()){
				result.add(c);
			}
		}
		
		return result;
	}
	
	//Censos donde se encuentra una persona
	public Collection<Census> allCensusForPerson(String username){
		Collection<Census> result;
		Collection<Census> aux;
		
		result = new ArrayList<Census>();
		
		aux = censusRepository.findAll();  //Obtenemos todos los censos creados hasta la fecha
		

		for(Census census: aux){
			HashMap<String, Boolean> mapaUsuarios = census.getVoto_por_usuario();
			
			if(mapaUsuarios.containsKey(username)){ // Se comprueba que el usuario pasado por parametros este contenido en el censo.
				result.add(census);
			}
		}
		
		return result;
	}
	
	//Censos donde el usuario aún no ha votado
	public Collection<Census> allCensusCanVote(String username){
		Collection<Census> result;
		Collection<Census> aux;
		String voteCanVote = "{\"result\":\"yes\"}";
		result = new ArrayList<Census>();
		
		aux = censusRepository.findAll();  //Obtenemos todos los censos creados hasta la fecha
		

		for(Census census: aux){
			HashMap<String, Boolean> mapaUsuarios = census.getVoto_por_usuario();
			
			if(mapaUsuarios.containsKey(username) && voteCanVote.equals(canVote(census.getIdVotacion(), username))){ // Se comprueba que el usuario pasado por parametros este contenido en el censo. Y que pueda votar
				result.add(census);
			}
		}
		
		return result;
	}
	
	//Usuarios que ya han votado para una votación
	public Collection<String> findPeopleWhoHasVoted(int idVotacion){
		Collection<String> res = new ArrayList<String>();
		//Obtenemos el Censo de la votación
		Census c = censusRepository.findCensusByVote(idVotacion);
		
		//Recorremos la lista de nombres viendo los que han votado
		for(String v : c.getVoto_por_usuario().keySet()){
			//Si han votado añadimos el nombre al resultado
			if(c.getVoto_por_usuario().get(v)){
				res.add(v);
			}
		}
		return res;
	}
	
	//Usuarios que no han votado para una votación
	public Collection<String> findPeopleWhoHasNotVoted(int idVotacion){
		Collection<String> res = new ArrayList<String>();
		//Obtenemos el Censo de la votación
		Census c = censusRepository.findCensusByVote(idVotacion);
		
		//Recorremos la lista de nombres viendo los que han votado
		for(String v : c.getVoto_por_usuario().keySet()){
			//Si no han votado añadimos el nombre al resultado
			if(!c.getVoto_por_usuario().get(v)){
				res.add(v);
			}
		}
		return res;
	}
	
	//Metodo para saber si todos los votantes de un censo han votado
	public String CensusWhereAllUsersVoted(int idVotacion){
		String allVoted = "{\"result\":\"yes\"}";
		
		//Obtenemos el Censo de la votación
		Census c = censusRepository.findCensusByVote(idVotacion);
		
		//Recorremos el censo y miramos si todos los votantes han votado
		for(String u: c.getVoto_por_usuario().keySet()){
			//En el caso de que algun usuario no haya votado se devuelve no
			if(c.getVoto_por_usuario().get(u) == false){
				allVoted = "{\"result\":\"no\"}";
			}
		}
		return allVoted;
	}
	
	/*14 NOVIEMBRE*/
	
	//Metodo para abrir un censo que solo permite votar una vez para poder cambiar el voto
	public String openCensus(int idVotacion){
		Census c;
		String result;
		//Obtenemos el Censo de la votación
		c = censusRepository.findCensusByVote(idVotacion);
		
		if(!c.getOpen()){
			c.setOpen(true);
			save(c);
			result = "{\"result\":\"yes\"}";
		}else{
			result = "{\"result\":\"no\"}";
		}
		
		return result;
	}
	
	//Metodo para cerrar un censo para que solo permita votar una vez 
	public String closeCensus(int idVotacion){
		Census c;
		String result;
		//Obtenemos el Censo de la votación
		c = censusRepository.findCensusByVote(idVotacion);
		
		if(c.getOpen()){
			c.setOpen(false);
			save(c);
			result = "{\"result\":\"yes\"}";
		}else{
			result = "{\"result\":\"no\"}";
		}
		
		return result;
	}
	
	//Devolveremos los censos que cumplen un porcentaje entre 0 y 1. 
	//Si nuestro boleano es true devolvera censos mayores que el porcentaje, si es false dara los inferiores
	public Collection<Census> censusInThePercentaje(double percentaje,boolean superior){
		Collection<Census> res = new ArrayList<Census>();
		Collection<Census> allCensus = findAll();
		int participations = 0;
		int totalPeopleInCensus = 0;
		//Primero vamos a obtener el total de personas de cada censo y su total de participacion
		
		for(Census census : allCensus){
			Collection<Boolean> allVotes = census.getVoto_por_usuario().values();//obtenemos todos los votos de ese censo, si son false es que no se ha votado.
			
			for(Boolean bol : allVotes){
				totalPeopleInCensus++;//por cada voto sumamos este parametro, el cual nos dara el total de gente que habia en el censo
				
				if(bol){// si el voto es true significa que esa persona ha votado con lo cual sumaremos un voto mas a nuestro total
					participations++;
					
				}
				
			}
			double resultado = participations*1.0/totalPeopleInCensus*1.0;//obtenemos ya el porcentaje de participacion
			
			//reseteamos valores iniciales para el siguiente censo
			participations  = 0;
			totalPeopleInCensus = 0;
			
			//Si queremos que los censos sean superiores o igual al porcentaje dado:
			if(superior){
				if(resultado >= percentaje){
					res.add(census);
				}
			}
			//si queremos que los censos sean inferiores al porcentaje dado
			else if(!superior){
				if(resultado<= percentaje){
					res.add(census);
				}
			}
		}
		
		
		return res;
	}
	
	//Metodo que devuelve los censos activos (aun no ha llegado el final de la 
	//votación) de un mismo creador
	public Collection<Census> findActiveCensusByCreator(String username) {
		//Conseguimos la lista de todos los censos del creador
		Collection<Census> cs = censusRepository.findCensusByCreator(username);	
		//Inicializamos la variable resultado
		Collection<Census> res = new ArrayList<Census>();
		//Recorremos la lista buscando aquellos cuya fecha de fin sea
		//posterior al dia de hoy
		for(Census c : cs){
			if(c.getFechaFinVotacion().after(new Date())){
				res.add(c);
			}
		}
		return res;
	}
	
	//Metodo que devuelve los censos no activos (la fecha de fin ya ha pasado)
	//de un mismo creador
	public Collection<Census> findExpiredCensusByCreator(String username) {
		//Conseguimos la lista de todos los censos del creador
		Collection<Census> cs = censusRepository.findCensusByCreator(username);	
		//Inicializamos la variable resultado
		Collection<Census> res = new ArrayList<Census>();
		//Recorremos la lista buscando aquellos cuya fecha de fin sea
		//anterior al dia de hoy
		for(Census c : cs){
			if(c.getFechaFinVotacion().before(new Date())){
				res.add(c);
			}
		}
		return res;
	}
	
	//Metodo que elimina los usuarios de un censo pasandole una lista de
	//usuarios que están en el censo
	public Census deleteAllUsersFromAList(int idVotacion, Collection<String> UsersToDelete) {
	Census CensusWithoutDeletedUsers = new Census();
	
	Census censusAllUsers;
	String result;
	//Obtenemos el Censo de la votación
	censusAllUsers = censusRepository.findCensusByVote(idVotacion);
	
	for(String userToDelete: UsersToDelete){
		if(censusAllUsers.getVoto_por_usuario().containsKey(userToDelete)){
			if(censusAllUsers.getVoto_por_usuario().get(userToDelete)== false){
				censusAllUsers.getVoto_por_usuario().remove(userToDelete);
			}
		}
	}
	
	//Despues del for la lista de censusAllUsers ya no contiene a todos los usuarios
	CensusWithoutDeletedUsers = censusAllUsers;
	
	return CensusWithoutDeletedUsers;
	}
}

