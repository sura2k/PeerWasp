package org.peerbox.server.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.peerbox.filerecovery.IFileRecoveryHandler;
import org.peerbox.server.servlets.messages.ServerReturnCode;
import org.peerbox.server.servlets.messages.ServerReturnMessage;
import org.peerbox.server.servlets.messages.FileRecoveryMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class FileRecoveryServlet extends BaseServlet {

	private static final Logger logger = LoggerFactory.getLogger(FileRecoveryServlet.class);
	private static final long serialVersionUID = 1L;
	
	private final Provider<IFileRecoveryHandler> fileRecoveryHandlerProvider;
	
	@Inject
	public FileRecoveryServlet(Provider<IFileRecoveryHandler> fileRecoveryHandlerProvider) {
		this.fileRecoveryHandlerProvider = fileRecoveryHandlerProvider;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			
			if (!checkContentTypeJSON(req, resp)) {
				logger.error("Received request with wrong ContentType: {}", req.getContentType());
				sendErrorMessage(resp, new ServerReturnMessage(ServerReturnCode.WRONG_CONTENT_TYPE));
				return;
			}
			
			String content = readContentAsString(req);
			if (content.isEmpty()) {
				sendErrorMessage(resp, new ServerReturnMessage(ServerReturnCode.EMPTY_REQUEST));
				return;
			}
			
			Gson gson = createGsonInstance();
			FileRecoveryMessage msg = null;
			msg = gson.fromJson(content, FileRecoveryMessage.class);
			
			handleRecoveryRequest(msg);
			
			sendEmptyOK(resp);
		} catch (JsonSyntaxException e) {
			sendErrorMessage(resp, new ServerReturnMessage(ServerReturnCode.DESERIALIZE_ERROR));
			logger.error("Could not deserialize json.", e);
		} catch (Exception e) {
			sendErrorMessage(resp, new ServerReturnMessage(ServerReturnCode.REQUEST_EXCEPTION));
			logger.error("Unexpected exception. ", e);
		}
	}


	private void handleRecoveryRequest(FileRecoveryMessage msg) throws Exception {
		if(msg == null) {
			throw new IllegalArgumentException("msg==null");
		}
		if(msg.getPath() == null) {
			throw new Exception("msg.getPath()==null");
		}
		
		logger.info("Got request for file recovery: {}", msg.getPath());
		IFileRecoveryHandler handler = fileRecoveryHandlerProvider.get();
		handler.recoverFile(msg.getPath());
	}

}
