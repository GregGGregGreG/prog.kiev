package ua.kiev.prog;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

public class GetListServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private MessageList msgList = MessageList.getInstance();

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		OutputStream os = resp.getOutputStream();
		String fromStr = req.getParameter("from");
		String login = req.getParameter("login");
		String chatroom = req.getParameter("chatroom");
		int from = Integer.parseInt(fromStr);
		Message fake = new Message();
		Message m;

		List<Message> list = msgList.get();
		try {
			for (int i = from; i < list.size(); i++) {
				m = list.get(i);
				if (m.chatRoom.equals(chatroom)) {
					if (m.to.equals(login) || m.to.equals("")) {

						m.writeToStream(os);

					} else
						fake.writeToStream(os);
				} else
					fake.writeToStream(os);
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}