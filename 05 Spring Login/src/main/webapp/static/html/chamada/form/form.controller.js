app.controller("formController", function ($log, $location, $http, NgTableParams, chamadaService) {
	var self = this;

	/**
	 * Filtros
	 */
	self.chamada = chamadaService.getChamada();
	
	if (!self.chamada) {
        $location.path('/listaChamadas');
        return;
	}

	//window.M.updateTextFields();
	setTimeout(M.updateTextFields, 1);
	setTimeout(function() { 
		$('.modal').modal();
	}, 100);

	self.salva = function() {
		$http.post("chamada", self.chamada).then(function(data) {
			if (data.data.result == "OK") {
				M.toast({html: "Chamada salva com sucesso!"});
		        $location.path('/listaChamadas');
			}
			else
				M.toast({html: data.data.message});
		});
	}
	
	self.lista = function() {
        $location.path('/listaChamada');
	}
	
	/**
	 * Remove um campo da chamada
	 */
	self.removeCampo= function(id) {
		var index = self.chamada.campo.indexOf(id);
		if (index >= 0) self.chamada.campo.splice(index, 1);
	}
	
	/**
	 * Adiciona um campo na chamada
	 */
	self.adicionaCampo = function(resumo) {
		var possoAdicionar = true;
		for(i = 0; i < self.chamada.campo.length; i++){
			if (resumo.id == self.chamada.campo[i].id){
				possoAdicionar = false;
				break;
			}
		}
		if (possoAdicionar) self.chamada.campo.push(resumo);
	}
	
	/**
	 * Prepara a tabela
	 */
	self.tableCampo = new NgTableParams({}, {
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