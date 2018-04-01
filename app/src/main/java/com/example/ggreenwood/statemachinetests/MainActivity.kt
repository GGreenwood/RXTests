package com.example.ggreenwood.statemachinetests

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Switch
import com.example.ggreenwood.statemachinetests.events.ButtonUiEvent
import com.example.ggreenwood.statemachinetests.events.UiEvent
import com.example.ggreenwood.statemachinetests.events.ToggleUiEvent
import com.example.ggreenwood.statemachinetests.models.SubmitModel
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.ObservableTransformer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create the UI State object
        var submitModel = SubmitModel.Enabled()

        // Bind to the views
        val switchView = findViewById<Switch>(R.id.switch1)
        val buttonView = findViewById<Button>(R.id.button)
        val progressView = findViewById<ProgressBar>(R.id.progressBar)

        // Listen to user actions and create UiEvents
        val toggleEvents: Observable<ToggleUiEvent> = RxView.clicks(switchView).map({ ToggleUiEvent(switchView.isChecked) })
        val buttonEvents: Observable<ButtonUiEvent> = RxView.clicks(buttonView).map({ ButtonUiEvent() })

        // Merge all UI Events into a single stream
        val events: Observable<UiEvent> = Observable.merge(toggleEvents, buttonEvents)

        // Pass events to the proper transformers and update UI
        events.compose(submitUi()).subscribe({model ->
            buttonView.isEnabled = model.enabled
            when(model) {
                is SubmitModel.Enabled -> {
                    buttonView.isEnabled = model.enabled
                    switchView.isChecked = model.enabled
                }
                is SubmitModel.Disabled -> buttonView.isEnabled = model.enabled
                is SubmitModel.InProgress -> buttonView.isEnabled = model.enabled
                is SubmitModel.Success -> buttonView.isEnabled = model.enabled
                is SubmitModel.Failure -> buttonView.isEnabled = model.enabled
            }
        })
    }

    private fun submitUi(): ObservableTransformer<UiEvent, SubmitModel> {
        val toggleSubmit = ObservableTransformer<ToggleUiEvent, SubmitModel>  {
            it.map { switchEvent ->
                if(switchEvent.toggleValue)
                    SubmitModel.Enabled()
                else
                    SubmitModel.Disabled()
            }
        }

        val buttonPress = ObservableTransformer<ButtonUiEvent, SubmitModel> {
            it.map {SubmitModel.Success() }
        }

        return ObservableTransformer<UiEvent, SubmitModel> {
            events -> events.publish({
            it -> Observable.merge(
                it.ofType(ToggleUiEvent::class.java).compose(toggleSubmit),
                it.ofType(ButtonUiEvent::class.java).compose(buttonPress))
        })
        }
    }
}
