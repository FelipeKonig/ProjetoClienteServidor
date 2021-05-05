
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;

public class ServidorWorker implements Runnable {

	private Socket s;

	public ServidorWorker(Socket s) {
		this.s = s;
	}

	@Override
	public void run() {

		try {

			boolean login = false;
			boolean post = false;

			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			String requestString = br.readLine();
			System.out.println("Cliente enviou : " + requestString);

			if (requestString.contains("POST")) {
				post = true;
			}

			System.out.println("--------------HEADER--------------");
			String linha = br.readLine();
//	        Lê a primeira linha
			// Enquanto a linha não for vazia
			while (!linha.isEmpty()) {
				// imprime a linha
				System.out.println(linha);
				// lê a proxima linha
				linha = br.readLine();
			}
			System.out.println("--------------BODY----------------");

			// code to read the post payload data
			StringBuilder payload = new StringBuilder();
			while (br.ready()) {
				payload.append((char) br.read());
			}

			if (post) {
				
				String infoPost = null;
				infoPost = payload.toString();
				
				String info[] = infoPost.split("=");

				String usuario[] = info[1].split("&");
				String senha = info[2];
				
				System.out.println("usuario:" + usuario[0]);
				System.out.println("senha:" + senha);

				if (usuario[0].equalsIgnoreCase("felipe") && senha.contentEquals("felipe")) {
					login = true;
				}
			}

			System.out.println("---------------------------------------------");

			/* Aqui faz algum processamento */

			DataOutputStream outToClient = new DataOutputStream(s.getOutputStream());

			SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:Ss z");

			String response = null;

			if (post) {
				if (login) {
					response = "HTTP/1.1 200 OK\n" + 
							"Server: HTTP server/0.1\n" + 
							"Date: " + format.format(new java.util.Date()) + "\n" +
							"Content-type: text/html; charset=UTF-8\n" + 
							"Content-Length: 22\n\n" + 
							"<h1>Usuario logou</h1>";
					outToClient.write(response.getBytes());
				} else {
					response = "HTTP/1.1 401 Unauthorized\n" + 
							"Server: HTTP server/0.1\n" + 
							"Date: " + format.format(new java.util.Date()) + "\n" + 
							"Content-type: text/html; charset=UTF-8\n" + 
							"Content-Length: 26\n\n" + 
							"<h1>Usuario não logou</h1>";
					outToClient.write(response.getBytes());
				}
			} else {
				response = "HTTP/1.1 200 OK\n" + 
						"Server: HTTP server/0.1\n" + 
						"Date: " + format.format(new java.util.Date()) + "\n" + 
						"Content-type: text/html; charset=UTF-8\n" + 
						"Content-Length: 27\n\n" +
						"<h1>Usuario fez um get</h1>";
				outToClient.write(response.getBytes());
			}
			System.out.println("Resposta");
			System.out.println("-----------------------------------------------");
			System.out.println(response);
			System.out.println("------------------------------------------------");

			outToClient.flush();
			outToClient.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
