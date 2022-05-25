package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import javax.lang.model.element.Element;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bson.Document;
import org.bson.Transformer;
import org.bson.types.ObjectId;
import org.w3c.dom.Attr;

import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import servidor.conexio;

public class Client extends Application {

	public static Label logs = new Label("[Xat]");
	private TextField ficaNom = new TextField();
	public static TextField entraMissatge = new TextField();
	private Scene abans, xat;
	private Button enviar = new Button("Genera Xat");
    boolean x;
    boolean esValid;
    String categoriaULT;
    String dniULT;
    int idparaulaFi;
    int idCliFi;


	public static String nomCli = "defualt nomCli";

	@Override
	public void start(Stage pantallaClient) throws Exception {

		// abans: enter nomCli first
		GridPane panel1 = new GridPane();
		panel1.setPrefSize(400, 200);
		panel1.setPadding(new Insets(0, 20, 20, 20)); 
		panel1.setVgap(15); 
		panel1.setHgap(5); 
		panel1.setAlignment(Pos.CENTER);
		panel1.setStyle("-fx-base: #CEECEE;");
		panel1.add(new Label("Introdueix el DNI: "), 0, 0);
		panel1.add(ficaNom, 0, 1);
		panel1.add(enviar, 1, 1);

		abans = new Scene(panel1);
	
		pantallaClient.setScene(abans);
		pantallaClient.setTitle("Sala");
		pantallaClient.show();

		ConectarServer connectServer = new ConectarServer();

		// abans -- enviar Button
		enviar.setOnAction(e -> {
			// getText
			nomCli = ficaNom.getText();

			Connection conn = conexio.obtenir_connexio_BD();
			Statement stmt = null;
			try {
				stmt = conn.createStatement();
		        String vaID = "SELECT nom FROM envia where dni='"+nomCli+"' ";
				ResultSet rs = stmt.executeQuery(vaID);
				
			while(rs.next()) {
				String nom = rs.getString("nom");
				System.out.println("Validacio correcta "+nomCli+" "+nom);
				nomCli = nom;
				esValid = true;
				
			}
			
			if (esValid != true) {
				System.out.println("dni invàlid , obra nova pestanya");
				System.exit(0);
			}
			
			
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			
			Thread filConexio = new Thread(connectServer);
			filConexio.start();

			
			ScrollPane layout = new ScrollPane();
			layout.setPrefSize(400, 600);
			layout.setStyle("-fx-base: #CEECEE;");
			layout.setContent(logs);

			BorderPane root2 = new BorderPane();
			root2.setPrefSize(350, 400);
			root2.setCenter(layout);
			root2.setBottom(entraMissatge);

			xat = new Scene(root2);
			pantallaClient.setScene(xat);
		});

		// xat -- entraMissatge
		Client.entraMissatge.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				DataOutputStream out = connectServer.getDataOuputStream();
				
				String msg = Client.entraMissatge.getText();
				
				String palabra = "";
				String prParaula = "";
				
				char l = 't';
				
				//creem l'array list per guardar la paraula 
				ArrayList<String> msgnou = new ArrayList <String> ();
				
				//creem el array de missatge i fiquem el msg.split
				String [] frase = msg.split(" ");
				
				//recorrem el array
				for(String Pigual : frase) {
					//cridem la conexio
					Connection conn = conexio.obtenir_connexio_BD();
			        String vaID = "SELECT nom FROM paraules where nom='"+Pigual+"' ";
					try {
						System.out.println("try");
						Statement stmtID = conn.createStatement();
						ResultSet rs = stmtID.executeQuery(vaID);
						//obtenim valor de la base de dades
						
						rs.next();
						
						palabra = Pigual;
						prParaula = palabra; //guardar paraula

						if(rs.getRow()==1) {
							palabra = "";
							x = true;
							for(int i=0; i<Pigual.length();i++) {
								l = Pigual.charAt(i);
								if(i>0) {
									l = '*';
								}
								palabra += l;
							}
						}
						palabra = palabra +" ";
						msgnou.add(palabra);
						palabra = "";		
				
				        
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}			        
				}
				//passem el arraylist a array string
				String[] missatge = new String [msgnou.size()];
				for(int i = 0; i<msgnou.size(); i++) {
					missatge[i] = msgnou.get(i);
				}
				
				//passem el array de string a cadena 
				
				String fr = "";
				for(int j=0;j<missatge.length;j++) {
					fr += missatge[j];
				}
				
				
				try {
					//envia el missatge per a que es vegui reflexat a la pantalla de client i servidors
				   if(x==true) {
					 System.out.println("ha dit paraulota"); 	
					 String mongoAdr = "mongodb://192.168.3.1:27017";
					
					 try(MongoClient mongoC = MongoClients.create(mongoAdr)){
						 MongoDatabase bd = mongoC.getDatabase("projecte3");
						try {
							//mongo insertar frase paraulota p***
						    LocalDateTime dataHora = LocalDateTime.now();
							Document docMongo = new Document();
							docMongo.append("_id", new ObjectId());
							docMongo.append("frase", fr);
							docMongo.append("usuari", Client.nomCli);
							docMongo.append("dataHora", dataHora);
							bd.getCollection("paraules").insertOne(docMongo);
							
							//mongo insertar sol paraulota
							Document paraulota = new Document();
							paraulota.append("_id", new ObjectId());
							paraulota.append("paraula", prParaula);
							paraulota.append("dataHora", dataHora);
							paraulota.append("qui", Client.nomCli);

							bd.getCollection("solParaula").insertOne(paraulota);
							
							System.out.println("insert a mongo fet");
						}catch (MongoException e) {
							System.err.println(e);
						}
					 }	
				
			
					 
				//DOM
					 try {
				         DocumentBuilderFactory dbFactory =
				         DocumentBuilderFactory.newInstance();
				         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				         org.w3c.dom.Document doc = dBuilder.newDocument();
				         //agafar dni per nom client
				         Connection conn3 = conexio.obtenir_connexio_BD();
							Statement stmt3 = null;
							try {
								stmt3 = conn3.createStatement();
						        String dniSQL = "SELECT dni FROM envia where nom ='"+nomCli+"' ";
								ResultSet rs = stmt3.executeQuery(dniSQL);
								
							while(rs.next()) {
								String dni = rs.getString("dni");
								System.out.println("dni select" + dni);
								dniULT = dni;
							}
							}catch (SQLException e) {
								System.out.println(e);
							}
				         
				         
				         //
				         // root element
				         org.w3c.dom.Element rootElement = doc.createElement("paraules");
				         doc.appendChild(rootElement);
				         
				         org.w3c.dom.Element quiParaules = doc.createElement("quiParaules");
				         rootElement.appendChild(quiParaules);
				         
				         //paraula, categoria parauala, dataHora, nomEnvia, nomRep, dni
				         //agafar tipus paraula
				     	Connection conn2 = conexio.obtenir_connexio_BD();
						Statement stmt2 = null;
						try {
							stmt2 = conn2.createStatement();
					        String categoriaSQL = "SELECT categoria FROM paraules where nom='"+prParaula+"' ";
							ResultSet rs = stmt2.executeQuery(categoriaSQL);
							
						while(rs.next()) {
							String categoriaFi = rs.getString("categoria");
							categoriaULT = categoriaFi;
						}
						}catch (Exception e) {
							System.out.println(e);
						}
				         
				         
				         
				         
				         //
				         org.w3c.dom.Element paraula = doc.createElement("Paraula");
				         paraula.appendChild(doc.createTextNode(prParaula));
				         quiParaules.appendChild(paraula);
				         
				         org.w3c.dom.Element categoria = doc.createElement("Categoria");
				         categoria.appendChild(doc.createTextNode(categoriaULT));
				         quiParaules.appendChild(categoria);
				         
				         
				         DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
						 LocalDateTime dataHora = LocalDateTime.now();
						 String dataHoraString = dataHora.format(format);
						 
				         org.w3c.dom.Element dataEnvio = doc.createElement("DataHora");
				         dataEnvio.appendChild(doc.createTextNode(dataHoraString));
				         quiParaules.appendChild(dataEnvio);
				         
				         org.w3c.dom.Element quiEnvia = doc.createElement("Envia");
				         quiEnvia.appendChild(doc.createTextNode(Client.nomCli));
				         quiParaules.appendChild(quiEnvia);
				         
				         org.w3c.dom.Element rep = doc.createElement("Rep");
				         rep.appendChild(doc.createTextNode("destinatari x"));
				         quiParaules.appendChild(rep);
				         
				         org.w3c.dom.Element dni = doc.createElement("Dni");
				         dni.appendChild(doc.createTextNode(dniULT));
				         quiParaules.appendChild(dni);
				         
				         TransformerFactory transformerFactory = TransformerFactory.newInstance();
				         javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
				         DOMSource source = new DOMSource(doc);
				         StreamResult result = new StreamResult(new File("C:\\Prova\\"+dniULT+".xml"));
				         transformer.transform(source, result);
				         
				         StreamResult consoleResult = new StreamResult(System.out);
				         transformer.transform(source, consoleResult);	         
				         //
				         //insert a postgress
				         Connection conn4 = conexio.obtenir_connexio_BD();
							Statement stmt4 = null;
							//agafar id paraules
							try {
								stmt4 = conn4.createStatement();
						        String dniSQL = "SELECT id_paraula FROM paraules where nom ='"+prParaula+"' ";
								ResultSet rs = stmt4.executeQuery(dniSQL);
								
							while(rs.next()) {
								int idParaula  = rs.getInt("id_paraula");
								idparaulaFi = idParaula;
							}
							}catch (SQLException e) {
								System.out.println(e);
							}
							//agafar id envia
							try {
								stmt4 = conn4.createStatement();
						        String dniSQL = "SELECT id_envia FROM envia where nom ='"+Client.nomCli+"' ";
								ResultSet rs = stmt4.executeQuery(dniSQL);
								
							while(rs.next()) {
								int id_envia = rs.getInt("id_envia");
								idCliFi = id_envia;
							}
							}catch (SQLException e) {
								System.out.println(e);
							}
							
				//insert final id_envia, id_paraula i datahora per poder relacionar el qui envia la paraula amb la paraula
							Connection conn5 = conexio.obtenir_connexio_BD();	
							Statement stmt5 = null;
							try {
								 stmt5 = conn5.createStatement();
						         String sql = "INSERT INTO diu (id_paraula,id_envia,horadata) "
						               + "VALUES ("+idparaulaFi+","+idCliFi+",'"+dataHoraString+"')";
					        // stmt5.executeQuery(sql);
					         stmt5.executeUpdate(sql);
							}catch (SQLException e) {
								System.out.println(e);
							}
							
							
							
				         
				         //
				         
				         
				      } catch (Exception e) {
				         e.printStackTrace();
				      }
		 
				//	 
					 out.writeUTF(fr);	//envia missatge amb parual malsonant p****
				   }else {
					  System.out.println("no ha dit paraulota"); 	
					 out.writeUTF(msg); // envia missatge normals sense malsonant
				   }
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				entraMissatge.setText("");
			}
		});
	}


	public static void main(String[] args) {
		launch(args);
	}
}

