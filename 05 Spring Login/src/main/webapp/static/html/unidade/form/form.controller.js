app.controller("formController", function ($log, $location, $http, NgTableParams, unidadeService) {
	var self = this;

	/*
	 * Filtros
	 */
	self.unidade = unidadeService.getUnidade();
	
	if (!self.unidade) {
        $location.path('/lista');
        return;
	}

	//window.M.updateTextFields();
	setTimeout(M.updateTextFields, 1);
	setTimeout(function() { 
		$('.modal').modal();
	}, 100);

	self.salva = function() {
		$http.post("unidade", self.unidade).then(function(data) {
			if (data.data.result == "OK") {
				M.toast({html: "Unidade funcional salva com sucesso!"});
		        $location.path('/lista');
			}
			else
				M.toast({html: data.data.message});
		});
	}
	
	self.lista = function() {
        $location.path('/lista');
	}
	
	/**
	 * Remove um gestor da lista
	 */
	self.removeGestor = function(id) {
		var index = self.unidade.gestores.indexOf(id);
		if (index >= 0) self.unidade.gestores.splice(index, 1);
	}
	
	/**
	 * Adiciona um gestor na lista
	 */
	self.adicionaUsuarioGestor = function(resumo) {
		var possoAdicionar = true;
		for(i = 0; i < self.unidade.gestores.length; i++){
			if (resumo.id == self.unidade.gestores[i].id){
				possoAdicionar = false;
				break;
			}
		}
		if (possoAdicionar) self.unidade.gestores.push(resumo);
	}
	
	/**
	 * Prepara a tabela
	 */
	self.tableGestores = new NgTableParams({}, {
		getData: function (params) {
			return lista(params.page() - 1, params.count(), self.filtros).then(function (data) {
				if(data.data.TotalRecordCount == 0) {
					self.noSite = true;
				}
				else {
					params.total(data.data.TotalRecordCount);
					self.noSite = false;
					return data = data.data.Records;
				}
			});
		}
	});
});