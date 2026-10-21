package com.example.diariodedespesas.data.model

import androidx.compose.ui.graphics.Color

enum class Category(
    val displayName: String,
    val emoji: String,
    val color: Color
) {
    SUPERMERCADO("Supermercado", "🛒", Color(0xFF38B57D)),
    COMBUSTIVEL("Combustível", "⛽", Color(0xFFF2C22E)),
    FARMACIA("Farmácia/Saúde", "💊", Color(0xFFF24242)),
    OUTROS("Outros", "📦", Color(0xFF4585F2));

    companion object {
        fun fromName(name: String): Category =
            entries.firstOrNull { it.name == name } ?: OUTROS
    }
}

enum class PaymentMethod(val displayName: String) {
    PIX("Pix"),
    DINHEIRO("Dinheiro"),
    CARTAO_CREDITO("Cartão de Crédito"),
    CARTAO_DEBITO("Cartão de Débito");

    companion object {
        fun fromName(name: String): PaymentMethod =
            entries.firstOrNull { it.name == name } ?: PIX
    }
}
