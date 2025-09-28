package com.warmhouse.temperature.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TemperatureService {

    private static class TemperatureState {
        double current;
        double target;
        long lastUpdateEpochMillis;
    }

    private final Map<String, TemperatureState> locationToState = new ConcurrentHashMap<>();
    private final Random random = new Random();

    // Bounds for realistic outdoor temperatures in Celsius
    private static final double MIN_TEMP = -35.0;
    private static final double MAX_TEMP = 45.0;

    // Maximum step per second to avoid abrupt changes
    private static final double MAX_DELTA_PER_SECOND = 0.2; // deg C per second

    public double getTemperature(String location) {
        long now = Instant.now().toEpochMilli();
        TemperatureState state = locationToState.computeIfAbsent(location, key -> initialState(now));

        // Occasionally choose a new target to drift toward
        if (now - state.lastUpdateEpochMillis > 15_000) { // every 15s pick a new nearby target
            double span = Math.min(5.0, (MAX_TEMP - MIN_TEMP) / 10.0);
            double newTarget = clamp(state.current + (random.nextGaussian() * span), MIN_TEMP, MAX_TEMP);
            state.target = newTarget;
        }

        // Smoothly move current toward target depending on elapsed time
        double elapsedSec = Math.max(0.0, (now - state.lastUpdateEpochMillis) / 1000.0);
        double maxDelta = MAX_DELTA_PER_SECOND * elapsedSec;
        double desiredDelta = state.target - state.current;
        double appliedDelta = clamp(desiredDelta, -maxDelta, maxDelta);
        state.current = clamp(state.current + appliedDelta, MIN_TEMP, MAX_TEMP);
        state.lastUpdateEpochMillis = now;

        // Add a tiny noise so it is not perfectly linear
        double noise = random.nextGaussian() * 0.05; // 0.05C stddev
        state.current = clamp(state.current + noise, MIN_TEMP, MAX_TEMP);

        // Round to one decimal place for realism
        return Math.round(state.current * 10.0) / 10.0;
    }

    private TemperatureState initialState(long now) {
        TemperatureState s = new TemperatureState();
        s.current = randomBetween(5.0, 25.0); // start at a mild temperature
        s.target = s.current + randomBetween(-2.0, 2.0);
        s.lastUpdateEpochMillis = now;
        return s;
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private double randomBetween(double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
}


