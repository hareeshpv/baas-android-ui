package com.payu.baas.coreUI.view.ui.snackbaar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.payu.baas.coreUI.R
import com.payu.baas.coreUI.view.callback.SnackBarListener

class SimpleCustomSnackbar(parent: ViewGroup, content: SimpleCustomSnackbarView) :
    BaseTransientBottomBar<SimpleCustomSnackbar>(parent, content, content) {
    init {
        getView().setBackgroundColor(
            ContextCompat.getColor(
                view.context,
                android.R.color.transparent
            )
        )
        getView().setPadding(0, 0, 0, 0)
    }

    companion object {
        fun make(
            view: View,
            message: String, duration: Int, icon: Int
        ): SimpleCustomSnackbar? {
            // First we find a suitable parent for our custom view
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            // We inflate our custom view
            try {
                val customView = LayoutInflater.from(view.context).inflate(
                    R.layout.snackbar_layout,
                    parent,
                    false
                ) as SimpleCustomSnackbarView
                // We create and return our Snackbar
                customView.tvMsg.text = message
                customView.imLeft.setImageResource(icon)

                return  SimpleCustomSnackbar(
                    parent,
                    customView
                ).setDuration(duration)
            } catch (e: Exception) {
            }

            return null
        }

    }

    object resendOTPSnackBar {
        fun make(
            view: View,
            message: String, duration: Int
        ): SimpleCustomSnackbar? {
            // First we find a suitable parent for our custom view
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            // We inflate our custom view
            try {
                val customView = LayoutInflater.from(view.context).inflate(
                    R.layout.snackbar_layout,
                    parent,
                    false
                ) as SimpleCustomSnackbarView
                // We create and return our Snackbar
                customView.tvMsg.text = message
//                customView.imLeft.setImageResource(icon)
                customView.tic_black_background_icon.visibility = View.VISIBLE
                customView.imLeft.visibility = View.GONE
                customView.tvUndo.visibility = View.GONE
                customView.tvReTry.visibility = View.GONE

                return SimpleCustomSnackbar(
                    parent,
                    customView
                ).setDuration(duration)
            } catch (e: Exception) {
            }

            return null
        }

    }
    object addressChange{
        fun make(
            view: View,
            message: String, duration: Int
        ): SimpleCustomSnackbar? {
            // First we find a suitable parent for our custom view
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            // We inflate our custom view
            try {
                val customView = LayoutInflater.from(view.context).inflate(
                    R.layout.snackbar_layout,
                    parent,
                    false
                ) as SimpleCustomSnackbarView
                // We create and return our Snackbar
                customView.tvMsg.text = message
//                customView.imLeft.setImageResource(icon)
                customView.tic_black_background_icon.visibility = View.VISIBLE
                customView.imLeft.visibility = View.GONE
                customView.tvUndo.visibility = View.GONE
                customView.tvReTry.visibility = View.GONE

                return SimpleCustomSnackbar(
                    parent,
                    customView
                ).setDuration(duration)
            } catch (e: Exception) {
            }

            return null
        }

    }
    object passcodeChange{
        fun make(
            view: View,
            message: String, duration: Int
        ): SimpleCustomSnackbar? {
            // First we find a suitable parent for our custom view
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            // We inflate our custom view
            try {
                val customView = LayoutInflater.from(view.context).inflate(
                    R.layout.snackbar_layout,
                    parent,
                    false
                ) as SimpleCustomSnackbarView
                // We create and return our Snackbar
                customView.tvMsg.text = message
//                customView.imLeft.setImageResource(icon)
                customView.tic_black_background_icon.visibility = View.VISIBLE
                customView.imLeft.visibility = View.GONE
                customView.tvUndo.visibility = View.GONE
                customView.tvReTry.visibility = View.GONE

                return SimpleCustomSnackbar(
                    parent,
                    customView
                ).setDuration(duration)
            } catch (e: Exception) {
            }

            return null
        }

    }
    object showSwitchMsg {
        fun make(
            view: View,
            message: String, duration: Int, isSuccess: Boolean
        ): SimpleCustomSnackbar? {
            // First we find a suitable parent for our custom view
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            // We inflate our custom view
            try {
                val customView = LayoutInflater.from(view.context).inflate(
                    R.layout.snackbar_layout,
                    parent,
                    false
                ) as SimpleCustomSnackbarView
                // We create and return our Snackbar
                customView.tvMsg.text = message

                customView.tic_black_background_icon.visibility = View.GONE
                customView.imLeft.visibility = View.GONE
                customView.tvUndo.visibility = View.GONE
                customView.tvReTry.visibility = View.GONE
                if (isSuccess) {
                    customView.tic_black_background_icon.visibility = View.VISIBLE
                } else {
                    customView.imLeft.visibility = View.VISIBLE
                }
                val snack = SimpleCustomSnackbar(
                    parent,
                    customView
                ).setDuration(duration)
                return snack
            } catch (e: Exception) {
            }

            return null
        }

    }

    object undoItems {
        fun make(
            view: View,
            message: String, duration: Int,
            listener: SnackBarListener
        ): SimpleCustomSnackbar? {
            // First we find a suitable parent for our custom view
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            // We inflate our custom view
            try {
                val customView = LayoutInflater.from(view.context).inflate(
                    R.layout.snackbar_layout,
                    parent,
                    false
                ) as SimpleCustomSnackbarView
                // We create and return our Snackbar
                customView.tvMsg.text = message
                customView.tic_black_background_icon.visibility = View.GONE
                customView.imLeft.visibility = View.GONE
                customView.tvUndo.visibility = View.VISIBLE
                customView.tvReTry.visibility = View.GONE
                customView.listener = listener
                customView.tvUndo.setOnClickListener {
                    listener.undo()
                }
                return SimpleCustomSnackbar(
                    parent,
                    customView
                ).setDuration(duration)
            } catch (e: Exception) {
            }

            return null
        }
    }

    object retry {
        fun make(
            view: View,
            message: String, duration: Int,
            listener: SnackBarListener
        ): SimpleCustomSnackbar? {
            // First we find a suitable parent for our custom view
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )

            // We inflate our custom view
            try {
                val customView = LayoutInflater.from(view.context).inflate(
                    R.layout.snackbar_layout,
                    parent,
                    false
                ) as SimpleCustomSnackbarView
                // We create and return our Snackbar
                customView.tvMsg.text = message
                customView.tic_black_background_icon.visibility = View.GONE
                customView.imLeft.visibility = View.GONE
                customView.tvUndo.visibility = View.GONE
                customView.tvReTry.visibility = View.VISIBLE
                customView.listener = listener
                customView.tvReTry.setOnClickListener {
                    listener.undo()
                }
                return SimpleCustomSnackbar(
                    parent,
                    customView
                ).setDuration(duration)
            } catch (e: Exception) {
            }

            return null
        }
    }
}