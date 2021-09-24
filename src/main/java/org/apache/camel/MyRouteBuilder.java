package org.apache.camel;

import ai.djl.modality.cv.output.DetectedObjects;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;

/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {
        BindyCsvDataFormat bindy = new BindyCsvDataFormat(ProbabilityRecord.class);

        bindy.setLocale("default");

        from("file:{{input.dir}}?recursive=true&noop=true")
                .convertBodyTo(byte[].class)
                .to("djl:cv/object_detection?artifactId=ai.djl.mxnet:ssd:0.0.2")
                .process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        DetectedObjects detectedObjects = exchange.getMessage().getBody(DetectedObjects.class);

                        detectedObjects.items().forEach(i -> System.out.println(i.getClassName()));
                    }
                })
                .to("mock:result");
    }

}
