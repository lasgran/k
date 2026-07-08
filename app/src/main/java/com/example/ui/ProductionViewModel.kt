package com.example.ui

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ProductionViewModel(
    private val productionRepository: ProductionRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    // Session status
    val sessionState: StateFlow<UserSession> = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSession(false, "", "")
        )

    // Raw databases flows
    val allProductionRecords: StateFlow<List<ProductionRecord>> = productionRepository.allProductionRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allClosingRecords: StateFlow<List<ClosingRecord>> = productionRepository.allClosingRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<User>> = productionRepository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search query & filter state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Filtered lists for the History and Search screen
    val filteredRecords: Flow<List<HistoryItem>> = combine(
        allProductionRecords,
        allClosingRecords,
        searchQuery
    ) { productions, closings, query ->
        val items = mutableListOf<HistoryItem>()

        // Group productions and closings by date
        val dates = (productions.map { it.date } + closings.map { it.date }).distinct().sortedDescending()

        for (date in dates) {
            val dayProductions = productions.filter { it.date == date }
            val dayClosing = closings.find { it.date == date }

            // Apply search query if not empty
            if (query.isNotEmpty()) {
                val matchesDate = date.contains(query, ignoreCase = true)
                val matchesUser = dayProductions.any { it.user.contains(query, ignoreCase = true) } ||
                        (dayClosing != null && dayClosing.user.contains(query, ignoreCase = true))

                val matchesProduct = query.lowercase().let { q ->
                    (q.contains("salmão") && dayProductions.any { it.uramakiSalmao > 0 || it.nigiriSalmao > 0 }) ||
                    (q.contains("shimeji") && dayProductions.any { it.uramakiShimeji > 0 }) ||
                    (q.contains("skin") && dayProductions.any { it.uramakiSkin > 0 || it.nigiriSkin > 0 }) ||
                    (q.contains("grelhado") && dayProductions.any { it.uramakiGrelhado > 0 }) ||
                    (q.contains("jow") && dayProductions.any { it.jow > 0 }) ||
                    (q.contains("batera") && dayProductions.any { it.batera > 0 }) ||
                    (q.contains("uramaki") && dayProductions.any { it.uramakiSalmao > 0 || it.uramakiShimeji > 0 || it.uramakiSkin > 0 || it.uramakiGrelhado > 0 }) ||
                    (q.contains("nigiri") && dayProductions.any { it.nigiriSalmao > 0 || it.nigiriSkin > 0 })
                }

                if (!matchesDate && !matchesUser && !matchesProduct) {
                    continue // Skip this date as it doesn't match query
                }
            }

            items.add(
                HistoryItem(
                    date = date,
                    productions = dayProductions,
                    closing = dayClosing
                )
            )
        }
        items
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Helper functions to get current date/time
    fun getCurrentDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun getCurrentTimeString(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
    }

    fun formatDisplayDate(dateStr: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = parser.parse(dateStr)
            date?.let { formatter.format(it) } ?: dateStr
        } catch (e: Exception) {
            dateStr
        }
    }

    fun formatDisplayTime(timestamp: Long): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(timestamp))
    }

    // Login logic
    fun login(username: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = productionRepository.getUserByUsername(username)
            if (user == null) {
                onResult(false, "Usuário não encontrado.")
            } else if (user.password != password) {
                onResult(false, "Senha incorreta.")
            } else {
                userPreferencesRepository.saveSession(user.username, user.role)
                onResult(true, "Login realizado com sucesso.")
            }
        }
    }

    // Logout logic
    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearSession()
        }
    }

    // User management
    fun addUser(username: String, password: String, role: String, onResult: (Boolean, String) -> Unit) {
        if (username.isBlank() || password.isBlank()) {
            onResult(false, "Todos os campos são obrigatórios.")
            return
        }
        viewModelScope.launch {
            val existing = productionRepository.getUserByUsername(username)
            if (existing != null) {
                onResult(false, "Usuário já cadastrado.")
                return@launch
            }
            productionRepository.insertUser(User(username = username, password = password, role = role))
            onResult(true, "Usuário cadastrado com sucesso.")
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            if (user.username != "admin") { // Do not delete default admin
                productionRepository.deleteUser(user)
            }
        }
    }

    // Production registrations
    fun saveProduction(
        shift: String, // "Manhã" or "Noite"
        uramakiSalmao: Int,
        uramakiShimeji: Int,
        uramakiSkin: Int,
        uramakiGrelhado: Int,
        nigiriSalmao: Int,
        nigiriSkin: Int,
        jow: Int,
        batera: Int,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val user = sessionState.value.username
            val date = getCurrentDateString()
            val timestamp = System.currentTimeMillis()

            // Check if shift already submitted for today
            val todayRecords = allProductionRecords.value.filter { it.date == date && it.shift == shift }
            if (todayRecords.isNotEmpty() && sessionState.value.role != "Administrador") {
                onResult(false, "Produção da $shift já foi cadastrada hoje por ${todayRecords.first().user}.")
                return@launch
            }

            // If already registered, update or delete and re-insert
            if (todayRecords.isNotEmpty()) {
                // Admin can overwrite
                val existing = todayRecords.first()
                productionRepository.deleteProductionRecord(existing)
            }

            val record = ProductionRecord(
                date = date,
                shift = shift,
                user = user,
                timestamp = timestamp,
                uramakiSalmao = uramakiSalmao,
                uramakiShimeji = uramakiShimeji,
                uramakiSkin = uramakiSkin,
                uramakiGrelhado = uramakiGrelhado,
                nigiriSalmao = nigiriSalmao,
                nigiriSkin = nigiriSkin,
                jow = jow,
                batera = batera
            )
            productionRepository.insertProductionRecord(record)
            onResult(true, "Produção da $shift salva com sucesso!")
        }
    }

    // Overwrite/update specific production record (used by admin in history)
    fun updateProduction(record: ProductionRecord) {
        viewModelScope.launch {
            productionRepository.updateProductionRecord(record)
        }
    }

    fun deleteProduction(record: ProductionRecord) {
        viewModelScope.launch {
            productionRepository.deleteProductionRecord(record)
        }
    }

    // Closing registrations
    fun saveClosing(
        lombo: Double,
        barriga: Double,
        observations: String,
        sobraUramakiSalmao: Int,
        sobraUramakiShimeji: Int,
        sobraUramakiSkin: Int,
        sobraUramakiGrelhado: Int,
        sobraNigiriSalmao: Int,
        sobraNigiriSkin: Int,
        sobraJow: Int,
        sobraBatera: Int,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val user = sessionState.value.username
            val date = getCurrentDateString()
            val timestamp = System.currentTimeMillis()

            val existing = allClosingRecords.value.find { it.date == date }
            if (existing != null && sessionState.value.role != "Administrador") {
                onResult(false, "Fechamento de hoje já foi realizado por ${existing.user}.")
                return@launch
            }

            if (existing != null) {
                // Admin overwrite
                productionRepository.deleteClosingRecord(existing)
            }

            val closing = ClosingRecord(
                date = date,
                user = user,
                timestamp = timestamp,
                lomboRestante = lombo,
                barrigaRestante = barriga,
                observations = observations,
                sobraUramakiSalmao = sobraUramakiSalmao,
                sobraUramakiShimeji = sobraUramakiShimeji,
                sobraUramakiSkin = sobraUramakiSkin,
                sobraUramakiGrelhado = sobraUramakiGrelhado,
                sobraNigiriSalmao = sobraNigiriSalmao,
                sobraNigiriSkin = sobraNigiriSkin,
                sobraJow = sobraJow,
                sobraBatera = sobraBatera
            )
            productionRepository.insertClosingRecord(closing)
            onResult(true, "Fechamento realizado com sucesso!")
        }
    }

    fun updateClosing(closing: ClosingRecord) {
        viewModelScope.launch {
            productionRepository.updateClosingRecord(closing)
        }
    }

    fun deleteClosing(closing: ClosingRecord) {
        viewModelScope.launch {
            productionRepository.deleteClosingRecord(closing)
        }
    }

    // Check if current date is already closed (to prevent further non-admin registrations)
    fun isTodayClosedFlow(): Flow<Boolean> {
        val today = getCurrentDateString()
        return allClosingRecords.map { closings ->
            closings.any { it.date == today }
        }
    }

    // EXPORT EXCEL (CSV)
    fun exportToExcel(context: Context) {
        try {
            val productions = allProductionRecords.value
            val closings = allClosingRecords.value

            val csvContent = StringBuilder()
            // Header
            csvContent.append("Data;Turno/Tipo;Responsavel;Horario;Uramaki Salmao;Uramaki Shimeji;Uramaki Skin;Uramaki Grelhado;Nigiri Salmao;Nigiri Skin;Jow;Batera;Lombo Restante (kg);Barriga Restante (kg);Observacoes\n")

            // Append productions
            for (p in productions) {
                val timeStr = formatDisplayTime(p.timestamp)
                csvContent.append("${p.date};${p.shift};${p.user};$timeStr;${p.uramakiSalmao};${p.uramakiShimeji};${p.uramakiSkin};${p.uramakiGrelhado};${p.nigiriSalmao};${p.nigiriSkin};${p.jow};${p.batera};-;-;-\n")
            }

            // Append closings
            for (c in closings) {
                val timeStr = formatDisplayTime(c.timestamp)
                csvContent.append("${c.date};Fechamento;${c.user};$timeStr;${c.sobraUramakiSalmao};${c.sobraUramakiShimeji};${c.sobraUramakiSkin};${c.sobraUramakiGrelhado};${c.sobraNigiriSalmao};${c.sobraNigiriSkin};${c.sobraJow};${c.sobraBatera};${c.lomboRestante};${c.barrigaRestante};${c.observations}\n")
            }

            val file = File(context.cacheDir, "relatorio_producao_${System.currentTimeMillis()}.csv")
            FileOutputStream(file).use { out ->
                out.write(csvContent.toString().toByteArray())
            }

            shareFile(context, file, "text/csv", "Relatório de Produção")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // EXPORT PDF NATIVE DRAWING
    fun exportToPdf(context: Context) {
        try {
            val productions = allProductionRecords.value.sortedByDescending { it.date }
            val closings = allClosingRecords.value

            val pdfDocument = PdfDocument()
            val paint = Paint()
            val textPaint = Paint().apply {
                textSize = 12f
                color = Color.BLACK
            }
            val titlePaint = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
                color = Color.DKGRAY
            }
            val subtitlePaint = Paint().apply {
                textSize = 14f
                isFakeBoldText = true
                color = Color.GRAY
            }

            var pageNumber = 1
            var pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
            var page = pdfDocument.startPage(pageInfo)
            var canvas = page.canvas

            var y = 40f

            // Draw Header
            canvas.drawText("Controle de Produção Japonesa - Relatório Diário", 40f, y, titlePaint)
            y += 20f
            canvas.drawText("Gerado em: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())}", 40f, y, textPaint)
            y += 30f

            // Table Header for Production
            canvas.drawText("Histórico de Produção", 40f, y, subtitlePaint)
            y += 20f

            paint.color = Color.LTGRAY
            canvas.drawRect(30f, y - 12f, 565f, y + 4f, paint)

            paint.color = Color.BLACK
            canvas.drawText("Data", 40f, y, textPaint)
            canvas.drawText("Turno", 110f, y, textPaint)
            canvas.drawText("U. Salmão", 160f, y, textPaint)
            canvas.drawText("U. Shimeji", 220f, y, textPaint)
            canvas.drawText("N. Salmão", 280f, y, textPaint)
            canvas.drawText("Jow", 340f, y, textPaint)
            canvas.drawText("Batera", 390f, y, textPaint)
            canvas.drawText("Usuário", 450f, y, textPaint)
            y += 20f

            // Add rows
            for (p in productions) {
                if (y > 780f) {
                    pdfDocument.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    y = 40f
                }

                canvas.drawText(formatDisplayDate(p.date), 40f, y, textPaint)
                canvas.drawText(p.shift, 110f, y, textPaint)
                canvas.drawText(p.uramakiSalmao.toString(), 160f, y, textPaint)
                canvas.drawText(p.uramakiShimeji.toString(), 220f, y, textPaint)
                canvas.drawText(p.nigiriSalmao.toString(), 280f, y, textPaint)
                canvas.drawText(p.jow.toString(), 340f, y, textPaint)
                canvas.drawText(p.batera.toString(), 390f, y, textPaint)
                canvas.drawText(p.user, 450f, y, textPaint)
                y += 18f
            }

            y += 30f

            // Table Header for Closings
            if (y > 680f) {
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                y = 40f
            }

            canvas.drawText("Histórico de Fechamento de Insumos", 40f, y, subtitlePaint)
            y += 20f

            paint.color = Color.LTGRAY
            canvas.drawRect(30f, y - 12f, 565f, y + 4f, paint)

            paint.color = Color.BLACK
            canvas.drawText("Data", 40f, y, textPaint)
            canvas.drawText("Lombo (kg)", 130f, y, textPaint)
            canvas.drawText("Barriga (kg)", 220f, y, textPaint)
            canvas.drawText("Usuário", 310f, y, textPaint)
            canvas.drawText("Observações", 400f, y, textPaint)
            y += 20f

            for (c in closings.sortedByDescending { it.date }) {
                if (y > 780f) {
                    pdfDocument.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(595, 842, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    y = 40f
                }

                canvas.drawText(formatDisplayDate(c.date), 40f, y, textPaint)
                canvas.drawText(String.format(Locale.US, "%.2f kg", c.lomboRestante), 130f, y, textPaint)
                canvas.drawText(String.format(Locale.US, "%.2f kg", c.barrigaRestante), 220f, y, textPaint)
                canvas.drawText(c.user, 310f, y, textPaint)
                canvas.drawText(if (c.observations.length > 20) c.observations.take(17) + "..." else c.observations, 400f, y, textPaint)
                y += 18f
            }

            pdfDocument.finishPage(page)

            val file = File(context.cacheDir, "relatorio_producao_${System.currentTimeMillis()}.pdf")
            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()

            shareFile(context, file, "application/pdf", "Relatório de Produção PDF")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun shareRecordsAsText(context: Context, historyItems: List<HistoryItem>) {
        if (historyItems.isEmpty()) return
        val textBuilder = StringBuilder()
        textBuilder.append("🍣 *RELATÓRIO DE PRODUÇÃO E SOBRAS*\n")
        textBuilder.append("===================================\n\n")

        for (item in historyItems) {
            textBuilder.append("📅 *Data: ${formatDisplayDate(item.date)}*\n")
            textBuilder.append("-----------------------------------\n")

            // Morning
            val morning = item.productions.find { it.shift == "Manhã" }
            if (morning != null) {
                textBuilder.append("🌅 *Turno Manhã* (Resp: ${morning.user})\n")
                textBuilder.append(" • Uramaki Salmão: ${morning.uramakiSalmao}\n")
                textBuilder.append(" • Uramaki Shimeji: ${morning.uramakiShimeji}\n")
                textBuilder.append(" • Uramaki Skin: ${morning.uramakiSkin}\n")
                textBuilder.append(" • Uramaki Grelhado: ${morning.uramakiGrelhado}\n")
                textBuilder.append(" • Nigiri Salmão: ${morning.nigiriSalmao}\n")
                textBuilder.append(" • Nigiri Skin: ${morning.nigiriSkin}\n")
                textBuilder.append(" • Jow: ${morning.jow}\n")
                textBuilder.append(" • Batera: ${morning.batera}\n\n")
            } else {
                textBuilder.append("🌅 *Turno Manhã:* Sem lançamentos\n\n")
            }

            // Night
            val night = item.productions.find { it.shift == "Noite" }
            if (night != null) {
                textBuilder.append("🌃 *Turno Noite* (Resp: ${night.user})\n")
                textBuilder.append(" • Uramaki Salmão: ${night.uramakiSalmao}\n")
                textBuilder.append(" • Uramaki Shimeji: ${night.uramakiShimeji}\n")
                textBuilder.append(" • Uramaki Skin: ${night.uramakiSkin}\n")
                textBuilder.append(" • Uramaki Grelhado: ${night.uramakiGrelhado}\n")
                textBuilder.append(" • Nigiri Salmão: ${night.nigiriSalmao}\n")
                textBuilder.append(" • Nigiri Skin: ${night.nigiriSkin}\n")
                textBuilder.append(" • Jow: ${night.jow}\n")
                textBuilder.append(" • Batera: ${night.batera}\n\n")
            } else {
                textBuilder.append("🌃 *Turno Noite:* Sem lançamentos\n\n")
            }

            // Closing (Insumos e Sobras)
            val closing = item.closing
            if (closing != null) {
                textBuilder.append("⚖️ *Fechamento do Dia* (Resp: ${closing.user})\n")
                textBuilder.append(" 🥩 Lombo Restante: ${closing.lomboRestante} kg\n")
                textBuilder.append(" 🥓 Barriga Restante: ${closing.barrigaRestante} kg\n")
                textBuilder.append(" 🍱 Sobras de sushis (Produção):\n")
                textBuilder.append("   - Uramaki Salmão: ${closing.sobraUramakiSalmao}\n")
                textBuilder.append("   - Uramaki Shimeji: ${closing.sobraUramakiShimeji}\n")
                textBuilder.append("   - Uramaki Skin: ${closing.sobraUramakiSkin}\n")
                textBuilder.append("   - Uramaki Grelhado: ${closing.sobraUramakiGrelhado}\n")
                textBuilder.append("   - Nigiri Salmão: ${closing.sobraNigiriSalmao}\n")
                textBuilder.append("   - Nigiri Skin: ${closing.sobraNigiriSkin}\n")
                textBuilder.append("   - Jow: ${closing.sobraJow}\n")
                textBuilder.append("   - Batera: ${closing.sobraBatera}\n")
                if (closing.observations.isNotBlank()) {
                    textBuilder.append(" 📝 Obs: ${closing.observations}\n")
                }
                textBuilder.append("\n")
            } else {
                textBuilder.append("⚠️ *Fechamento do Dia:* Sem fechamento de insumos/sobras lançado.\n\n")
            }
            textBuilder.append("===================================\n\n")
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, textBuilder.toString())
            putExtra(Intent.EXTRA_SUBJECT, "Relatório de Produção e Sobras")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Compartilhar via"))
    }

    private fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartilhar Relatório"))
    }
}

class ProductionViewModelFactory(
    private val productionRepository: ProductionRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductionViewModel(productionRepository, userPreferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

// Custom wrapper to hold both production and closing for a single date
data class HistoryItem(
    val date: String,
    val productions: List<ProductionRecord>,
    val closing: ClosingRecord?
)
