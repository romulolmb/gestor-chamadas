app.service("chamadaService", function () {
	var self = this;

	self.unidade = null;

    return {
        getUnidade: function () {
            return self.chamada;
        },
        setUnidade: function (chamada) {
            self.chamada = chamada;
        }
    };
});