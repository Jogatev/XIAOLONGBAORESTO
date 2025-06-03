data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val category: String,
    val imageUrl: String = "",
    val description: String = "",
    var isAvailable: Boolean = true
)