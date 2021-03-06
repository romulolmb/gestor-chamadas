package br.unirio.dsw.service.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import br.unirio.dsw.model.chamada.CampoChamada;
import br.unirio.dsw.model.chamada.Chamada;
import br.unirio.dsw.utils.DateUtils;


/**
 * Classe responsavel pela persistência de chamadas
 * 
 * @author Mlandrini
 */
@Service("chamadaDAO") 
public class ChamadaDAO extends AbstractDAO
{	
	/**
	 * Carrega os dados de uma chamada a partir do resultado de uma consulta
	 */
	private Chamada carregaChamada(ResultSet rs) throws SQLException
	{
		Chamada chamada = new Chamada();
		chamada.setId(rs.getInt("id"));
		chamada.setDataRegistro(DateUtils.toDateTime(rs.getTimestamp("dataRegistro")));
		chamada.setDataAtualizacao(DateUtils.toDateTime(rs.getTimestamp("dataAtualizacao")));
		chamada.setIdUnidade(rs.getInt("idUnidade"));
		chamada.setNome(rs.getString("nome"));
		chamada.setSigla(rs.getString("sigla"));
		chamada.setDataAbertura(DateUtils.toDateTime(rs.getTimestamp("dataAbertura")));
		chamada.setDataEncerramento(DateUtils.toDateTime(rs.getTimestamp("dataEncerramento")));
		chamada.setCancelada(rs.getInt("cancelada") != 0);
		chamada.setEncerrada(rs.getInt("encerrada") != 0);
		return chamada;
	}
	
	/**
	 * Carrega os dados de um campo da chamada a partir de uma consulta
	 */
	private CampoChamada carregaCampo(ResultSet rs) throws SQLException
	{
		int id = rs.getInt("id");
		String titulo = rs.getString("titulo");
		int tipo = rs.getInt("tipo");
		int decimais = rs.getInt("decimais");
		boolean opcional = rs.getInt("opcional") != 0;
		CampoChamada campo = new CampoChamada(id, titulo, tipo, decimais, opcional);
		
		Gson gson = new Gson();
		ArrayList<String> listaOpcoes = gson.fromJson(rs.getString("jsonOpcoes"), new TypeToken<ArrayList<String>>(){}.getType());
		for(String opcao: listaOpcoes)
			campo.adicionaOpcao(opcao);
		return campo;
	}
	
