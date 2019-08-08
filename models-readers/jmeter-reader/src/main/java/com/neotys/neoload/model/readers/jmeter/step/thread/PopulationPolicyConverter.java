package com.neotys.neoload.model.readers.jmeter.step.thread;

import com.neotys.neoload.model.readers.jmeter.EventListenerUtils;
import com.neotys.neoload.model.readers.jmeter.ContainerUtils;
import com.neotys.neoload.model.v3.project.scenario.*;
import org.apache.jmeter.threads.ThreadGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class convert the ThreadGroup of JMeter into a PopulationPolicy of Neoload
 */
class PopulationPolicyConverter {

    //Attributs
    private static final Logger LOGGER = LoggerFactory.getLogger(PopulationPolicyConverter.class);

    //Constructor
    private PopulationPolicyConverter() {
        throw new IllegalAccessError();
    }

    ///Methods
    static PopulationPolicy convert(ThreadGroup threadGroup) {
        int nbUser = threadGroup.getNumThreads();
        int rampUp = threadGroup.getRampUp();

        final String populationPolicy = "PopulationPolicy";
        if(nbUser == 0){
            LOGGER.warn("There is a problem with the NumberUser of PopulationPolicy "
            + "Please check that you have fill correctly this form and don't use Variable "
            + " for NumberUser or Rampup or The TimeDuration");
            EventListenerUtils.readUnsupportedParameter(populationPolicy, "Variable String","NbUser");

        }

        int loop;

        /*
        First, we try to take the value of the Loop,
        If there is an error, we try to check the variables and take the good one
        Finally, if there is an error again,
        Maybe the user put a variable with a function or fill the form with a wrong value
         */
        try{
            loop = Integer.parseInt(threadGroup.getSamplerController().getPropertyAsString("LoopController.loops"));
        }catch(Exception e){
            try{
                loop = Integer.parseInt(ContainerUtils.getValue(threadGroup.getSamplerController().getPropertyAsString("LoopController.loops")));
            } catch (Exception e1) {
                LOGGER.warn("We can't manage the variable into the Loop Number. "
                        + "So We put 0 in value of Loop Number", e1);
                EventListenerUtils.readUnsupportedParameter(populationPolicy, "Variable String","Loop");

                loop = 0;
            }
        }
        boolean planifier = threadGroup.getScheduler();
        //Infinite Loop if LoadDuration is null
        final LoadDuration loadDuration = getIterationLoadDuration(threadGroup, loop, planifier);
        final LoadPolicy loadPolicy = getLoadPolicy(threadGroup, nbUser, rampUp, loadDuration);
        EventListenerUtils.readSupportedFunction("ThreadGroup Parameters", populationPolicy);
        LOGGER.info("Convertion of Population Policy");
        return PopulationPolicy.builder()
                .loadPolicy(loadPolicy)
                .name(threadGroup.getName())
                .description(threadGroup.getComment())
                .build();
    }

    private static LoadDuration getIterationLoadDuration(ThreadGroup threadGroup, int loop, boolean planifier) {
        EventListenerUtils.readSupportedAction("LoadDuration");
        if (planifier) {
            return getTimeLoadDuration(threadGroup);
        } else if (loop != -1) {
            return getIterationLoadDuration(loop);
        }
        return null;
    }

    private static LoadDuration getIterationLoadDuration(int loop) {
        EventListenerUtils.readSupportedAction("IterationDuration");
        return LoadDuration.builder()
                .type(LoadDuration.Type.ITERATION)
                .value(loop)
                .build();
    }

    private static LoadDuration getTimeLoadDuration(ThreadGroup threadGroup) {
        EventListenerUtils.readSupportedAction("TimeDuration");
        return LoadDuration.builder()
                .type(LoadDuration.Type.TIME)
                .value((int) threadGroup.getDuration())
                .build();
    }

    private static LoadPolicy getLoadPolicy(ThreadGroup threadGroup, int nbUser, int rampUp, LoadDuration loadDuration) {
        final LoadPolicy loadPolicy;
        //Sans planification
        if (rampUp == 0) {
            loadPolicy = getConstantLoadPolicy((int) threadGroup.getDelay(), nbUser, loadDuration);
        } else {
            loadPolicy = getRampupLoadPolicy((int) threadGroup.getDelay(), nbUser, rampUp, loadDuration);
        }
        return loadPolicy;
    }

    @SuppressWarnings("ConstantConditions")
    private static LoadPolicy getRampupLoadPolicy(Integer delay, int nbUser, int rampUp, LoadDuration loadDuration) {
        EventListenerUtils.readSupportedAction("RampUpPolicy");
        return RampupLoadPolicy.builder()
                .minUsers(1)
                .maxUsers(nbUser)
                .incrementUsers(Math.max(1, rampUp / nbUser))
                .incrementEvery(LoadDuration.builder()
                        .value(1)
                        .type(LoadDuration.Type.ITERATION)
                        .build())
                .duration(loadDuration)
                .rampup(delay == 0 ? null : delay)
                .build();
    }

    @SuppressWarnings("ConstantConditions")
    private static LoadPolicy  getConstantLoadPolicy(Integer delay, int nbUser, LoadDuration loadDuration) {
        EventListenerUtils.readSupportedAction("ConstantPolicy");
        return ConstantLoadPolicy.builder()
                .users(nbUser)
                .duration(loadDuration)
                .rampup(delay == 0 ? null : delay)
                .build();
    }

}