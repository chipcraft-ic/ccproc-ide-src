package com.chipcraftic.eplugin.core.board;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IProject;

import com.chipcraftic.eplugin.core.CcPlugin;
import com.chipcraftic.eplugin.core.CcUtils;

public class BoardInfoResolver {
	private static final String BOARDS_DIR = "boards";
	private static final String BOARD_PROPS_FILENAME = "board.properties";
	
	public static BoardInfo getBoardInfo(String boardName, IProject project) {
		String sdkLocation = CcUtils.getSdkLocation(project);
		String boardPropsPath = sdkLocation + File.separator + BOARDS_DIR + File.separator + 
				boardName + File.separator + BOARD_PROPS_FILENAME;
		try (InputStream is = new BufferedInputStream(new FileInputStream(boardPropsPath))) {
			Properties props = new Properties();
			props.load(is);
			return new BoardInfo(props);
		} catch (IOException e) {
			CcPlugin.log(e);
			return null;
		}
	}
	
	public static List<String> listBoards(String sdkLocation) {
		File boardsDir = new File(sdkLocation + File.separator + BOARDS_DIR);
		if (!boardsDir.exists()) {
			return Collections.emptyList();
		}
		// Add items from boards sub-directory
		File[] boards = boardsDir.listFiles(f -> {
			return f.isDirectory() && new File(f, BOARD_PROPS_FILENAME).exists();
		});
		List<String> boardNames = new ArrayList<>(boards.length);
		for (File board: boards) {
			boardNames.add(board.getName());
		}
		return boardNames;
	}
	
	public static boolean isValidBoard(String boardName, String sdkLocation) {
		return new File(sdkLocation + File.separator + BOARDS_DIR + File.separator + 
				boardName + File.separator + BOARD_PROPS_FILENAME).exists();
	}
}
