package ru.gnkoshelev.performance.tests;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OperationsPerInvocation;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

/**
 * Created by kgn on 20.03.2018.
 */
@Fork(value = 3, warmups = 0)
@Warmup(iterations = 5, time = 1_000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1_000, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(value = TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class FinalOrNotFinalBenchmark {
    private double x1, y1, z1;
    private double x2, y2, z2;

    @Setup(value = Level.Iteration)
    public void setup() {
        x1 = 123.4;
        y1 = 234.5;
        z1 = 345.6;
        x2 = 456.7;
        y2 = 567.8;
        z2 = 678.9;
    }

    @Benchmark
    @OperationsPerInvocation(10_000)
    public void computeWithFinalsBenchmark(Blackhole bh) {
        double sum = 0;
        for (int i = 0; i < 10_000; i++) {
            sum += computeWithFinals(x1, y1, z1, x2, y2, z2);
        }
        bh.consume(sum);
    }

    @Benchmark
    @OperationsPerInvocation(10_000)
    public void computeWithNonFinalsBenchmark(Blackhole bh) {
        double sum = 0;
        for (int i = 0; i < 10_000; i++) {
            sum += computeWithNonFinals(x1, y1, z1, x2, y2, z2);
        }
        bh.consume(sum);
    }

    public static double computeWithFinals(
            double x1, double y1, double z1,
            double x2, double y2, double z2) {
        FinalVector v1 = new FinalVector(x1, y1, z1);
        FinalVector v2 = new FinalVector(x2, y2, z2);
        return v1.crossProduct(v2).squared();
    }

    public static double computeWithNonFinals(
            double x1, double y1, double z1,
            double x2, double y2, double z2) {
        NonFinalVector v1 = new NonFinalVector(x1, y1, z1);
        NonFinalVector v2 = new NonFinalVector(x2, y2, z2);
        return v1.crossProduct(v2).squared();
    }

    public final static class FinalVector {
        private final double x, y, z;

        public FinalVector(double x, double y, double z) {
            this.x = x; this.y = y; this.z = z;
        }

        public double squared() {
            return x * x + y * y + z * z;
        }

        public FinalVector crossProduct(FinalVector v) {
            return new FinalVector(
                    y * v.z - z * v.y,
                    z * v.x - x * v.z,
                    x * v.y - y * v.x);
        }
    }

    public final static class NonFinalVector {
        private double x, y, z;

        public NonFinalVector(double x, double y, double z) {
            this.x = x; this.y = y; this.z = z;
        }

        public double squared() {
            return x * x + y * y + z * z;
        }

        public NonFinalVector crossProduct(NonFinalVector v) {
            return new NonFinalVector(
                    y * v.z - z * v.y,
                    z * v.x - x * v.z,
                    x * v.y - y * v.x);
        }
    }
}
