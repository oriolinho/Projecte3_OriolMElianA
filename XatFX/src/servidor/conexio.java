package servidor;

import java.sql.Connection;
import java.sql.DriverManager;

public class conexio {
	public static Connection obtenir_connexio_BD() {

		Connection conn = null;
		String servidor = "jdbc:postgresql://192.168.2.31:5432/";
		String bbdd = "projecte3";
		String user = "oriadix";
		String password = "Fat/3232";

		try{
			Class.forName("org.postgresql.Driver");
			conn= DriverManager.getConnection(servidor + bbdd, user, password);
		} catch (Exception e){
			e.printStackTrace();
			return conn;
		}
			return conn;
		}
	
	
	
	
}
