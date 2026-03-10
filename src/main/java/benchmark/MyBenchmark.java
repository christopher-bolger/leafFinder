/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package benchmark;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import leafFinder.model.ImageProcessor;
import leafFinder.model.Settings;
import leafFinder.utility.SwingFXUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.Main;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Measurement(iterations=10)
@Warmup(iterations=5)
@Fork(value=1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
public class MyBenchmark {
    ImageProcessor processor, processor2;
    Settings settings, settings2;
    double[] computeArguments = new double[]{0, 30, 0, 1, 0.25, 1};
    BufferedImage img;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        InputStream is = getClass().getResourceAsStream("/images/meadow-4508994_1920_1200.jpg");
        BufferedImage b = ImageIO.read(is);
        WritableImage fxImg = SwingFXUtils.toFXImage(b, null);


        settings = new Settings("1/2", 20,1, Color.BLUE, Color.RED, Color.LIME,
                Color.MAGENTA, 1, Color.BLACK, 5, 5);
        settings2 = new Settings("1/1", 20,1, Color.BLUE, Color.RED, Color.LIME,
                Color.MAGENTA, 1, Color.BLACK, 5, 5);

        processor = new ImageProcessor(fxImg, settings);
        processor2 = new ImageProcessor(fxImg, settings2);

        processor.setComputeArguments(computeArguments);
        processor2.setComputeArguments(computeArguments);
    }

    @Benchmark
    public void testComputeHalf() {
        processor.compute();
    }

    @Benchmark
    public void testComputeFull() {
        processor2.compute();
    }

    public static void main(String[] args) throws RunnerException, IOException {
        Main.main(args);
    }
}
