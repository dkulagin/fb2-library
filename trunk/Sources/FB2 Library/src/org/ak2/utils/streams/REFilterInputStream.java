package org.ak2.utils.streams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class REFilterInputStream extends FilterInputStream {

	public REFilterInputStream(InputStream stream,
			Map<String, String> replacements) throws IOException {
		super(stream);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int len = stream.available();
		while (len > 0) {
			byte[] buffer = new byte[len];
			stream.read(buffer);
			baos.write(buffer);
			len = stream.available();
		}

		String text = baos.toString();
		for (String expr : replacements.keySet()) {
			text = text.replaceAll(expr, replacements.get(expr));
		}
		
		in = new ByteArrayInputStream(text.getBytes());
	}

}