	/**
	 * Carrega uma chamada, dado seu identificador
	 */
	public Chamada carregaChamadaId(int id)
	{
		Connection c = getConnection();
		
		if (c == null)
			return null;
		
		try
		{
			PreparedStatement ps = c.prepareStatement("SELECT * FROM Chamada WHERE id = ?");
			ps.setLong(1, id);
			ResultSet rs = ps.executeQuery();
			Chamada item = null;
			
			if (rs.next())
			{
				item = carregaChamada(rs);
				carregaCampos(c, item);
			}
			
			c.close();
			return item;

		} catch (SQLException e)
		{
			log("ChamadaDAO.carregaChamadaId: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Carrega todos os campos de uma chamada
	 */
	private void carregaCampos(Connection c, Chamada chamada) throws SQLException
	{
		PreparedStatement ps = c.prepareStatement("SELECT * FROM CampoChamada WHERE idChamada = ?");
		ps.setLong(1, chamada.getId());
		ResultSet rs = ps.executeQuery();
		
		while (rs.next())
		{
			CampoChamada campo = carregaCampo(rs);
			chamada.adicionaCamposChamada(campo);
		}
	}

	/**
	 * Conta o número de chamadas segundo um filtro
	 */
	public int conta(String filtroNome, String filtroSigla)
	{
		String SQL = "SELECT COUNT(*) " +
					 "FROM Chamada " + 
					 "WHERE nome like ? " +
					 "AND sigla like ? ";
		
		Connection c = getConnection();
		
		if (c == null)
			return 0;
		
		try
		{
			PreparedStatement ps = c.prepareStatement(SQL);
			ps.setString(1, "%" + filtroNome+ "%");
			ps.setString(2, "%" + filtroSigla + "%");
			ResultSet rs = ps.executeQuery();
			int contador = rs.next() ? rs.getInt(1) : null;
			c.close();
			return contador;

		} catch (SQLException e)
		{
			log("ChamadaDAO.conta: " + e.getMessage());
			return 0;
		}
	}

	/**
	 * Retorna uma lista de chamadas segundo um filtro
	 */
	public List<Chamada> lista(int pagina, int tamanhoPagina, String filtroNome, String filtroSigla)
	{
		String SQL = "SELECT * " +
					 "FROM Chamada " + 
					 "WHERE nome like ? " +
					 "AND sigla like ? " +
					 "AND cancelada != 1 " + //Não busca as chamadas canceladas
					 "LIMIT ? OFFSET ? ";
		
		Connection c = getConnection();
		List<Chamada> chamadas = new ArrayList<Chamada>();
		
		if (c == null)
			return chamadas;
		
		try
		{
			PreparedStatement ps = c.prepareStatement(SQL);
			ps.setString(1, "%" + filtroNome + "%");
			ps.setString(2, "%" + filtroSigla + "%");
			ps.setInt(3, tamanhoPagina);
			ps.setInt(4, pagina * tamanhoPagina);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next())
			{
				Chamada chamada = carregaChamada(rs);
				carregaCampos(c, chamada);
				chamadas.add(chamada);
			}
			
			c.close();
			return chamadas;

		} catch (SQLException e)
		{
			log("ChamadaDAO.lista: " + e.getMessage());
			return chamadas;
		}
	}

	/**
	 * Adiciona uma chamada no sistema
	 */
	public boolean cria(Chamada chamada)
	{
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call ChamadaInsere(?, ?, ?, ?, ?, ?)}");
			cs.setInt(1, chamada.getIdUnidade());
			cs.setString(2, chamada.getNome());
			cs.setString(3, chamada.getSigla());
			cs.setTimestamp(4, new Timestamp(chamada.getDataAbertura().getMillis()));
			cs.setTimestamp(5, new Timestamp(chamada.getDataEncerramento().getMillis()));
			cs.registerOutParameter(6, Types.INTEGER);
			cs.execute();
			
			chamada.setId(cs.getInt(6));
			adicionaCampos(chamada);
			
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("ChamadaDAO.cria: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Atualiza uma Chamada no sistema
	 */
	public boolean atualiza(Chamada chamada)
	{
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call ChamadaAtualiza(?, ?, ?, ?, ?, ?)}");
			cs.setInt(1, chamada.getIdUnidade());
			cs.setInt(2, chamada.getId());			
			cs.setString(3, chamada.getNome());
			cs.setString(4, chamada.getSigla());
			cs.setTimestamp(5, new Timestamp(chamada.getDataAbertura().getMillis()));
			cs.setTimestamp(6, new Timestamp(chamada.getDataEncerramento().getMillis()));	
			cs.execute();
			
			removeCampos(chamada);
			adicionaCampos(chamada);

			c.close();
			return true;

		} catch (SQLException e)
		{
			log("ChamadaDAO.atualiza: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Remove uma Chamada no sistema
	 */
	public boolean remove(int idChamada)
	{
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call ChamadaRemove(?)}");
			cs.setInt(1, idChamada);
			cs.execute();
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("ChamadaDAO.remove: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Encerra uma chamada
	 */
	public boolean encerra(int idChamada)
	{
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call ChamadaEncerra(?)}");
			cs.setInt(1, idChamada);
			cs.execute();
			c.close();
			return true;

		} catch (SQLException e)
		{
			log("ChamadaDAO.encerra: " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Adiciona os campos em uma chamada
	 */
	private void adicionaCampos(Chamada chamada) throws SQLException
	{
		for (CampoChamada campoChamada: chamada.pegaCamposChamada())
			adicionaCampo(chamada.getId(), campoChamada);
	}

	/**
	 * Adiciona um campo em uma unidade
	 */
	private void adicionaCampo(int idChamada, CampoChamada campoChamada) throws SQLException
	{
		Connection c = getConnection();
		CallableStatement cs = c.prepareCall("{call CampoChamadaInsere(?, ?, ?, ?, ?, ?, ?)}");
		cs.setInt(1, idChamada);
		cs.setString(2, campoChamada.getTitulo());
		cs.setInt(3, campoChamada.getTipo());
		cs.setInt(4, campoChamada.getDecimais());
		cs.setInt(5, campoChamada.isOpcional() ? 1 : 0);
		cs.setString(6, new Gson().toJson(campoChamada.pegaOpcoes()));
		cs.registerOutParameter(7, Types.INTEGER);
		cs.execute();
		
		campoChamada.setIdChamada(idChamada);
		campoChamada.setId(cs.getInt(7));
		c.close();
	}

	/**
	 * Remove todos os campos de uma chamada
	 */
	private void removeCampos(Chamada chamada) throws SQLException
	{
		for(CampoChamada campoChamada: chamada.pegaCamposChamada())
			removeCampo(campoChamada);
	}
	
	/**
	 * Remove um campo de uma chamada
	 */
	private void removeCampo(CampoChamada campoChamada) throws SQLException
	{
		Connection c = getConnection();
		CallableStatement cs = c.prepareCall("{call CampoChamadaRemove(?)}");
		cs.setInt(1, campoChamada.getId());
		cs.execute();
		c.close();
	}
	
	/**
	 * Atualiza um campo de uma chamada
	 */
	public boolean atualizaCampoChamada(CampoChamada campoChamada)
	{
		Connection c = getConnection();
		
		if (c == null)
			return false;
		
		try
		{
			CallableStatement cs = c.prepareCall("{call CampoChamadaAtualiza(?, ?, ?, ?, ?, ?)}");
			cs.setInt(1, campoChamada.getId());
			cs.setString(2, campoChamada.getTitulo());
			cs.setInt(3, campoChamada.getTipo());
			cs.setInt(4, campoChamada.getDecimais());
			cs.setInt(5, campoChamada.isOpcional() ? 1 : 0);
			cs.setString(6, new Gson().toJson(campoChamada.pegaOpcoes()));
			cs.execute();

			c.close();
			return true;

		} catch (SQLException e)
		{
			log("ChamadaDAO.atualiza: " + e.getMessage());
			return false;
		}
	}
}