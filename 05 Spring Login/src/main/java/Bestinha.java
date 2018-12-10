import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import br.unirio.dsw.model.chamada.Chamada;
import br.unirio.dsw.model.unidade.Unidade;
import br.unirio.dsw.service.dao.ChamadaDAO;
import br.unirio.dsw.service.dao.UnidadeDAO;

public class Bestinha {
	public static final void main(String[] args) {
//		Unidade unidade = new Unidade();
//		unidade.setNome("nome 1");
//		unidade.setId(1);
//		unidade.setSigla("ABC");
//		unidade.adicionaGestor(1, "Ze");
//		System.out.println(new Gson().toJson(unidade));
//		
//		Unidade unidade2 = (Unidade) new Gson().fromJson("{\"id\":2,\"sigla\":\"BSI\",\"nome\":\"Bacharelado de Sistemas de Informação\",\"gestores\":[{\"id\":1,\"nome\":\"Romulo Brito\"}]}", Unidade.class);
//		System.out.println(unidade2.getNome());
		
		UnidadeDAO unidadeDAO = new UnidadeDAO();
		ChamadaDAO chamadaDAO = new ChamadaDAO();
//		List<Unidade> listaUnidade = unidadeDAO.listaPorGestor(2);
//		
//		System.out.println("123");
		
		List<Chamada> chamadas = chamadaDAO.lista(1, 10, "", "");
		List<Unidade> unidades = unidadeDAO.listaPorGestor(2);
		int total = chamadaDAO.conta("", "");

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
		System.out.println(root.toString());
	}
}
