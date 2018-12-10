app.service("chamadaService", function () {
	var self = this;

	self.chamada = null;

    return {
        getChamada: function () {
            return self.chamada;
        },
        setChamada: function (chamada) {
            self.chamada = chamada;
        }
    };
});