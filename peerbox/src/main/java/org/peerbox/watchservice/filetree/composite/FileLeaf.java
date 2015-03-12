package org.peerbox.watchservice.filetree.composite;

import java.nio.file.Path;

import org.peerbox.watchservice.PathUtils;
import org.peerbox.watchservice.states.AbstractActionState;

public class FileLeaf extends AbstractFileComponent {

	public FileLeaf(final Path path, boolean updateContentHash) {
		super(path, updateContentHash);

		if (updateContentHash) {
			computeContentHash();
		}
	}

	@Override
	protected boolean computeContentHash() {
		String newHash = PathUtils.computeFileContentHash(getPath());
		System.out.println("new hash: " + newHash);
		if (!getContentHash().equals(newHash)) {
			setContentHash(newHash);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isFile() {
		return true;
	}

	@Override
	public boolean isReady() {
		boolean parentUploaded = getParent().isUploaded();
		return parentUploaded;
	}

	public void updateStateOnLocalDelete(AbstractActionState state){
		getAction().getCurrentState().changeStateOnLocalDelete();
	}

	@Override
	public void updateStateOnLocalDelete() {
		getAction().getCurrentState().changeStateOnLocalDelete();
	}

	@Override
	public String toString() {
		String s = String.format("File[path(%s), contentHash(%s), isUploaded(%s), isSynchronized(%s)]",
				getPath(), getContentHash(), isUploaded(), isSynchronized());
		return s;
	}

	@Override
	public void setIsSynchronizedRecursively(boolean b) {
		setIsSynchronized(b);
	}

}