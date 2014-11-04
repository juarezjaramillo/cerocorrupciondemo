package com.rauljuarezjaramillo.cerocorrupcion.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.utils.SystemProperty;
import com.rauljuarezjaramillo.cerocorrupcion.util.Util;

/**
 * Clase ligera para el acceso a Base de Datos CCMX
 * @author Appix Creative
 *
 */
public class BD {
	private static final String AES_KEY = "C3R0";
	/**
	 * La conexion
	 */
	Connection c = null;

	/**
	 * Crea una nueva instancia de esta clase
	 */
	public BD() {

	}

	/**
	 * Abre la conexion con la base de datos
	 */
	public void abrir() {
		String url = null;
		try {
			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
				// Load the class that provides the new "jdbc:google:mysql://" prefix.
				Class.forName("com.mysql.jdbc.GoogleDriver");
				url = "jdbc:google:mysql://cerocorrupcionmx:ccmx/ccmx?user=root";
			} else {
				// Local MySQL instance to use during development.
				Class.forName("com.mysql.jdbc.Driver");
				url = "jdbc:mysql://173.194.85.206:3306/ccmx";

				// Alternatively, connect to a Google Cloud SQL instance using:
				// jdbc:mysql://ip-address-of-google-cloud-sql-instance:3306/guestbook?user=root
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		try {
			Connection conn = null;
			if (SystemProperty.environment.value() == SystemProperty.Environment.Value.Production) {
				conn = DriverManager.getConnection(url);
			} else {
				conn = DriverManager.getConnection(url, "root", "qw1as2zx3");
			}
			c = conn;
		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Cierra la conexion con la bd
	 */
	public void cerrar() {
		try {
			c.close();
		} catch (SQLException ex) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Metodo de utileria para cerrar un Statement, lidiando con excepciones
	 * @param stmt el Statement
	 */
	public void cerrar(Statement stmt) {
		try {
			stmt.close();
		} catch (Exception ex) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Metodo de utileria para cerrar un Prepared Statement, lidiando con excepciones
	 * @param ps el PreparedStatement
	 */
	public void cerrar(PreparedStatement ps) {
		try {
			ps.close();
		} catch (Exception ex) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Metodo de utileria para cerrar un ResultSet, lidiando con excepciones
	 * @param rs el ResultSet
	 */
	public void cerrar(ResultSet rs) {
		try {
			rs.close();
		} catch (Exception ex) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Obtiene la conexion a la base de datos
	 * @return la conexion
	 */
	public Connection getConnection() {
		return c;
	}

	/**
	 * Inserta un registro que representa un archivo
	 * @param idDenuncia 
	 * @param archivo
	 * @param nombre
	 * @param extension
	 * @param mime
	 * @param ubicacion
	 * @param latitud
	 * @param longitud
	 * @param userId
	 * @return
	 */
	public int insertarArchivo(int idDenuncia, String archivo, String nombre, String extension, String mime, String ubicacion, double latitud,
			double longitud, String userId) {
		int id = -1;
		try {
			PreparedStatement stmt = c.prepareStatement(
					"INSERT INTO EVIDENCIA(IDDENUNCIA, ARCHIVO, MIMETYPE, EXTENSION, NOMBRE, UBICACION, LATITUD, LONGITUD, USUARIOCREACION, FECHACREACION) "
							+ " VALUES (?,?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, idDenuncia);
			stmt.setString(2, archivo);
			stmt.setString(3, mime);
			stmt.setString(4, extension);
			stmt.setString(5, nombre);
			stmt.setString(6, ubicacion);
			stmt.setDouble(7, latitud);
			stmt.setDouble(8, longitud);
			stmt.setString(9, userId);
			stmt.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
			stmt.executeUpdate();
			ResultSet resultSet = stmt.getGeneratedKeys();
			while (resultSet.next()) {
				id = resultSet.getInt(1);
			}
			resultSet.close();
			stmt.close();
			return id;
		} catch (SQLException ex) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
		}
		return -1;
	}

	/**
	 * Obtiene los archivos agregados a una denuncia
	 * @param idDenuncia el identificador de la denuncia
	 * @return la lita de archivos (evidencias)
	 */
	public List<Map<String, Object>> getArchivos(int idDenuncia) {
		List<Map<String, Object>> lista = new ArrayList<Map<String, Object>>();
		try {
			Statement stmt = c.createStatement();
			ResultSet resultSet = stmt
					.executeQuery("SELECT IDEVIDENCIA, IDDENUNCIA, ARCHIVO, MIMETYPE, EXTENSION, NOMBRE, UBICACION, LATITUD, LONGITUD, USUARIOCREACION, FECHACREACION FROM EVIDENCIA WHERE IDDENUNCIA="
							+ idDenuncia);
			while (resultSet.next()) {
				Map<String, Object> info = new HashMap<String, Object>();
				info.put("IDEVIDENCIA", Integer.valueOf(resultSet.getInt(1)));
				info.put("IDDENUNCIA", Integer.valueOf(resultSet.getInt(2)));
				info.put("ARCHIVO", resultSet.getString(3));
				info.put("MIMETYPE", resultSet.getString(4));
				info.put("EXTENSION", resultSet.getString(5));
				info.put("NOMBRE", resultSet.getString(6));
				info.put("UBICACION", resultSet.getString(7));
				info.put("LATITUD", Double.valueOf(resultSet.getDouble(8)));
				info.put("LONGITUD", Double.valueOf(resultSet.getDouble(9)));
				info.put("USUARIOCREACION", resultSet.getString(10));
				info.put("FECHACREACION", new Date(resultSet.getTimestamp(11).getTime()));
				lista.add(info);
			}
			resultSet.close();
			stmt.close();
			return lista;
		} catch (SQLException ex) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
		}
		return lista;
	}
	
	/**
	 * Obtiene los archivos agregados a una denuncia
	 * @param idDenuncia el identificador de la denuncia
	 * @return la lita de archivos (evidencias)
	 */
	public Map<String, Object> getArchivo(int idEvidencia) {
		
		try {
			Map<String, Object> info = new HashMap<String, Object>();
			Statement stmt = c.createStatement();
			ResultSet resultSet = stmt
					.executeQuery("SELECT IDEVIDENCIA, IDDENUNCIA, ARCHIVO, MIMETYPE, EXTENSION, NOMBRE, UBICACION, LATITUD, LONGITUD, USUARIOCREACION, FECHACREACION FROM EVIDENCIA WHERE IDEVIDENCIA="
							+ idEvidencia);
			if (resultSet.next()) {
				
				info.put("IDEVIDENCIA", Integer.valueOf(resultSet.getInt(1)));
				info.put("IDDENUNCIA", Integer.valueOf(resultSet.getInt(2)));
				info.put("ARCHIVO", resultSet.getString(3));
				info.put("MIMETYPE", resultSet.getString(4));
				info.put("EXTENSION", resultSet.getString(5));
				info.put("NOMBRE", resultSet.getString(6));
				info.put("UBICACION", resultSet.getString(7));
				info.put("LATITUD", Double.valueOf(resultSet.getDouble(8)));
				info.put("LONGITUD", Double.valueOf(resultSet.getDouble(9)));
				info.put("USUARIOCREACION", resultSet.getString(10));
				info.put("FECHACREACION", new Date(resultSet.getTimestamp(11).getTime()));
			}
			resultSet.close();
			stmt.close();
			return info;
		} catch (SQLException ex) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	public int ultimoIdInsertado() {
		int id = -1;
		try {
			Statement stmt = c.createStatement();
			ResultSet resultSet = stmt.getGeneratedKeys();
			while (resultSet.next()) {
				id = resultSet.getInt(1);
			}
			resultSet.close();
			stmt.close();
		} catch (SQLException ex) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
		}
		return id;
	}

	/**
	 * Inserta un registro de denuncia
	 * @param tipo
	 * @param idEstatus
	 * @param folio
	 * @param idPersona
	 * @param persona
	 * @param hechos
	 * @param ubicacion
	 * @param latitud
	 * @param longitud
	 * @param anonima
	 * @param nombre
	 * @param direccion
	 * @param telefono
	 * @param correo
	 * @param idDenunciaLocal
	 * @param userId
	 * @return el identificador generado para la denuncia
	 */
	public int insertarDenuncia(int tipo, int idEstatus, String folio, int idPersona, String persona, String hechos, String ubicacion,
			double latitud, double longitud, int anonima, String nombre, String direccion, String telefono, String correo, int idDenunciaLocal,
			String userId) {
		int id = -1;
		try {
			PreparedStatement stmt = c
					.prepareStatement(
							"INSERT INTO DENUNCIA(TIPODENUNCIA, IDESTATUS, FECHAESTATUS, FOLIO, IDPERSONA, NOMBREPERSONA, HECHOS, UBICACION, LATITUD, LONGITUD, ANONIMA, NOMBRE, DIRECCION, TELEFONO, CORREO, USUARIOCREACION, FECHACREACION, IDDENUNCIALOCAL) "
									+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, tipo);
			stmt.setInt(2, idEstatus);
			stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			stmt.setString(4, folio);
			stmt.setInt(5, idPersona);
			stmt.setString(6, persona);

			stmt.setString(7, hechos);
			stmt.setString(8, ubicacion);
			stmt.setDouble(9, latitud);
			stmt.setDouble(10, longitud);
			stmt.setInt(11, anonima);
			stmt.setString(12, nombre);
			stmt.setString(13, direccion);
			stmt.setString(14, telefono);
			stmt.setString(15, correo);
			stmt.setString(16, userId);
			stmt.setTimestamp(17, new Timestamp(System.currentTimeMillis()));
			stmt.setInt(18, idDenunciaLocal);
			stmt.executeUpdate();
			ResultSet resultSet = stmt.getGeneratedKeys();
			while (resultSet.next()) {
				id = resultSet.getInt(1);
			}
			resultSet.close();
			stmt.close();
		} catch (SQLException ex) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
		}
		return id;
	}

	/**
	 * Establece el folio de la denuncia 
	 * @param idDenuncia el identificador de la denuncia
	 * @param folio el folio de la denuncia (debiera estar encriptado)
	 * @return true, si todo se ok, false en caso contrario
	 */
	public boolean actualizarFolioDenuncia(int idDenuncia, String folio) {
		boolean b = false;
		try {
			PreparedStatement stmt = c.prepareStatement("UPDATE DENUNCIA SET FOLIO=? WHERE IDDENUNCIA=?");
			stmt.setString(1, folio);
			stmt.setInt(2, idDenuncia);
			stmt.executeUpdate();
			stmt.close();
			b = true;
		} catch (SQLException ex) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
		}
		return b;
	}

	/**
	 * Obtiene la informacion y estatus de una denuncia
	 * @param idDenuncia el identificador de la denuncia
	 * @return mapa con la informacion de la denuncia
	 */
	public Map<String, Object> getDenuncia(int idDenuncia) {
		Map<String, Object> info = new LinkedHashMap<String, Object>();
		try {
			Statement stmt = c.createStatement();
			ResultSet resultSet = stmt
					.executeQuery("SELECT IDDENUNCIA, TIPODENUNCIA, DENUNCIA.IDESTATUS,ESTATUS.DESCRIPCION ESTATUS, FECHAESTATUS, FOLIO, IDPERSONA, NOMBREPERSONA, HECHOS, UBICACION, LATITUD, LONGITUD, ANONIMA, NOMBRE, DIRECCION, TELEFONO, CORREO, USUARIOCREACION, FECHACREACION"
							+ " FROM DENUNCIA" + " LEFT JOIN ESTATUS ON DENUNCIA.IDESTATUS=ESTATUS.IDESTATUS" + " WHERE IDDENUNCIA=" + idDenuncia);
			if (resultSet.next()) {
				System.out.println("denuncia encontrada");
				info.put("IDDENUNCIA", Integer.valueOf(resultSet.getInt(1)));
				info.put("TIPODENUNCIA", Integer.valueOf(resultSet.getInt(2)));
				info.put("IDESTATUS", Integer.valueOf(resultSet.getInt(3)));
				info.put("ESTATUS", resultSet.getString(4));
				info.put("FECHAESTATUS", new Date(resultSet.getTimestamp(5).getTime()));
				info.put("FOLIO", resultSet.getString(6));
				info.put("IDPERSONA", Integer.valueOf(resultSet.getInt(7)));
				info.put("PERSONA", resultSet.getString(8));
				info.put("HECHOS", resultSet.getString(9));
				info.put("UBICACION", resultSet.getString(10));
				info.put("LATITUD", Double.valueOf(resultSet.getDouble(11)));
				info.put("LONGITUD", Double.valueOf(resultSet.getDouble(12)));
				info.put("ANONIMA", Integer.valueOf(resultSet.getInt(13)));
				info.put("NOMBRE", resultSet.getString(14));
				info.put("DIRECCION", resultSet.getString(15));
				info.put("TELEFONO", resultSet.getString(16));
				info.put("CORREO", resultSet.getString(17));
				info.put("USUARIOCREACION", resultSet.getString(18));
				info.put("FECHACREACION", new Date(resultSet.getTimestamp(19).getTime()));
			} else {
				System.out.println("denuncia no encontrado");
			}
			resultSet.close();
			stmt.close();
			return info;
		} catch (SQLException ex) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
		}
		return info;
	}

	/**
	 * Inserta un registro de sugerencia
	 * @param descripcion
	 * @param userId
	 * @return el identificador generado para la sugerencia
	 */
	public int insertarSugerencia(String descripcion, String userId) {
		int id = -1;
		try {
			PreparedStatement stmt = c.prepareStatement("INSERT INTO SUGERENCIAS(COMENTARIOS,USUARIOCREACION, FECHACREACION) " + " VALUES(?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setString(1, descripcion);
			stmt.setString(2, userId);
			stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			stmt.executeUpdate();
			ResultSet resultSet = stmt.getGeneratedKeys();
			while (resultSet.next()) {
				id = resultSet.getInt(1);
			}
			resultSet.close();
			stmt.close();
		} catch (SQLException ex) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
		}
		return id;
	}

	/**
	 * Guarda la configuracion del usuario. Si la configuracion no existe la inserta, y en caso de existir solo la actualiza
	 * @param userId el usuario (id del dispositivo)
	 * @param geo si debe utilizarse la geolocalizacion
	 * @param push si deben enviarsele notificaciones push
	 * @return true, si todo ok, false en caso contrario
	 */
	public boolean insertarOActualizarConfiguracion(String userId, int geo, int push) {
		boolean b=false;
		int cont = 0;
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
		try {
			stmt = c.prepareStatement("SELECT COUNT(*) FROM CONFIG WHERE USUARIOCREACION=?");
			stmt.setString(1, userId);
			resultSet = stmt.executeQuery();
			if (resultSet.next()) {
				cont = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			cerrar(resultSet);
			cerrar(stmt);
		}

		if (cont == 0) {
			try {
				stmt = c.prepareStatement("INSERT INTO CONFIG(USUARIOCREACION, USARGEO, NOTIFICACIONESPUSH) VALUES(?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS);
				stmt.setString(1, userId);
				stmt.setInt(2, geo);
				stmt.setInt(3, push);
				stmt.executeUpdate();
				resultSet = stmt.getGeneratedKeys();
				b=true;
			} catch (SQLException ex) {
				Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				cerrar(resultSet);
				cerrar(stmt);
			}
		} else {
			try {
				stmt = c.prepareStatement("UPDATE CONFIG SET USARGEO=?, NOTIFICACIONESPUSH=? WHERE USUARIOCREACION=?");

				stmt.setInt(1, geo);
				stmt.setInt(2, push);
				stmt.setString(3, userId);
				stmt.executeUpdate();
				b=true;
			} catch (SQLException ex) {
				Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				cerrar(stmt);
			}
		}
		return b;
	}
	
	/**
	 * De acuerdo con 'q' se buscan personas con nombre coincidente
	 * @param q parte del nombre a buscar
	 * @return lista de personas coincidentes
	 */
	public List<Map<String,String>> buscarPersonas(String q){
		
		StringBuilder sb=new StringBuilder();
		String split[]=q.split(" ");
		for(int i =0;i<split.length;i++){
			if(i>0){
				sb.append(" ");
			}
			sb.append(split[i]).append("%");
		}
		System.out.println("sb="+sb);
		PreparedStatement stmt=null;
		ResultSet rs=null;
		List<Map<String,String>> lista=new ArrayList<Map<String,String>>();
		try {
			stmt = c.prepareStatement("select CONCAT_WS(' ',NOMBRE,APATERNO,AMATERNO) NOMBRE,UNIDADADMINISTRATIVA AREA,CARGO SUBAREA from PERSONASPOT where NOMBRECOMPLETO like ? limit 10");
			stmt.setString(1, sb.toString());
			rs=stmt.executeQuery();
			while(rs.next()){
				Map<String,String> resultado=new HashMap<String, String>();
				resultado.put("nombre", rs.getString(1));
				resultado.put("area", rs.getString(2));
				resultado.put("subarea", rs.getString(3));
				lista.add(resultado);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			cerrar(rs);
			cerrar(stmt);
		}
		
		return lista;
	}
	
	/**
	 * Obtiene el registro de configuracion de usuario (usar geolocalizacion, notificaciones push)
	 * @param userId el usuario
	 * @return la configuracion del usuario en caso de existir, la configuracion por defecto en caso contrario
	 */
	public Map<String,Object> getConfiguracionDeUsuario(String userId) {
		Map<String,Object> mapa=new HashMap<String,Object>();
		PreparedStatement stmt = null;
		ResultSet resultSet = null;
					try {
				stmt = c.prepareStatement("SELECT USARGEO GEO, NOTIFICACIONESPUSH PUSH FROM CONFIG WHERE USUARIOCREACION=?");
				stmt.setString(1, userId);
				resultSet = stmt.executeQuery();
				if(resultSet.next()){
					mapa.put("GEO", resultSet.getInt(1));
					mapa.put("PUSH", resultSet.getInt(2));
				}
			} catch (SQLException ex) {
				Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
			} finally {
				cerrar(resultSet);
				cerrar(stmt);
			}
		if(!mapa.containsKey("GEO")){//Si no se encontro en BD se regresa el default
			mapa.put("GEO", 1);
			mapa.put("PUSH", 1);
		}
		return mapa;
	}
	
	/************************************************************************************************************************************************************/
	public int validarLogin(String usuario,String contrasena){
		int idUsuario = 0;
		try {
			PreparedStatement stmt = c.prepareStatement(
					"SELECT IDUSUARIO FROM USUARIO WHERE USUARIO=? AND AES_DECRYPT(PASSWORD, ?) = ?", Statement.KEEP_CURRENT_RESULT);
			stmt.setString(1, usuario);
			stmt.setString(2, AES_KEY);
			stmt.setString(3, contrasena);
			
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				idUsuario = resultSet.getInt("IDUSUARIO");
			}
			resultSet.close();
			stmt.close();
			return idUsuario;
		} catch (Exception e) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, e);
		}
		
		return idUsuario;
	}

	public List obtenerDenuncias(int idEstatus){
		List<Map<String, Object>> denuncias = new ArrayList<Map<String, Object>>();
		
		String sql = " SELECT "  +
				" 	DEN.IDDENUNCIA,"  +
				" 	CASE DEN.TIPODENUNCIA"  +
				" 		WHEN 1 THEN 'QUEJA' "  +
				" 		WHEN 2 THEN 'DENUNCIA'"  +
				" 	END TIPO,"  +
				" 	STS.DESCRIPCION ESTATUS,"  +
				" 	DEN.FECHAESTATUS,"  +
				" 	DEN.FOLIO,"  +
				" 	DEN.NOMBREPERSONA,"  +
				" 	DEN.HECHOS,"  +
				" 	DEN.UBICACION,"  +
				" 	DEN.LATITUD,"  +
				" 	DEN.LONGITUD,"  +
				" 	CASE DEN.ANONIMA"  +
				" 		WHEN 1 THEN 'Si'"  +
				" 		WHEN 2 THEN 'No'"  +
				" 	END ANONIMA"  +
				" FROM "  +
				" 	DENUNCIA DEN"  +
				" LEFT JOIN"  +
				" 	ESTATUS STS ON DEN.IDESTATUS = STS.IDESTATUS"  +
				" WHERE"  +
				" 	DEN.IDESTATUS = ?"  +
				" ORDER BY"  +
				" 	FECHACREACION DESC" ;
		
		try {
			PreparedStatement stmt = c.prepareStatement(
					sql, Statement.KEEP_CURRENT_RESULT);
			stmt.setInt(1, idEstatus);
			
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				Map<String, Object> den = new HashMap<String, Object>();
				den.put("IDDENUNCIA", resultSet.getInt(1));
				den.put("TIPO", resultSet.getString(2));
				den.put("ESTATUS", resultSet.getString(3));
				den.put("FECHAESTATUS", resultSet.getDate(4));
				den.put("FOLIO", resultSet.getString(5));
				den.put("NOMBREPERSONA", resultSet.getString(6));
				den.put("HECHOS", resultSet.getString(7));
				den.put("UBICACION", resultSet.getString(8));
				den.put("LATITUD", resultSet.getBigDecimal(9));
				den.put("LONGITUD", resultSet.getBigDecimal(10));
				den.put("ANONIMA", resultSet.getString(11));
				
				denuncias.add(den);
			}
			resultSet.close();
			stmt.close();
			return denuncias;
		} catch (Exception e) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, e);
		}
		return null;
	}
	
