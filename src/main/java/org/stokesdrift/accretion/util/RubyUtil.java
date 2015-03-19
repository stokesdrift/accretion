package org.stokesdrift.accretion.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jruby.exceptions.RaiseException;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;

public class RubyUtil {

	private static final Logger logger = Logger.getLogger(RubyUtil.class.getName());

	public void captureMessage(final RaiseException re) {
		try {
			IRubyObject rubyException = re.getException();
			ThreadContext context = rubyException.getRuntime().getCurrentContext();
			// JRuby-Rack internals (@see jruby/rack/capture.rb) :
			rubyException.callMethod(context, "capture");
			rubyException.callMethod(context, "store");
		} catch (Exception e) {
			logger.log(Level.INFO, "failed to capture exception message", e);
		}
	}
	
	/**
	 * Derived from the Jrubyrack project - IOHelpers
	 * @author kares 
	 */
	public static String inputStreamToString(final InputStream stream) throws IOException {
		if (stream == null)
			return null;

		final StringBuilder str = new StringBuilder(128);
		String coding = "UTF-8";
		int c = stream.read();
		if (c == '#') { // look for a coding: pragma
			str.append((char) c);
			while ((c = stream.read()) != -1 && c != 10) {
				str.append((char) c);
			}
			Pattern pattern = Pattern.compile("coding:\\s*(\\S+)");
			Matcher matcher = pattern.matcher(str.toString());
			if (matcher.find()) {
				coding = matcher.group(1);
			}
		}

		str.append((char) c);
		Reader reader = new InputStreamReader(stream, coding);

		while ((c = reader.read()) != -1) {
			str.append((char) c);
		}

		return str.toString();
	}

	/**
	 * Derived from the Jrubyrack project - IOHelpers
	 * @author kares 
	 */
	public static String rubyMagicCommentValue(final String script, final String prefix) throws IOException {
		if (script == null)
			return null;

		final BufferedReader reader = new BufferedReader(new StringReader(script), 80);

		String line, comment = null;
		Pattern pattern = null;
		while ((line = reader.readLine()) != null) {
			// we only support (magic) comments at the beginning :
			if (line.length() == 0 || line.charAt(0) != '#')
				break;

			if (pattern == null) {
				pattern = Pattern.compile(prefix + "\\s*(\\S+)");
			}
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
				comment = matcher.group(1);
				break;
			}
		}
		reader.close();
		return comment;
	}
}
