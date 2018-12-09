app.controller("listaController", function ($log, $location, $http, NgTableParams, unidadeService) {
	var self = this;

	/**
	 * Filtros
	 */
	self.filtros = {
		sigla: "",
		nome: ""
	}
	
	/**
	 * Altera os filtros de consulta
	 */
	self.atualizaFiltro = function () {
		atualizaLista();
	}
	
	/**
	 * Atualiza a lista de unidades
	 */
	var atualizaLista = function() {
		self.tableParams.reload();
	}
	
	/**
	 * Lista as unidades
	 */
	var lista = function(page, size, filtros) {
		return $http.get("cd?page=" + page + "&size=" + size + "&nome=" + (filtros.nome || "") + "&sigla=" + (filtros.sigla || ""));
	}
	
	/**
	 * Cria uma nova unidade
	 */
	self.nova = function() {
		var unidade = { id: -1, sigla: "", nome: "", gestores: [] };
		unidadeService.setUnidade (unidade);
        $location.path('/form');
	}
	
	/**
	 * Edita uma unidade
	 */
	self.edita = function(item) {
		var unidade = angular.copy(item);
		unidadeService.setUnidade (unidade);
        $location.path('/form');
	}
	
	/**
	 * Remove a unidade selecionada
	 */
	self.remove = function(id) {
		$http.delete("unidade/" + id).then(function(data){
			if (data.data.result == "OK"){
				M.toast({html: "Unidade Funcional deletada com sucesso!"});
				atualizaLista();
			}else {
				M.toast({html: data.data.message});
			}
		});
	}

	/**
	 * Prepara a tabela
	 */
	self.tableParams = new NgTableParams({}, {
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