<%@include file="../helper/header.jsp"%>

<div class="container">
	<br/>
	<br/>
	<h3>Esta � a sua homepage.</h3>
	
	<sec:authorize access="hasRole('ROLE_ADMIN')">
		<a href="unidade">Gerenciar Unidades</a>
	</sec:authorize>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
	<br/>
</div>

<%@include file="../helper/footer.jsp"%>
