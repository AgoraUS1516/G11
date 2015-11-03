package utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.web.client.RestTemplate;

public class RESTClient {
	
	/***
	 * 
	 * Metodo que le un Json de autenticacion y devuelve una lista con los nombres de usuario para que el
	 * admin del censo pueda añadir usuarios. 
	 * 
	 * @return
	 */
	public static Collection<String> getListUsernamesByJsonAutentication(){
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject("http://localhost/auth/api/index.php?method=getUsers", String.class);
		String[] lista = result.split(",");
		
		Collection<String> usernames = new ArrayList<String>();
		for(int i=0; i<lista.length; i++){
			String aux = lista[i];
			String aux2 = "";
			if(i==0){
				aux = aux.replace("[{\"username\":\"", "");
				aux = aux.replace("\"", "");
				aux = aux.replace("ï»¿", "");
				aux2 = aux;
				
			}else{
				if(aux.contains("username")){
					aux = aux.replace("{\"username\":\"", "");
					aux = aux.replace("\"", "");
					aux2 = aux;
				}
			}
			if(!aux2.equals("")){
				aux2 = aux2.replace(" ", "");
				usernames.add(aux2);
			}
			
		}
		
		return usernames;
	}

	
	public static void main(String[] args) throws IOException {
		Collection<String> usernames = getListUsernamesByJsonAutentication();
		for(String st: usernames){
			System.out.println(st);
		}
	}
}