class ConectarServer implements Runnable {
//	Socket socket = null;
	SSLSocket socket = null;

	DataInputStream in = null; 
	DataOutputStream out = null; 

	public DataOutputStream getDataOuputStream() {
		return out;
	}

	@Override
	public void run() {

		try {
			
			System.setProperty("javax.net.ssl.trustStore", "serverKey.jks"); //client seria igual que el del server , al ficar el key de client peta. correcte
			System.setProperty("javax.net.ssl.trustStorePassword", "servpass");
			SSLSocketFactory cF = (SSLSocketFactory) SSLSocketFactory.getDefault();
			//socket = (SSLSocket) new Socket("127.0.0.1", 8080);
			socket = (SSLSocket) cF.createSocket("127.0.0.1", 8080); //es crea el socket amb el SSLSocketFactory 

			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());

			out.writeUTF(Client.nomCli); // envia nom client al server per poder visualitzar
			System.out.println(Client.nomCli + " s'ha connectat al Xat ");

		} catch (IOException e) {
		}

		try {
			//segeuix llegint dades del servidor
			while (true) {
				String str = in.readUTF();

				Platform.runLater(() -> {
					Client.logs.setText(Client.logs.getText() + "\n" + str);
				});
			}
		} catch (IOException e) {
		}

	}
	
	
}
