package org.apache.camel;

import ai.djl.modality.cv.output.DetectedObjects;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Camel Java DSL Router
 */
public class MyRouteBuilder extends RouteBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(MyRouteBuilder.class);

    /**
     * Let's configure the Camel routing rules using Java code...
     */
    public void configure() {
        from("file:src/main/resources/data?recursive=true&noop=true")
                .convertBodyTo(byte[].class)
                .to("djl:cv/object_detection?artifactId=ai.djl.mxnet:ssd:0.0.2")
                .log("${header.CamelFileName} = ${body}")
                .process(exchange -> {
                    DetectedObjects detectedObjects = exchange.getMessage().getBody(DetectedObjects.class);

                    detectedObjects.items().forEach(i -> LOG.info("Class name: {}", i.getClassName()));
                })
                .to("mock:result");
    }

}
