package com.nammamistri.app

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max

class MainActivity : Activity() {
    private val prefs by lazy { getSharedPreferences("namma_mistri_store", MODE_PRIVATE) }
    private val money = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    private val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
    private val storeDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    private lateinit var activeSiteText: TextView
    private lateinit var siteStatusText: TextView
    private lateinit var dashboardCostText: TextView
    private lateinit var dashboardMaterialText: TextView
    private lateinit var dashboardCrewText: TextView
    private lateinit var dashboardPhotosText: TextView
    private lateinit var siteInput: EditText
    private lateinit var calculatorPanel: LinearLayout
    private lateinit var teamPanel: LinearLayout
    private lateinit var photosPanel: LinearLayout
    private lateinit var ratesPanel: LinearLayout
    private lateinit var calculatorTab: Button
    private lateinit var teamTab: Button
    private lateinit var photosTab: Button
    private lateinit var ratesTab: Button
    private lateinit var wallRadio: RadioButton
    private lateinit var lengthInput: EditText
    private lateinit var widthInput: EditText
    private lateinit var heightInput: EditText
    private lateinit var thicknessSpinner: Spinner
    private lateinit var ratioSpinner: Spinner
    private lateinit var wasteInput: EditText
    private lateinit var brickSummaryText: TextView
    private lateinit var cementSummaryText: TextView
    private lateinit var sandSummaryText: TextView
    private lateinit var estimateDetailsText: TextView
    private lateinit var costSplitText: TextView
    private lateinit var totalCostText: TextView
    private lateinit var workerNameInput: EditText
    private lateinit var dailyWageInput: EditText
    private lateinit var daysWorkedInput: EditText
    private lateinit var advanceInput: EditText
    private lateinit var teamStatsText: TextView
    private lateinit var workerList: LinearLayout
    private lateinit var photoNoteInput: EditText
    private lateinit var photosStatsText: TextView
    private lateinit var clientUpdateText: TextView
    private lateinit var photoList: LinearLayout
    private lateinit var brickRateInput: EditText
    private lateinit var cementRateInput: EditText
    private lateinit var sandRateInput: EditText
    private lateinit var ratesSummaryText: TextView

