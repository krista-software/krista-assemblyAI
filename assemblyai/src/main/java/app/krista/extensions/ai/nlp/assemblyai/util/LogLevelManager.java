/*
 * AssemblyAI Extension for Krista
 * Copyright (C) 2024 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package app.krista.extensions.ai.nlp.assemblyai.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Singleton;

/**
 * Utility class for managing log levels dynamically at runtime.
 * Uses Log4j2 for log level configuration (compatible with Krista platform).
 *
 * Supports runtime changes to log levels without requiring application restart.
 * This is particularly useful for debugging production issues.
 */
@Service
@Singleton
public class LogLevelManager {

    private static final Logger LOG = LogManager.getLogger(LogLevelManager.class.getName());
    private static final String PACKAGE_NAME = "app.krista.extensions.ai.nlp.assemblyai";

    /**
     * Sets the log level for the AssemblyAI extension package.
     *
     * @param logLevel The log level to set (TRACE, DEBUG, INFO, WARN, ERROR, OFF)
     */
    public static void setLogLevel(String logLevel) {
        if (logLevel == null || logLevel.trim().isEmpty()) {
            LOG.warn("Log level not specified, using default INFO level");
            logLevel = "INFO";
        }

        try {
            // Convert string to Log4j2 Level
            Level level = Level.toLevel(logLevel.toUpperCase(), Level.INFO);

            // Get the Log4j2 LoggerContext
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration config = context.getConfiguration();

            // Get or create LoggerConfig for the package
            LoggerConfig loggerConfig = config.getLoggerConfig(PACKAGE_NAME);

            // If the logger config is for a parent logger, create a new one for this package
            if (!loggerConfig.getName().equals(PACKAGE_NAME)) {
                loggerConfig = new LoggerConfig(PACKAGE_NAME, level, true);
                config.addLogger(PACKAGE_NAME, loggerConfig);
            } else {
                loggerConfig.setLevel(level);
            }

            // Update the configuration
            context.updateLoggers();

            LOG.info("Log level set to {} for package {}", level, PACKAGE_NAME);
        } catch (Exception e) {
            LOG.error("Failed to set log level to {}: {}", logLevel, e.getMessage(), e);
        }
    }

    /**
     * Gets the current log level for the AssemblyAI extension package.
     *
     * @return The current log level as a string
     */
    public static String getLogLevel() {
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            Configuration config = context.getConfiguration();
            LoggerConfig loggerConfig = config.getLoggerConfig(PACKAGE_NAME);
            Level level = loggerConfig.getLevel();
            return level != null ? level.toString() : "INFO";
        } catch (Exception e) {
            LOG.error("Failed to get log level: {}", e.getMessage(), e);
            return "INFO";
        }
    }

    /**
     * Resets the log level to INFO (default).
     */
    public static void resetLogLevel() {
        setLogLevel("INFO");
    }
}

