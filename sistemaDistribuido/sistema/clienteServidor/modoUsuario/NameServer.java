/*
	Alumna:  Guerrero Muñoz Silvia Veronica Guadalupe
	Sección: 03
	practica 5
*/


package sistemaDistribuido.sistema.clienteServidor.modoUsuario;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import java.awt.GridLayout;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JLabel;

/*se ha creado la clase datosServidor para guardar los datos del servidor creado*/
@SuppressWarnings("unused")
public class NameServer extends JFrame {
	static Hashtable<Integer, datosServidor> servers  = new Hashtable<Integer, datosServidor>();

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public static JTextArea visorsucesos;
	public static JTextArea listaservers;
		public NameServer() {
		setTitle("Name Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(3, 1, 0, 0));
		
		JTextArea listaservers = new JTextArea();
		listaservers.setEditable(false);
		JScrollPane scrollPane2 = new JScrollPane(listaservers);
		contentPane.add(scrollPane2, BorderLayout.CENTER);
		
		JTextArea visorsucesos = new JTextArea();
		visorsucesos.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(visorsucesos);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(new GridLayout(2, 1, 0, 0));
		
		JLabel lblTextArea = new JLabel("Text Area 1: Lista de Servidores en Linea");
		panel.add(lblTextArea);
		
		JLabel lblTextArea_1 = new JLabel("Text Area 2: Vision de sucesos");
		panel.add(lblTextArea_1);
		
		this.visorsucesos = visorsucesos;
		this.listaservers = listaservers;
	}

		public static void escribe(String s){
		visorsucesos.append(s);	
		}
		
		public static void escribeservers(String s){
			listaservers.append(s);	
			}
		
		public void registrarserver(){
			
		}

		public static void registrar(datosServidor ds, int idarguardar) {
			boolean seguir = buscar(ds.getName());
			if (seguir) {
			servers.put(idarguardar, ds);
			escribe("\nSe ha registrado servidor con nombre "+ds.getName());
			muestraservers();
			} else{
			escribe("\nERROR: nombre de servidor duplicado, " +
					"\nse ha modificado el nombre para ser registrado...");
			 int completar = (int)(Math.random()*10);
			String name = ds.getName()+String.valueOf(completar);
			 ds.setName(name);
			 servers.put(idarguardar, ds);
			 muestraservers();
			 
		}
		}
		
		public static boolean buscar(String name) {
			Enumeration elements = servers.elements();
			while(elements.hasMoreElements()){
				datosServidor ds = (datosServidor)elements.nextElement();
				escribe(ds.getName());
				if (ds.getName().equals(name)){
				return false;
				}
			}
			return true;
		}

		public static void muestraservers(){
			listaservers.setText("");
			Enumeration elementos=servers.elements();
			Enumeration llaves = servers.keys();
			datosServidor datos;
			escribeservers("LISTA DE SERVIDORES REGISTRADOS:\n");
			while(elementos.hasMoreElements() && llaves.hasMoreElements()){
				datos = (datosServidor) elementos.nextElement();
				escribeservers("Id: "+llaves.nextElement());
				escribeservers(", Nombre: "+datos.getName());
				escribeservers(", IP: "+datos.getIp()+"\n");
			}
		}
		
		public static void deregistro(int id) {
			try {
				escribe("\nSe ha solicitado deregistro..");
			  	servers.remove(id);
			  	muestraservers();
			} catch (NullPointerException e) {
				escribe("\nEste proceso era un cliente..");
			}
		}

		public static boolean buscarparaenviar(String nombre) {
			escribe("\nProceso intenta buscar servidor... ");
			
			Enumeration elements = servers.elements();
			while(elements.hasMoreElements()){
				datosServidor ds = (datosServidor)elements.nextElement();
				escribe(ds.getName());
				if (ds.getName().equals(nombre)){
					escribe("\nEl servidor SI  existe");
					ProcesoCliente.datosServidor(ds);
					int id = ds.getIdprocess();
					escribe("\nEnviando respuesta... ");
				return true;
				
				}
			}
			escribe("\nEl servidor NO existe");
			escribe("\nEnviando respuesta... ");
			return false;
			
		}
}

