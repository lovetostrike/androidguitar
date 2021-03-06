package edu.umd.cs.guitar.event;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Hashtable;
import java.util.List;

import edu.umd.cs.guitar.model.ADRComponent;
import edu.umd.cs.guitar.model.GComponent;
import edu.umd.cs.guitar.model.GObject;


public class ADREditableTextHandler extends ADREventHandler {
	
	final static int port = 10737;

	@Override
	protected void performImpl(GObject gComponent,
			Hashtable<String, List<String>> optionalData) {
			Socket socket = null;
			BufferedWriter out = null;
			BufferedReader in = null;
			
			try {
				System.out.println("==> Enter Text");

				socket = new Socket();
				socket.connect(new InetSocketAddress("127.0.0.1", port));

				out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

				out.write("edit");
				out.newLine();
				out.write(String.valueOf(((ADRComponent)gComponent).getIndex()));
				out.newLine();
				out.write("Red");
				out.newLine();
				out.flush();
			} catch (IOException ex) {
				ex.printStackTrace();
			} finally {
				try {
					if (in != null) {
						in.close();
					}
					if (out != null) {
						out.close();
					}
					socket.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
	}

	@Override
	protected void performImpl(GObject gComponent, Object parameters,
			Hashtable<String, List<String>> optionalData) {
		performImpl(gComponent, optionalData);
	}

	@Override
	public boolean isSupportedBy(GObject gComponent) {
		String sClass = "";//gComponent.getClassVal();
		
		if (sClass.equals("android.widget.EditText"))
			return true;
		else
			return false;
	}

}
