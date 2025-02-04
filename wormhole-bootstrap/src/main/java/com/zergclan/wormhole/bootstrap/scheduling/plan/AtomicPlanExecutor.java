/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zergclan.wormhole.bootstrap.scheduling.plan;

import com.zergclan.wormhole.bootstrap.scheduling.ExecutionState;
import com.zergclan.wormhole.bootstrap.scheduling.event.PlanExecutionEvent;
import com.zergclan.wormhole.bootstrap.scheduling.event.TaskExecutionEvent;
import com.zergclan.wormhole.bootstrap.scheduling.task.PromiseTaskExecutor;
import com.zergclan.wormhole.bootstrap.scheduling.task.PromiseTaskResult;
import com.zergclan.wormhole.bootstrap.scheduling.task.TaskResult;
import com.zergclan.wormhole.common.WormholeEvent;
import com.zergclan.wormhole.common.eventbus.WormholeEventBus;
import com.zergclan.wormhole.common.metadata.catched.CachedPlanMetaData;
import com.zergclan.wormhole.common.metadata.catched.CachedTaskMetaData;
import com.zergclan.wormhole.tool.concurrent.ExecutorServiceManager;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;

/**
 * Atomic plan implemented of {@link PlanExecutor}.
 */
@RequiredArgsConstructor
public final class AtomicPlanExecutor implements PlanExecutor {
    
    private final CachedPlanMetaData cachedPlanMetadata;
    
    @Override
    public void execute() {
        String planIdentifier = cachedPlanMetadata.getPlanIdentifier();
        long planBatch = cachedPlanMetadata.getPlanBatch();
        handleWormholeEvent(PlanExecutionEvent.buildExecutionEvent(planBatch, ExecutionState.RUN));
        WormholeEventBus.post(PlanExecutionEvent.buildExecutionEvent(planBatch, ExecutionState.RUN));
        try {
            for (Map<String, CachedTaskMetaData> each : cachedPlanMetadata.getCachedOrderedTasks()) {
                Optional<String> failedTask = transactionalExecute(planBatch, planIdentifier, each);
                if (failedTask.isPresent()) {
                    handleWormholeEvent(PlanExecutionEvent.buildCompleteEvent(planBatch, ExecutionState.FAILED));
                    return;
                }
            }
            // CHECKSTYLE:OFF
        } catch (final Exception ex) {
            // CHECKSTYLE:ON
            handleWormholeEvent(PlanExecutionEvent.buildCompleteEvent(planBatch, ExecutionState.ERROR));
        }
    }
    
    private Optional<String> transactionalExecute(final long planBatch, final String planIdentifier, final Map<String, CachedTaskMetaData> cachedTaskMetadata) {
        CompletionService<PromiseTaskResult> completionService = new ExecutorCompletionService<>(ExecutorServiceManager.getSchedulingExecutor());
        for (Map.Entry<String, CachedTaskMetaData> entry : cachedTaskMetadata.entrySet()) {
            CachedTaskMetaData cachedTaskMetaData = entry.getValue();
            String taskIdentifier = cachedTaskMetaData.getTaskIdentifier();
            long taskBatch = cachedTaskMetaData.getTaskBatch();
            handleWormholeEvent(TaskExecutionEvent.buildNewEvent(cachedPlanMetadata.getPlanIdentifier(), planBatch, taskIdentifier, taskBatch));
            completionService.submit(new PromiseTaskExecutor(planIdentifier, planBatch, entry.getValue()));
        }
        int size = cachedTaskMetadata.size();
        PromiseTaskResult promiseTaskResult;
        for (int i = 0; i < size; i++) {
            try {
                promiseTaskResult = completionService.take().get();
                if (!promiseTaskResult.isSuccess()) {
                    handleWormholeEvent(TaskExecutionEvent.buildCompleteEvent(promiseTaskResult.getResultData().getTaskBatch(), ExecutionState.FAILED));
                    return Optional.of(promiseTaskResult.getResultData().getCachedTaskIdentifier());
                }
                TaskResult result = promiseTaskResult.getResultData();
                if (0 == result.getTotalRow()) {
                    handleWormholeEvent(TaskExecutionEvent.buildCompleteEvent(promiseTaskResult.getResultData().getTaskBatch(), ExecutionState.SUCCESS));
                } else {
                    handleWormholeEvent(TaskExecutionEvent.buildExecutionEvent(promiseTaskResult.getResultData().getTaskBatch(), promiseTaskResult.getResultData().getTotalRow()));
                }
            } catch (final ExecutionException | InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        return Optional.empty();
    }
    
    private void handleWormholeEvent(final WormholeEvent event) {
        WormholeEventBus.post(event);
    }
}
