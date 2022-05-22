package servidor;


import java.awt.event.ActionEvent;
import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ChatServer extends Application {

	public static Label logs = new Label("[Interfície servidor]");
	private Button enviar = new Button("Encriptar Xat");
	private Button desencriptar = new Button("Desencriptar Xat");
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage servidorStage) throws Exception {
		//crear fil pel server 
		Thread startServer = new Thread(new IniciarChat());
		startServer.start();
		enviar.setLayoutX(50);
		enviar.setLayoutY(500);
		enviar.setOnAction(value -> {
			
			try {
				Usuaris.guardarfile(STYLESHEET_MODENA, STYLESHEET_CASPIAN);
			} catch (IOException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			}
			System.out.println("Encriptació correcta");
			
		});
		
		desencriptar.setLayoutX(200);
		desencriptar.setLayoutY(500);
		desencriptar.setOnAction(value -> {
			
			try {
				Usuaris.desencriptarfitxer();
			} catch (IOException e) {
				// TODO Bloque catch generado automáticamente
				e.printStackTrace();
			}
			System.out.println("desencriptar correcta");
			
		});
		
		
		
		
		
		
		AnchorPane pr = new AnchorPane();
		pr.setPrefSize(400,600);
		pr.getChildren().addAll(logs,enviar, desencriptar);
		
		
		ScrollPane SP = new ScrollPane();
		SP.setPrefSize(400, 600);
		SP.setContent(logs);
				
		servidorStage.setTitle("Chat Servidor");
		servidorStage.setScene(new Scene(pr));
		servidorStage.show();
		
				
		
	}
	
	void enviar(ActionEvent event) {
		try {
			System.out.println("s'ha executat el petit boto");
			Usuaris.guardarfile(STYLESHEET_MODENA, STYLESHEET_CASPIAN);
		} catch (IOException e) {
			// TODO Bloque catch generado automáticamente
			e.printStackTrace();
		}
		
	}

}
