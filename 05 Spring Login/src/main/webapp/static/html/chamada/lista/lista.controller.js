app.controller("listaController", function ($log, $location, $http, NgTableParams, chamadaService) {
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
	 * Atualiza a lista de chamada
	 */
	var atualizaLista = function() {
		self.tableParams.reload();
	}
	
	/**
	 * Lista as chamadas
	 */
	var lista = function(page, size, filtros) {
		return $http.get("listaChamadas?page=" + page + "&size=" + size + "&nome=" + (filtros.nome || "") + "&sigla=" + (filtros.sigla || "") + "&idGestor=2");
	}
	
	/**
	 * Formata a data para dd/MM/yyyy
	 */
	self.formataData = function(data) {
		var dataConvertida = new Date(data.iMillis);
		var dia = dataConvertida.getDate().toString();
		var mes = (dataConvertida.getMonth()+1).toString();
		var ano = dataConvertida.getFullYear().toString();
		var pad = "00";
		var novaData = pad.substring(0, pad.length - dia.length) + dia + "/" + pad.substring(0, pad.length - mes.length) + mes + "/" + ano; 
		return novaData
	}
	
	/**
	 * Cria uma nova chamada
	 */
	self.nova = function() {
		var chamada = { id: -1, sigla: "", nome: "", camposChamada: [], idUnidade: 5 };
		chamadaService.setChamada(chamada);
        $location.path('/form');
	}
	
	/**
	 * Edita uma chamada
	 */
	self.edita = function(item) {
		var chamada = angular.copy(item);
		chamada.dataAbertura = new Date(chamada.dataAbertura.iMillis);
		chamada.dataEncerramento = new Date(chamada.dataEncerramento.iMillis);
		chamadaService.setChamada (chamada);
        $location.path('/form');
	}
	
	/**
	 * Remove a chamada selecionada
	 */
	self.remove = function(id) {
		$http.delete("listaChamadas/" + id).then(function(data){
			if (data.data.result == "OK"){
				M.toast({html: "Chamada deletada com sucesso!"});
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