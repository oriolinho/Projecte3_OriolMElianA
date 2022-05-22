
package Lectura;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class LlegirXML {
	public static void main(String[] args) throws ParserConfigurationException, SAXException { 
		try {
			System.out.println("Introduix nom o dni del l'usuari");
			Scanner lector = new Scanner(System.in);
			String text = lector.nextLine();
			String consultasql;

			Connection conn = conexio.obtenir_connexio_BD();

			String vaID = "SELECT dni FROM envia where nom='"+text+"' OR dni = '"+text+"'";




			Statement stmtID = conn.createStatement();
			ResultSet rs = stmtID.executeQuery(vaID);
			//obtenim valor de la base de dades

			rs.next();

			consultasql = rs.getString("dni");

			File file = new File("C:\\Prova\\"+consultasql+".xml" );
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			Document document = db.parse(file);
			if(document == null) {
				System.out.println("No hi ha historial del usuari");
			}else {
				document.getDocumentElement().normalize();
				NodeList nList = document.getElementsByTagName("quiParaules");
				System.out.println("----------------------------");
				System.out.println("/*******************************HISTORIAL DEL USUARI ****************************/");
				System.out.println("");
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						System.out.println("Paraula : " + eElement.getElementsByTagName("Paraula").item(0).getTextContent());
						System.out.println("Tipus de paraula : " + eElement.getElementsByTagName("Categoria").item(0).getTextContent());
						System.out.println("Data i Hora : " + eElement.getElementsByTagName("DataHora").item(0).getTextContent());
						System.out.println("Persona emisora : " + eElement.getElementsByTagName("Envia").item(0).getTextContent());
						System.out.println("Persona receptora : " + eElement.getElementsByTagName("Rep").item(0).getTextContent());
						System.out.println("Dni emisor : " + eElement.getElementsByTagName("Dni").item(0).getTextContent());
					}
				}
				
			}
			
			



		}
		catch(IOException e) {
			System.out.println(e);
		} catch (SQLException e) {
			// TODO Bloque catch generado automáticamente
			System.out.println("Hi han diferents usuaris amb el mateix nom");
		} 
	}

}
