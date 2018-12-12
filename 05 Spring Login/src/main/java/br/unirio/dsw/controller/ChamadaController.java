package br.unirio.dsw.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import br.unirio.dsw.model.chamada.Chamada;
import br.unirio.dsw.model.unidade.Unidade;
import br.unirio.dsw.model.usuario.Usuario;
import br.unirio.dsw.service.dao.ChamadaDAO;
import br.unirio.dsw.service.dao.UnidadeDAO;
import br.unirio.dsw.utils.JsonUtils;

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
	public String lista(@ModelAttribute("page") int pagina, @ModelAttribute("size") int tamanho, @ModelAttribute("sigla") String filtroSigla, @ModelAttribute("nome") String filtroNome, Principal principal)
	{
		List<Chamada> chamadas = chamadaDAO.lista(pagina, tamanho, filtroSigla, filtroNome);
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		List<Unidade> unidades = unidadeDAO.listaPorGestor(usuario.getId());
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
		root.add("unidades", jsonUnidades);
		return root.toString();
	}

	/**
	 * Ação que salva os dados de uma nova unidade
	 */
	@ResponseBody
	@RequestMapping(value = "/chamada", method = RequestMethod.POST, consumes="application/json")
	public String salva(@RequestBody Chamada chamada)
	{
		if (chamada.getSigla().length() == 0)
			return JsonUtils.ajaxError("O código não pode ficar vazio.");
		
		if (chamada.getSigla().length() > 10)
			return JsonUtils.ajaxError("O código não deve ter mais do que 10 caracteres.");
		
		if (chamada.getNome().length() <= 0)
			return JsonUtils.ajaxError("O nome não pode ficar vazio.");
		
		if (chamada.getNome().length() > 80)
			return JsonUtils.ajaxError("O nome não pode ter mais do que 80 caracteres.");
 
		// TODO; testar os gestores
		
		if (chamada.getId() == -1)
			chamadaDAO.cria(chamada);
		else
			chamadaDAO.atualiza(chamada);
		
		return JsonUtils.ajaxSuccess();
	}
	
	/**
	 * Ação que remove uma unidade
	 */
	@ResponseBody
	@RequestMapping(value = "/chamada/{id}", method = RequestMethod.DELETE)
	public String remove(@PathVariable("id") int id, Locale locale)
	{
		Chamada chamada = chamadaDAO.carregaChamadaId(id);

		if (chamada == null)
			return JsonUtils.ajaxError(messageSource.getMessage("chamada.lista.remocao.nao.encontrado", null, locale));

		chamadaDAO.remove(id);
		return JsonUtils.ajaxSuccess();
	}
	
}
