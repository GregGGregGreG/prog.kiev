package ua.kiev.prog;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

public class AddServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private MessageList msgList = MessageList.getInstance();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Message m = null;

		try {
			m = Message.readFromStream(req.getInputStream());
		} catch (JAXBException | ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		if (m == null) {
			resp.setStatus(400); // bad request
		} else if (m.to.equals("status")) {
			m.to = m.from;
			m.from = "System";
			if (m.text.equalsIgnoreCase("all")) {
				StringBuilder sb = new StringBuilder();
				for (Map.Entry<String, String> entry : ClientsStatusServlet.status
						.entrySet()) {
					if (!entry.getValue().equals("offline")) {
						sb.append(entry.getKey() + " = " + entry.getValue() + "\n");
					}
				}
				m.text = sb.toString();
			}
			else {
				if(ClientsStatusServlet.status.containsKey(m.text)) {
					m.text = m.text + " = " + ClientsStatusServlet.status.get(m.text);
				}
				else {
					m.text = "The user does not exist";
				}
			}
		}
		msgList.add(m);
	}
}