/*
 * Copyright 2015 Yan Zhenjie
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
package com.yanzhenjie.nohttp.rest;

import com.yanzhenjie.nohttp.HandlerDelivery;

/**
 * Created by Yan Zhenjie on 2017/2/16.
 */
class Messenger<T> {

    private final int what;
    private final OnResponseListener<T> listener;

    private Messenger(int what, OnResponseListener<T> listener) {
        this.what = what;
        this.listener = listener;
    }

    static <T> Messenger<T> newInstance(int what, OnResponseListener<T> listener) {
        return new Messenger<>(what, listener);
    }

    void start() {
        HandlerDelivery.getInstance().post(Poster.newInstance(what, listener).start());
    }

    void response(Response<T> response) {
        HandlerDelivery.getInstance().post(Poster.newInstance(what, listener).response(response));
    }

    void finish() {
        HandlerDelivery.getInstance().post(Poster.newInstance(what, listener).finish());
    }

    private static class Poster<T> implements Runnable {

        private static <T> Poster<T> newInstance(int what, OnResponseListener<T> listener) {
            return new Poster<>(what, listener);
        }

        private final int what;
        private final OnResponseListener<T> listener;

        private int command;
        private Response<T> response;

        private Poster(int what, OnResponseListener<T> listener) {
            this.what = what;
            this.listener = listener;
        }

        Poster<T> start() {
            this.command = -1;
            return this;
        }

        Poster<T> response(Response<T> response) {
            this.command = -2;
            this.response = response;
            return this;
        }

        Poster<T> finish() {
            this.command = -3;
            return this;
        }

        @Override
        public void run() {
            if (listener == null) return;
            switch (command) {
                case -1: {
                    listener.onStart(what);
                    break;
                }
                case -2: {
                    if (response.isSucceed())
                        listener.onSucceed(what, response);
                    else
                        listener.onFailed(what, response);
                    break;
                }
                case -3: {
                    listener.onFinish(what);
                    break;
                }
            }
        }
    }
}
