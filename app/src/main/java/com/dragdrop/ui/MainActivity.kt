package com.dragdrop.ui

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dragdrop.R
import com.dragdrop.data.network.RetroConnect
import com.dragdrop.databinding.MainActivityBinding
import kotlinx.android.synthetic.main.main_activity.draggedTimeFrame
import kotlinx.android.synthetic.main.main_activity.dropPoint
import kotlinx.android.synthetic.main.main_activity.dropText
import kotlinx.android.synthetic.main.main_activity.squareFrame
import kotlinx.android.synthetic.main.main_activity.toolbar

class MainActivity : AppCompatActivity() {

  private val viewModel: MainViewModel by lazy {
    ViewModelProviders.of(this, MainViewModelFactory(RetroConnect.getTimeApi()))
      .get(MainViewModel::class.java)
  }

  private var dragX: Float = 0.toFloat()
  private var dragY: Float = 0.toFloat()
  private var dX: Float = 0.toFloat()
  private var dY: Float = 0.toFloat()

  private var originalDropPointHeight = 0

  private var isAnimationExecuted = false
  private var isDragging = false
  private var isDragged = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setupViewModel(DataBindingUtil.setContentView(this, R.layout.main_activity))
    setSupportActionBar(toolbar)
    viewModel.loadTime()
  }

  private fun setupViewModel(viewBinding: MainActivityBinding) {
    viewBinding.apply {
      lifecycleOwner = this@MainActivity
      viewModel = this@MainActivity.viewModel
    }
    observeData()
  }

  private fun observeData() {
    viewModel.time.observe(this, Observer {
      if (!isAnimationExecuted) {
        rotateSquare()
        startDragAndDrop()
        isAnimationExecuted = true
      }
    })
  }

  private fun rotateSquare() {
    squareFrame.animate().apply {
      duration = 1000
      rotation(90.0f)
      setListener(object : AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
          setListener(null)
          squareFrame.rotation = 0.0f
          duration = 1000
          rotation(90.0f)
          setListener(this)
          start()
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }
      })
    }.start()
  }

  private fun startDragAndDrop() {
    draggedTimeFrame.setOnTouchListener { view, event ->
      when (event.action) {
        MotionEvent.ACTION_DOWN -> {
          scaleSquareALittleBit(view)
          setDimens(view, event)
          animateDropPoint(draggedTimeFrame.height)
          return@setOnTouchListener true
        }
        MotionEvent.ACTION_MOVE -> {
          animateSquareMovement(view, event)
          return@setOnTouchListener true
        }
        MotionEvent.ACTION_UP -> {
          resetSquareScale(view)
          animateDropPoint(originalDropPointHeight)
          returnBackViewToOriginalPosition(view)
          placeViewIntoNewPositionAndHidePlaceHolder(view)
          return@setOnTouchListener true
        }

      }
      return@setOnTouchListener false
    }
  }

  private fun scaleSquareALittleBit(view: View) {
    view.animate()
      .scaleX(0.9f)
      .scaleY(0.9f)
      .setDuration(200)
      .start()
  }

  private fun resetSquareScale(view: View) {
    view.animate()
      .scaleX(1.0f)
      .scaleY(1.0f)
      .setDuration(200)
      .start()
  }

  private fun setDimens(view: View, event: MotionEvent) {
    if (!isDragged) {
      originalDropPointHeight = dropText.height
      dragX = view.x
      dragY = view.y
    }
    dX = view.x - event.rawX
    dY = view.y - event.rawY
  }

  private fun animateDropPoint(height: Int) {
    val anim2 = ValueAnimator.ofInt(dropPoint.measuredHeight, height)
    anim2.addUpdateListener { valueAnimator ->
      val value = valueAnimator.animatedValue as Int
      val layoutParams = dropPoint.layoutParams
      layoutParams.height = value
      dropPoint.layoutParams = layoutParams
    }
    anim2.duration = 300
    anim2.start()
  }

  private fun animateSquareMovement(view: View, event: MotionEvent) {
    isDragging = true
    isDragged = true
    dropText.visibility = View.VISIBLE
    view.animate()
      .x(event.rawX + dX)
      .y(event.rawY + dY)
      .scaleX(0.9f)
      .scaleY(0.9f)
      .setDuration(0)
      .start()
  }

  private fun returnBackViewToOriginalPosition(view: View) {
    if (isDragging) {
      view.animate()
        .x(dragX)
        .y(dragY)
        .scaleX(1.0f)
        .scaleY(1.0f)
        .setDuration(200)
        .start()
      isDragging = false
    }
  }

  private fun placeViewIntoNewPositionAndHidePlaceHolder(view: View) {
    if (view.y >= dropPoint.y) {
      animateDropPoint(draggedTimeFrame.height)
      draggedTimeFrame.animate()
        .x(dropPoint.x)
        .y(dropPoint.y)
        .translationX(dropPoint.x / 2)
        .scaleX(1.0f)
        .scaleY(1.0f)
        .setDuration(200)
        .start()
      dropText.visibility = View.INVISIBLE
    }
  }
}
