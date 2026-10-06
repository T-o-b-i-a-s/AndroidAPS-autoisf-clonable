package app.aaps.activities

import android.content.Intent
import android.os.Bundle
import app.aaps.MainActivity
import app.aaps.plugins.configuration.activities.DaggerAppCompatActivityWithResult

/**
 * Entry point for external apps that want to open the Bolus Wizard
 * with pre-filled carbs. Forwards to MainActivity which then shows
 * the WizardDialog. The user must confirm the bolus through the
 * standard AAPS confirmation flow.
 *
 * Intent contract:
 *   action: info.nightscout.androidaps.action.OPEN_BOLUS_WIZARD
 *   extras:
 *     carbs  (int,    required, 1..80, grams)
 *     notes  (string, optional)
 *     source (string, optional, free-form caller tag)
 */
class WizardLaunchActivity : DaggerAppCompatActivityWithResult() {

    companion object {
        const val EXTRA_CARBS    = "carbs"
        const val EXTRA_NOTES    = "notes"
        const val EXTRA_SOURCE   = "source"
        const val INTERNAL_CARBS = "open_wizard_carbs"
        const val INTERNAL_NOTES = "open_wizard_notes"

        // TODO: move to Inter-app settings preference in a follow-up PR
        private val ALLOWED_CALLERS = setOf(
            "de.be10.carbcam",
            "de.be10.carbcam.debug"
            // additional packages can be added here or via Preferences later
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val caller = callingPackage ?: referrer?.host
        if (caller !in ALLOWED_CALLERS) { finish(); return }

        val carbs = intent.getIntExtra(EXTRA_CARBS, 0)
        val notes = intent.getStringExtra(EXTRA_NOTES) ?: ""
        if (carbs <= 0 || carbs > 80) { finish(); return }

        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra(INTERNAL_CARBS, carbs)
            putExtra(INTERNAL_NOTES, notes)
        })
        finish()
    }
