package com.ik.aws.lambda;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
public class StreamLambdaHandler implements RequestStreamHandler {

    private static final SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> HANDLER;

    static {
        try {
            log.info("Initializing Spring Boot Lambda container");
            HANDLER = SpringBootLambdaContainerHandler.getAwsProxyHandler(WeatherLambdaApplication.class);
        } catch (Exception e) {
            log.error("Could not initialize Spring Boot Lambda container", e);
            throw new RuntimeException("Could not initialize Spring Boot Lambda container", e);
        }
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        log.info("Received Lambda invocation request");
        try {
            HANDLER.proxyStream(input, output, context);
        } catch (Exception e) {
            log.error("Error processing Lambda request", e);
            throw e;
        }
    }
}
