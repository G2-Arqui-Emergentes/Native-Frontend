// views/layout/project/Membership.kt
package com.example.taskmaster.views.layout.project

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.taskmaster.viewmodel.model.ProjectsViewModel

data class MembershipPlan(
    val id: String,
    val name: String,
    val price: String,
    val subtitle: String,
    val features: List<String>,
    val buttonLabel: String,
    val isPopular: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Membership(
    nav: NavHostController,
    vm: ProjectsViewModel = remember { ProjectsViewModel() }
) {
    val plans = remember {
        listOf(
            MembershipPlan(
                id = "free",
                name = "Gratis",
                price = "0",
                subtitle = "Para equipos pequeños",
                features = listOf(
                    "Hasta 5 usuarios",
                    "3 proyectos activos",
                    "Almacenamiento 5GB",
                    "Soporte por email",
                    "Reportes avanzados",
                    "Integraciones"
                ),
                buttonLabel = "Empezar gratis"
            ),
            MembershipPlan(
                id = "pro",
                name = "Pro",
                price = "5",
                subtitle = "Para empresas medianas",
                features = listOf(
                    "Usuarios ilimitados",
                    "Proyectos ilimitados",
                    "Almacenamiento 100GB",
                    "Soporte prioritario",
                    "Reportes avanzados",
                    "Integraciones básicas"
                ),
                buttonLabel = "Elegir Pro",
                isPopular = true
            ),
            MembershipPlan(
                id = "enterprise",
                name = "Enterprise",
                price = "15",
                subtitle = "Escalabilidad total",
                features = listOf(
                    "Usuarios ilimitados",
                    "Proyectos ilimitados",
                    "Almacenamiento ilimitado",
                    "Soporte dedicado 24/7",
                    "Analytics avanzados",
                    "Todas las integraciones"
                ),
                buttonLabel = "Elegir Enterprise"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Membresias") },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(plans) { plan ->
                    MembershipCard(
                        plan = plan,
                        onClick = {
                            // nav.navigate("checkout/${plan.id}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MembershipCard(
    plan: MembershipPlan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(260.dp)
            .height(420.dp), // para que todas se vean del mismo alto
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // espacio para el badge
                if (plan.isPopular) Spacer(Modifier.height(10.dp))

                Text(
                    text = plan.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "S/ ",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = plan.price,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(Modifier.width(2.dp))
                    Text(
                        text = "/ mes",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = plan.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    plan.features.forEach { feature ->
                        Text(
                            text = feature,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.weight(1f))

                if (plan.id == "free") {
                    OutlinedButton(
                        onClick = onClick,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            plan.buttonLabel,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            plan.buttonLabel,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        if (plan.isPopular) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-14).dp)
            ) {
                Text(
                    text = "Más popular",
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 4.dp
                    ),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
