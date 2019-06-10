package libr;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import javax.swing.JOptionPane;



public class Libreria {

	public static void CrearDirectorio (String fecha) {		
		String fecha1 = fecha.replace("/", "-");
		new File("c:/boletines/BOE.ES/" + fecha1 + "/pdf").mkdirs();
	}
	
	public static boolean esFecha (String fecha) {         
		boolean valido = false;
			fecha = fecha.replace(" ", "/");
			Pattern pat = Pattern.compile("[a-zA-Z]");
			Matcher mat = pat.matcher(fecha);
			Pattern pat1 = Pattern.compile("\\d{4,4}[/.-]\\d{1,2}[/.-]\\d{1,2}");
			Matcher mat1 = pat1.matcher(fecha);
			Pattern pat2 = Pattern.compile("\\d{1,2}[/.-]\\d{1,2}[/.-]\\d{4,4}");
			Matcher mat2 = pat2.matcher(fecha);
		if(mat.find()) {
			
		}
		if (mat1.matches() | mat2.matches()) {
			valido = true;
		} else JOptionPane.showMessageDialog(null, "Formato incorrecto. Escriba este formato: dd-MM-yyyy");
		return valido;
	}
	
	public static String unificarFechas(String fecha) throws Exception, DateTimeParseException{         
		fecha = fecha.replace("-", "/");
        fecha = fecha.replace(".", "/");
        fecha = fecha.replace("\\", "/");
        fecha = fecha.replace(" ", "/");
        LocalDate fechaDate;
		LocalDate hoy = LocalDate.now();
		Period meses = Period.ofMonths(3);
		Period dias = Period.ofDays(1);
		LocalDate tresMeses = hoy.minus(meses).minus(dias);
		DateTimeFormatter diasP = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter añosP = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		Pattern pat = Pattern.compile("\\d{1,2}/.*");
		Matcher mat = pat.matcher(fecha);
		if (mat.matches()) {
			fechaDate = LocalDate.parse(fecha, diasP);
			fecha = fechaDate.format(añosP);
			if(tresMeses.isBefore(fechaDate)) {
				return fecha;
			} else { 
				fecha = tresMeses.plus(dias).format(añosP);
				JOptionPane.showMessageDialog(null, "Ha introducido una fecha anterior a 3 meses. Se establece su fecha en el límite de 3 meses: " + fecha + ".");
			}
			if(hoy.isBefore(fechaDate)) {
				fecha = hoy.format(añosP);
				JOptionPane.showMessageDialog(null, "Ha introducido una fecha posterior al día actual. Se establece su fecha a hoy: " + hoy + ".");
				return fecha;
			}
		} else {
			fechaDate = LocalDate.parse(fecha, añosP);
			if(tresMeses.isBefore(fechaDate)) {
				return fecha;
			} else {
				fecha = tresMeses.plus(dias).format(añosP);
				JOptionPane.showMessageDialog(null, "Ha introducido una fecha anterior a 3 meses. Se establece su fecha en el límite de 3 meses: " + fecha + ".");
			}
			if(hoy.isBefore(fechaDate)) {
				fecha = hoy.format(añosP);
				JOptionPane.showMessageDialog(null, "Ha introducido una fecha posterior al día actual. Se estable su fecha a hoy: " + hoy + ".");
				return fecha;
			}
		}
	return fecha;
	}
	public static String getHTML(String urlToRead) {
		StringBuilder result = new StringBuilder();             
		try {
			URL url = new URL(urlToRead);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();             
			conn.setRequestMethod("GET");													
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));      
			String line;
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			rd.close();
		} catch (Exception e) {
			result = new StringBuilder();         
		}
		return result.toString();
	}
	
	public static void guardarPdfs(List<String> html, String ruta) {
		html.forEach(x -> {
			int boen;
			if ((boen = x.indexOf("BOE-N")) != -1) {
			String directorio = x.substring(boen, boen+17);
			directorio = ruta + directorio +".pdf";
			getDownloadFileSSL(x, directorio);
			}
		});
	}
	
	public static void saveHTML(String html, String archivoDestino) {
		try {
			PrintWriter salida = new PrintWriter(new FileWriter(archivoDestino));
			salida.write(html);
			salida.close();
		} catch (IOException ioe) {}
	}
	public static List<String> getEnlacesHTML(String url, String buscar) {
		String html = getHTML(url);
		String urlBase = "";
		try {
			URL base = new URL(url);
			int puerto = base.getPort();
			if(puerto != -1){
				urlBase = base.getProtocol() + "://" + base.getHost() + ":" + base.getPort();
			} else urlBase = base.getProtocol() + "://" + base.getHost();
		} catch (Exception e) {}
		List<String> enlaces = new ArrayList<String>();
		html = html.replace("\'", "\"").replace(" ", "").toLowerCase();
		int posicionHref;
		while ((posicionHref= html.indexOf(buscar)) != -1) {
			String enlace = html.substring(posicionHref + buscar.length()-3);
			enlace = enlace.substring(0, enlace.indexOf("\""));
			if (!enlaces.contains(enlace)) enlaces.add(enlace);
			html = html.substring(posicionHref + buscar.length());
			}
		return enlaces;
	}	
	public static List<String> getLink(String url) {
		return getEnlacesHTML(url, "boe_n");
	}
	public static List<String> getLinks(String url) {
		return getEnlacesHTML(url, "href=\"/boe");
	}
	public static String mayusculasPdf(String enlace) {
		String res = "";
		if(!enlace.endsWith(".php")) {
			int posicion = enlace.indexOf("boe-");
			String parte1 = enlace.substring(0, posicion);
			String parte2 = "";
			if(enlace.endsWith(".pdf")) {
				parte2 = enlace.substring(posicion, enlace.indexOf(".pdf"));
				res += parte1;
				res += parte2.toUpperCase();
				res += ".pdf";
			} else {
				parte2 = enlace.substring(posicion);
				res += parte1;
				res += parte2.toUpperCase();
				res += "&fix_bug_chrome=foo.pdf";
			}
		}
		return res;	
	}
	
	
	public static void getDownloadFileSSL(String url, String archivoDestino) {
		try {
			URL enlace = new URL(url);
			HttpsURLConnection conexion = (HttpsURLConnection) enlace.openConnection();
			conexion.setReadTimeout(10000);                                           
			conexion.setRequestProperty("User-Agent", "Mi robot java");               
			conexion.connect();
			InputStream documento = conexion.getInputStream();
			FileOutputStream guardar = new FileOutputStream(new File(archivoDestino));
			byte[] arrayBytes = new byte[65536];
			int bytesLeidos = 0;
			while((bytesLeidos = documento.read(arrayBytes)) != -1) {
				guardar.write(arrayBytes, 0, bytesLeidos);          
				guardar.flush();
			}
			guardar.close();
			documento.close();
			} catch (Exception e) {}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
