package domain;

import java.util.Date;
import java.util.HashMap;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;


@Entity
@XmlRootElement(name="census")
@Access(AccessType.PROPERTY)
public class Census extends DomainEntity{
	
	private String username;//Username del usuario que crea la votacion
	private int idVotacion;//Id que identifica de forma únia a la votacion
	private String tituloVotacion;//Titulo de la votacion
	private HashMap<String, Boolean> voto_por_usuario = new HashMap<String, Boolean>();//Mapa encargado de asignar un true o false (ha votado o no) a un token unico de un usuario
	private Date fechaInicioVotacion;//Fecha en la que se inicia la votacion
	private Date fechaFinVotacion;//Fecha en la que finaliza la votacion
	
	
	public Census(){
		
	}

    @MapKeyColumn(name="token")
    @Column(name="valor")
    @CollectionTable(name="value", joinColumns=@JoinColumn(name="token"))
	public HashMap<String, Boolean> getVoto_por_usuario() {
		return voto_por_usuario;
	}

	public void setVoto_por_usuario(HashMap<String, Boolean> voto_por_usuario) {
		this.voto_por_usuario = voto_por_usuario;
	}

	@NotBlank
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(unique = true)
	public int getIdVotacion() {
		return idVotacion;
	}

	public void setIdVotacion(int idVotacion) {
		this.idVotacion = idVotacion;
	}

	@NotBlank
	public String getTituloVotacion() {
		return tituloVotacion;
	}

	public void setTituloVotacion(String tituloVotacion) {
		this.tituloVotacion = tituloVotacion;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	@NotNull
	public Date getFechaInicioVotacion() {
		return fechaInicioVotacion;
	}

	public void setFechaInicioVotacion(Date fechaInicioVotacion) {
		this.fechaInicioVotacion = fechaInicioVotacion;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm")
	@NotNull
	public Date getFechaFinVotacion() {
		return fechaFinVotacion;
	}

	public void setFechaFinVotacion(Date fechaFinVotacion) {
		this.fechaFinVotacion = fechaFinVotacion;
	}

	

	
	
	
	

}


