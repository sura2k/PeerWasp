package org.peerbox.forcesync;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.peerbox.watchservice.conflicthandling.ConflictHandler;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ConflictHandler.class)
public class RemoteExistsLocalAdd extends ListSyncTest{

	@Test
	public void contentEqual() throws Exception{
		local.put(filePath, file1);
		remote.put(filePath, file1);
		remoteDatabase.put(filePath, file1);

		listSync.sync(local, localDatabase, remote, remoteDatabase);

		Mockito.verifyNoMoreInteractions(fileEventManager);
	}

	@Test
	public void contentNotEqual() throws Exception{
		PowerMockito.mockStatic(ConflictHandler.class);

		local.put(filePath, file2);
		remote.put(filePath, file1);
		remoteDatabase.put(filePath, file1);

		listSync.sync(local, localDatabase, remote, remoteDatabase);

		PowerMockito.stub(PowerMockito.method(ConflictHandler.class, "rename")).toReturn(Paths.get("asdf"));
		PowerMockito.verifyStatic();
		ConflictHandler.resolveConflict(Matchers.any(Path.class));

		Mockito.verifyNoMoreInteractions(fileEventManager);
	}

}
