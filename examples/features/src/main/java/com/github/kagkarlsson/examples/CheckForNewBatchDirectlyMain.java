/**
 * Copyright (C) Gustav Karlsson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.kagkarlsson.examples;

import com.github.kagkarlsson.examples.helpers.Example;
import com.github.kagkarlsson.examples.helpers.ExampleHelpers;
import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.task.helper.OneTimeTask;
import com.github.kagkarlsson.scheduler.task.helper.Tasks;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.Instant;

import static com.github.kagkarlsson.examples.helpers.ExampleHelpers.sleep;

public class CheckForNewBatchDirectlyMain extends Example {

    public static void main(String[] args) {
        new CheckForNewBatchDirectlyMain().runWithDatasource();
    }

    @Override
    public void run(DataSource dataSource) {
        OneTimeTask<Void> onetimeTask = Tasks.oneTime("my_task")
            .execute((taskInstance, executionContext) -> {
                System.out.println("Executed!");
            });

        final Scheduler scheduler = Scheduler
            .create(dataSource, onetimeTask)
            .pollingInterval(Duration.ofSeconds(10))
            .threads(2)
            .build();

        ExampleHelpers.registerShutdownHook(scheduler);

        scheduler.start();

        sleep(2);
        System.out.println("Scheduling 100 task-instances.");
        for (int i = 0; i < 100; i++) {
            scheduler.schedule(onetimeTask.instance(String.valueOf(i)), Instant.now());
        }

    }
}
