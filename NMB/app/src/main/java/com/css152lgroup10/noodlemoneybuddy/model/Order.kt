data class Order(
    val id: String,
    val items: MutableList<OrderItem> = mutableListOf(),
    val timestamp: Long = System.currentTimeMillis(),
    var status: OrderStatus = OrderStatus.PENDING,
    var paymentMethod: PaymentMethod? = null,
    var customerName: String = "",
    var notes: String = ""
) {
    val subtotal: Double
        get() = items.sumOf { it.totalPrice }

    val tax: Double
        get() = subtotal * 0.12 // 12% tax

    val total: Double
        get() = subtotal + tax
}