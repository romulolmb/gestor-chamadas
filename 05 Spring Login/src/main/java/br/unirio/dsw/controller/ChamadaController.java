package br.unirio.dsw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import br.unirio.dsw.model.chamada.Chamada;
import br.unirio.dsw.model.unidade.Unidade;
import br.unirio.dsw.service.dao.ChamadaDAO;
import br.unirio.dsw.service.dao.UnidadeDAO;

@RestController
public class ChamadaController {
	
	@Autowired
	private MessageSource messageSource;

	@Autowired
	private ChamadaDAO chamadaDAO;
	
	@Autowired
	private UnidadeDAO unidadeDAO;
	
	@ResponseBody
	@RequestMapping(value = "/listaChamadas", method = RequestMethod.GET, produces = "application/json")
	public String lista(@ModelAttribute("page") int pagina, @ModelAttribute("size") int tamanho, @ModelAttribute("sigla") String filtroSigla, @ModelAttribute("nome") String filtroNome, @ModelAttribute("idGestor") int idGestor)
	{
		List<Chamada> chamadas = chamadaDAO.lista(pagina, tamanho, filtroSigla, filtroNome);
		List<Unidade> unidades = unidadeDAO.listaPorGestor(idGestor);
		int total = chamadaDAO.conta(filtroSigla, filtroNome);

		Gson gson = new Gson();
		JsonArray jsonChamadas = new JsonArray();
		JsonArray jsonUnidades = new JsonArray();

		for (Chamada chamada : chamadas)
			jsonChamadas.add(gson.toJsonTree(chamada));
		
		for (Unidade unidade : unidades)
			jsonUnidades.add(gson.toJsonTree(unidade));

		JsonObject root = new JsonObject();
		root.addProperty("Result", "OK");
		root.addProperty("TotalRecordCount", total);
		root.add("Records", jsonChamadas);
		root.add("Unidades", jsonUnidades);
		return root.toString();
	}

	
	
	
}
