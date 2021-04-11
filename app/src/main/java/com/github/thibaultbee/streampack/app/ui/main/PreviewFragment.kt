package com.github.thibaultbee.streampack.app.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.thibaultbee.streampack.app.databinding.MainFragmentBinding
import com.github.thibaultbee.streampack.app.utils.DialogUtils
import com.github.thibaultbee.streampack.app.utils.PreviewUtils.Companion.chooseBigEnoughSize
import com.github.thibaultbee.streampack.listeners.OnConnectionListener
import com.github.thibaultbee.streampack.listeners.OnErrorListener
import com.github.thibaultbee.streampack.utils.Error
import com.github.thibaultbee.streampack.utils.getOutputSizes
import com.jakewharton.rxbinding4.view.clicks
import com.tbruyelle.rxpermissions3.RxPermissions
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit


class PreviewFragment : Fragment() {
    private val fragmentDisposables = CompositeDisposable()
    private lateinit var binding: MainFragmentBinding

    private val viewModel: PreviewViewModel by lazy {
        ViewModelProvider(this).get(PreviewViewModel::class.java)
    }

    private val rxPermissions: RxPermissions by lazy { RxPermissions(this) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        bindProperties()
        return binding.root
    }

    @SuppressLint("MissingPermission")
    private fun bindProperties() {
        binding.liveButton.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .compose(
                rxPermissions.ensureEachCombined(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
            .subscribe { permission ->
                if (!permission.granted) {
                    binding.liveButton.isChecked = false
                    context?.let { DialogUtils.showPermissionAlertDialog(it) }
                } else {
                    if (binding.liveButton.isChecked) {
                        try {
                            viewModel.startStream()
                        } catch (e: Exception) {
                            Log.e(this::class.java.simpleName, "Oops", e)
                            viewModel.stopStream()
                            binding.liveButton.isChecked = false
                            context?.let { it ->
                                DialogUtils.showAlertDialog(
                                    it,
                                    e.javaClass.simpleName,
                                    e.message ?: ""
                                )
                            }
                        }
                    } else {
                        viewModel.stopStream()
                    }
                }
            }
            .let(fragmentDisposables::add)

        binding.switchButton.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .throttleFirst(3000, TimeUnit.MILLISECONDS)
            .compose(rxPermissions.ensure(Manifest.permission.CAMERA))
            .subscribe { granted ->
                if (!granted) {
                    context?.let { DialogUtils.showPermissionAlertDialog(it) }
                } else {
                    viewModel.toggleVideoSource()
                }
            }
            .let(fragmentDisposables::add)
    }

    override fun onResume() {
        super.onResume()

        viewModel.buildStreamer()
        viewModel.captureLiveStream.onErrorListener = object : OnErrorListener {
            override fun onError(name: String, type: Error) {
                DialogUtils.showAlertDialog(requireContext(), "Error", "$type on $name")
            }
        }

        viewModel.captureLiveStream.onConnectionListener = object : OnConnectionListener {
            override fun onLost() {
                DialogUtils.showAlertDialog(requireContext(), "Connection Lost")
                binding.liveButton.isChecked = false
            }
        }

        binding.surfaceView.holder.addCallback(surfaceViewCallback)
    }

    override fun onStop() {
        super.onStop()
        binding.surfaceView.holder.setFixedSize(
            0,
            0
        ) // Ensure to trigger surface holder callback on resume
    }


    override fun onDestroy() {
        super.onDestroy()
        fragmentDisposables.clear()
    }

    @SuppressLint("MissingPermission")
    private val surfaceViewCallback = object : SurfaceHolder.Callback {
        var nbOnSurfaceChange = 0

        override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
            require(context != null)

            rxPermissions
                .requestEachCombined(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                .subscribe { permission ->
                    if (!permission.granted) {
                        context?.let { DialogUtils.showPermissionAlertDialog(it) }
                    } else {
                        holder?.let {
                            nbOnSurfaceChange++
                            if (nbOnSurfaceChange == 2) {
                                viewModel.startCapture(holder.surface)
                            } else {
                                val choices = context!!.getOutputSizes(
                                    SurfaceHolder::class.java,
                                    viewModel.cameraId
                                )

                                chooseBigEnoughSize(choices, width, height)?.let { size ->
                                    holder.setFixedSize(size.width, size.height)
                                }
                            }
                        }
                    }
                }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder?) {
            viewModel.stopCapture()
            binding.surfaceView.holder.removeCallback(this)
        }

        override fun surfaceCreated(holder: SurfaceHolder?) {
            nbOnSurfaceChange = 0
        }
    }
}