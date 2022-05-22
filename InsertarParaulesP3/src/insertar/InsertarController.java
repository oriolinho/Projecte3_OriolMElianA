package insertar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

import javax.swing.JOptionPane;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class InsertarController {
	@FXML
    private Button btnInsert;

    @FXML
    private TextField txtCategoria;

    @FXML
    private TextField txtParaula;

    Connection con;
    PreparedStatement st;
    Connection conn5 = conexio.obtenir_connexio_BD();	
	PreparedStatement stmt5 = null;
    
    
    @FXML
    void Add(ActionEvent event) {
    	String paraula = txtParaula.getText();
    	String catgeoria = txtCategoria.getText();
    	
    	try {
    		stmt5 = conn5.prepareStatement("insert into paraules(nom,categoria)values(?,?)");
    		stmt5.setString(1, paraula);
    		stmt5.setString(2, catgeoria);
    		
    		int estat = stmt5.executeUpdate();
    		
    		if(estat==1) {
    			JOptionPane.showMessageDialog(null, "Paraula afegida");
    			txtParaula.setText("");
    			txtCategoria.setText("");
    		}else {
    			JOptionPane.showMessageDialog(null, "Error insert");
    		}
    				
    		
    	}catch (Exception e) {
		}
    	
    
    }
	
}
