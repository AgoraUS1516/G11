package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Census;


@Repository
public interface CensusRepository extends JpaRepository<Census, Integer>{
	
	@Query("select c from Census c where c.idVotacion = ?1")
	public Census findCensusByVote(int idVotacion);
	
	@Query("select c from Census c where c.username = ?1")
	public Collection<Census> findCensusByCreator(String username);

}