    private var activeSite = "New House Site"
    private var brickRate = 9.0
    private var cementRate = 430.0
    private var sandRate = 5200.0
    private var latestEstimate: Estimate? = null
    private val workers = mutableListOf<WorkerEntry>()
    private val photos = mutableListOf<PhotoEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindViews()
        configureSpinners()
        loadState()
        renderAll()
        wireActions()
    }

    private fun bindViews() {
        activeSiteText = findViewById(R.id.activeSiteText)
        siteStatusText = findViewById(R.id.siteStatusText)
        dashboardCostText = findViewById(R.id.dashboardCostText)
        dashboardMaterialText = findViewById(R.id.dashboardMaterialText)
        dashboardCrewText = findViewById(R.id.dashboardCrewText)
        dashboardPhotosText = findViewById(R.id.dashboardPhotosText)
        siteInput = findViewById(R.id.siteInput)
        calculatorPanel = findViewById(R.id.calculatorPanel)
        teamPanel = findViewById(R.id.teamPanel)
        photosPanel = findViewById(R.id.photosPanel)
        ratesPanel = findViewById(R.id.ratesPanel)
        calculatorTab = findViewById(R.id.calculatorTab)
        teamTab = findViewById(R.id.teamTab)
        photosTab = findViewById(R.id.photosTab)
        ratesTab = findViewById(R.id.ratesTab)
        wallRadio = findViewById(R.id.wallRadio)
        lengthInput = findViewById(R.id.lengthInput)
        widthInput = findViewById(R.id.widthInput)
        heightInput = findViewById(R.id.heightInput)
        thicknessSpinner = findViewById(R.id.thicknessSpinner)
        ratioSpinner = findViewById(R.id.ratioSpinner)
        wasteInput = findViewById(R.id.wasteInput)
        brickSummaryText = findViewById(R.id.brickSummaryText)
        cementSummaryText = findViewById(R.id.cementSummaryText)
        sandSummaryText = findViewById(R.id.sandSummaryText)
        estimateDetailsText = findViewById(R.id.estimateDetailsText)
        costSplitText = findViewById(R.id.costSplitText)
        totalCostText = findViewById(R.id.totalCostText)
        workerNameInput = findViewById(R.id.workerNameInput)
        dailyWageInput = findViewById(R.id.dailyWageInput)
        daysWorkedInput = findViewById(R.id.daysWorkedInput)
        advanceInput = findViewById(R.id.advanceInput)
        teamStatsText = findViewById(R.id.teamStatsText)
        workerList = findViewById(R.id.workerList)
        photoNoteInput = findViewById(R.id.photoNoteInput)
        photosStatsText = findViewById(R.id.photosStatsText)
        clientUpdateText = findViewById(R.id.clientUpdateText)
        photoList = findViewById(R.id.photoList)
        brickRateInput = findViewById(R.id.brickRateInput)
        cementRateInput = findViewById(R.id.cementRateInput)
        sandRateInput = findViewById(R.id.sandRateInput)
        ratesSummaryText = findViewById(R.id.ratesSummaryText)
    }

    private fun configureSpinners() {
        thicknessSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("4.5 in partition", "6 in wall", "9 in wall")
        )
        thicknessSpinner.setSelection(2)
        ratioSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            listOf("1:4 strong", "1:5 standard", "1:6 common")
        )
        ratioSpinner.setSelection(2)
    }

    private fun wireActions() {
        findViewById<Button>(R.id.saveSiteButton).setOnClickListener {
            activeSite = siteInput.text.toString().trim().ifBlank { activeSite }
            saveState()
            renderAll()
        }
        findViewById<Button>(R.id.calculateButton).setOnClickListener { calculateEstimate() }
        findViewById<Button>(R.id.addWorkerButton).setOnClickListener { addWorker() }
        findViewById<Button>(R.id.pickPhotoButton).setOnClickListener { choosePhoto() }
        findViewById<Button>(R.id.saveRatesButton).setOnClickListener { saveRates() }

        calculatorTab.setOnClickListener { showPanel("calculator") }
        teamTab.setOnClickListener { showPanel("team") }
        photosTab.setOnClickListener { showPanel("photos") }
        ratesTab.setOnClickListener { showPanel("rates") }
    }

    private fun showPanel(name: String) {
        calculatorPanel.visibility = if (name == "calculator") View.VISIBLE else View.GONE
        teamPanel.visibility = if (name == "team") View.VISIBLE else View.GONE
        photosPanel.visibility = if (name == "photos") View.VISIBLE else View.GONE
        ratesPanel.visibility = if (name == "rates") View.VISIBLE else View.GONE
        listOf(calculatorTab, teamTab, photosTab, ratesTab).forEach {
            it.setBackgroundResource(R.drawable.tab_unselected)
            it.setTextColor(getColor(R.color.ink))
        }
        val selected = when (name) {
            "team" -> teamTab
            "photos" -> photosTab
            "rates" -> ratesTab
            else -> calculatorTab
        }
        selected.setBackgroundResource(R.drawable.tab_selected)
        selected.setTextColor(getColor(android.R.color.white))
    }

    private fun calculateEstimate() {
        val length = lengthInput.doubleValue()
        val width = widthInput.doubleValue()
        val height = heightInput.doubleValue()
        val thicknessIn = when (thicknessSpinner.selectedItemPosition) {
            0 -> 4.5
            1 -> 6.0
            else -> 9.0
        }
        val ratioSand = when (ratioSpinner.selectedItemPosition) {
            0 -> 4.0
            1 -> 5.0
            else -> 6.0
        }
        val waste = wasteInput.doubleValue() / 100.0
        val wallLength = if (wallRadio.isChecked) length else 2.0 * (length + width)
        val brickworkCft = wallLength * height * (thicknessIn / 12.0)
        val brickworkCum = brickworkCft * 0.0283168
        val wetMortarCum = brickworkCum * 0.30
        val dryMortarCum = wetMortarCum * 1.33
        val cementCum = dryMortarCum * (1.0 / (1.0 + ratioSand))
        val sandCum = dryMortarCum * (ratioSand / (1.0 + ratioSand))
        val bricks = ceil(brickworkCum * 500.0 * (1.0 + waste)).toInt()
        val cementBags = ceil((cementCum / 0.0347) * (1.0 + waste)).toInt()
        val sandLoads = max(1, ceil((sandCum / 2.83) * (1.0 + waste)).toInt())

        latestEstimate = Estimate(
            type = if (wallRadio.isChecked) "Wall" else "Room",
            wallLength = wallLength,
            height = height,
            thicknessIn = thicknessIn,
            ratioSand = ratioSand.toInt(),
            wastePercent = (waste * 100).toInt(),
            bricks = bricks,
            cementBags = cementBags,
            sandLoads = sandLoads,
            brickCost = bricks * brickRate,
            cementCost = cementBags * cementRate,
            sandCost = sandLoads * sandRate
        )
        saveState()
        renderEstimate()
        renderDashboard()
        renderRates()
    }

    private fun addWorker() {
        val name = workerNameInput.text.toString().trim()
        if (name.isBlank()) return
        val wage = dailyWageInput.doubleValue()
        val days = daysWorkedInput.doubleValue()
        val advance = advanceInput.doubleValue()
        workers.add(
            0,
            WorkerEntry(
                site = activeSite,
                name = name,
                date = storeDateFormat.format(Date()),
                wage = wage,
                days = days,
                advance = advance
            )
        )
        workerNameInput.text.clear()
        advanceInput.setText("0")
        saveState()
        renderWorkers()
        renderDashboard()
    }

    private fun choosePhoto() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        startActivityForResult(intent, PHOTO_PICK_REQUEST)
    }

    @Deprecated("Deprecated by Android platform, sufficient for this no-AndroidX project.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode != PHOTO_PICK_REQUEST || resultCode != Activity.RESULT_OK) return
        val uri = data?.data ?: return
        contentResolver.takePersistableUriPermissionIfPossible(uri, data)
        photos.add(
            0,
            PhotoEntry(
                site = activeSite,
                date = storeDateFormat.format(Date()),
                note = photoNoteInput.text.toString().trim().ifBlank { "Site progress" },
                uri = uri.toString()
            )
        )
        photoNoteInput.text.clear()
        saveState()
        renderPhotos()
        renderDashboard()
    }

    private fun saveRates() {
        brickRate = brickRateInput.doubleValue().takeIf { it > 0 } ?: brickRate
        cementRate = cementRateInput.doubleValue().takeIf { it > 0 } ?: cementRate
        sandRate = sandRateInput.doubleValue().takeIf { it > 0 } ?: sandRate
        calculateEstimate()
        saveState()
        renderRates()
    }

    private fun renderAll() {
        siteInput.setText(activeSite)
        activeSiteText.text = activeSite
        brickRateInput.setText(trimNumber(brickRate))
        cementRateInput.setText(trimNumber(cementRate))
        sandRateInput.setText(trimNumber(sandRate))
        showPanel("calculator")
        if (latestEstimate == null) calculateEstimate() else renderEstimate()
        renderWorkers()
        renderPhotos()
        renderRates()
        renderDashboard()
    }

    private fun renderDashboard() {
        val estimate = latestEstimate
        val siteWorkers = workers.filter { it.site == activeSite }
        val sitePhotos = photos.filter { it.site == activeSite }
        val labourDue = siteWorkers.sumOf { it.balance }
        val materials = estimate?.let { "${it.bricks} bricks\n${it.cementBags} bags cement" } ?: "No BOM\nRun estimate"

        siteStatusText.text = "Offline field controls | ${dateFormat.format(Date())}"
        dashboardCostText.text = "Estimate\n${money.format(estimate?.total ?: 0.0)}"
        dashboardMaterialText.text = "Materials\n$materials"
        dashboardCrewText.text = "Crew Due\n${money.format(labourDue)}"
        dashboardPhotosText.text = "Progress\n${sitePhotos.size} photos"
    }

    private fun renderEstimate() {
        val estimate = latestEstimate ?: return
        brickSummaryText.text = "Bricks\n${estimate.bricks}\n${money.format(estimate.brickCost)}"
        cementSummaryText.text = "Cement\n${estimate.cementBags} bags\n${money.format(estimate.cementCost)}"
        sandSummaryText.text = "Sand\n${estimate.sandLoads} loads\n${money.format(estimate.sandCost)}"
        totalCostText.text = "Total: ${money.format(estimate.total)}"
        estimateDetailsText.text =
            "${estimate.type} takeoff | ${trimNumber(estimate.wallLength)} ft running length | " +
                "${trimNumber(estimate.height)} ft height | ${trimNumber(estimate.thicknessIn)} in wall | " +
                "Mortar 1:${estimate.ratioSand} | Waste ${estimate.wastePercent}%"
        costSplitText.text =
            "Cost split\nBricks: ${money.format(estimate.brickCost)}\n" +
                "Cement: ${money.format(estimate.cementCost)}\n" +
                "Sand: ${money.format(estimate.sandCost)}"
    }

    private fun renderWorkers() {
        val siteWorkers = workers.filter { it.site == activeSite }
        val earned = siteWorkers.sumOf { it.earned }
        val advances = siteWorkers.sumOf { it.advance }
        val totalDue = siteWorkers.sumOf { it.balance }
        teamStatsText.text =
            "Entries: ${siteWorkers.size}\nEarned: ${money.format(earned)}\n" +
                "Advances: ${money.format(advances)}\nBalance due: ${money.format(totalDue)}"
        workerList.removeAllViews()
        if (siteWorkers.isEmpty()) {
            workerList.addView(outputText("No crew entries yet. Add today's attendance and advance details."))
            return
        }
        siteWorkers.forEach { worker ->
            workerList.addView(
                outputText(
                    "${worker.name}  |  ${worker.date}\n" +
                        "Days: ${trimNumber(worker.days)}  Daily: ${money.format(worker.wage)}\n" +
                        "Earned: ${money.format(worker.earned)}  Advance: ${money.format(worker.advance)}\n" +
                        "Balance due: ${money.format(worker.balance)}"
                )
            )
        }
    }

    private fun renderPhotos() {
        val sitePhotos = photos.filter { it.site == activeSite }
        photosStatsText.text = "Photos saved: ${sitePhotos.size}\nLatest: ${sitePhotos.firstOrNull()?.date ?: "No update yet"}"
        clientUpdateText.text = if (sitePhotos.isEmpty()) {
            "No progress photos yet. Add work photos to create a clean owner-facing site timeline."
        } else {
            "Latest owner update: ${sitePhotos.first().note}"
        }
        photoList.removeAllViews()
        if (sitePhotos.isEmpty()) {
            photoList.addView(outputText("Photo timeline is empty. Capture foundation, masonry, lintel, roof, and finishing milestones."))
            return
        }
        sitePhotos.forEach { photo ->
            val image = ImageView(this).apply {
                setImageURI(Uri.parse(photo.uri))
                adjustViewBounds = true
                maxHeight = resources.displayMetrics.widthPixels
                scaleType = ImageView.ScaleType.CENTER_CROP
                setPadding(0, 12, 0, 4)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            photoList.addView(image)
            photoList.addView(outputText("${photo.date}\n${photo.note}"))
        }
    }

    private fun renderRates() {
        val estimate = latestEstimate
        ratesSummaryText.text = if (estimate == null) {
            "Current market book\nBrick: ${money.format(brickRate)} per piece\n" +
                "Cement: ${money.format(cementRate)} per bag\nSand: ${money.format(sandRate)} per load"
        } else {
            "Current market book\nBrick: ${money.format(brickRate)} per piece\n" +
                "Cement: ${money.format(cementRate)} per bag\nSand: ${money.format(sandRate)} per load\n\n" +
                "Latest estimate impact\nBricks: ${money.format(estimate.brickCost)}\n" +
                "Cement: ${money.format(estimate.cementCost)}\nSand: ${money.format(estimate.sandCost)}"
        }
    }

    private fun outputText(value: String): TextView =
        TextView(this).apply {
            text = value
            textSize = 15f
            setTextColor(getColor(R.color.ink))
            setBackgroundResource(R.drawable.list_item_bg)
            setPadding(18, 18, 18, 18)
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 8, 0, 8)
            layoutParams = params
        }

    private fun saveState() {
        val root = JSONObject()
        root.put("site", activeSite)
        root.put("brickRate", brickRate)
        root.put("cementRate", cementRate)
        root.put("sandRate", sandRate)
        latestEstimate?.let { root.put("estimate", it.toJson()) }
        root.put("workers", JSONArray(workers.map { it.toJson() }))
        root.put("photos", JSONArray(photos.map { it.toJson() }))
        prefs.edit().putString("state", root.toString()).apply()
    }

    private fun loadState() {
        val raw = prefs.getString("state", null) ?: return
        val root = JSONObject(raw)
        activeSite = root.optString("site", activeSite)
        brickRate = root.optDouble("brickRate", brickRate)
        cementRate = root.optDouble("cementRate", cementRate)
        sandRate = root.optDouble("sandRate", sandRate)
        root.optJSONObject("estimate")?.let { latestEstimate = Estimate.fromJson(it) }
        workers.clear()
        root.optJSONArray("workers")?.let { array ->
            for (index in 0 until array.length()) workers.add(WorkerEntry.fromJson(array.getJSONObject(index)))
        }
        photos.clear()
        root.optJSONArray("photos")?.let { array ->
            for (index in 0 until array.length()) photos.add(PhotoEntry.fromJson(array.getJSONObject(index)))
        }
        saveState()
    }

    private fun android.content.ContentResolver.takePersistableUriPermissionIfPossible(uri: Uri, intent: Intent?) {
        try {
            val flags = intent?.flags ?: return
            takePersistableUriPermission(uri, flags and Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: SecurityException) {
            // Some gallery providers do not offer persistable permissions.
        }
    }

    private fun EditText.doubleValue(): Double = text.toString().toDoubleOrNull() ?: 0.0
    private fun trimNumber(value: Double): String = if (value % 1.0 == 0.0) value.toInt().toString() else "%.2f".format(Locale.US, value)

    data class Estimate(
        val type: String,
        val wallLength: Double,
        val height: Double,
        val thicknessIn: Double,
        val ratioSand: Int,
        val wastePercent: Int,
        val bricks: Int,
        val cementBags: Int,
        val sandLoads: Int,
        val brickCost: Double,
        val cementCost: Double,
        val sandCost: Double
    ) {
        val total: Double get() = brickCost + cementCost + sandCost

        fun toJson(): JSONObject = JSONObject()
            .put("type", type)
            .put("wallLength", wallLength)
            .put("height", height)
            .put("thicknessIn", thicknessIn)
            .put("ratioSand", ratioSand)
            .put("wastePercent", wastePercent)
            .put("bricks", bricks)
            .put("cementBags", cementBags)
            .put("sandLoads", sandLoads)
            .put("brickCost", brickCost)
            .put("cementCost", cementCost)
            .put("sandCost", sandCost)

        companion object {
            fun fromJson(json: JSONObject): Estimate = Estimate(
                type = json.getString("type"),
                wallLength = json.getDouble("wallLength"),
                height = json.getDouble("height"),
                thicknessIn = json.getDouble("thicknessIn"),
                ratioSand = json.getInt("ratioSand"),
                wastePercent = json.getInt("wastePercent"),
                bricks = json.getInt("bricks"),
                cementBags = json.getInt("cementBags"),
                sandLoads = json.getInt("sandLoads"),
                brickCost = json.getDouble("brickCost"),
                cementCost = json.getDouble("cementCost"),
                sandCost = json.getDouble("sandCost")
            )
        }
    }

    data class WorkerEntry(
        val site: String,
        val name: String,
        val date: String,
        val wage: Double,
        val days: Double,
        val advance: Double
    ) {
        val earned: Double get() = wage * days
        val balance: Double get() = earned - advance

        fun toJson(): JSONObject = JSONObject()
            .put("site", site)
            .put("name", name)
            .put("date", date)
            .put("wage", wage)
            .put("days", days)
            .put("advance", advance)

        companion object {
            fun fromJson(json: JSONObject): WorkerEntry = WorkerEntry(
                site = json.getString("site"),
                name = json.getString("name"),
                date = json.getString("date"),
                wage = json.getDouble("wage"),
                days = json.getDouble("days"),
                advance = json.getDouble("advance")
            )
        }
    }

    data class PhotoEntry(
        val site: String,
        val date: String,
        val note: String,
        val uri: String
    ) {
        fun toJson(): JSONObject = JSONObject()
            .put("site", site)
            .put("date", date)
            .put("note", note)
            .put("uri", uri)

        companion object {
            fun fromJson(json: JSONObject): PhotoEntry = PhotoEntry(
                site = json.getString("site"),
                date = json.getString("date"),
                note = json.getString("note"),
                uri = json.getString("uri")
            )
        }
    }

    companion object {
        private const val PHOTO_PICK_REQUEST = 42
    }
}
