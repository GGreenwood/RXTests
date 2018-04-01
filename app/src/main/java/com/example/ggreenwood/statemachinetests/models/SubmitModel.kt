package com.example.ggreenwood.statemachinetests.models

/**
 * Created by Garrett Greenwood on 3/29/18.
 */
sealed class SubmitModel(
        val enabled: Boolean,
        val inProgress: Boolean,
        val success: Boolean,
        val errorMessage: String
) {
    class Enabled: SubmitModel(true, false, false, "")
    class Disabled: SubmitModel(false, false, false, "")
    class InProgress: SubmitModel(false, true, false, "")
    class Success: SubmitModel(true, false, true, "")
    class Failure(errorMessage: String): SubmitModel(true, false, false, "")
}