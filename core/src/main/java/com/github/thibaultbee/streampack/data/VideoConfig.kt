/*
 * Copyright (C) 2021 Thibault B.
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
package com.github.thibaultbee.streampack.data

import android.content.Context
import android.media.MediaCodecInfo.CodecProfileLevel
import android.media.MediaFormat
import android.os.Build
import android.util.Size
import com.github.thibaultbee.streampack.internal.utils.isPortrait
import com.github.thibaultbee.streampack.internal.utils.isVideo
import com.github.thibaultbee.streampack.streamers.bases.BaseStreamer
import com.github.thibaultbee.streampack.streamers.helpers.IConfigurationHelper
import com.github.thibaultbee.streampack.streamers.helpers.IVideoConfigurationHelper
import java.io.IOException

/**
 * Video configuration class.
 * If you don't know how to set class members, [Video encoding recommendations](https://developer.android.com/guide/topics/media/media-formats#video-encoding) should give you hints.
 *
 * @see [BaseStreamer.configure]
 */
class VideoConfig(
    /**
     * Video encoder mime type.
     * Only [MediaFormat.MIMETYPE_VIDEO_AVC] is supported yet.
     *
     * **See Also:** [MediaFormat MIMETYPE_VIDEO_*](https://developer.android.com/reference/android/media/MediaFormat)
     */
    mimeType: String,
    /**
     * Video encoder bitrate in bits/s.
     */
    startBitrate: Int,
    /**
     * Video output resolution in pixel.
     */
    val resolution: Size,
    /**
     * Video framerate.
     * This is a best effort as few camera can not generate a fixed framerate.
     */
    val fps: Int,
    /**
     * Video encoder profile. Encoders may not support requested profile. In this case, StreamPack fallbacks to default profile.
     * ** See ** [MediaCodecInfo.CodecProfileLevel](https://developer.android.com/reference/android/media/MediaCodecInfo.CodecProfileLevel)
     */
    val profile: Int,
    /**
     * Video encoder level. Encoders may not support requested level. In this case, StreamPack fallbacks to default level.
     * ** See ** [MediaCodecInfo.CodecProfileLevel](https://developer.android.com/reference/android/media/MediaCodecInfo.CodecProfileLevel)
     */
    val level: Int,
) : Config(mimeType, startBitrate) {
    init {
        require(mimeType.isVideo()) { "MimeType must be video" }
    }

    /**
     * Get resolution according to device orientation
     *
     * @param context activity context
     * @return oriented resolution
     */
    fun getOrientedResolution(context: Context): Size {
        return if (context.isPortrait()) {
            Size(resolution.height, resolution.width)
        } else {
            Size(resolution.width, resolution.height)
        }
    }

    /**
     * Builder class for [VideoConfig] objects. Use this class to configure and create an [VideoConfig] instance.
     */
    data class Builder(
        private var mimeType: String = MediaFormat.MIMETYPE_VIDEO_AVC,
        private var startBitrate: Int = 2000000,
        private var resolution: Size = Size(1280, 720),
        private var fps: Int = 30,
        private var profile: Int = getDefaultProfile(mimeType),
        private var level: Int = getDefaultLevel(mimeType)
    ) {
        companion object {
            private fun getDefaultProfile(mimeType: String) =
                when (mimeType) {
                    MediaFormat.MIMETYPE_VIDEO_AVC -> CodecProfileLevel.AVCProfileHigh
                    MediaFormat.MIMETYPE_VIDEO_HEVC -> CodecProfileLevel.HEVCProfileMain
                    else -> throw IOException("Not supported mime type: $mimeType")
                }


            private fun getDefaultLevel(mimeType: String) = when (mimeType) {
                MediaFormat.MIMETYPE_VIDEO_AVC -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    CodecProfileLevel.AVCLevel62
                } else {
                    CodecProfileLevel.AVCLevel52
                }
                MediaFormat.MIMETYPE_VIDEO_HEVC -> CodecProfileLevel.HEVCMainTierLevel62
                else -> throw IOException("Not supported mime type: $mimeType")
            }
        }

        /**
         * Set video encoder mime type.
         * Get supported video encoder mime type with [IVideoConfigurationHelper.supportedEncoders] with [IConfigurationHelper.video].
         * Warning: It resets profile and level.
         *
         * @param mimeType video encoder mime type from [MediaFormat MIMETYPE_VIDEO_*](https://developer.android.com/reference/android/media/MediaFormat)
         */
        fun setMimeType(mimeType: String) = apply {
            this.mimeType = mimeType
            this.profile = getDefaultProfile(mimeType)
            this.level = getDefaultLevel(mimeType)
        }

        /**
         * Set video encoder bitrate.
         *
         * @param startBitrate video encoder bitrate in bits/s.
         */
        fun setStartBitrate(startBitrate: Int) = apply { this.startBitrate = startBitrate }

        /**
         * Set video resolution.
         *
         * @param resolution video resolution
         */
        fun setResolution(resolution: Size) = apply { this.resolution = resolution }

        /**
         * Set video frame rate.
         *
         * @param fps video frame rate
         */
        fun setFps(fps: Int) = apply { this.fps = fps }

        /**
         * Set encoder profile.
         * Warning: It is reset by [setMimeType]. Always call [setEncoderProfile] after [setMimeType].
         *
         * @param profile encoder profile from [MediaCodecInfo.CodecProfileLevel](https://developer.android.com/reference/android/media/MediaCodecInfo.CodecProfileLevel)
         */
        fun setEncoderProfile(profile: Int) = apply { this.profile = profile }

        /**
         * Set encoder level.
         * Warning: It is reset by [setMimeType]. Always call [setEncoderLevel] after [setMimeType].
         *
         * @param level encoder level from [MediaCodecInfo.CodecProfileLevel](https://developer.android.com/reference/android/media/MediaCodecInfo.CodecProfileLevel)
         */
        fun setEncoderLevel(level: Int) = apply { this.level = level }

        /**
         * Combines all of the characteristics that have been set and return a new [VideoConfig] object.
         *
         * @return a new [VideoConfig] object
         */
        fun build() =
            VideoConfig(mimeType, startBitrate, resolution, fps, profile, level)
    }
}
