package com.appdynamics.extensions.azure.customnamespace.utils;

import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.metrics.Metric;
import com.google.common.collect.Lists;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 Copyright 2019. AppDynamics LLC and its affiliates.
 All Rights Reserved.
 This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 The copyright notice above does not evidence any actual or intended publication of such source code.
*/
public class CommonUtilities {

    private static final Logger LOGGER = ExtensionsLoggerFactory.getLogger(CommonUtilities.class);
    public static boolean checkStringPatternMatch(String fullName, List<String> configPatterns) {
        for (String configPattern : configPatterns) {
            if (checkRegexMatch(fullName, configPattern)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean checkRegexMatch(String text, String pattern) {
        Pattern regexPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        Matcher regexMatcher = regexPattern.matcher(text);
        return regexMatcher.matches();
    }

    public static List<Metric> collectFutureMetrics(List<FutureTask<List<Metric>>> tasks, int timeout, String taskSource) {
        List<Metric> metrics = Lists.newArrayList();
        LOGGER.debug("Task collection started for : " + taskSource);
        for (FutureTask<List<Metric>> task : tasks) {
            try {
                metrics = task.get(timeout, TimeUnit.SECONDS);
            } catch (InterruptedException var6) {
                LOGGER.error("Task interrupted. ", var6);
            } catch (ExecutionException var7) {
                LOGGER.error("Task execution failed. ", var7);
            } catch (TimeoutException var8) {
                LOGGER.error("Task timed out. ", var8);
            }
        }
        return metrics;
    }
}
