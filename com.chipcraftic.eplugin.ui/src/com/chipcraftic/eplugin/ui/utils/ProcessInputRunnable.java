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
* File Name : ProcessInputRunnable.java
* Author    : Rafal Harabien
* ******************************************************************************
* $Date: 2019-03-19 12:27:38 +0100 (Tue, 19 Mar 2019) $
* $Revision: 397 $
*H*****************************************************************************/

package com.chipcraftic.eplugin.ui.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.chipcraftic.eplugin.core.CcPlugin;

public class ProcessInputRunnable implements Runnable {

	private final InputStream inputStream;
	private final OutputStream outputStream;
	
	public ProcessInputRunnable(InputStream inputStream, Process process) {
		this.inputStream = inputStream;
		this.outputStream = process.getOutputStream();
	}
	
	@Override
	public void run() {
		// Note: input stream is part of console and should not be closed
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {
			while (true) {
				String line = reader.readLine();
				if (line == null) {
					break;
				}
				//System.out.println("Sending to process input " + line);
				writer.write(line);
				writer.newLine();
				writer.flush();
			}
			System.out.println("Process input stream EOF");
		} catch (Exception e) {
			CcPlugin.log(e);
		}
	}
}
