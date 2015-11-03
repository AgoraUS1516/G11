<%@page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security"	uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags" %>

<display:table pagesize="5" class="displaytag" name="censues" requestURI="${requestURI}" id="row">
	
	<!-- Action links -->
	
	<jstl:if test="${!misVotaciones}">
		<display:column>
			<a href="census/details.do?censusId=${row.id}">
				<img alt="<spring:message	code="census.details" />" src="images/details.png" style="height: 25px; width: 25px;">
				
			</a>
		</display:column>
	</jstl:if>
	
	<jstl:if test="${misVotaciones}">
		<display:column>
		<a href="http://localhost:8000/cabinaus/${row.idVotacion}">
			<img alt="<spring:message	code="census.details" />" src="images/votar.gif" style="height: 25px; width: 25px;">
			
		</a>
	</display:column>
	</jstl:if>
	
	<jstl:if test="">
	
	</jstl:if>
			
	<!-- Attributes -->
	<spring:message code="census.token_propietario" var="username" />
	<display:column property="username" title="${username}" sortable="true" />
	
	<jstl:if test="${!misVotaciones}">
		<spring:message code="census.votacion_id" var="idVotacion" />
		<display:column property="idVotacion" title="${idVotacion}" sortable="true" />
	</jstl:if>
	<spring:message code="census.votacio.name" var="tituloVotacion" />
	<display:column property="tituloVotacion" title="${tituloVotacion}" sortable="true" />
	
	<spring:message code="census.fecha.inicio" var="fechaInicioVotacion" />
	<display:column title="${fechaInicioVotacion}" sortable="true" >
		<fmt:formatDate value="${row.fechaInicioVotacion}" pattern="dd/MM/yyyy"/><br/>
	</display:column>
	
	<spring:message code="census.fecha.fin" var="fechaFinVotacion" />
	<display:column title="${fechaFinVotacion}" sortable="true" >
		<fmt:formatDate value="${row.fechaFinVotacion}" pattern="dd/MM/yyyy"/><br/>
	</display:column>
	
	<spring:message code="census.number.person" var="sizeHeader" />
	<display:column  title="${sizeHeader}" sortable="true" >
		<jstl:out value="${row.voto_por_usuario.size() }"></jstl:out>
	</display:column>
	
	
</display:table>
<br/>
<acme:cancel url="welcome/index.do" code="census.back"/>
