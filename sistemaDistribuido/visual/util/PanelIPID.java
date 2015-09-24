/*
	Alumna:  Guerrero Muñoz Silvia Veronica Guadalupe
	Sección: 03
	practica 5
*/

package sistemaDistribuido.visual.util;

import java.awt.Panel;
import java.awt.Label;
import java.awt.TextField;

public class PanelIPID extends Panel{
  private static final long serialVersionUID=1;
  private TextField campoIP;
private static TextField campoID;

  public PanelIPID(){
    campoIP=new TextField(10);
    campoID=new TextField(10);
    add(new Label("Maquina Destino:"));
    add(campoIP);
    add(new Label("Nombre del servidor:"));
    add(campoID);
  }
  
  public String dameIP(){
    return campoIP.getText();
  }
  
  public String dameID(){
    return campoID.getText();
  }

public static String damenombre() {
	return campoID.getText();
}
}