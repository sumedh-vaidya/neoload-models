package com.neotys.neoload.model.writers.neoload.settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import com.neotys.neoload.model.writers.neoload.NeoLoadWriter.ConfigFiles;

public class ProjectSettingsWriter {
		
	private static final String PROJECT_SETTINGS_DEFAULT_FILE = "defaultProjectSettings.properties";	
	private static final String SETTINGS_XML_FILE = ConfigFiles.SETTINGS.getFileName();	

	private ProjectSettingsWriter() {
	}

	public static void writeSettingsXML(final String nlProjectFolder, final Map<String,String> overwrittenProjectSettings) throws IOException{
		final Map<String,String> allProjectSettings = readProjectSettingsDefault();
		allProjectSettings.putAll(overwrittenProjectSettings);	    
	    final Reader source = new InputStreamReader(ProjectSettingsWriter.class.getResourceAsStream(SETTINGS_XML_FILE));
	    try(final Reader reader = new TokenReplacingReader(source, allProjectSettings)){
		    int intValueOfChar;
	        final StringBuilder buffer = new StringBuilder();
	        while ((intValueOfChar = reader.read()) != -1) {
	            buffer.append((char) intValueOfChar);
	        }	      
	        writeSettingsXML(nlProjectFolder, buffer.toString());	        
	    }
	}

	public static void writeSettingsXML(final String nlProjectFolder, final String content) throws IOException {
		final File targetFile = new File(nlProjectFolder,SETTINGS_XML_FILE);
        if(!targetFile.createNewFile()) {
        	throw new IOException("Cannot create "+ConfigFiles.SETTINGS.getFileName()+" file");
        } 
		try (Writer targetFileWriter = new FileWriter(targetFile)) {
			targetFileWriter.write(content);
		}		
	}


	private static Map<String,String> readProjectSettingsDefault() throws IOException {
		final Properties properties = new Properties();
		try (final InputStream in = ProjectSettingsWriter.class.getResourceAsStream(PROJECT_SETTINGS_DEFAULT_FILE)){
			properties.load(in);
		} 
		return properties.entrySet().stream().collect(
			    Collectors.toMap(
			            e -> e.getKey().toString(),
			            e -> e.getValue().toString()
			       )
			   );
	}	
}
