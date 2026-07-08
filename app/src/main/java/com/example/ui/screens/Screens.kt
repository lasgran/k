package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.*
import com.example.ui.HistoryItem
import com.example.ui.ProductionViewModel
import java.text.SimpleDateFormat
import java.util.*

// ==========================================
// TELA DE LOGIN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: ProductionViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Sushi Premium Minimalist Icon using Canvas or Icon
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Restaurant,
                        contentDescription = "Logo",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Controle de Produção",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Restaurante Japonês",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Login",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Usuário") },
                            leadingIcon = { Icon(Icons.Filled.Person, "User Icon") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("username_input"),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Senha") },
                            leadingIcon = { Icon(Icons.Filled.Lock, "Lock Icon") },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                        contentDescription = "Ver senha"
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("password_input"),
                            shape = RoundedCornerShape(16.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                if (username.isBlank() || password.isBlank()) {
                                    Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                isLoading = true
                                viewModel.login(username, password) { success, msg ->
                                    isLoading = false
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    if (success) {
                                        onLoginSuccess()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("login_button"),
                            shape = RoundedCornerShape(28.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                            } else {
                                Text("Entrar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TELA INICIAL (HOME)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ProductionViewModel,
    onNavigateToMorning: () -> Unit,
    onNavigateToNight: () -> Unit,
    onNavigateToClosing: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToUsers: () -> Unit,
    onLogout: () -> Unit
) {
    val session by viewModel.sessionState.collectAsState()
    val isTodayClosed by viewModel.isTodayClosedFlow().collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Controle de Produção",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Logado como: ${session.username} (${session.role})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Sair")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Closed status notice banner
            if (isTodayClosed) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Lock,
                            contentDescription = "Lock",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Dia Finalizado! Novos registros não são permitidos por funcionários.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            Text(
                text = "Escolha uma Operação",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Primary items - Available for both roles
            HomeMenuCard(
                title = "Produção da Manhã",
                subtitle = "Registrar preparos do turno matutino",
                icon = Icons.Filled.WbSunny,
                color = MaterialTheme.colorScheme.primary,
                onClick = onNavigateToMorning
            )

            HomeMenuCard(
                title = "Produção da Noite",
                subtitle = "Registrar preparos do turno noturno",
                icon = Icons.Filled.NightsStay,
                color = MaterialTheme.colorScheme.secondary,
                onClick = onNavigateToNight
            )

            HomeMenuCard(
                title = "Fechamento",
                subtitle = "Registrar estoque restante de peixe",
                icon = Icons.Filled.FactCheck,
                color = MaterialTheme.colorScheme.tertiary,
                onClick = onNavigateToClosing
            )

            // Consult records (Visible to everyone, but limited by screen filters)
            HomeMenuCard(
                title = if (session.role == "Administrador") "Histórico Geral" else "Consultar Dia Atual",
                subtitle = if (session.role == "Administrador") "Ver, editar, excluir e exportar relatórios" else "Consultar seus lançamentos do dia",
                icon = Icons.Filled.History,
                color = MaterialTheme.colorScheme.outline,
                onClick = onNavigateToHistory
            )

            // Administrator views
            if (session.role == "Administrador") {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Área Administrativa",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                HomeMenuCard(
                    title = "Dashboard Geral",
                    subtitle = "Análise gráfica, totais e tendências",
                    icon = Icons.Filled.Dashboard,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = onNavigateToDashboard
                )

                HomeMenuCard(
                    title = "Gerenciar Usuários",
                    subtitle = "Cadastrar, alterar e remover funcionários",
                    icon = Icons.Filled.People,
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = onNavigateToUsers
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeMenuCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1.5f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Acessar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ==========================================
// TELA REGISTRO DE PRODUÇÃO (MANHÃ / NOITE)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionFormScreen(
    viewModel: ProductionViewModel,
    shift: String, // "Manhã" or "Noite"
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val isTodayClosed by viewModel.isTodayClosedFlow().collectAsState(initial = false)
    val session by viewModel.sessionState.collectAsState()

    // Quantities state
    var uramakiSalmao by remember { mutableStateOf(0) }
    var uramakiShimeji by remember { mutableStateOf(0) }
    var uramakiSkin by remember { mutableStateOf(0) }
    var uramakiGrelhado by remember { mutableStateOf(0) }
    var nigiriSalmao by remember { mutableStateOf(0) }
    var nigiriSkin by remember { mutableStateOf(0) }
    var jow by remember { mutableStateOf(0) }
    var batera by remember { mutableStateOf(0) }

    // Prepopulate if entry already exists (Admin can edit, Employee can view if not closed)
    LaunchedEffect(Unit) {
        val today = viewModel.getCurrentDateString()
        val records = viewModel.allProductionRecords.value.filter { it.date == today && it.shift == shift }
        if (records.isNotEmpty()) {
            val rec = records.first()
            uramakiSalmao = rec.uramakiSalmao
            uramakiShimeji = rec.uramakiShimeji
            uramakiSkin = rec.uramakiSkin
            uramakiGrelhado = rec.uramakiGrelhado
            nigiriSalmao = rec.nigiriSalmao
            nigiriSkin = rec.nigiriSkin
            jow = rec.jow
            batera = rec.batera
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Produção da $shift", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(
                            text = "Cozinha Central • Hoje",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Info Box
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Info, contentDescription = "Info", tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Insira as quantidades de itens produzidos no turno da $shift hoje.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Production Items Inputs
                item { ItemCounterCard(name = "Uramaki Salmão", count = uramakiSalmao, onCountChange = { uramakiSalmao = it }) }
                item { ItemCounterCard(name = "Uramaki Shimeji", count = uramakiShimeji, onCountChange = { uramakiShimeji = it }) }
                item { ItemCounterCard(name = "Uramaki Skin", count = uramakiSkin, onCountChange = { uramakiSkin = it }) }
                item { ItemCounterCard(name = "Uramaki Grelhado", count = uramakiGrelhado, onCountChange = { uramakiGrelhado = it }) }
                item { ItemCounterCard(name = "Nigiri Salmão", count = nigiriSalmao, onCountChange = { nigiriSalmao = it }) }
                item { ItemCounterCard(name = "Nigiri Skin", count = nigiriSkin, onCountChange = { nigiriSkin = it }) }
                item { ItemCounterCard(name = "Jow", count = jow, onCountChange = { jow = it }) }
                item { ItemCounterCard(name = "Batera", count = batera, onCountChange = { batera = it }) }
            }

            // Save Action
            Card(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    val allowed = !isTodayClosed || session.role == "Administrador"

                    Button(
                        onClick = {
                            viewModel.saveProduction(
                                shift = shift,
                                uramakiSalmao = uramakiSalmao,
                                uramakiShimeji = uramakiShimeji,
                                uramakiSkin = uramakiSkin,
                                uramakiGrelhado = uramakiGrelhado,
                                nigiriSalmao = nigiriSalmao,
                                nigiriSkin = nigiriSkin,
                                jow = jow,
                                batera = batera
                            ) { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    onNavigateBack()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("save_production_button"),
                        shape = RoundedCornerShape(28.dp),
                        enabled = allowed
                    ) {
                        Text(
                            text = if (allowed) "Salvar Produção" else "Lançamentos Bloqueados",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCounterCard(
    name: String,
    count: Int,
    onCountChange: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.8.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Quantidade",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Decrement [-] Button (Minimum touch target 48dp)
                OutlinedIconButton(
                    onClick = { if (count > 0) onCountChange(count - 1) },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    colors = IconButtonDefaults.outlinedIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Diminuir",
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Center Quantity field - Clean Minimalist large number
                Text(
                    text = String.format("%02d", count),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(36.dp),
                    textAlign = TextAlign.Center
                )

                // Increment [+] Button (Minimum touch target 48dp)
                FilledIconButton(
                    onClick = { onCountChange(count + 1) },
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Aumentar",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// TELA DE FECHAMENTO (FECHAMENTO DO DIA)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClosingFormScreen(
    viewModel: ProductionViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val session by viewModel.sessionState.collectAsState()
    val isTodayClosed by viewModel.isTodayClosedFlow().collectAsState(initial = false)

    var lomboText by remember { mutableStateOf("") }
    var barrigaText by remember { mutableStateOf("") }
    var observations by remember { mutableStateOf("") }

    var uSalmao by remember { mutableStateOf(0) }
    var uShimeji by remember { mutableStateOf(0) }
    var uSkin by remember { mutableStateOf(0) }
    var uGrelhado by remember { mutableStateOf(0) }
    var nSalmao by remember { mutableStateOf(0) }
    var nSkin by remember { mutableStateOf(0) }
    var jowVal by remember { mutableStateOf(0) }
    var batVal by remember { mutableStateOf(0) }

    var showConfirmDialog by remember { mutableStateOf(false) }

    // Prepopulate today's values if exists
    LaunchedEffect(Unit) {
        val today = viewModel.getCurrentDateString()
        val closing = viewModel.allClosingRecords.value.find { it.date == today }
        if (closing != null) {
            lomboText = closing.lomboRestante.toString()
            barrigaText = closing.barrigaRestante.toString()
            observations = closing.observations
            uSalmao = closing.sobraUramakiSalmao
            uShimeji = closing.sobraUramakiShimeji
            uSkin = closing.sobraUramakiSkin
            uGrelhado = closing.sobraUramakiGrelhado
            nSalmao = closing.sobraNigiriSalmao
            nSkin = closing.sobraNigiriSkin
            jowVal = closing.sobraJow
            batVal = closing.sobraBatera
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Fechamento do Dia", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Estoque de Insumos (kg)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Insira as sobras de peixe medidas no final do dia. Aceita valores decimais.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = lomboText,
                            onValueChange = { lomboText = it },
                            label = { Text("Lombo Restante (kg)") },
                            leadingIcon = { Icon(Icons.Filled.Scale, "Scale") },
                            placeholder = { Text("Ex: 2.5") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("lombo_input")
                        )

                        OutlinedTextField(
                            value = barrigaText,
                            onValueChange = { barrigaText = it },
                            label = { Text("Barriga Restante (kg)") },
                            leadingIcon = { Icon(Icons.Filled.Scale, "Scale") },
                            placeholder = { Text("Ex: 1.3") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("barriga_input")
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Sobras de Produção (Sushis)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "Insira a quantidade de peças de sushi que sobraram no final do dia.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        ItemCounterCard(name = "Uramaki Salmão", count = uSalmao, onCountChange = { uSalmao = it })
                        ItemCounterCard(name = "Uramaki Shimeji", count = uShimeji, onCountChange = { uShimeji = it })
                        ItemCounterCard(name = "Uramaki Skin", count = uSkin, onCountChange = { uSkin = it })
                        ItemCounterCard(name = "Uramaki Grelhado", count = uGrelhado, onCountChange = { uGrelhado = it })
                        ItemCounterCard(name = "Nigiri Salmão", count = nSalmao, onCountChange = { nSalmao = it })
                        ItemCounterCard(name = "Nigiri Skin", count = nSkin, onCountChange = { nSkin = it })
                        ItemCounterCard(name = "Jow", count = jowVal, onCountChange = { jowVal = it })
                        ItemCounterCard(name = "Batera", count = batVal, onCountChange = { batVal = it })
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Observações Adicionais",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = observations,
                            onValueChange = { observations = it },
                            label = { Text("Notas do dia (Opcional)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("observations_input")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val allowed = !isTodayClosed || session.role == "Administrador"

            Button(
                onClick = {
                    if (lomboText.isBlank() || barrigaText.isBlank()) {
                        Toast.makeText(context, "Todos os campos numéricos de insumos devem ser preenchidos.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    showConfirmDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("finalize_day_button")
                    .windowInsetsPadding(WindowInsets.navigationBars),
                shape = RoundedCornerShape(16.dp),
                enabled = allowed
            ) {
                Text(
                    text = if (allowed) "Finalizar Dia" else "Fechamento já realizado hoje",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Confirmation Dialog
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Confirmar Fechamento", fontWeight = FontWeight.Bold) },
                text = {
                    Text("Deseja realmente finalizar as atividades de hoje? Após o fechamento, os lançamentos de turnos não poderão mais ser alterados sem autorização.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showConfirmDialog = false
                            val lombo = lomboText.toDoubleOrNull() ?: 0.0
                            val barriga = barrigaText.toDoubleOrNull() ?: 0.0
                            viewModel.saveClosing(
                                lombo = lombo,
                                barriga = barriga,
                                observations = observations,
                                sobraUramakiSalmao = uSalmao,
                                sobraUramakiShimeji = uShimeji,
                                sobraUramakiSkin = uSkin,
                                sobraUramakiGrelhado = uGrelhado,
                                sobraNigiriSalmao = nSalmao,
                                sobraNigiriSkin = nSkin,
                                sobraJow = jowVal,
                                sobraBatera = batVal
                            ) { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    onNavigateBack()
                                }
                            }
                        }
                    ) {
                        Text("Sim, Finalizar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

// ==========================================
// TELA DE HISTÓRICO (HISTÓRICO E CONSULTA)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: ProductionViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val session by viewModel.sessionState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val historyItems by viewModel.filteredRecords.collectAsState(initial = emptyList())

    // Filter items according to Employee view permission: ONLY allow current day
    val today = viewModel.getCurrentDateString()
    val viewableHistory = if (session.role == "Administrador") {
        historyItems
    } else {
        // Employee: Consult only current day
        historyItems.filter { it.date == today }
    }

    var selectedProductionToEdit by remember { mutableStateOf<ProductionRecord?>(null) }
    var selectedClosingToEdit by remember { mutableStateOf<ClosingRecord?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registros de Produção", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (session.role == "Administrador") {
                        IconButton(onClick = { viewModel.exportToExcel(context) }) {
                            Icon(Icons.Filled.GridOn, contentDescription = "Exportar Excel")
                        }
                        IconButton(onClick = { viewModel.exportToPdf(context) }) {
                            Icon(Icons.Filled.PictureAsPdf, contentDescription = "Exportar PDF")
                        }
                    }
                    IconButton(onClick = { viewModel.shareRecordsAsText(context, viewableHistory) }) {
                        Icon(Icons.Filled.Share, contentDescription = "Compartilhar Texto")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search field (Only useful or shown for admin since employee only sees today)
            if (session.role == "Administrador") {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = { Text("Pesquisar por data, produto ou usuário...") },
                    leadingIcon = { Icon(Icons.Filled.Search, "Search") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Exibindo apenas lançamentos do dia de hoje (${viewModel.formatDisplayDate(today)}).",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (viewableHistory.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Inbox,
                            contentDescription = "Empty",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nenhum registro encontrado.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(viewableHistory) { historyItem ->
                        var expanded by remember { mutableStateOf(false) }

                        Card(
                            onClick = { expanded = !expanded },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Filled.CalendarToday,
                                            contentDescription = "Data",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = viewModel.formatDisplayDate(historyItem.date),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (historyItem.closing != null) {
                                            SuggestionChip(
                                                onClick = {},
                                                label = { Text("Fechado") },
                                                icon = { Icon(Icons.Filled.Check, null, modifier = Modifier.size(12.dp)) }
                                            )
                                        } else {
                                            SuggestionChip(
                                                onClick = {},
                                                label = { Text("Aberto") }
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                            contentDescription = "Toggle Expand",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                // Collapsed small brief
                                if (!expanded) {
                                    val morningTotal = historyItem.productions.find { it.shift == "Manhã" }?.let {
                                        it.uramakiSalmao + it.uramakiShimeji + it.uramakiSkin + it.uramakiGrelhado + it.nigiriSalmao + it.nigiriSkin + it.jow + it.batera
                                    } ?: 0
                                    val nightTotal = historyItem.productions.find { it.shift == "Noite" }?.let {
                                        it.uramakiSalmao + it.uramakiShimeji + it.uramakiSkin + it.uramakiGrelhado + it.nigiriSalmao + it.nigiriSkin + it.jow + it.batera
                                    } ?: 0

                                    Text(
                                        text = "Produção Total: ${morningTotal + nightTotal} peças (M: $morningTotal, N: $nightTotal)",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }

                                // Expanded view
                                AnimatedVisibility(visible = expanded) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        HorizontalDivider()

                                        // Turno Manhã details
                                        val morningRecord = historyItem.productions.find { it.shift == "Manhã" }
                                        ShiftDetailView(
                                            title = "Produção da Manhã",
                                            record = morningRecord,
                                            isAdmin = session.role == "Administrador",
                                            onEdit = { selectedProductionToEdit = morningRecord },
                                            onDelete = { morningRecord?.let { viewModel.deleteProduction(it) } }
                                        )

                                        HorizontalDivider()

                                        // Turno Noite details
                                        val nightRecord = historyItem.productions.find { it.shift == "Noite" }
                                        ShiftDetailView(
                                            title = "Produção da Noite",
                                            record = nightRecord,
                                            isAdmin = session.role == "Administrador",
                                            onEdit = { selectedProductionToEdit = nightRecord },
                                            onDelete = { nightRecord?.let { viewModel.deleteProduction(it) } }
                                        )

                                        HorizontalDivider()

                                        // Fechamento details
                                        if (historyItem.closing != null) {
                                            Column {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(Icons.Filled.Scale, null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text("Fechamento do Dia", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                                    }

                                                    if (session.role == "Administrador") {
                                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                            IconButton(onClick = { selectedClosingToEdit = historyItem.closing }) {
                                                                Icon(Icons.Filled.Edit, "Editar", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                                            }
                                                            IconButton(onClick = { viewModel.deleteClosing(historyItem.closing) }) {
                                                                Icon(Icons.Filled.Delete, "Excluir", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                                                            }
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text("Responsável: ${historyItem.closing.user} às ${viewModel.formatDisplayTime(historyItem.closing.timestamp)}", fontSize = 12.sp, color = Color.Gray)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text("Lombo Restante: ${historyItem.closing.lomboRestante} kg", fontSize = 14.sp)
                                                Text("Barriga Restante: ${historyItem.closing.barrigaRestante} kg", fontSize = 14.sp)

                                                Spacer(modifier = Modifier.height(8.dp))
                                                Text("Sobras de Sushis:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                                                Column(modifier = Modifier.padding(start = 8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                    Row(modifier = Modifier.fillMaxWidth()) {
                                                        Text("Uramaki Salmão: ${historyItem.closing.sobraUramakiSalmao}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                                                        Text("Uramaki Shimeji: ${historyItem.closing.sobraUramakiShimeji}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                                                    }
                                                    Row(modifier = Modifier.fillMaxWidth()) {
                                                        Text("Uramaki Skin: ${historyItem.closing.sobraUramakiSkin}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                                                        Text("Uramaki Grelhado: ${historyItem.closing.sobraUramakiGrelhado}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                                                    }
                                                    Row(modifier = Modifier.fillMaxWidth()) {
                                                        Text("Nigiri Salmão: ${historyItem.closing.sobraNigiriSalmao}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                                                        Text("Nigiri Skin: ${historyItem.closing.sobraNigiriSkin}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                                                    }
                                                    Row(modifier = Modifier.fillMaxWidth()) {
                                                        Text("Jow: ${historyItem.closing.sobraJow}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                                                        Text("Batera: ${historyItem.closing.sobraBatera}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                                                    }
                                                }

                                                if (historyItem.closing.observations.isNotBlank()) {
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Text("Obs: ${historyItem.closing.observations}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                }
                                            }
                                        } else {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Filled.Warning, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Text("Sem registro de Fechamento de Insumos.", fontSize = 13.sp, color = Color.Gray)
                                            }
                                        }

                                        HorizontalDivider()

                                        Button(
                                            onClick = { viewModel.shareRecordsAsText(context, listOf(historyItem)) },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                            ),
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Compartilhar Resumo do Dia em Texto")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Production Edit Dialog
        selectedProductionToEdit?.let { record ->
            var uSalmao by remember { mutableStateOf(record.uramakiSalmao) }
            var uShimeji by remember { mutableStateOf(record.uramakiShimeji) }
            var uSkin by remember { mutableStateOf(record.uramakiSkin) }
            var uGrelhado by remember { mutableStateOf(record.uramakiGrelhado) }
            var nSalmao by remember { mutableStateOf(record.nigiriSalmao) }
            var nSkin by remember { mutableStateOf(record.nigiriSkin) }
            var jowVal by remember { mutableStateOf(record.jow) }
            var batVal by remember { mutableStateOf(record.batera) }

            AlertDialog(
                onDismissRequest = { selectedProductionToEdit = null },
                title = { Text("Editar Registro (${record.shift})") },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Edição rápida de quantidades para a data ${viewModel.formatDisplayDate(record.date)}:")

                        // Inputs
                        EditIntegerInput(label = "Uramaki Salmão", value = uSalmao, onValueChange = { uSalmao = it })
                        EditIntegerInput(label = "Uramaki Shimeji", value = uShimeji, onValueChange = { uShimeji = it })
                        EditIntegerInput(label = "Uramaki Skin", value = uSkin, onValueChange = { uSkin = it })
                        EditIntegerInput(label = "Uramaki Grelhado", value = uGrelhado, onValueChange = { uGrelhado = it })
                        EditIntegerInput(label = "Nigiri Salmão", value = nSalmao, onValueChange = { nSalmao = it })
                        EditIntegerInput(label = "Nigiri Skin", value = nSkin, onValueChange = { nSkin = it })
                        EditIntegerInput(label = "Jow", value = jowVal, onValueChange = { jowVal = it })
                        EditIntegerInput(label = "Batera", value = batVal, onValueChange = { batVal = it })
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updateProduction(
                                record.copy(
                                    uramakiSalmao = uSalmao,
                                    uramakiShimeji = uShimeji,
                                    uramakiSkin = uSkin,
                                    uramakiGrelhado = uGrelhado,
                                    nigiriSalmao = nSalmao,
                                    nigiriSkin = nSkin,
                                    jow = jowVal,
                                    batera = batVal
                                )
                            )
                            selectedProductionToEdit = null
                            Toast.makeText(context, "Registro atualizado!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Salvar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedProductionToEdit = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // Closing Edit Dialog
        selectedClosingToEdit?.let { closing ->
            var lomboText by remember { mutableStateOf(closing.lomboRestante.toString()) }
            var barrigaText by remember { mutableStateOf(closing.barrigaRestante.toString()) }
            var obsText by remember { mutableStateOf(closing.observations) }

            var uSalmao by remember { mutableStateOf(closing.sobraUramakiSalmao) }
            var uShimeji by remember { mutableStateOf(closing.sobraUramakiShimeji) }
            var uSkin by remember { mutableStateOf(closing.sobraUramakiSkin) }
            var uGrelhado by remember { mutableStateOf(closing.sobraUramakiGrelhado) }
            var nSalmao by remember { mutableStateOf(closing.sobraNigiriSalmao) }
            var nSkin by remember { mutableStateOf(closing.sobraNigiriSkin) }
            var jowVal by remember { mutableStateOf(closing.sobraJow) }
            var batVal by remember { mutableStateOf(closing.sobraBatera) }

            AlertDialog(
                onDismissRequest = { selectedClosingToEdit = null },
                title = { Text("Editar Fechamento") },
                text = {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Modificar sobras medidas de insumos:")

                        OutlinedTextField(
                            value = lomboText,
                            onValueChange = { lomboText = it },
                            label = { Text("Lombo Restante (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = barrigaText,
                            onValueChange = { barrigaText = it },
                            label = { Text("Barriga Restante (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Modificar sobras de sushis:")
                        EditIntegerInput(label = "Uramaki Salmão", value = uSalmao, onValueChange = { uSalmao = it })
                        EditIntegerInput(label = "Uramaki Shimeji", value = uShimeji, onValueChange = { uShimeji = it })
                        EditIntegerInput(label = "Uramaki Skin", value = uSkin, onValueChange = { uSkin = it })
                        EditIntegerInput(label = "Uramaki Grelhado", value = uGrelhado, onValueChange = { uGrelhado = it })
                        EditIntegerInput(label = "Nigiri Salmão", value = nSalmao, onValueChange = { nSalmao = it })
                        EditIntegerInput(label = "Nigiri Skin", value = nSkin, onValueChange = { nSkin = it })
                        EditIntegerInput(label = "Jow", value = jowVal, onValueChange = { jowVal = it })
                        EditIntegerInput(label = "Batera", value = batVal, onValueChange = { batVal = it })

                        OutlinedTextField(
                            value = obsText,
                            onValueChange = { obsText = it },
                            label = { Text("Observações") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val lombo = lomboText.toDoubleOrNull() ?: closing.lomboRestante
                            val barriga = barrigaText.toDoubleOrNull() ?: closing.barrigaRestante
                            viewModel.updateClosing(
                                closing.copy(
                                    lomboRestante = lombo,
                                    barrigaRestante = barriga,
                                    observations = obsText,
                                    sobraUramakiSalmao = uSalmao,
                                    sobraUramakiShimeji = uShimeji,
                                    sobraUramakiSkin = uSkin,
                                    sobraUramakiGrelhado = uGrelhado,
                                    sobraNigiriSalmao = nSalmao,
                                    sobraNigiriSkin = nSkin,
                                    sobraJow = jowVal,
                                    sobraBatera = batVal
                                )
                            )
                            selectedClosingToEdit = null
                            Toast.makeText(context, "Fechamento updated!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text("Salvar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedClosingToEdit = null }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun EditIntegerInput(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    var text by remember { mutableStateOf(value.toString()) }
    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            it.toIntOrNull()?.let { intVal -> onValueChange(intVal) }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ShiftDetailView(
    title: String,
    record: ProductionRecord?,
    isAdmin: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (title.contains("Manhã")) Icons.Filled.WbSunny else Icons.Filled.NightsStay,
                    contentDescription = null,
                    tint = if (title.contains("Manhã")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            if (record != null && isAdmin) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, "Editar", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Filled.Delete, "Excluir", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (record != null) {
            Text(
                text = "Responsável: ${record.user} às ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(record.timestamp))}",
                fontSize = 11.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Beautiful compact Grid layout for produced quantities
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Uramaki Salmão: ${record.uramakiSalmao}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                    Text("Uramaki Shimeji: ${record.uramakiShimeji}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Uramaki Skin: ${record.uramakiSkin}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                    Text("Uramaki Grelhado: ${record.uramakiGrelhado}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Nigiri Salmão: ${record.nigiriSalmao}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                    Text("Nigiri Skin: ${record.nigiriSkin}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("Jow: ${record.jow}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                    Text("Batera: ${record.batera}", modifier = Modifier.weight(1f), fontSize = 13.sp)
                }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Block, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sem registro para este turno hoje.", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

// ==========================================
// TELA DASHBOARD (MÉTRICAS & GRÁFICOS)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ProductionViewModel,
    onNavigateBack: () -> Unit
) {
    val productions by viewModel.allProductionRecords.collectAsState()
    val closings by viewModel.allClosingRecords.collectAsState()

    val today = viewModel.getCurrentDateString()
    val todayRecords = productions.filter { it.date == today }

    // Computations
    val totalToday = todayRecords.sumOf {
        it.uramakiSalmao + it.uramakiShimeji + it.uramakiSkin + it.uramakiGrelhado +
                it.nigiriSalmao + it.nigiriSkin + it.jow + it.batera
    }

    val morningQty = todayRecords.filter { it.shift == "Manhã" }.sumOf {
        it.uramakiSalmao + it.uramakiShimeji + it.uramakiSkin + it.uramakiGrelhado +
                it.nigiriSalmao + it.nigiriSkin + it.jow + it.batera
    }

    val nightQty = todayRecords.filter { it.shift == "Noite" }.sumOf {
        it.uramakiSalmao + it.uramakiShimeji + it.uramakiSkin + it.uramakiGrelhado +
                it.nigiriSalmao + it.nigiriSkin + it.jow + it.batera
    }

    // Totals by item (All history combined)
    val totalUramakiSalmao = productions.sumOf { it.uramakiSalmao }
    val totalUramakiShimeji = productions.sumOf { it.uramakiShimeji }
    val totalUramakiSkin = productions.sumOf { it.uramakiSkin }
    val totalUramakiGrelhado = productions.sumOf { it.uramakiGrelhado }
    val totalNigiriSalmao = productions.sumOf { it.nigiriSalmao }
    val totalNigiriSkin = productions.sumOf { it.nigiriSkin }
    val totalJow = productions.sumOf { it.jow }
    val totalBatera = productions.sumOf { it.batera }

    // Map of product totals for finding the most produced
    val productTotals = mapOf(
        "Uramaki Salmão" to totalUramakiSalmao,
        "Uramaki Shimeji" to totalUramakiShimeji,
        "Uramaki Skin" to totalUramakiSkin,
        "Uramaki Grelhado" to totalUramakiGrelhado,
        "Nigiri Salmão" to totalNigiriSalmao,
        "Nigiri Skin" to totalNigiriSkin,
        "Jow" to totalJow,
        "Batera" to totalBatera
    )
    val mostProduced = productTotals.maxByOrNull { it.value }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Métricas de Produção", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Totals Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total Produzido Hoje", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$totalToday peças", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            // Shifts breakdown
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Produção por Turno (Hoje)", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    // Manhã
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Manhã", fontSize = 13.sp)
                            Text("$morningQty pçs", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        val progressMorning = if (totalToday > 0) morningQty.toFloat() / totalToday else 0f
                        LinearProgressIndicator(
                            progress = { progressMorning },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }

                    // Noite
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Noite", fontSize = 13.sp)
                            Text("$nightQty pçs", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        val progressNight = if (totalToday > 0) nightQty.toFloat() / totalToday else 0f
                        LinearProgressIndicator(
                            progress = { progressNight },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.secondary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }

            // High Fidelity Custom Chart: Weekly Progress
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Gráfico de Produção Semanal", fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.padding(bottom = 12.dp))

                    // Compute weekly data points
                    val last7Days = (0..6).map { offset ->
                        val cal = Calendar.getInstance()
                        cal.add(Calendar.DAY_OF_YEAR, -offset)
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                    }.reversed()

                    val weeklyPoints = last7Days.map { date ->
                        val recs = productions.filter { it.date == date }
                        recs.sumOf {
                            it.uramakiSalmao + it.uramakiShimeji + it.uramakiSkin + it.uramakiGrelhado +
                                    it.nigiriSalmao + it.nigiriSkin + it.jow + it.batera
                        }
                    }

                    val maxPoint = weeklyPoints.maxOrNull()?.coerceAtLeast(1) ?: 100

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(top = 16.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val canvasWidth = size.width
                            val canvasHeight = size.height
                            val spacing = canvasWidth / (weeklyPoints.size - 1).coerceAtLeast(1)

                            // Draw baseline grid
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.2f),
                                start = Offset(0f, canvasHeight * 0.25f),
                                end = Offset(canvasWidth, canvasHeight * 0.25f),
                                strokeWidth = 1.dp.toPx()
                            )
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.2f),
                                start = Offset(0f, canvasHeight * 0.5f),
                                end = Offset(canvasWidth, canvasHeight * 0.5f),
                                strokeWidth = 1.dp.toPx()
                            )
                            drawLine(
                                color = Color.Gray.copy(alpha = 0.2f),
                                start = Offset(0f, canvasHeight * 0.75f),
                                end = Offset(canvasWidth, canvasHeight * 0.75f),
                                strokeWidth = 1.dp.toPx()
                            )

                            val path = Path()
                            weeklyPoints.forEachIndexed { idx, point ->
                                val x = idx * spacing
                                val y = canvasHeight - ((point.toFloat() / maxPoint) * (canvasHeight - 30f)) - 15f
                                if (idx == 0) {
                                    path.moveTo(x, y)
                                } else {
                                    path.lineTo(x, y)
                                }

                                // Point indicator
                                drawCircle(
                                    color = Color(0xFFFF5252),
                                    radius = 4.dp.toPx(),
                                    center = Offset(x, y)
                                )
                            }

                            // Line stroke
                            drawPath(
                                path = path,
                                color = Color(0xFFD32F2F),
                                style = Stroke(width = 3.dp.toPx())
                            )
                        }
                    }

                    // Labels Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        last7Days.forEach { date ->
                            val label = try {
                                val d = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
                                SimpleDateFormat("dd/MM", Locale.getDefault()).format(d!!)
                            } catch (e: Exception) {
                                ""
                            }
                            Text(label, fontSize = 10.sp, color = Color.Gray)
                        }
                    }
                }
            }

            // Products Ranking List
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Acumulado de Itens Produzidos (Histórico)", fontWeight = FontWeight.Bold, fontSize = 15.sp)

                    productTotals.entries.sortedByDescending { it.value }.forEach { (name, total) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(name, fontSize = 14.sp)
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ) {
                                Text("$total peças", modifier = Modifier.padding(4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }

            // Highlights
            mostProduced?.let {
                if (it.value > 0) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.EmojiEvents, contentDescription = "Destaque", tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Sushi Mais Produzido", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                Text("${it.key} (${it.value} peças no total)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// TELA USUÁRIOS (GERENCIAMENTO)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    viewModel: ProductionViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val users by viewModel.allUsers.collectAsState()

    var usernameText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Funcionário") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gerenciar Usuários", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // New user registry Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Cadastrar Novo Funcionário", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)

                    OutlinedTextField(
                        value = usernameText,
                        onValueChange = { usernameText = it },
                        label = { Text("Nome de Usuário") },
                        leadingIcon = { Icon(Icons.Filled.Person, "User") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = passwordText,
                        onValueChange = { passwordText = it },
                        label = { Text("Senha") },
                        leadingIcon = { Icon(Icons.Filled.Lock, "Pass") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Role Select Option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Perfil:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedRole == "Funcionário",
                                onClick = { selectedRole = "Funcionário" }
                            )
                            Text("Funcionário", fontSize = 14.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedRole == "Administrador",
                                onClick = { selectedRole = "Administrador" }
                            )
                            Text("Admin", fontSize = 14.sp)
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.addUser(usernameText, passwordText, selectedRole) { success, msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                if (success) {
                                    usernameText = ""
                                    passwordText = ""
                                    selectedRole = "Funcionário"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cadastrar Usuário", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text("Usuários Registrados", fontWeight = FontWeight.Bold, fontSize = 15.sp)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.Person,
                                        contentDescription = "User icon",
                                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(user.username, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(user.role, fontSize = 11.sp, color = Color.Gray)
                                }
                            }

                            // Don't delete original default admin to prevent lockout
                            if (user.username != "admin") {
                                IconButton(onClick = { viewModel.deleteUser(user) }) {
                                    Icon(Icons.Filled.Delete, "Excluir", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
