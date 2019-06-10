package proyecto;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.Color;
import libr.Libreria;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.event.ActionEvent;
import javax.swing.JTextArea;

public class AppBoe {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppBoe window = new AppBoe();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public AppBoe() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 737, 440);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Boletines");
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Fecha del BOE");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNewLabel.setBounds(33, 29, 145, 19);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNmeroDelBoletn = new JLabel("N\u00FAmero del Bolet\u00EDn");
		lblNmeroDelBoletn.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNmeroDelBoletn.setBounds(33, 89, 145, 19);
		frame.getContentPane().add(lblNmeroDelBoletn);
		
		textField = new JTextField();
		textField.setBounds(216, 23, 145, 27);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBackground(Color.LIGHT_GRAY);
		textField_1.setBounds(216, 81, 145, 27);
		frame.getContentPane().add(textField_1);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(118, 260, 472, 58);
		frame.getContentPane().add(textArea);
		
		JLabel lblDocumentoPdf = new JLabel("Documento PDF: ");
		lblDocumentoPdf.setBounds(108, 222, 110, 27);
		frame.getContentPane().add(lblDocumentoPdf);
		
		JButton btnNewButton = new JButton("Descargar");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String fecha = textField.getText().trim();
				if(Libreria.esFecha(fecha)) {
					try {
						fecha = Libreria.unificarFechas(fecha);
					} catch (Exception e1) {
						fecha = "";
						e1.printStackTrace();
					}
					if (!(fecha == "")) {
						Libreria.CrearDirectorio(fecha);
		
						String fecha1 = fecha.replace("/", "-");
						textArea.setText("C:/boletines/BOE.ES/" + fecha1 + "/pdf");
					
						String url = "https://www.boe.es/boe/dias/" + fecha + "/";     
						String boeHtml = Libreria.getHTML(url);
						saveHTML(boeHtml,"C:/boletines/BOE.ES/"+ fecha1 + "/" + fecha1 + " BOE.html"); 
					
						List<String> links = Libreria.getLink(url);  
						if (links.size() >= 1) {
							String numeroBoe = links.get(0);  											
							String[] nb1 = numeroBoe.split("=");
							String[] nb2 = nb1[1].split("&");
							String nbfinal = nb2[0];
							textField_1.setText(nbfinal);
					
							String urlNoti = "https://www.boe.es/boe_n/dias/" + fecha + "/index.php?d=" + nbfinal +"&s=N";
							List <String> htmlNoti = Libreria.getLinks(urlNoti);
							htmlNoti = htmlNoti.stream().map(x -> {
								x = "https://www.boe.es/" + x;
								x = Libreria.mayusculasPdf(x);
								return x;
							}).collect(Collectors.toList());
							String rutaPDF = "c:/boletines/BOE.ES/" + fecha1 + "/pdf/";
							Libreria.guardarPdfs(htmlNoti, rutaPDF);
					
							JOptionPane.showMessageDialog(null, "Su solicitud ha sido realizada");
						} else JOptionPane.showMessageDialog(null, "No ha introducido una fecha válida. Intentelo de nuevo");
					} else JOptionPane.showMessageDialog(null, "No ha introducido una fecha válida.");
				}
				
			}
		});
		btnNewButton.setToolTipText("");
		btnNewButton.setBackground(Color.WHITE);
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnNewButton.setForeground(new Color(165, 42, 42));
		btnNewButton.setBounds(470, 29, 137, 27);
		frame.getContentPane().add(btnNewButton);
		
		
	}
	
	
	void saveHTML(String html, String archivoDestino) {
		try {
			PrintWriter salida = new PrintWriter(new FileWriter(archivoDestino));
			salida.write(html);
			salida.close();
		} catch (IOException ioe) {
			
		}
	}
}

