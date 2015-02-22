package ua.kiev.prog;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ClientsStatusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	public static final Map<String, String> status = Collections.synchronizedMap(new HashMap<String, String>());
	static {
		for(String login : LoginServlet.clients.keySet()) {
			status.put(login, "offline");
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		String login = req.getParameter("login");
		status.put(login, "offline");
		resp.setStatus(200);
	}
}