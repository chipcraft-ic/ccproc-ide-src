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
* File Name : ProcessOutputRunnable.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.utils;

import java.io.InputStream;

import org.eclipse.ui.console.IOConsoleOutputStream;

import com.chipcraftic.eplugin.core.CcPlugin;

public class ProcessOutputRunnable implements Runnable {

	private IOConsoleOutputStream consoleStream;
	private InputStream inputStream;
	
	public ProcessOutputRunnable(IOConsoleOutputStream consoleStream, InputStream inputStream) {
		this.consoleStream = consoleStream;
		this.inputStream = inputStream;
	}
	
	@Override
	public void run() {
		try {
			byte[] buf = new byte[1024];
			while (true) {
				int read = inputStream.read(buf);
				if (read <= 0) {
					break;
				}
				//System.out.println("read from process " + buf);
				consoleStream.write(buf, 0, read);
				consoleStream.flush();
			}
			System.out.println("Process output stream EOF");
		} catch (Exception e) {
			CcPlugin.log(e);
		}
		StreamUtils.closeStream(consoleStream);
		StreamUtils.closeStream(inputStream);
	}

}
