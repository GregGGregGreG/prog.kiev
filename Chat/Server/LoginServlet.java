package ua.kiev.prog;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final Map<String, String> clients = Collections.synchronizedMap(new HashMap<String, String>());
	
	static {
		clients.put("maks", "12345");
		clients.put("olha", "qwerty");
		clients.put("serj", "777");
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String login = req.getParameter("login");
		String password = req.getParameter("password");

		if (clients.containsKey(login) && clients.get(login).equals(password)) {
			ClientsStatusServlet.status.put(login, "online");
			resp.setStatus(200);

			try {
				resp.getWriter().println("Welcome to Chat-777");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			resp.setStatus(400);
	}
}