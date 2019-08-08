package com.neotys.neoload.model.readers.jmeter.step.httpRequest;

import com.neotys.neoload.model.readers.jmeter.EventListenerUtils;
import com.neotys.neoload.model.v3.project.userpath.Request;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.apache.jorphan.collections.HashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class convert the HTTPHeaderManager of JMeter into the Header of the HTTPRequest attached
 */
class HTTPHeaderConverter {

    //Attributs
    private static final Logger LOGGER = LoggerFactory.getLogger(HTTPSamplerProxyConverter.class);

    //Constructor
     private HTTPHeaderConverter() {
        throw new IllegalAccessError();
    }

    //Methods
    static void createHeader( Request.Builder request, HashTree subTree){
        for (Object o : subTree.list()) {
            if (o instanceof HeaderManager) {
                HeaderManager head = (HeaderManager) o;
                CollectionProperty headers = head.getHeaders();
                changeHttpHeader(request, headers);
            }
        }
        LOGGER.info("Header on the HTTP Request is a success");
        EventListenerUtils.readSupportedFunction("HTTPHeaderManager","HTTP Header ");
    }

    private static void changeHttpHeader(Request.Builder request, CollectionProperty headers) {
        for (JMeterProperty headerProperty : headers) {
            if(headerProperty instanceof TestElementProperty) {
                TestElementProperty tep = (TestElementProperty) headerProperty;
                Object objectHeader = tep.getObjectValue();
                if(objectHeader instanceof Header) {
                    Header header = (Header) objectHeader;
                    request.addHeaders(com.neotys.neoload.model.v3.project.userpath.Header.builder().name(header.getName()).value(header.getValue()).build());
                }
            }
        }
    }
}