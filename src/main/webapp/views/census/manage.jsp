<%--
 * action-2.jsp
 *
 * Copyright (C) 2013 Universidad de Sevilla
 * 
 * The use of this project is hereby constrained to the conditions of the 
 * TDG Licence, a copy of which you may download from 
 * http://www.tdg-seville.info/License.html
 --%>

<%@page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<%@taglib prefix="jstl"	uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@taglib prefix="security" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="display" uri="http://displaytag.sf.net"%>
<%@taglib prefix="acme" tagdir="/WEB-INF/tags" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>


		<spring:message code="census.token_propietario" />:
		<jstl:out value="${census.username}"/><br/>
		
		<spring:message code="census.votacion_id" />:
		<jstl:out value="${census.idVotacion}"/><br />
		
		<spring:message code="census.votacio.name" />:
		<jstl:out value="${census.tituloVotacion}"/><br />
		
		<spring:message code="census.votacio.name" />:
		<jstl:out value="${census.tituloVotacion}"/><br />
		
		<spring:message code="census.fecha.inicio" />:
		<fmt:formatDate value="${census.fechaInicioVotacion}" pattern="dd/MM/yyyy"/><br/>		
		
		<spring:message code="census.fecha.fin" />:
		<fmt:formatDate value="${census.fechaFinVotacion}" pattern="dd/MM/yyyy"/><br/>
	
<br/>

<jstl:if test="${!empty usernames}">

	<display:table pagesize="5" class="displaytag" name="usernames" requestURI="${requestURI}" id="row">
		
		<!-- Action links -->
		
		<!-- Attributes -->
		<spring:message code="user.name" var="nameHeader" />
		<display:column title="${nameHeader}" sortable="true" >
		<jstl:out value="${row}"></jstl:out>
		</display:column>
		
		<spring:message code="census.joins" var="joinsHeader" />
		<display:column title="${joinsHeader}">
			<jstl:if test="${!(fn:contains(user, row))}">
				<a href="census/addUser.do?censusId=${census.id}&username_add=${row}"> 
				<img src="images/join.png" style="width: 32px; height: 32px;"/></a>
			</jstl:if>
		</display:column>
		
		<spring:message code="census.no.joins" var="noJoinsHeader" />
		<display:column title="${noJoinsHeader}">
			<jstl:if test="${(fn:contains(user, row))}">
			<a href="census/removeUser.do?censusId=${census.id}&username_remove=${row}"> 
			<img src="images/trash.png" style="width: 32px; height: 32px;"/></a>
			</jstl:if>
		</display:column>		
	</display:table>
	<br/>
	<br/>

</jstl:if>



<jstl:if test="${empty usernames}">

</jstl:if>

<acme:cancel url="census/details.do?censusId=${census.id}" code="census.back"/>
