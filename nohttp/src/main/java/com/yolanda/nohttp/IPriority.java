/*
 * Copyright 2016 Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yolanda.nohttp;

/**
 * Created on 2016/6/21.
 *
 * @author Yan Zhenjie.
 */
public interface IPriority {

    /**
     * Set the priority of the request object. The default priority is {@link Priority#DEFAULT}.
     *
     * @param priority {@link Priority}.
     */
    void setPriority(Priority priority);

    /**
     * Get the priority of the request object.
     *
     * @return {@link Priority}.
     */
    Priority getPriority();

    /**
     * Set the sequence in the queue, under the condition of two requests as priority, {@code left.sequence-right.sequence} decision to order.
     *
     * @param sequence sequence code.
     */
    void setSequence(int sequence);

    /**
     * Get the sequence in the queue, under the condition of two requests as priority, {@code left.sequence-right.sequence} decision to order.
     *
     * @return sequence code.
     */
    int getSequence();

}
