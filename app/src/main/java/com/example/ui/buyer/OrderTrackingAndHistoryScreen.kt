package com.example.ui.buyer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.ui.components.formatPkr
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

val OrderTimelineSteps = listOf(
    "PENDING" to "Order Placed",
    "CONFIRMED" to "Order Confirmed by Seller",
    "PROCESSING" to "Processing & Packaging",
    "SHIPPED" to "Dispatched via Logistics",
    "OUT_FOR_DELIVERY" to "Out for Delivery",
    "DELIVERED" to "Delivered Successfully"
)

@Composable
fun OrderSuccessScreen(
    order: OrderEntity,
    onTrackOrder: (String) -> Unit,
    onContinueShopping: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BichkandBgLight)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(BichkandGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Order Placed Successfully!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Black,
            color = BichkandNavyDark
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Order #${order.orderNumber}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = BichkandElectricBlue
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Order Details", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Payment Method:", fontSize = 12.sp, color = BichkandTextSecondary)
                    Text(order.paymentMethod, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Amount:", fontSize = 12.sp, color = BichkandTextSecondary)
                    Text(formatPkr(order.totalAmount), fontSize = 13.sp, fontWeight = FontWeight.Black, color = BichkandElectricBlue)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Deliver to:", fontSize = 12.sp, color = BichkandTextSecondary)
                    Text(order.city, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onTrackOrder(order.id) },
            colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("track_order_button"),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.LocalShipping, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Track Order Progress", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onContinueShopping,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Continue Shopping")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
    order: OrderEntity,
    onBack: () -> Unit
) {
    val currentStepIndex = OrderTimelineSteps.indexOfFirst { it.first.equals(order.orderStatus, ignoreCase = true) }
        .let { if (it == -1) 0 else it }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Order #${order.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 17.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BichkandSurfaceLight)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BichkandBgLight),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Status Summary Header
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandNavyDark),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Current Status", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                            Surface(
                                color = getStatusColor(order.orderStatus),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = order.orderStatus,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = getStatusDescription(order.orderStatus),
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Estimated delivery in 2-3 business days across Pakistan",
                            color = BichkandCyan,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Timeline Steps
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Order Journey", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(14.dp))

                        OrderTimelineSteps.forEachIndexed { index, (statusKey, stepLabel) ->
                            val isCompleted = index <= currentStepIndex && order.orderStatus != "CANCELLED"
                            val isCurrent = index == currentStepIndex && order.orderStatus != "CANCELLED"

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isCurrent -> BichkandElectricBlue
                                                    isCompleted -> BichkandGreen
                                                    else -> Color(0xFFCBD5E1)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isCompleted && !isCurrent) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        } else if (isCurrent) {
                                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
                                        }
                                    }

                                    if (index < OrderTimelineSteps.size - 1) {
                                        Box(
                                            modifier = Modifier
                                                .width(2.dp)
                                                .height(30.dp)
                                                .background(if (index < currentStepIndex) BichkandGreen else Color(0xFFCBD5E1))
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = stepLabel,
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp,
                                        color = if (isCompleted) BichkandNavyDark else BichkandTextSecondary
                                    )
                                    if (isCurrent) {
                                        Text(
                                            text = "Active update • ${formatDate(order.updatedAt)}",
                                            fontSize = 10.sp,
                                            color = BichkandElectricBlue
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Delivery and Payment Details
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Shipping & Payment", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Recipient: ${order.buyerName} (${order.buyerPhone})", fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Address: ${order.deliveryAddress}", fontSize = 12.sp, color = BichkandTextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Payment: ${order.paymentMethod}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Status: ${order.paymentStatus}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BichkandGreen)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Paid:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(formatPkr(order.totalAmount), fontSize = 14.sp, fontWeight = FontWeight.Black, color = BichkandElectricBlue)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersHistoryScreen(
    orders: List<OrderEntity>,
    onTrackOrder: (String) -> Unit,
    onStartShopping: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BichkandSurfaceLight)
            )
        }
    ) { paddingValues ->
        if (orders.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.LocalShipping, contentDescription = null, tint = BichkandTextSecondary, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("No orders placed yet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BichkandNavyDark)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Your purchases will appear here with live tracking.", fontSize = 12.sp, color = BichkandTextSecondary)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onStartShopping,
                    colors = ButtonDefaults.buttonColors(containerColor = BichkandElectricBlue)
                ) {
                    Text("Explore Marketplace")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(BichkandBgLight),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(orders) { order ->
                    Card(
                        onClick = { onTrackOrder(order.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = BichkandSurfaceLight),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("order_history_card_${order.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Order #${order.orderNumber}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = BichkandNavyDark
                                )

                                Surface(
                                    color = getStatusColor(order.orderStatus),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = order.orderStatus,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Placed on ${formatDate(order.createdAt)} • ${order.city}",
                                fontSize = 11.sp,
                                color = BichkandTextSecondary
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Total Amount", fontSize = 10.sp, color = BichkandTextSecondary)
                                    Text(
                                        text = formatPkr(order.totalAmount),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = BichkandElectricBlue
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Track Order", color = BichkandElectricBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = BichkandElectricBlue, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getStatusColor(status: String): Color {
    return when (status.uppercase()) {
        "DELIVERED" -> BichkandGreen
        "OUT_FOR_DELIVERY" -> BichkandCyan
        "SHIPPED", "PROCESSING", "CONFIRMED" -> BichkandElectricBlue
        "CANCELLED", "RETURNED" -> BichkandRose
        else -> BichkandAmber
    }
}

fun getStatusDescription(status: String): String {
    return when (status.uppercase()) {
        "DELIVERED" -> "Your package has been safely delivered!"
        "OUT_FOR_DELIVERY" -> "Your courier rider is on the way to your address."
        "SHIPPED" -> "Your package has left the seller hub."
        "PROCESSING" -> "Seller is carefully packing your items."
        "CONFIRMED" -> "Seller verified your order."
        "CANCELLED" -> "This order was cancelled."
        else -> "Order received by seller. Awaiting confirmation."
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
