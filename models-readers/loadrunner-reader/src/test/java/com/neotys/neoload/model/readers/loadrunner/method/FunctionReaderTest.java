package com.neotys.neoload.model.readers.loadrunner.method;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.neotys.neoload.model.function.Atoi;
import com.neotys.neoload.model.function.ImmutableAtoi;
import com.neotys.neoload.model.listener.TestEventListener;
import com.neotys.neoload.model.parsers.CPP14Parser.MethodcallContext;
import com.neotys.neoload.model.readers.loadrunner.ImmutableMethodCall;
import com.neotys.neoload.model.readers.loadrunner.LoadRunnerReader;
import com.neotys.neoload.model.readers.loadrunner.LoadRunnerVUVisitor;
import com.neotys.neoload.model.repository.EvalString;
import com.neotys.neoload.model.repository.ImmutableEvalString;

public class FunctionReaderTest {
		
	private static final LoadRunnerReader LOAD_RUNNER_READER = new LoadRunnerReader(new TestEventListener(), "", "");
	private static final LoadRunnerVUVisitor LOAD_RUNNER_VISITOR = new LoadRunnerVUVisitor(LOAD_RUNNER_READER, "{", "}", "");
	private static final MethodcallContext METHOD_CALL_CONTEXT = new MethodcallContext(null, 0);
	
	@Test
	public void testEvalStringReader() {				
		final EvalString actualEvalString = (EvalString) (new LREvalStringMethod()).getElement(LOAD_RUNNER_VISITOR, ImmutableMethodCall.builder()
				.name("\"lr_eval_string\"")
				.addParameters("\"{think_time}\"")
				.build(), METHOD_CALL_CONTEXT);
		final EvalString expectedEvalString = ImmutableEvalString.builder()
				.name("lr_eval_string")
				.returnValue("${think_time}")	
				.build();		
		assertEquals(expectedEvalString, actualEvalString);
	}
	
	@Test
	public void testAtoiReader() {				
		final Atoi actualAtoi = (Atoi) (new AtoiMethod()).getElement(LOAD_RUNNER_VISITOR, ImmutableMethodCall.builder()
				.name("\"atoi\"")
				.addParameters("\"1\"")
				.build(), METHOD_CALL_CONTEXT);
		final Atoi expectedAtoi = ImmutableAtoi.builder()
				.name("atoi_1")
				.returnValue("${atoi_1}")	
				.args(ImmutableList.of("1"))
				.build();		
		assertEquals(expectedAtoi, actualAtoi);
	}

}