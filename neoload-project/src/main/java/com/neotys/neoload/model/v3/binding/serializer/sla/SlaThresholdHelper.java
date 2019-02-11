package com.neotys.neoload.model.v3.binding.serializer.sla;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import com.neotys.neoload.model.v3.project.sla.SlaThreshold;
import com.neotys.neoload.model.v3.binding.serializer.sla.SlaThresholdLexer;
import com.neotys.neoload.model.v3.binding.serializer.sla.SlaThresholdParser;
import com.neotys.neoload.model.v3.binding.serializer.sla.SlaThresholdParser.ThresholdContext;

public final class SlaThresholdHelper {

	private SlaThresholdHelper() {
		super();
	}

	public static SlaThreshold convertToThreshold(final String input) throws IOException {
		// Normalyze threshold
		final String thresholdAsText = (input != null) ? input : "";
		
		// Manages the errors
		final DefaultSlaThresholdErrorListener thresholdErrorListener = new DefaultSlaThresholdErrorListener();
		
		// Lexer
		final SlaThresholdLexer lexer = new SlaThresholdLexer(CharStreams.fromString(thresholdAsText));
	    lexer.removeErrorListeners();
	    lexer.addErrorListener(thresholdErrorListener);
	    // Tokens
	    final CommonTokenStream tokens = new CommonTokenStream(lexer);
	    // Parser
	    final SlaThresholdParser parser = new SlaThresholdParser(tokens);
	    parser.removeErrorListeners();
	    parser.addErrorListener(thresholdErrorListener);
	    // Context
	    final ThresholdContext context;
	    try {
	    	context = parser.threshold();
	    }
	    catch (final Exception e) {
	    	throw toException(thresholdAsText, Arrays.asList(e.getMessage()));
		}
	    
	    // Throw the errors if necessary
	    final List<String> errors = thresholdErrorListener.getErrors();
	    if (!errors.isEmpty()) {
	    	throw toException(thresholdAsText, errors);
	    }
	    
	    // Threshold visitor
	    final DefaultSlaThresholdVisitor visitor = new DefaultSlaThresholdVisitor();
	    final SlaThreshold threshold = visitor.visit(context);
	    return threshold;
	}
	
	private static IOException toException(final String threshold, final List<String> errors) {
		final StringBuilder message = new StringBuilder();
   		message.append(threshold);
   		message.append(" is not a valid threshold: ");
   		for (String error : errors) {
			message.append(System.lineSeparator());
			message.append(error);
		}
   		return new IOException(message.toString());
	}
}
