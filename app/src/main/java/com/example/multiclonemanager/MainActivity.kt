package com.example.multiclonemanager

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.view.Gravity
import android.view.View
import android.widget.*
import org.json.JSONArray
import org.json.JSONObject

data class Profile(val app: String, val label: String)

class MainActivity : Activity() {
    private lateinit var prefs: SharedPreferences
    private lateinit var list: LinearLayout
    private lateinit var count: TextView
    private val apps = listOf(
        "WhatsApp" to "com.whatsapp",
        "Telegram" to "org.telegram.messenger",
        "Instagram" to "com.instagram.android",
        "Facebook" to "com.facebook.katana",
        "MyJio" to "com.jio.myjio",
        "Airtel Thanks" to "com.myairtelapp",
        "Swiggy" to "in.swiggy.android"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("profiles", MODE_PRIVATE)

        val scroll = ScrollView(this)
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(36, 36, 36, 36)
        }
        scroll.addView(page)

        page.addView(TextView(this).apply {
            text = "Multi Clone Manager"
            textSize = 25f
            setTypeface(null, Typeface.BOLD)
        })
        page.addView(TextView(this).apply {
            text = "Manage account labels and open official installed apps."
            textSize = 14f
            setPadding(0, 8, 0, 20)
        })

        val appSpinner = Spinner(this)
        appSpinner.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, apps.map { it.first }
        )
        page.addView(appSpinner)

        val labelInput = EditText(this).apply {
            hint = "Profile label (e.g. Personal, Work, SIM 2)"
            maxLines = 1
        }
        page.addView(labelInput)

        page.addView(Button(this).apply {
            text = "Add profile"
            setOnClickListener {
                val label = labelInput.text.toString().trim()
                if (label.isEmpty()) {
                    labelInput.error = "Enter a profile label"
                    return@setOnClickListener
                }
                val selected = apps[appSpinner.selectedItemPosition].first
                val rows = readProfiles()
                rows.add(Profile(selected, label))
                saveProfiles(rows)
                labelInput.text.clear()
                renderProfiles()
            }
        })

        count = TextView(this).apply {
            textSize = 16f
            setPadding(0, 22, 0, 10)
        }
        page.addView(count)

        list = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        page.addView(list)

        page.addView(TextView(this).apply {
            text = "\nImportant: Profiles store labels only. They do not create isolated app copies, store OTPs, or bypass sign-in limits. Use Android Dual Apps or Work Profile where supported."
            textSize = 13f
        })

        setContentView(scroll)
        renderProfiles()
    }

    private fun readProfiles(): MutableList<Profile> {
        val result = mutableListOf<Profile>()
        val array = JSONArray(prefs.getString("items", "[]"))
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            result.add(Profile(obj.optString("app"), obj.optString("label")))
        }
        return result
    }

    private fun saveProfiles(rows: List<Profile>) {
        val array = JSONArray()
        rows.forEach {
            array.put(JSONObject().put("app", it.app).put("label", it.label))
        }
        prefs.edit().putString("items", array.toString()).apply()
    }

    private fun renderProfiles() {
        val rows = readProfiles()
        count.text = "Saved profiles: ${rows.size}"
        list.removeAllViews()
        if (rows.isEmpty()) {
            list.addView(TextView(this).apply { text = "No profiles yet." })
        }
        rows.forEachIndexed { index, profile ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(18, 14, 18, 14)
                setBackgroundColor(0xFFF1F3F4.toInt())
            }
            card.addView(TextView(this).apply {
                text = "${profile.app} — ${profile.label}"
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
            })
            val actions = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.END
            }
            actions.addView(Button(this).apply {
                text = "Open app"
                setOnClickListener { launchOfficialApp(profile.app) }
            })
            actions.addView(Button(this).apply {
                text = "Delete"
                setOnClickListener {
                    val updated = readProfiles().toMutableList()
                    updated.removeAt(index)
                    saveProfiles(updated)
                    renderProfiles()
                }
            })
            card.addView(actions)
            val spacer = View(this).apply { minimumHeight = 12 }
            list.addView(card)
            list.addView(spacer)
        }
    }

    private fun launchOfficialApp(appName: String) {
        val pkg = apps.firstOrNull { it.first == appName }?.second ?: return
        val intent: Intent? = packageManager.getLaunchIntentForPackage(pkg)
        if (intent == null) {
            Toast.makeText(this, "$appName is not installed", Toast.LENGTH_SHORT).show()
        } else {
            startActivity(intent)
        }
    }
}