	public int guardarSeguimiento(String idDenuncia,String idEstatus, String observaciones, String userId) {
		int id = -1;
		try {
			PreparedStatement stmt = c.prepareStatement("INSERT INTO DENUNCIASEGUIMIENTO(IDDENUNCIA,IDESTATUS, FECHAESTATUS, OBSERVACIONES, USUARIOCREACION, FECHACREACION) " + " VALUES(?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, Util.intOCero(idDenuncia));
			stmt.setInt(2, Util.intOCero(idEstatus));
			stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			stmt.setString(4,observaciones);
			stmt.setString(5, userId);
			stmt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			stmt.executeUpdate();
			ResultSet resultSet = stmt.getGeneratedKeys();
			while (resultSet.next()) {
				id = resultSet.getInt(1);
			}
			resultSet.close();
			stmt.close();
		} catch (SQLException ex) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
		}
		return id;
	}
	
	public boolean actualizarEstatusDenuncia(int idDenuncia, int idEstatus, Date fechaEstatus) {
		boolean b = false;
		try {
			PreparedStatement stmt = c.prepareStatement("UPDATE DENUNCIA SET IDESTATUS=?, FECHAESTATUS=? WHERE IDDENUNCIA=?");
			stmt.setInt(1, idEstatus);
			stmt.setTimestamp(2, new Timestamp(fechaEstatus.getTime()));
			stmt.setInt(3, idDenuncia);
			stmt.executeUpdate();
			stmt.close();
			b = true;
		} catch (SQLException ex) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, ex);
		}
		return b;
	}
	
	public boolean notificacionesActivas(String idDispositivo){
		boolean n = true;

		try {
			PreparedStatement stmt = c.prepareStatement(
					"SELECT NOTIFICACIONESPUSH FROM CONFIG WHERE USUARIOCREACION=?", Statement.KEEP_CURRENT_RESULT);
			stmt.setString(1, idDispositivo);
			
			ResultSet resultSet = stmt.executeQuery();
			while (resultSet.next()) {
				n = resultSet.getInt("NOTIFICACIONESPUSH") == 1;
			}
			resultSet.close();
			stmt.close();
		} catch (Exception e) {
			Logger.getLogger(BD.class.getName()).log(Level.SEVERE, null, e);
		}
		
		return n;
	}
	
	/************************************************************************************************************************************************************/
	
	
}
