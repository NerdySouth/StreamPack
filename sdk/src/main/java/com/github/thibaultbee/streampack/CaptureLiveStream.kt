package com.github.thibaultbee.streampack

import android.Manifest
import android.content.Context
import android.view.Surface
import androidx.annotation.RequiresPermission
import com.github.thibaultbee.streampack.data.AudioConfig
import com.github.thibaultbee.streampack.data.Frame
import com.github.thibaultbee.streampack.data.VideoConfig
import com.github.thibaultbee.streampack.encoders.AudioMediaCodecEncoder
import com.github.thibaultbee.streampack.encoders.IEncoderListener
import com.github.thibaultbee.streampack.encoders.VideoMediaCodecEncoder
import com.github.thibaultbee.streampack.endpoints.IEndpoint
import com.github.thibaultbee.streampack.listeners.OnErrorListener
import com.github.thibaultbee.streampack.muxers.IMuxerListener
import com.github.thibaultbee.streampack.muxers.ts.TSMuxer
import com.github.thibaultbee.streampack.muxers.ts.data.ServiceInfo
import com.github.thibaultbee.streampack.sources.AudioCapture
import com.github.thibaultbee.streampack.sources.CameraCapture
import com.github.thibaultbee.streampack.utils.Error
import com.github.thibaultbee.streampack.utils.EventHandlerManager
import com.github.thibaultbee.streampack.utils.Logger
import java.nio.ByteBuffer

class CaptureLiveStream(
    context: Context,
    private val tsServiceInfo: ServiceInfo,
    private val endpoint: IEndpoint,
    val logger: Logger
) : EventHandlerManager() {
    override var onErrorListener: OnErrorListener? = null

    private var audioTsStreamId: Short? = null
    private var videoTsStreamId: Short? = null

    // Keep video configuration
    private var videoConfig: VideoConfig? = null
    private var audioConfig: AudioConfig? = null

    private var baseTimestamp = -1L

    private val onCodecErrorListener = object : OnErrorListener {
        override fun onError(name: String, type: Error) {
            stopStream()
            onErrorListener?.onError(name, type)
        }
    }

    private val onCaptureErrorListener = object : OnErrorListener {
        override fun onError(name: String, type: Error) {
            stopStream()
            stopCapture()
            onErrorListener?.onError(name, type)
        }
    }

    private val audioEncoderListener = object : IEncoderListener {
        override fun onInputFrame(buffer: ByteBuffer): Frame {
            return audioSource.getFrame(buffer)
        }

        override fun onOutputFrame(frame: Frame) {
            if (baseTimestamp == -1L) {
                baseTimestamp = frame.pts
            }

            frame.pts -= baseTimestamp
            frame.dts?.let { it - baseTimestamp }

            audioTsStreamId?.let {
                try {
                    tsMux.encode(frame, it)
                } catch (e: Exception) {
                    reportError(e)
                }
            }
        }
    }

    private val videoEncoderListener = object : IEncoderListener {
        override fun onInputFrame(buffer: ByteBuffer): Frame {
            // Not needed for video
            throw RuntimeException("No video input on VideoEncoder")
        }

        override fun onOutputFrame(frame: Frame) {
            if (baseTimestamp == -1L) {
                baseTimestamp = frame.pts
            }

            frame.pts -= baseTimestamp
            frame.dts?.let { it - baseTimestamp }

            videoTsStreamId?.let {
                try {
                    tsMux.encode(frame, it)
                } catch (e: Exception) {
                    reportError(e)
                }
            }
        }
    }

    private val muxListener = object : IMuxerListener {
        override fun onOutputFrame(buffer: ByteBuffer) {
            try {
                endpoint.write(buffer)
            } catch (e: Exception) {
                stopStream()
                reportConnectionLost()
            }
        }
    }

    private val audioSource = AudioCapture(logger)
    val videoSource = CameraCapture(context, onCaptureErrorListener, logger)

    private var audioEncoder =
        AudioMediaCodecEncoder(audioEncoderListener, onCodecErrorListener, logger)
    private var videoEncoder =
        VideoMediaCodecEncoder(videoEncoderListener, onCodecErrorListener, logger)

    private val tsMux = TSMuxer(muxListener)

    @RequiresPermission(Manifest.permission.CAMERA)
    fun configure(audioConfig: AudioConfig, videoConfig: VideoConfig) {
        // Keep settings when we need to reconfigure
        this.videoConfig = videoConfig
        this.audioConfig = audioConfig

        audioSource.configure(audioConfig)
        audioEncoder.configure(audioConfig)
        videoSource.configure(videoConfig.fps)
        videoEncoder.configure(videoConfig)

        endpoint.configure(videoConfig.startBitrate + audioConfig.startBitrate)
    }

    @RequiresPermission(allOf = [Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA])
    fun startCapture(previewSurface: Surface, cameraId: String = "0") {
        require(audioConfig != null)
        require(videoConfig != null)

        videoSource.previewSurface = previewSurface
        videoSource.encoderSurface = videoEncoder.intputSurface
            ?: throw IllegalStateException("Codec surface is null: video codec must be configured")
        videoSource.startPreview(cameraId)

        audioSource.startStream()
    }

    fun stopCapture() {
        videoSource.stopPreview()
        audioSource.stopStream()
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    fun changeVideoSource(cameraId: String = "0") {
        videoSource.stopPreview()
        videoSource.startPreview(cameraId)
    }

    @Throws
    fun startStream() {
        require(audioConfig != null)
        require(videoConfig != null)
        require(videoEncoder.mimeType != null)
        require(audioEncoder.mimeType != null)

        baseTimestamp = -1L

        endpoint.startStream()

        val streams = mutableListOf<String>()
        videoEncoder.mimeType?.let { streams.add(it) }
        audioEncoder.mimeType?.let { streams.add(it) }

        tsMux.addService(tsServiceInfo)
        tsMux.addStreams(tsServiceInfo, streams)
        videoEncoder.mimeType?.let { videoTsStreamId = tsMux.getStreams(it)[0].pid }
        audioEncoder.mimeType?.let { audioTsStreamId = tsMux.getStreams(it)[0].pid }

        audioEncoder.startStream()
        videoSource.startStream()
        videoEncoder.startStream()
    }

    @RequiresPermission(allOf = [Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA])
    fun stopStream() {
        videoSource.stopStream()
        videoEncoder.stopStream()
        audioEncoder.stopStream()

        tsMux.stop()

        endpoint.stopStream()

        // Encoder does not return to CONFIGURED state... so we have to reset everything for video...
        resetAudio()
        resetVideo()
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    private fun resetAudio(): Error {
        require(audioConfig != null)

        audioEncoder.release()

        // Reconfigure
        audioEncoder.configure(audioConfig!!)
        return Error.SUCCESS
    }

    @RequiresPermission(Manifest.permission.CAMERA)
    private fun resetVideo() {
        require(videoConfig != null)

        videoSource.stopPreview()
        videoEncoder.release()

        // And restart...
        videoEncoder.configure(videoConfig!!)
        val encoderSurface = videoEncoder.intputSurface
            ?: throw InvalidParameterException("Surface can't be null")
        videoSource.encoderSurface = encoderSurface
        videoSource.startPreview()
    }

    fun release() {
        audioEncoder.release()
        videoEncoder.release()
        audioSource.release()
        videoSource.release()
        endpoint.release()
    }
}