/*H*****************************************************************************
*
* Copyright (c) 2018 ChipCraft Sp. z o.o. All rights reserved
*
* This software is subject to the terms of the Eclipse Public License v2.0
* You must accept the terms of that agreement to use this software.
* A copy of the License is available at
* https://www.eclipse.org/legal/epl-2.0
*
* ******************************************************************************
* File Name : StreamUtils.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.chipcraftic.eplugin.core.CcPlugin;

public class StreamUtils {
	private StreamUtils() {
		// disallowed
	}
	
	public static void closeStream(InputStream inputStream) {
		try {
			inputStream.close();
		} catch (IOException e) {
			CcPlugin.log(e);
		}
	}
	
	public static void closeStream(OutputStream outputStream) {
		try {
			outputStream.close();
		} catch (IOException e) {
			CcPlugin.log(e);
		}
	}
	
	public static void copy(OutputStream outputStream, InputStream inputStream) throws IOException {
		byte[] buf = new byte[1024];
		while (true) {
			int read = inputStream.read(buf);
			if (read <= 0) {
				break;
			}
			outputStream.write(buf, 0, read);
		}
	}
}
