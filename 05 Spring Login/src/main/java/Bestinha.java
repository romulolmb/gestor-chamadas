import com.google.gson.Gson;

import br.unirio.dsw.model.unidade.Unidade;

public class Bestinha {
	public static final void main(String[] args) {
		Unidade unidade = new Unidade();
		unidade.setNome("nome 1");
		unidade.setId(1);
		unidade.setSigla("ABC");
		unidade.adicionaGestor(1, "Ze");
		System.out.println(new Gson().toJson(unidade));
		
		Unidade unidade2 = (Unidade) new Gson().fromJson("{\"id\":2,\"sigla\":\"BSI\",\"nome\":\"Bacharelado de Sistemas de Informação\",\"gestores\":[{\"id\":1,\"nome\":\"Romulo Brito\"}]}", Unidade.class);
		System.out.println(unidade2.getNome());
	}
}
