package org.peerbox.watchservice.states;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;
import org.hive2hive.core.exceptions.IllegalFileLocation;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.peerbox.FileManager;
import org.peerbox.watchservice.Action;
import org.peerbox.watchservice.FileComponent;
import org.peerbox.watchservice.FileEventManager;
import org.peerbox.watchservice.IFileEventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.SetMultimap;

/**
 * the Initial state is given when a file is considered as new, synced or unknown.
 * The transition to another state is always valid and will be therefore accepted.
 * 
 * @author winzenried
 *
 */
public class InitialState extends AbstractActionState {

	private final static Logger logger = LoggerFactory.getLogger(InitialState.class);

	public InitialState(Action action) {
		super(action);
	}

	@Override
	public AbstractActionState changeStateOnLocalCreate() {
		logger.debug("Local Create Event: Initial -> Local Create ({})", action.getFilePath());
		return new LocalCreateState(action);
	}

	@Override
	public AbstractActionState changeStateOnLocalUpdate() {
		logger.debug("Local Update Event: Initial -> Local Update ({})", action.getFilePath());
		return new InitialState(action);
	
	}

	@Override
	public AbstractActionState changeStateOnLocalDelete() {
		logger.debug("Local Delete Event: Initial -> Local Delete ({})", action.getFilePath());
		return new LocalDeleteState(action);
	}

	@Override
	public AbstractActionState changeStateOnLocalMove(Path oldPath) {
		logger.debug("Local Move Event: Initial -> Local Move ({})", action.getFilePath());
		return this;//new LocalMoveState(action, oldPath);
	}

	@Override
	public AbstractActionState changeStateOnRemoteUpdate() {
		logger.debug("Remote Update Event: Initial -> Remote Update ({})", action.getFilePath());
		return new RemoteUpdateState(action);
	}
	
	@Override
	public AbstractActionState changeStateOnRemoteCreate() {
		logger.debug("Remote Create Event: Initial -> Remote Create ({})", action.getFilePath());
		return new RemoteCreateState(action);
	}

	@Override
	public AbstractActionState changeStateOnRemoteDelete() {
		logger.debug("Remote Delete Event: Initial -> Remote Delete ({})", action.getFilePath());
		return new RemoteDeleteState(action);
	}

	@Override
	public AbstractActionState changeStateOnRemoteMove(Path oldFilePath) {
		logger.debug("Remote Move Event: Initial -> Remote Move ({}) {}", action.getFilePath(), action.hashCode());
		
		Path path = action.getFilePath();
		logger.debug("Execute REMOTE MOVE: {}", path);
		try {
			Files.move(oldFilePath, path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new RemoteMoveState(action, oldFilePath);
	}

	@Override
	public AbstractActionState changeStateOnLocalRecover(int versionToRecover) {
		logger.debug("Recover Event: Initial -> Recover ({})", action.getFilePath());
		return new RecoverState(action, versionToRecover);
	}

	@Override
	public AbstractActionState handleLocalCreate() {
		action.putFile(action.getFilePath().toString(), action.getFile());
		action.getFile().updateContentHash();
		
		IFileEventManager eventManager = action.getFileEventManager();
		FileComponent moveSource = eventManager.findDeletedByContent(action.getFile());
		if(moveSource == null){
			logger.trace("Handle regular create of {}, as no possible move source has been found.", action.getFilePath());
			action.getFileEventManager().getFileComponentQueue().add(action.getFile());
			return changeStateOnLocalCreate();
		} else {
			logger.trace("Handle move of {}, from {}.", action.getFilePath(), moveSource.getPath());
			moveSource.getAction().handleLocalMoveEvent(action.getFilePath());
			eventManager.getFileComponentQueue().remove(action.getFile());
			return changeStateOnLocalMove(action.getFilePath());
		}
		
	}

	@Override
	public AbstractActionState handleLocalDelete() {
		throw new NotImplementedException("InitialState.handleLocalDelete");
	}

	@Override
	public AbstractActionState handleLocalUpdate() {
		// TODO Auto-generated method stub
		logger.debug("Local Update is ignored for {}", action.getFilePath());
		return changeStateOnLocalUpdate();
	}

	@Override
	public AbstractActionState handleLocalMove(Path oldPath) {
		// TODO Auto-generated method stub
		IFileEventManager eventManager = action.getFileEventManager();
		eventManager.deleteFileComponent(action.getFilePath());
		
		return changeStateOnLocalMove(oldPath);
	}

	@Override
	public AbstractActionState handleLocalRecover() {
		// TODO Auto-generated method stub
		throw new NotImplementedException("InitialState.handleLocalRecover");
	}

	@Override
	public AbstractActionState handleRemoteCreate() {
		action.putFile(action.getFilePath().toString(), action.getFile());
		updateTimeAndQueue();
		return changeStateOnRemoteCreate();
	}

	@Override
	public AbstractActionState handleRemoteDelete() {
		// TODO Auto-generated method stub
		throw new NotImplementedException("InitialState.handleRemoteDelete");
	}

	@Override
	public AbstractActionState handleRemoteUpdate() {
		// TODO Auto-generated method stub
		throw new NotImplementedException("InitialState.handleRemoteUpdate");
	}

	@Override
	public AbstractActionState handleRemoteMove() {
		// TODO Auto-generated method stub
		throw new NotImplementedException("InitialState.handleRemoteMove");
	}
	

	@Override
	public void execute(FileManager fileManager) throws NoSessionException,
			NoPeerConnectionException, IllegalFileLocation {
		logger.warn("Execute is not defined in the initial state  ({})", action.getFilePath());
		notifyActionExecuteSucceeded();
	}
}
