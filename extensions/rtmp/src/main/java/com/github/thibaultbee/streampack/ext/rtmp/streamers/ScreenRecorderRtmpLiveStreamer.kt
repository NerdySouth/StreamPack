/*
 * Copyright (C) 2022 Thibault B.
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
package com.github.thibaultbee.streampack.ext.rtmp.streamers

import android.Manifest
import android.app.Service
import android.content.Context
import androidx.annotation.RequiresPermission
import com.github.thibaultbee.streampack.data.AudioConfig
import com.github.thibaultbee.streampack.data.VideoConfig
import com.github.thibaultbee.streampack.ext.rtmp.internal.endpoints.RtmpProducer
import com.github.thibaultbee.streampack.internal.muxers.flv.FlvMuxer
import com.github.thibaultbee.streampack.listeners.OnConnectionListener
import com.github.thibaultbee.streampack.logger.ILogger
import com.github.thibaultbee.streampack.logger.StreamPackLogger
import com.github.thibaultbee.streampack.streamers.bases.BaseScreenRecorderStreamer
import com.github.thibaultbee.streampack.streamers.interfaces.IRtmpLiveStreamer
import com.github.thibaultbee.streampack.streamers.interfaces.builders.IStreamerBuilder
import java.net.SocketException

/**
 * [BaseScreenRecorderStreamer] that sends audio/video frames to a remote device with RTMP Protocol.
 * To run this streamer while application is on background, you will have to create a [Service].
 * As an example, check for `screenrecorder`.
 *
 * @param context application context
 * @param logger a [ILogger] implementation
 * @param enableAudio [Boolean.true] to also capture audio. False to disable audio capture.
 */
class ScreenRecorderRtmpLiveStreamer(
    context: Context,
    logger: ILogger,
    enableAudio: Boolean
) : BaseScreenRecorderStreamer(
    context = context,
    muxer = FlvMuxer(context = context, writeToFile = false),
    endpoint = RtmpProducer(logger = logger),
    logger = logger,
    enableAudio = enableAudio
),
    IRtmpLiveStreamer {

    /**
     * Listener to manage RTMP connection.
     */
    override var onConnectionListener: OnConnectionListener? = null
        set(value) {
            rtmpProducer.onConnectionListener = value
            field = value
        }

    private val rtmpProducer = endpoint as RtmpProducer

    /**
     * Connect to an RTMP server.
     * To avoid creating an unresponsive UI, do not call on main thread.
     *
     * @param url server url
     * @throws Exception if connection has failed or configuration has failed
     */
    override suspend fun connect(url: String) {
        rtmpProducer.connect(url)
    }

    /**
     * Disconnect from the connected RTMP server.
     *
     * @throws SocketException is not connected
     */
    override fun disconnect() {
        rtmpProducer.disconnect()
    }

    /**
     * Connect to an RTMP server and start stream.
     * Same as calling [connect], then [startStream].
     * To avoid creating an unresponsive UI, do not call on main thread.
     *
     * @param url server url
     * @throws Exception if connection has failed or configuration has failed or [startStream] has failed too.
     */
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override suspend fun startStream(url: String) {
        connect(url)
        startStream()
    }

    /**
     * Builder class for [ScreenRecorderRtmpLiveStreamer] objects. Use this class to configure and create an [ScreenRecorderRtmpLiveStreamer] instance.
     */
    data class Builder(
        private var logger: ILogger = StreamPackLogger(),
        private var audioConfig: AudioConfig? = null,
        private var videoConfig: VideoConfig? = null,
        private var enableAudio: Boolean = true,
    ) : IStreamerBuilder {
        private lateinit var context: Context

        /**
         * Set application context. It is mandatory to set context.
         *
         * @param context application context.
         */
        override fun setContext(context: Context) = apply { this.context = context }

        /**
         * Set logger.
         *
         * @param logger [ILogger] implementation
         */
        override fun setLogger(logger: ILogger) = apply { this.logger = logger }

        /**
         * Set both audio and video configuration.
         * Configurations can be change later with [configure].
         * Same as calling both [setAudioConfiguration] and [setVideoConfiguration].
         *
         * @param audioConfig audio configuration
         * @param videoConfig video configuration
         */
        override fun setConfiguration(audioConfig: AudioConfig, videoConfig: VideoConfig) = apply {
            this.audioConfig = audioConfig
            this.videoConfig = videoConfig
        }

        /**
         * Set audio configurations.
         * Configurations can be change later with [configure].
         *
         * @param audioConfig audio configuration
         */
        override fun setAudioConfiguration(audioConfig: AudioConfig) = apply {
            this.audioConfig = audioConfig
        }

        /**
         * Set video configurations.
         * Configurations can be change later with [configure].
         *
         * @param videoConfig video configuration
         */
        override fun setVideoConfiguration(videoConfig: VideoConfig) = apply {
            this.videoConfig = videoConfig
        }

        /**
         * Disable audio.
         * Audio is enabled by default.
         * When audio is disabled, there is no way to enable it again.
         */
        override fun disableAudio() = apply {
            this.enableAudio = false
        }

        /**
         * Combines all of the characteristics that have been set and return a new [ScreenRecorderRtmpLiveStreamer] object.
         *
         * @return a new [ScreenRecorderRtmpLiveStreamer] object
         */
        @RequiresPermission(allOf = [Manifest.permission.RECORD_AUDIO])
        override fun build(): ScreenRecorderRtmpLiveStreamer {
            return ScreenRecorderRtmpLiveStreamer(
                context,
                logger,
                enableAudio
            ).also { streamer ->
                if (videoConfig != null) {
                    streamer.configure(audioConfig, videoConfig!!)
                }
            }
        }
    }
}