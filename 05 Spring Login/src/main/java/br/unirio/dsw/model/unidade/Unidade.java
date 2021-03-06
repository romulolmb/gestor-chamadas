package br.unirio.dsw.model.unidade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Classe que representa uma unidade funcional da UNIRIO
 * 
 * @author Marcio
 */
public class Unidade
{
	private @Setter @Getter int id;
	private @Setter @Getter String sigla;
	private @Setter @Getter String nome;
	private @Setter List<GestorUnidade> gestores;

	/**
	 * Inicializa uma unidade
	 */
	public Unidade()
	{
		this.id = -1;
		this.sigla = "";
		this.nome = "";
		this.gestores = new ArrayList<GestorUnidade>();
	}
	
	/**
	 * Conta o número de gestores na unidade
	 */
	public int contaGestores()
	{
		return gestores.size();
	}
	
	/**
	 * Retorna um gestor da unidade, dado seu índice
	 */
	public GestorUnidade pegaGestorIndice(int indice)
	{
		return gestores.get(indice);
	}
	
	/**
	 * Retorna todos os gestores da unidade
	 */
	public Iterable<GestorUnidade> pegaGestores()
	{
		if (gestores == null) return Collections.emptyList();
		return gestores;
	}
	
	/**
	 * Adiciona um gestor na unidade
	 */
	public void adicionaGestor(int id, String nome)
	{
		gestores.add(new GestorUnidade(id, nome));		
	}

	/**
	 * Remove um gestor, dado seu ID
	 */
	public void removeGestor(int id)
	{
		gestores.removeIf(g -> g.getId() == id); 
	}
	
	/**
	 * Remove todos os gestores da unidade
	 */
	public void limpaGestores()
	{
		gestores.clear();
	}
}