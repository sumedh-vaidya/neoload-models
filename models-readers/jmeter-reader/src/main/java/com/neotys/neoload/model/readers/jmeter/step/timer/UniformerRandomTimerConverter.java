package com.neotys.neoload.model.readers.jmeter.step.timer;

import com.google.common.collect.ImmutableList;
import com.neotys.neoload.model.readers.jmeter.EventListenerUtils;
import com.neotys.neoload.model.v3.project.userpath.Delay;
import com.neotys.neoload.model.v3.project.userpath.Step;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.testelement.property.PropertyIterator;
import org.apache.jmeter.timers.UniformRandomTimer;
import org.apache.jorphan.collections.HashTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.BiFunction;

/**
 * This function convert the UniforRandomTimer of JMeter into Delay Step of Neoload
 */
public class UniformerRandomTimerConverter implements BiFunction<UniformRandomTimer, HashTree, List<Step>> {

    //Attributs
    private static final Logger LOGGER = LoggerFactory.getLogger(UniformerRandomTimerConverter.class);

    //Constructor
    public UniformerRandomTimerConverter() { }

    //Methods
    @Override
    public List<Step> apply(UniformRandomTimer uniformRandomTimer, HashTree hashTree) {
        Delay delay = Delay.builder()
                .name(uniformRandomTimer.getName())
                .description(uniformRandomTimer.getComment())
                .value(checkDelay(uniformRandomTimer))
                .build();
        LOGGER.info("Uniform Random Timer Correctly converted");
        EventListenerUtils.readSupportedFunction("UniformRandomTimer","Uniform Random Timer");
        return ImmutableList.of(delay);
    }

    /**
     * This Timer have the same problem that CSVData
     * We have to get the value of the base delay back
     * And add a random value between 1 and the Timer's Random Value
     * @param uniformRandomTimer
     * @return
     */
    static String checkDelay(UniformRandomTimer uniformRandomTimer) {
        double baseDelay = 0.0;
        double randomDelay = 0.0;
        String delay = "";
        final PropertyIterator propertyIterator = uniformRandomTimer.propertyIterator();
        while (propertyIterator.hasNext()) {
            JMeterProperty jMeterProperty = propertyIterator.next();
            switch (jMeterProperty.getName()) {
                case "ConstantTimer.delay":
                    baseDelay = Double.parseDouble(jMeterProperty.getStringValue());
                    break;
                case "RandomTimer.range":
                    randomDelay = Double.parseDouble(jMeterProperty.getStringValue());
                    break;
                default:
                    LOGGER.error("UniformRandomTimer has not be created with success");
                    EventListenerUtils.readUnsupportedAction("Not Right UniformRandomTimer");
            }
        }
        delay = String.valueOf(Math.round(baseDelay + (Math.random()*randomDelay)));
        return delay;
    }
}