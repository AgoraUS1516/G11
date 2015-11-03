package utilities;

import java.text.ParseException;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import security.LoginService;
import services.CensusService;
import utilities.PopulateDatabase;
import domain.Census;

@ContextConfiguration(
		locations = {
				"classpath:spring/datasource.xml",
				"classpath:spring/config/packages.xml"
		})
@RunWith(SpringJUnit4ClassRunner.class)
public class CreateCensues {
	
	@Autowired
	private LoginService loginService;
	
	@Autowired
	private CensusService censusService;
	
	public void authenticate(String customername){
		UserDetails userDetails;
		TestingAuthenticationToken authenticationToken;
		SecurityContext context;
		
		userDetails = loginService.loadUserByUsername(customername);
		authenticationToken = new TestingAuthenticationToken(userDetails, null);
		context = SecurityContextHolder.getContext();
		context.setAuthentication(authenticationToken);
	}
	
	@Before
	public void setUp(){
		PopulateDatabase.main(null);
	}
	
	/***************************POSITIVE
	 * @throws ParseException *******************************/
	
	//Crear una actividad
	@Test
	public void createCensus() throws ParseException{
		
		
		Census c= censusService.create(1, "juan22", "1416502444473", "1916502444473", "votacion prueba");
		
		HashMap<String, Boolean> vpo = c.getVoto_por_usuario();
		
		vpo.put("pepe", true);
		vpo.put("juan22", false);
		vpo.put("maria", true);
		
		c.setVoto_por_usuario(vpo);
		
		censusService.save(c);
		
		
		Census c2= censusService.create(2, "juan22", "1416502444473", "1916502444473", "votacion prueba2");
		
		HashMap<String, Boolean> vpo2 = c.getVoto_por_usuario();
		
		
		c.setVoto_por_usuario(vpo2);
		
		censusService.save(c2);
		
		
		Census c3= censusService.create(3, "juan23", "1416502444473", "1916502444473", "votacion prueba3");
		
		HashMap<String, Boolean> vpo3 = c3.getVoto_por_usuario();
		
		vpo3.put("juan22", false);
		
		c.setVoto_por_usuario(vpo3);
		
		censusService.save(c3);
	}
}
