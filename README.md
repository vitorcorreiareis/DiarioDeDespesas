# 💸 Diário de Despesas

Aplicativo Android para **controle de gastos e receitas pessoais**, desenvolvido como projeto acadêmico da disciplina de Desenvolvimento Mobile.

---

## 📱 Funcionalidades

- **Autenticação local** — cadastro e login com senha criptografada (SHA-256), sessão persistida
- **Lançar Despesa** — valor, categoria, descrição e forma de pagamento
- **Lançar Receita** — salário, freelance, pix recebido etc.
- **Dashboard mensal** — card com Receita / Despesa / Saldo do mês
- **Orçamento por categoria** — configure limites e acompanhe o progresso com barra visual
- **Histórico completo** — todos os lançamentos com distinção visual entre receitas e despesas

---

## 🗂️ Categorias de Despesa

| Emoji | Nome |
|-------|------|
| 🛒 | Supermercado |
| ⛽ | Combustível |
| 💊 | Farmácia / Saúde |
| 📦 | Outros |

---

## 🛠️ Tecnologias

| Camada | Tecnologia |
|--------|-----------|
| Linguagem | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Arquitetura | MVVM + StateFlow |
| Banco de dados | Room (SQLite) |
| Autenticação | Local (Room + SharedPreferences) |
| Navegação | Navigation Compose |
| Build | Android Gradle Plugin 9 + KSP |

---

## 🏗️ Arquitetura

```
app/
└── src/main/java/com/example/diariodedespesas/
    ├── data/
    │   ├── local/
    │   │   ├── dao/          # ExpenseDao, BudgetDao, UserDao
    │   │   ├── entity/       # ExpenseEntity, BudgetEntity, UserEntity
    │   │   └── AppDatabase   # Room singleton
    │   ├── model/            # Category, PaymentMethod (enums)
    │   └── repository/       # AuthRepository, ExpenseRepository, BudgetRepository
    ├── ui/
    │   ├── navigation/       # AppNavigation, rotas
    │   ├── screens/
    │   │   ├── auth/         # LoginScreen, RegisterScreen
    │   │   ├── dashboard/    # DashboardScreen
    │   │   ├── expense/      # AddExpenseScreen
    │   │   ├── budget/       # BudgetConfigScreen
    │   │   └── history/      # HistoryScreen
    │   ├── theme/            # Color, Theme, Type
    │   └── viewmodel/        # AuthViewModel, DashboardViewModel, AddExpenseViewModel, BudgetViewModel
    └── MainActivity.kt
```

---

## 🎨 Protótipo

Figma: [Visualizar protótipo](https://www.figma.com/design/qeL1vSrZVrBDFPCsJtQ9S1)

Telas: Login · Cadastro · Dashboard · Lançar · Orçamento · Histórico

---

## ▶️ Como executar

1. Clone o repositório
   ```bash
   git clone https://github.com/seu-usuario/DiarioDeDespesas.git
   ```
2. Abra no **Android Studio** (versão Ladybug ou superior)
3. Aguarde o **Sync Gradle** concluir
4. Execute em um emulador ou dispositivo com **Android 7.0+ (API 24)**

> Nenhuma configuração externa necessária — a autenticação é local, sem Firebase.

---

## 📋 Requisitos

- Android Studio Ladybug (2024.2+)
- JDK 11+
- Android SDK API 24–37

---

## 👤 Autor

Desenvolvido por **Vitor** — Projeto Acadêmico · 2026
