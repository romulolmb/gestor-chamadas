<%@include file="../helper/header.jsp"%>

  <div class="container">
    <br/>
	<div data-ng-view></div>
    <br/><br/>
    <br/><br/>
    <br/><br/>
    <br/><br/>
    <br/><br/>
  </div>

  <%@include file="../helper/footer.jsp"%>


  <!--  Scripts-->
  <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.9/angular.min.js"></script>
  <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.6.9/angular-route.js"></script>
  <script src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
  <script src="static/third-party/materialize/js/materialize.js"></script>
  <script src="static/third-party/ng-sanitize/js/angular-sanitize.min.js"></script>
  <script src="static/third-party/ng-table/ng-table.min.js"></script>
  <script src="static/third-party/materialize/js/init.js"></script>

  <script>
  var app = angular.module("myApp", ["ngRoute", "ngTable", "ngSanitize", "SeletorUsuarios"]);
  
  app.config(function($routeProvider) {
	$routeProvider
	    .when("/lista", {
	        templateUrl: "static/html/chamada/lista/lista.html",
	        controller: "listaController as ctrl"
	    })
	    .when("/form", {
	        templateUrl: "static/html/chamada/form/form.html",
	        controller: "formController as ctrl"
	    })
	    .otherwise("/lista");
  });
  </script>
  
  <!-- Modulos -->
  <script src="static/components/seletor-usuarios/seletor-usuarios.js"></script>
  <script src="static/html/chamada/service/chamada.service.js"></script>
  <script src="static/html/chamada/lista/lista.controller.js"></script>
  <script src="static/html/chamada/form/form.controller.js"></script>

  </body>
</html>
