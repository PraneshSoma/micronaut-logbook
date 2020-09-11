/* * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.psexpspace.micronaut.products;

import graphql.ExecutionResult;
import io.micronaut.configuration.graphql.*;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.exceptions.HttpStatusException;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static io.micronaut.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static io.micronaut.http.MediaType.*;

/*
*
 * The GraphQL controller handling GraphQL requests.
 *
 * @author Marcel Overdijk
 * @author James Kleeh
 * @since 1.0

*/

@Controller("${" + GraphQLConfiguration.PATH + ":" + GraphQLConfiguration.DEFAULT_PATH + "}")
public class GraphQLResource {

    private static final Logger log = LoggerFactory.getLogger(GraphQLResource.class);


    private final GraphQLInvocation graphQLInvocation;
    private final GraphQLExecutionResultHandler graphQLExecutionResultHandler;
    private final GraphQLJsonSerializer graphQLJsonSerializer;

/*
*
     * Default constructor.
     * @param graphQLInvocation             the {@link GraphQLInvocation} instance
     * @param graphQLExecutionResultHandler the {@link GraphQLExecutionResultHandler} instance
     * @param graphQLJsonSerializer         the {@link GraphQLJsonSerializer} instance
     * @param graphQLInvocation1
     * @param graphQLExecutionResultHandler1
     * @param graphQLJsonSerializer1

*/

    public GraphQLResource(GraphQLInvocation graphQLInvocation, GraphQLExecutionResultHandler graphQLExecutionResultHandler, GraphQLJsonSerializer graphQLJsonSerializer, GraphQLInvocation graphQLInvocation1, GraphQLExecutionResultHandler graphQLExecutionResultHandler1, GraphQLJsonSerializer graphQLJsonSerializer1) {
        this.graphQLInvocation = graphQLInvocation1;
        this.graphQLExecutionResultHandler = graphQLExecutionResultHandler1;
        this.graphQLJsonSerializer = graphQLJsonSerializer1;
    }

/*
*
     * Handles GraphQL {@code GET} requests.
     *
     * @param query         the GraphQL query
     * @param operationName the GraphQL operation name
     * @param variables     the GraphQL variables
     * @param httpRequest   the HTTP request
     * @return the GraphQL response

*/

    @Get(produces = APPLICATION_JSON, single = true)
    public Publisher<String> get(
            @QueryValue("query") String query,
            @Nullable @QueryValue("operationName") String operationName,
            @Nullable @QueryValue("variables") String variables,
            HttpRequest httpRequest) {

        return executeRequest(query, operationName, convertVariablesJson(variables), httpRequest);
    }

/**
     * Handles GraphQL {@code POST} requests.
     *
     * @param query         the GraphQL query
     * @param operationName the GraphQL operation name
     * @param variables     the GraphQL variables
     * @param body          the GraphQL request body
     * @param httpRequest   the HTTP request
     * @return the GraphQL response
     */

    @Post(uri = "/post",consumes = ALL, produces = APPLICATION_JSON, single = true)
    public Publisher<String> post(
            @Nullable @QueryValue("query") String query,
            @Nullable @QueryValue("operationName") String operationName,
            @Nullable @QueryValue("variables") String variables,
            @Nullable @Body String body,
            HttpRequest httpRequest) {

        Optional<MediaType> opt = httpRequest.getContentType();
        MediaType contentType = opt.orElse(null);

        if (body == null) {
            body = "";
        }

        if (APPLICATION_JSON_TYPE.equals(contentType)) {
            GraphQLRequestBody request = graphQLJsonSerializer.deserialize(body, GraphQLRequestBody.class);
            if (request.getQuery() == null) {
                request.setQuery("");
            }
            Publisher<String> stringPublisher = executeRequest(request.getQuery(), request.getOperationName(), request.getVariables(), httpRequest);

            stringPublisher
                    .subscribe(new Subscriber<String>() {
                        @Override
                        public void onSubscribe(Subscription s) {
                            s.request(Long.MAX_VALUE);
                        }

                        @Override
                        public void onNext(String string) {
                          log.debug("RESULT:::::::::::::::" + string);
                        }

                        @Override
                        public void onError(Throwable t) {}

                        @Override
                        public void onComplete() {}
                    });


            return stringPublisher;
        }

        // In addition to the above, we recommend supporting two additional cases:
        //
        // * If the "query" query string parameter is present (as in the GET example above),
        //   it should be parsed and handled in the same way as the HTTP GET case.

        if (query != null) {
            return executeRequest(query, operationName, convertVariablesJson(variables), httpRequest);
        }

        // * If the "application/graphql" Content-Type header is present,
        //   treat the HTTP POST body contents as the GraphQL query string.

        if (APPLICATION_GRAPHQL_TYPE.equals(contentType)) {
            return executeRequest(body, null, null, httpRequest);
        }

        throw new HttpStatusException(UNPROCESSABLE_ENTITY, "Could not process GraphQL request");
    }

    private Map<String, Object> convertVariablesJson(String jsonMap) {
        if (jsonMap == null) {
            return Collections.emptyMap();
        }
        return graphQLJsonSerializer.deserialize(jsonMap, Map.class);
    }
/*

*
     * Executes the GraphQL request and returns the serialized {@link GraphQLResponseBody}.
     *
     * @param query         the GraphQL query
     * @param operationName the GraphQL operation name
     * @param variables     the GraphQL variables
     * @param httpRequest   the HTTP request
     * @return the serialized GraphQL response

*/

    private Publisher<String> executeRequest(
            String query,
            String operationName,
            Map<String, Object> variables,
            HttpRequest httpRequest) {
        GraphQLInvocationData invocationData = new GraphQLInvocationData(query, operationName, variables);
        Publisher<ExecutionResult> executionResult = graphQLInvocation.invoke(invocationData, httpRequest);
        Publisher<GraphQLResponseBody> responseBody = graphQLExecutionResultHandler.handleExecutionResult(executionResult);

        return Publishers.map(responseBody, graphQLJsonSerializer::serialize);
    }

}
