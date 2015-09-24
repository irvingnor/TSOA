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
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import sistemaDistribuido.visual.clienteServidor.MicroNucleoFrame;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Framenombreserver extends JFrame {
	public int idarguardar;
	public String ip;
	public static JButton registrar;
	
	public int getIdarguardar() {
		return idarguardar;
	}

	public void setIdarguardar(int idarguardar) {
		this.idarguardar = idarguardar;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	private JPanel contentPane;
	private JTextField nombre;

		public Framenombreserver() {
			setResizable(false);
			setTitle("Nombre del servidor");
		setBounds(100, 100, 450, 84);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		JLabel lblEscribaElNombre = new JLabel("Escriba el nombre del nuevo servidor");
		panel.add(lblEscribaElNombre);
		
		nombre = new JTextField();
		panel.add(nombre);
		nombre.setColumns(10);
		
		final JButton registrar = new JButton("Registrar");
		registrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			String nom = nombre.getText();
			NameServer.escribe("\nRegistrando servidor datos: "+nom+"|"+idarguardar+"|"+ip);
			datosServidor ds = new datosServidor();
			ds.setIdprocess(idarguardar);
			ds.setIp(ip);
			ds.setName(nom);
			NameServer.registrar(ds, idarguardar);
			registrar.setEnabled(false);
			}
		});
		panel.add(registrar);
		
		this.registrar = registrar;
	}

}
