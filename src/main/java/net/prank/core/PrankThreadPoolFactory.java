package net.prank.core;

import java.util.concurrent.ExecutorService;

/**
 * To allow thread pool flexibility for Prankster which uses a fixed thread pool
 * by default.
 *
 * @author dmillett
 * <p>
 * Copyright 2012, 2013, 2014 David Millett
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public interface PrankThreadPoolFactory {

    /**
     * Define whatever implementation works and pass to Prankster
     * @return An executor service for this custom thread pool
     */
    public ExecutorService createThreadPool();
}