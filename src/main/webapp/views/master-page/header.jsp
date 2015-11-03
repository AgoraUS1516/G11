<%--
 * header.jsp
 *
 * Copyright (C) 2014 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>


<div id="cssmenu">
	<ul>
		<security:authorize access="isAnonymous()">
			<li class='has-sub'><a><spring:message code="master.page.api" /></a>
				<ul>
					<li><a target="_blank" href="census/create.do?idVotacion=100&fecha_inicio=1316502444473&fecha_fin=1916502444473&tituloVotacion=nueva votacion 10"><spring:message code="master.page.census.create" /></a></li>
					<li><a target="_blank" href="census/canDelete.do?idVotacion=1"><spring:message code="master.page.census.canDelete" /></a></li> 
					<li><a target="_blank" href="census/canVote.do?idVotacion=3"><spring:message code="master.page.census.canVote" /></a></li> 
					<li><a target="_blank" href="census/updateUser.do?idVotacion=3"><spring:message code="master.page.census.update.user" /></a></li> 
					<li><a target="_blank" href="census/findCensusByVote.do?idVotacion=1"><spring:message code="master.page.census.fomdUserByCensus" /></a></li> 
				</ul>
			</li>
			
			<li class='has-sub'><a><spring:message code="master.page.listar" /></a>
				<ul>
					<li><a href="census/votesByUser.do"><spring:message code="master.page.census.my.votes" /></a></li>
					<li><a href="census/getAllCensusByCreador.do"><spring:message code="master.page.census.byCreator" /></a></li> 
				</ul>
			</li>
			<li class='has-sub'><a href="http://localhost:8080/CreacionAdminVotaciones"><spring:message code="master.page.votaciones" /></a></li>
			<li class='has-sub'><a href="http://localhost/results_view/HTML/index.html"><spring:message code="master.page.resultados" /></a></li>
			<li class='has-sub'><a href="http://localhost:8080/Deliberations"><spring:message code="master.page.deliberaciones" /></a></li>
		</security:authorize>
	</ul>
</div>


